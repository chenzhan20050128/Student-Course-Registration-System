<template>
  <div id="teacher-home">
    <div class="teacher-wrapper">
      <!-- 顶部导航栏 -->
      <navbar current-page-name="教师授课"></navbar>
      
      <!-- 主内容区域 -->
      <div class="main-wrapper">
        <!-- 选项卡 -->
        <home-tabs
          :active-tab="selectedMenu"
          user-type="teacher"
          @change="setActiveTab"
        ></home-tabs>
        
        <!-- 内容容器 -->
        <div class="content-container">
          <teaching-info v-if="selectedMenu === 'teaching-info'"></teaching-info>
          <personal-info v-else-if="selectedMenu === 'personal-info'"></personal-info>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TeachingInfo from '@/components/teacher-components/TeachingInfo.vue';
import PersonalInfo from '@/components/teacher-components/PersonalInfo.vue';
import Navbar from '@/components/Navbar.vue';
import HomeTabs from '@/components/HomeTabs.vue';

export default {
  name: 'TeacherHome',
  components: {
    TeachingInfo,
    PersonalInfo,
    Navbar,
    HomeTabs,
  },
  data() {
    return {
      selectedMenu: 'teaching-info',
    };
  },
  methods: {
    setActiveTab(tab) {
      this.selectedMenu = tab;
    },
  },
};
</script>

<style scoped>
#teacher-home {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.teacher-wrapper {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
}

.content-container {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
}
</style>