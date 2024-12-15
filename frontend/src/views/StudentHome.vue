<template>
  <div id="student-home">
    <el-container style="height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="200px">
        <div class="logo">学生选课系统</div>
        <el-menu
          default-active="course-selection"
          @select="handleSelect"
        >
          <el-menu-item index="course-selection">选课</el-menu-item>
          <el-menu-item index="course-record">选课记录</el-menu-item>
          <el-menu-item index="personal-info">个人信息</el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-container>
        <el-header>学生首页</el-header>
        <el-main>
          <!-- 根据选中的菜单项显示对应的组件 -->
          <course-selection v-if="selectedMenu === 'course-selection'"></course-selection>
          <course-record v-else-if="selectedMenu === 'course-record'"></course-record>
          <personal-info v-else-if="selectedMenu === 'personal-info'"></personal-info>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
// 导入各个管理组件
import CourseSelection from '@/components/student-components/CourseSelection.vue';
import CourseRecord from '@/components/student-components/CourseRecord.vue';
import PersonalInfo from '@/components/student-components/PersonalInfo.vue';

export default {
  name: 'StudentHome',
  components: {
    CourseSelection,
    CourseRecord,
    PersonalInfo,
  },
  data() {
    return {
      selectedMenu: 'course-selection', // 默认选中的菜单项
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
#student-home {
  height: 100vh;
}
.logo {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  padding: 20px 0;
}
</style>