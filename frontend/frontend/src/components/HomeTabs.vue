<template>
  <div class="tabs-bar">
    <div class="tabs-left">
      <button 
        :class="['tab', { active: activeTab === 'course' }]"
        @click="$emit('change', 'course')"
      >
        课程查询
      </button>
      <button 
        v-if="isStudent"
        :class="['tab', { active: activeTab === 'myCourses' }]"
        @click="$emit('change', 'myCourses')"
      >
        我的课程
      </button>
      <button 
        v-if="isStudent"
        :class="['tab', { active: activeTab === 'smartSelection' }]"
        @click="$emit('change', 'smartSelection')"
      >
        智能选课
      </button>
    </div>
    <div class="tabs-extra" :class="{ 'tabs-extra-left': activeTab === 'smartSelection' }">
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
    isStudent: {
      type: Boolean,
      default: false
    }
  },
  emits: ['change']
}
</script>

<style scoped>
.tabs-bar {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  border-bottom: 2px solid #e0e0e0;
  padding-bottom: 0;
  margin-bottom: 0px;
  position: relative;
  width: 100%;
}

.tabs-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tab {
  padding: 10px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 15px;
  color: #666;
  border-bottom: 3px solid transparent;
  transition: all 0.3s;
  position: relative;
  bottom: -2px;
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
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.tabs-extra-left {
  position: static;
  transform: none;
  left: auto;
  top: auto;
}
</style>
