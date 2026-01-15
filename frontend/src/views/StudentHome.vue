<template>
  <div id="student-home">
    <div class="student-wrapper">
      <!-- 顶部导航栏 -->
      <navbar current-page-name="学生选课"></navbar>
      
      <!-- 主内容区域 -->
      <div class="main-wrapper">
        <!-- 选项卡 -->
        <home-tabs
          :active-tab="selectedMenu"
          user-type="student"
          @change="setActiveTab"
        ></home-tabs>
        
        <!-- 内容容器 -->
        <div class="content-container">
          <course-selection v-if="selectedMenu === 'course-selection'"></course-selection>
          <course-record v-else-if="selectedMenu === 'course-record'"></course-record>
          <personal-info v-else-if="selectedMenu === 'personal-info'"></personal-info>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CourseSelection from '@/components/student-components/CourseSelection.vue';
import CourseRecord from '@/components/student-components/CourseRecord.vue';
import PersonalInfo from '@/components/student-components/PersonalInfo.vue';
import Navbar from '@/components/Navbar.vue';
import HomeTabs from '@/components/HomeTabs.vue';

export default {
  name: 'StudentHome',
  components: {
    CourseSelection,
    CourseRecord,
    PersonalInfo,
    Navbar,
    HomeTabs,
  },
  data() {
    return {
      selectedMenu: 'course-selection',
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
#student-home {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.student-wrapper {
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