<template>
  <div id="admin-home">
    <el-container style="width: 1700px; height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="aside">
        <div class="logo">选课管理系统</div>
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
      <el-container class="admin-container">
        <el-header></el-header>
        <navbar :currentPageName="currentPageName"></navbar>
        <el-main class="main-content">
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
    Navbar,
  },
  data() {
    return {
      selectedMenu: 'student-management', // 默认选中的菜单项
    };
  },
  computed: {
    currentPageName() {
      // 根据选中的菜单项返回对应的页面名称
      const pageNames = {
        'student-management': '学生管理',
        'course-management': '课程管理',
        'teacher-management': '教师管理',
        'major-management': '专业管理',
        'college-management': '学院管理',
        'course-allocation': '课程分配管理',
        'student-course-selection': '学生选课管理',
        'statistics-analysis': '统计分析',
      };
      return pageNames[this.selectedMenu] || '未知页面';
    },
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
  height: 100vh; /* 页面高度固定为视窗高度 */
}

/* 侧边栏样式 */
.aside {
  background-color: #6a005f; /* 紫色 */
  color: white; /* 确保文字颜色清晰 */
  height: 100%; /* 让侧边栏高度占满页面 */
  min-height: 100vh; /* 确保侧边栏一直延伸到底部 */
  display: flex;
  flex-direction: column; /* 让菜单垂直排列 */
}

/* 侧边栏 logo 样式 */
.logo {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 20px 0;
  color: white; /* 确保 logo 文字可见 */
}

/* 菜单项选中状态 */
.el-menu-item.is-active {
  background-color: #a5519c !important; /* 深一点的紫色 */
  color: white !important;
}

/* 菜单项悬停效果 */
.el-menu-item:hover {
  background-color: #cd8dc6 !important; /* 浅紫色 */
}

/* 主内容区域微调 */
.el-main {
  margin-left: 10px;
  margin-top: -10px; /* 向上移动20px */
  padding: 15px;
  background-color: #F9F9F9; /* 浅灰色内容背景 */
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 轻微的阴影效果 */
}

.admin-container {
  margin-left: 10px;
}
</style>