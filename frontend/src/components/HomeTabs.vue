<template>
  <div class="tabs-bar">
    <div class="tabs-left">
      <!-- 学生页面选项卡 -->
      <template v-if="userType === 'student'">
        <button 
          :class="['tab', { active: activeTab === 'course-selection' }]"
          @click="$emit('change', 'course-selection')"
        >
          选课
        </button>
        <button 
          :class="['tab', { active: activeTab === 'course-record' }]"
          @click="$emit('change', 'course-record')"
        >
          选课记录
        </button>
        <button 
          :class="['tab', { active: activeTab === 'personal-info' }]"
          @click="$emit('change', 'personal-info')"
        >
          个人信息
        </button>
      </template>
      
      <!-- 教师页面选项卡 -->
      <template v-else-if="userType === 'teacher'">
        <button 
          :class="['tab', { active: activeTab === 'teaching-info' }]"
          @click="$emit('change', 'teaching-info')"
        >
          授课信息
        </button>
        <button 
          :class="['tab', { active: activeTab === 'personal-info' }]"
          @click="$emit('change', 'personal-info')"
        >
          个人信息
        </button>
      </template>
    </div>
    <div class="tabs-extra">
      <slot name="extra" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomeTabs',
  props: {
    activeTab: {
      type: String,
      required: true
    },
    userType: {
      type: String,
      default: 'student' // 默认为学生
    }
  },
  emits: ['change']
}
</script>

<style scoped>
.tabs-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-bottom: 2px solid #e0e0e0;
  padding: 0 20px;
  margin-bottom: 20px;
  position: relative;
  width: 100%;
  background-color: #ffffff;
}

.tabs-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tab {
  padding: 12px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 15px;
  color: #666;
  border-bottom: 3px solid transparent;
  transition: all 0.3s;
  position: relative;
  bottom: -2px;
  font-weight: 500;
}

.tab:hover {
  color: #667eea;
}

.tab.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 600;
}

.tabs-extra {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
