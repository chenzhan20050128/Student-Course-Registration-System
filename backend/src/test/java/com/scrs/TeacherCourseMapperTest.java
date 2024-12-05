package com.scrs;/*
 * @date 12/05 21:07
 */

import com.scrs.mapper.TeacherCourseMapper;
import com.scrs.pojo.TeacherCourse;
import com.scrs.service.TeacherCourseService;
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

    @Autowired
    private TeacherCourseService teacherCourseService;

    @Test
    public void testListTeacherCourse() {
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
    public void testListTeacherCourse_Service() {
        Integer testId = 1;
        List<TeacherCourse> teacherCourseList = teacherCourseService.listTeacherCourse(testId);
        for (TeacherCourse tc : teacherCourseList) {
            assertEquals(testId, tc.getId());
            assertNotNull(tc.getTeacher());
            assertNotNull(tc.getCourse());
            System.out.println(tc.getTeacher());
            System.out.println(tc.getCourse());
            System.out.println(tc.toString());
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