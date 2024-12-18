<template>
  <div id="teacher-home">
    <el-container style="height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="200px">
        <div class="logo">教师选课系统</div>
        <el-menu
          default-active="teaching-info"
          @select="handleSelect"
        >
          <el-menu-item index="teaching-info">授课信息</el-menu-item>
          <el-menu-item index="personal-info">个人信息</el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-container>
        <el-header>教师首页</el-header>
        <navbar></navbar>
        <el-main>
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
  height: 100vh;
}
.logo {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 20px 0;
}
</style>