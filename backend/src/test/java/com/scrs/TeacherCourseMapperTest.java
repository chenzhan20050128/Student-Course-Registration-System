package com.scrs;/*
 * @date 12/05 21:07
 */

import com.scrs.mapper.TeacherCourseMapper;
import com.scrs.pojo.TeacherCourse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
// 其他必要的导入

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TeacherCourseMapperTest {

    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

    @Test
    public void testListTeacherCourse_WithValidId() {
        Integer teacherCourseId = 1;
        List<TeacherCourse> result = teacherCourseMapper.listTeacherCourse(teacherCourseId);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        for (TeacherCourse tc : result) {
            assertEquals(teacherCourseId, tc.getId());
            assertNotNull(tc.getTeacher());
            assertNotNull(tc.getCourse());
            System.out.println(tc.toString());
        }

        // 进一步检查返回的结果是否符合预期
        for (TeacherCourse tc : result) {
            assertEquals(teacherCourseId, tc.getId());
            assertNotNull(tc.getTeacher());
            assertNotNull(tc.getCourse());
        }
    }

    @Test
    public void testListTeacherCourse_WithNullId() {
        List<TeacherCourse> result = teacherCourseMapper.listTeacherCourse(null);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // 检查返回的所有TeacherCourse对象是否有效
        for (TeacherCourse tc : result) {
            assertNotNull(tc.getId());
            assertNotNull(tc.getTeacher());
            assertNotNull(tc.getCourse());
        }
    }

    @Test
    public void testListTeacherCourse_WithInvalidId() {
        Integer teacherCourseId = -1;
        List<TeacherCourse> result = teacherCourseMapper.listTeacherCourse(teacherCourseId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}