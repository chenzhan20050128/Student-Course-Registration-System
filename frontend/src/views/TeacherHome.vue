<template>
  <div>
    <el-container>
      <el-aside width="200px">
        <el-menu :default-active="selectedMenu" @select="handleSelect">
          <!-- 菜单项 -->
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-container>
        <navbar></navbar>
        <el-main>
          <!-- 根据选中的菜单项显示对应的组件 -->
          <!-- 例如: <teacher-course v-if="selectedMenu === 'teacher-course'"></teacher-course> -->
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Navbar from '@/components/Navbar.vue';

export default {
  name: 'TeacherHome',
  components: {
    Navbar,
  },
  data() {
    return {
      selectedMenu: 'teacher-course',
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
/* 添加样式 */
</style>