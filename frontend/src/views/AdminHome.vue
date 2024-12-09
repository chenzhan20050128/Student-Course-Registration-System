<template>
  <div id="admin-home">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px">
        <div class="logo">学生选课系统</div>
        <el-menu
          default-active="student-management"
          @select="handleSelect"
          router
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
        <el-header>管理员首页</el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'AdminHome',
  methods: {
    handleSelect(key, keyPath) {
      console.log('选择了菜单项：', key);
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