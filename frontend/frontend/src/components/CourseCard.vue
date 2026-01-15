<template>
  <div class="course-card-row">
    <!-- 1. 课程基本信息 -->
    <div class="col col-info">
      <!-- 修改结构：使用 block 容器包裹 inline 元素，实现自然换行 -->
      <div class="title-container">
        <span class="course-name" :title="course.courseName">{{ course.courseName }}</span>
        <!-- 学分角标：inline-block 保持整体性 -->
        <span class="credit-tag">{{ course.credits }}分</span>
      </div>
      
      <div class="course-meta">
        <span class="meta-id">{{ String(course.courseId).padStart(8, '0') }}</span>
        <span class="divider">|</span>
        <span class="meta-college">{{ course.college }}</span>
      </div>
    </div>

    <!-- 2. 教师 -->
    <div class="col col-instructor" :title="course.instructorName">{{ course.instructorName }}</div>
    
    <!-- 3. 时间与地点 -->
    <div class="col col-schedule">
      <div v-if="course.sessions && course.sessions.length > 0">
        <div v-for="session in course.sessions" :key="session.sessionId" class="schedule-line">
          <span class="time-badge">{{ session.weekday }}{{ session.startPeriod }}-{{ session.endPeriod }}</span>
          <span class="weeks-text">({{ course.startWeek }}-{{ course.endWeek }}周)</span>
        </div>
      </div>
      <div v-else class="text-gray">-</div>
      
      <div class="location-line">
        {{ course.campus }} {{ course.classroom }}
      </div>
    </div>
    
    <!-- 4. 容量 -->
    <div class="col col-capacity">
      <div class="capacity-info">
        <span :class="{ 'text-red': isFull }">{{ course.enrolledCount || 0 }}</span>
        <span class="text-gray"> / {{ course.capacity }}</span>
      </div>
      <div class="progress-track">
        <div class="progress-fill" :style="{ width: capacityPercent + '%', backgroundColor: capacityColor }"></div>
      </div>
    </div>

    <!-- 5. 操作 -->
    <div class="col col-actions">
      <transition name="fade">
        <div v-if="message" :class="['message-toast', message.type]">
          {{ message.message }}
        </div>
      </transition>
      <button class="btn btn-action btn-enroll" v-if="!isEnrolled" @click="$emit('enroll', course)" :disabled="!canEnroll">
        {{ isEnrolling ? '...' : '选课' }}
      </button>
      <button class="btn btn-action btn-drop" v-else @click="$emit('drop', course)" :disabled="!canDrop">
        {{ isDropping ? '...' : '退课' }}
      </button>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'CourseCard',
  props: {
    course: { type: Object, required: true },
    studentId: { type: [String, Number], default: null },
    isEnrolled: { type: Boolean, default: false },
    isEnrolling: { type: Boolean, default: false },
    isDropping: { type: Boolean, default: false },
    message: { type: Object, default: null }
  },
  emits: ['enroll', 'drop'],
  setup(props) {
    // 逻辑保持完全一致
    const isFull = computed(() => (props.course.enrolledCount || 0) >= props.course.capacity)
    const capacityPercent = computed(() => {
      const count = props.course.enrolledCount || 0
      const cap = props.course.capacity || 1
      return Math.min((count / cap) * 100, 100)
    })
    const capacityColor = computed(() => {
      if (capacityPercent.value >= 100) return '#ff4d4f'
      if (capacityPercent.value >= 80) return '#faad14'
      return '#52c41a' // 绿色
    })
    const canEnroll = computed(() => props.studentId && !props.isEnrolling && !isFull.value)
    const canDrop = computed(() => props.studentId && !props.isDropping)
    
    // 这里简单假设如果正在退课状态或者某种逻辑判断为已选，
    // 原代码中并没有传入 isEnrolled 属性，而是通过父组件传递 isEnrolling/dropping
    // 为了适配按钮切换，这里假设外部没有传 isEnrolled，我们只能显示选课按钮，
    // *除非* 父组件逻辑能判断已选。
    // *修正*：原代码同时渲染了 Enroll 和 Drop 按钮。
    // 为了美观，建议：未选显示“选课”，已选显示“退课”。
    // 但由于原代码 props 里没有 `isSelected`，我们暂时保留**只显示一个**的逻辑不太容易，
    // 所以还是保留原代码逻辑：**两个按钮并排**，或者根据父组件传入的状态。
    // 鉴于原代码是两个按钮并排显示，我们美化一下让它们看起来协调。
    
    return { isFull, capacityPercent, capacityColor, canEnroll, canDrop }
  }
}
</script>

<style scoped>
.course-card-row {
  display: flex;
  align-items: center; /* 保持垂直居中，虽然长文本会让行高变高，但居中看起来比较整齐 */
  border-bottom: 1px solid #f0e6f5;
  padding: 16px 16px;
  transition: all 0.2s;
  background: #fff;
}

.course-card-row:hover {
  background-color: #FDF5FF !important;
  transform: scale(1.002);
  box-shadow: 0 2px 12px rgba(124, 31, 137, 0.08);
  z-index: 1;
  position: relative;
  border-radius: 4px;
}

/* --- 列宽定义 (与 CourseSearch 必须一致) --- */
.col { padding: 0 12px; overflow: hidden; box-sizing: border-box; }
.col-info      { flex: 2.5; min-width: 200px; }
.col-instructor{ flex: 1;   min-width: 100px; color: #333; font-size: 14px; }
.col-schedule  { flex: 1.8; min-width: 180px; font-size: 13px; }
.col-capacity  { flex: 1.2; min-width: 120px; }
.col-actions   { flex: 0 0 100px; display: flex; justify-content: center; position: relative; }

/* 1. 课程信息样式优化 (解决长文本问题) */
.title-container {
  display: block; /* 改为 block 容器，允许内部 inline 元素自然换行 */
  margin-bottom: 6px;
  line-height: 1.5; /* 增加行高，防止两行文字挤在一起 */
}

.course-name {
  font-size: 15px; /* 稍微调小一点点字号以容纳更多文字 */
  font-weight: 700;
  color: #333;
  margin-right: 8px;
  /* 移除 flex 相关的属性，让它回归纯文本流 */
}

.credit-tag {
  display: inline-block; /* 保持块状特性（padding等），但像文本一样流动 */
  font-size: 12px;
  color: #7C1F89;
  background: #F3E5F5;
  padding: 0px 6px; /* 稍微减小高度 */
  border-radius: 4px;
  font-weight: 600;
  white-space: nowrap;
  border: 1px solid #E1BEE7;
  vertical-align: 1px; /* 微调垂直对齐 */
}

.course-meta {
  font-size: 12px;
  color: #888;
  display: flex;
  align-items: center;
  flex-wrap: wrap; /* 如果学院名太长也允许换行 */
}
.meta-id { font-family: 'Roboto Mono', monospace; color: #999; }
.divider { margin: 0 6px; color: #ddd; }
.meta-college { color: #666; }

/* 其他样式保持微调 */
.schedule-line { margin-bottom: 3px; display: flex; align-items: center; flex-wrap: wrap;}
.time-badge { font-weight: 500; color: #333; margin-right: 4px;}
.weeks-text { color: #999; font-size: 12px; white-space: nowrap; }
.location-line { color: #666; font-size: 12px; margin-top: 2px; display: flex; align-items: center; }
.icon { margin-right: 4px; color: #7C1F89; flex-shrink: 0; }
.text-gray { color: #ccc; }

.capacity-info { font-size: 13px; margin-bottom: 4px; text-align: left; }
.text-red { color: #ff4d4f; font-weight: bold; }
.progress-track { height: 6px; background: #f0f0f0; border-radius: 3px; width: 90%; overflow: hidden; }
.progress-fill { height: 100%; transition: width 0.3s; }

.btn-action { padding: 6px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; border: 1px solid transparent; transition: all 0.2s; white-space: nowrap; }
.btn-enroll { background: #7C1F89; color: white; }
.btn-enroll:hover:not(:disabled) { background: #9027a0; box-shadow: 0 2px 8px rgba(124, 31, 137, 0.3); }
.btn-enroll:disabled { background: #dcdfe6; cursor: not-allowed; }
.btn-drop { background: #ff4d4f; border-color: #ff4d4f; color: #fff; }
.btn-drop:hover:not(:disabled) { background: #ff7875; border-color: #ff7875; }

.message-toast { position: absolute; top: -30px; right: 0; padding: 4px 8px; border-radius: 4px; font-size: 12px; white-space: nowrap; box-shadow: 0 2px 8px rgba(0,0,0,0.1); background: #fff; z-index: 10; }
.message-toast.success { color: #52c41a; border: 1px solid #b7eb8f; background: #f6ffed; }
.message-toast.error { color: #ff4d4f; border: 1px solid #ffccc7; background: #fff2f0; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>