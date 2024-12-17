<template>
  <div id="admin-home">
    <el-container style="height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="200px">
        <div class="logo">学生选课系统</div>
        <el-menu
          default-active="student-management"
          @select="handleSelect"
        >
          <el-menu-item index="student-management">学生管理</el-menu-item>
          <el-menu-item index="course-management">课程管理</el-menu-item>
          <el-menu-item index="teacher-management">教师管理</el-menu-item>
          <el-menu-item index="major-management">专业管理</el-menu-item>
          <el-menu-item index="college-management">学院管理</el-menu-item>
          <el-menu-item index="course-allocation">课程分配管理</el-menu-item>
          <el-menu-item index="student-course-selection">学生选课管理</el-menu-item>
          <el-menu-item index="statistics-analysis">统计分析</el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-container>
        <navbar></navbar>
        <el-main>
          <!-- 根据选中的菜单项显示对应的组件 -->
          <student-management v-if="selectedMenu === 'student-management'"></student-management>
          <course-management v-else-if="selectedMenu === 'course-management'"></course-management>
          <teacher-management v-else-if="selectedMenu === 'teacher-management'"></teacher-management>
          <major-management v-else-if="selectedMenu === 'major-management'"></major-management>
          <college-management v-else-if="selectedMenu === 'college-management'"></college-management>
          <course-allocation v-else-if="selectedMenu === 'course-allocation'"></course-allocation>
          <student-course-selection v-else-if="selectedMenu === 'student-course-selection'"></student-course-selection>
          <statistics-analysis v-else-if="selectedMenu === 'statistics-analysis'"></statistics-analysis>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
// 导入各个管理组件
import StudentManagement from '@/components/admin-components/StudentManagement.vue';
import CourseManagement from '@/components/admin-components/CourseManagement.vue';
import TeacherManagement from '@/components/admin-components/TeacherManagement.vue';
import MajorManagement from '@/components/admin-components/MajorManagement.vue';
import CollegeManagement from '@/components/admin-components/CollegeManagement.vue';
import CourseAllocation from '@/components/admin-components/CourseAllocation.vue';
import StudentCourseSelection from '@/components/admin-components/StudentCourseSelection.vue';
import StatisticsAnalysis from '@/components/admin-components/StatisticsAnalysis.vue';
import Navbar from '@/components/Navbar.vue';

export default {
  name: 'AdminHome',
  components: {
    StudentManagement,
    CourseManagement,
    TeacherManagement,
    MajorManagement,
    CollegeManagement,
    CourseAllocation,
    StudentCourseSelection,
    StatisticsAnalysis,
    Navbar
  },
  data() {
    return {
      selectedMenu: 'student-management', // 默认选中的菜单项
    };
  },
  methods: {
    handleSelect(key) {
      this.selectedMenu = key;
    },
  },
};
</script>

<style scoped>
#admin-home {
  height: 100vh;
}
.logo {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 20px 0;
}
</style>