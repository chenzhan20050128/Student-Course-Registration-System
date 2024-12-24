<template>
  <div id="teacher-home">
    <el-container style="width: 100vw; height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="aside">
        <div class="logo">教师授课系统</div>
        <el-menu
          default-active="teaching-info"
          @select="handleSelect"
        >
          <el-menu-item index="teaching-info">授课信息</el-menu-item>
          <el-menu-item index="personal-info">个人信息</el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-container class="teacher-container">
        <el-header></el-header>
        <navbar></navbar>
        <el-main class="main-content">
          <!-- 根据选中的菜单项显示对应的组件 -->
          <teaching-info v-if="selectedMenu === 'teaching-info'"></teaching-info>
          <personal-info v-else-if="selectedMenu === 'personal-info'"></personal-info>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
// 导入各个管理组件
import TeachingInfo from '@/components/teacher-components/TeachingInfo.vue';
import PersonalInfo from '@/components/teacher-components/PersonalInfo.vue';
import Navbar from '@/components/Navbar.vue';

export default {
  name: 'TeacherHome',
  components: {
    TeachingInfo,
    PersonalInfo,
    Navbar,
  },
  data() {
    return {
      selectedMenu: 'teaching-info', // 默认选中的菜单项
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
#teacher-home {
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

.teacher-container {
  margin-left: 10px;
}
</style>