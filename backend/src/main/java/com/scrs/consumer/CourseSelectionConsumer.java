package com.scrs.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

            // 检查是否已经选过该课程
            QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sid", sid);
            queryWrapper.eq("cid", cid);
            queryWrapper.eq("status", 1);
            StudentCourse existingCourse = studentCourseService.getOne(queryWrapper);
            if (existingCourse != null) {
                log.warn("学生{}已经选过课程{}", sid, cid);
                return;
            }

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

            // 检查专业匹配
            if (student.getMajor() != null && course.getMajor() != null) {
                if (!student.getMajor().equals(course.getMajor())) {
                    log.warn("学生专业{}与课程专业{}不符", student.getMajor(), course.getMajor());
                    return;
                }
            }

            // 检查课程库存
            if (course.getNum() == null || course.getStock() == null) {
                log.error("课程{}库存信息不完整", cid);
                return;
            }

            if (course.getNum() >= course.getStock()) {
                log.warn("课程{}已经选满", cid);
                return;
            }

            // 更新课程选课人数
            course.setNum(course.getNum() + 1);
            courseService.updateById(course);

            // 保存选课记录
            StudentCourse newStudentCourse = new StudentCourse();
            newStudentCourse.setSid(sid);
            newStudentCourse.setCid(cid);
            newStudentCourse.setStatus(1);
            boolean saveResult = studentCourseService.save(newStudentCourse);

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
                // 回滚课程人数
                course.setNum(course.getNum() - 1);
                courseService.updateById(course);
            }

        } catch (Exception e) {
            log.error("处理选课消息失败: {}", message, e);
        }
    }
}
