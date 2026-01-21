package com.scrs.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scrs.pojo.Course;
import com.scrs.pojo.Student;
import com.scrs.pojo.StudentCourse;
import com.scrs.service.CourseService;
import com.scrs.service.StudentCourseService;
import com.scrs.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CourseSelectionConsumer {

    @Autowired
    private StudentCourseService studentCourseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String COURSE_RANK_KEY = "course:rank";

    @KafkaListener(topics = "course_selection_topic", groupId = "course-selection-group")
    public void consumeCourseSelection(String message) {
        log.info("收到选课消息: {}", message);

        try {
            // 解析消息
            Map<String, Object> messageMap = JSON.parseObject(message, Map.class);
            Integer sid = (Integer) messageMap.get("studentId");
            Integer cid = (Integer) messageMap.get("courseId");

            log.info("处理选课: 学生ID={}, 课程ID={}", sid, cid);

            // 检查是否已经选过该课程（status=1表示已选）
            QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sid", sid);
            queryWrapper.eq("cid", cid);
            queryWrapper.eq("status", 1);
            StudentCourse existingCourse = studentCourseService.getOne(queryWrapper);
            if (existingCourse != null) {
                log.warn("学生{}已经选过课程{}", sid, cid);
                rollbackRedisStock(cid);
                return;
            }

            // 检查是否有退选记录（status=0）
            QueryWrapper<StudentCourse> withdrawnQuery = new QueryWrapper<>();
            withdrawnQuery.eq("sid", sid);
            withdrawnQuery.eq("cid", cid);
            withdrawnQuery.eq("status", 0);
            StudentCourse withdrawnCourse = studentCourseService.getOne(withdrawnQuery);

            // 获取学生和课程信息
            Student student = studentService.getById(sid);
            Course course = courseService.getById(cid);

            if (student == null) {
                log.error("学生不存在: {}", sid);
                return;
            }
            if (course == null) {
                log.error("课程不存在: {}", cid);
                return;
            }

            // 检查课程库存
            if (course.getEnrolledCount() == null || course.getCapacity() == null) {
                log.error("课程{}库存信息不完整", cid);
                // 回滚Redis
                rollbackRedisStock(cid);
                return;
            }

            if (course.getEnrolledCount() >= course.getCapacity()) {
                log.warn("课程{}已经选满", cid);
                // 回滚Redis
                rollbackRedisStock(cid);
                return;
            }

            // 使用乐观锁更新课程选课人数（防止并发问题）
            log.info("开始更新课程{}, 当前enrolledCount={}, capacity={}", cid, course.getEnrolledCount(), course.getCapacity());
            UpdateWrapper<Course> updateWrapper = new UpdateWrapper<>();
            updateWrapper.setSql("enrolled_count = enrolled_count + 1")
                    .eq("course_id", cid)
                    .lt("enrolled_count", course.getCapacity()); // 确保不超过库存
            boolean updated = courseService.update(new Course(), updateWrapper);
            log.info("更新结果: {}", updated);

            if (!updated) {
                log.warn("课程{}更新失败，可能已选满", cid);
                // 回滚Redis
                rollbackRedisStock(cid);
                return;
            }

            // 保存选课记录
            log.info("开始保存选课记录: sid={}, cid={}", sid, cid);
            boolean saveResult;
            if (withdrawnCourse != null) {
                // 如果有退选记录，直接更新status=1
                log.info("发现退选记录，更新status为1");
                withdrawnCourse.setStatus(1);
                saveResult = studentCourseService.updateById(withdrawnCourse);
            } else {
                // 否则插入新记录
                log.info("插入新选课记录");
                StudentCourse newStudentCourse = new StudentCourse();
                newStudentCourse.setSid(sid);
                newStudentCourse.setCid(cid);
                newStudentCourse.setStatus(1);
                saveResult = studentCourseService.save(newStudentCourse);
            }
            log.info("保存选课记录结果: {}", saveResult);

            if (saveResult) {
                log.info("选课成功: 学生ID={}, 课程ID={}", sid, cid);

                // 更新 Redis 排行榜
                try {
                    stringRedisTemplate.opsForZSet().incrementScore(COURSE_RANK_KEY, cid.toString(), 1);
                } catch (Exception e) {
                    log.error("更新 Redis 排行榜失败", e);
                }
            } else {
                log.error("保存选课记录失败: 学生ID={}, 课程ID={}", sid, cid);
                // 回滚数据库和Redis
                UpdateWrapper<Course> rollbackWrapper = new UpdateWrapper<>();
                rollbackWrapper.setSql("num = num - 1").eq("id", cid);
                courseService.update(new Course(), rollbackWrapper);
                rollbackRedisStock(cid);
            }

        } catch (Exception e) {
            log.error("处理选课消息失败: {}, 异常类型: {}, 异常信息: {}", message, e.getClass().getName(), e.getMessage());
            e.printStackTrace(); // 打印完整堆栈
        }
    }

    /**
     * 回滚Redis库存
     */
    private void rollbackRedisStock(Integer cid) {
        try {
            String numKey = "course:num:" + cid;
            stringRedisTemplate.opsForValue().decrement(numKey);
            log.info("回滚Redis库存: courseId={}", cid);
        } catch (Exception e) {
            log.error("回滚Redis库存失败: courseId={}", cid, e);
        }
    }
}
