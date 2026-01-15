<template>
  <div id="admin-home">
    <div class="admin-wrapper">
      <!-- 顶部导航栏 -->
      <navbar :current-page-name="currentPageName"></navbar>
      
      <!-- 主内容区域 -->
      <div class="main-wrapper">
        <!-- 侧边栏菜单 -->
        <aside class="admin-sidebar">
          <div class="sidebar-logo">选课管理系统</div>
          <nav class="sidebar-menu">
            <button 
              v-for="item in menuItems"
              :key="item.id"
              :class="['menu-item', { active: selectedMenu === item.id }]"
              @click="selectedMenu = item.id"
            >
              {{ item.label }}
            </button>
          </nav>
        </aside>

        <!-- 内容容器 -->
        <div class="content-area">
          <student-management v-if="selectedMenu === 'student-management'"></student-management>
          <course-management v-else-if="selectedMenu === 'course-management'"></course-management>
          <teacher-management v-else-if="selectedMenu === 'teacher-management'"></teacher-management>
          <major-management v-else-if="selectedMenu === 'major-management'"></major-management>
          <college-management v-else-if="selectedMenu === 'college-management'"></college-management>
          <course-allocation v-else-if="selectedMenu === 'course-allocation'"></course-allocation>
          <student-course-selection v-else-if="selectedMenu === 'student-course-selection'"></student-course-selection>
          <statistics-analysis v-else-if="selectedMenu === 'statistics-analysis'"></statistics-analysis>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
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
      selectedMenu: 'student-management',
      menuItems: [
        { id: 'student-management', label: '学生管理' },
        { id: 'course-management', label: '课程管理' },
        { id: 'teacher-management', label: '教师管理' },
        { id: 'major-management', label: '专业管理' },
        { id: 'college-management', label: '学院管理' },
        { id: 'course-allocation', label: '课程分配管理' },
        { id: 'student-course-selection', label: '学生选课管理' },
        { id: 'statistics-analysis', label: '统计分析' },
      ],
    };
  },
  computed: {
    currentPageName() {
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
};
</script>

<style scoped>
#admin-home {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-wrapper {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-wrapper {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.admin-sidebar {
  width: 200px;
  background-color: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.sidebar-logo {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  padding: 10px 0;
}

.menu-item {
  padding: 12px 16px;
  background: transparent;
  border: none;
  color: #bbb;
  font-size: 14px;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.08);
  color: white;
}

.menu-item.active {
  background-color: rgba(102, 126, 234, 0.2);
  color: #667eea;
  border-left-color: #667eea;
  font-weight: 600;
}

.content-area {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
  background-color: white;
}
</style>