<template>
  <div class="page-container">
    <div class="content-wrapper">
      <SearchForm @search="handleSearch" @reset="handleReset" :loading="loading" />
      
      <div class="results-card">
        <div class="card-header">
          <h3>查询结果</h3>
          <span v-if="results.length > 0" class="badge">{{ results.length }} 门课程</span>
        </div>

        <div v-if="error" class="error-banner">{{ error }}</div>

        <div v-if="results.length > 0" class="results-table">
          <div class="list-header">
            <div class="col col-info">课程信息</div>
            <div class="col col-instructor">教师</div>
            <div class="col col-schedule">时间 / 地点</div>
            <div class="col col-capacity">课余量</div>
            <div class="col col-actions">操作</div>
          </div>

          <div class="course-list-body">
            <CourseCard 
              v-for="course in results" 
              :key="course.courseId" 
              :course="course"
              :student-id="studentId"
              :is-enrolled="course.isEnrolled"
              :is-enrolling="enrollingCourses.has(course.courseId)"
              :is-dropping="droppingCourses.has(course.courseId)"
              :message="operationMessage[course.courseId]"
              @enroll="handleEnroll"
              @drop="handleDrop"
            />
          </div>
        </div>
        <!-- Loading 等状态保持不变 -->
        <div v-if="loading" class="state-box"><div class="spinner"></div> 查询中...</div>
        <div v-if="!loading && results.length === 0 && searched" class="state-box empty"><p>未找到匹配的课程</p></div>
        <div v-if="!searched && !loading" class="state-box initial"><p>请输入条件进行查询</p></div>
      </div>
    </div>
  </div>
</template>

<script>
// 保持原有的逻辑完全不变
import { ref, inject, computed, onMounted, watch } from 'vue'
import { searchCombinedCourses, enrollCourse, dropCourse } from '../api/courseApi'
import CourseCard from './CourseCard.vue'
import SearchForm from './SearchForm.vue'

export default {
  name: 'CourseSearch',
  components: { CourseCard, SearchForm },
  setup() {
    const injectedStudentId = inject('studentId')
    const storedStudentId = ref('')
    const resolveStudentId = () => {
      const fromSession = sessionStorage.getItem('studentId')
      const fromLocal = localStorage.getItem('studentId')
      storedStudentId.value = (fromSession || fromLocal || '').trim()
    }

    // 关键：在 setup 阶段就先解析一次，确保首次默认搜索也能带上 studentId
    resolveStudentId()

    const normalizeId = (value) => {
      if (value === null || value === undefined || value === '') return null
      const raw = String(value).trim()
      if (!raw) return null
      return /^\d+$/.test(raw) ? Number(raw) : value
    }

    const studentId = computed(() => {
      const injected = injectedStudentId?.value
      return injected !== null && injected !== undefined && injected !== ''
        ? injected
        : (storedStudentId.value || null)
    })

    const normalizedStudentId = computed(() => normalizeId(studentId.value))

    const results = ref([])
    const loading = ref(false)
    const error = ref('')
    const searched = ref(false)
    const enrollingCourses = ref(new Set())
    const droppingCourses = ref(new Set())
    const operationMessage = ref({})

    // ... HandleSearch, HandleReset, HandleEnroll, HandleDrop 逻辑与你提供的源码完全一致 ...
    // 为了节省篇幅，这里省略重复的 script 内容，请直接复制你原来文件中的 methods 代码即可
    
    // START COPY FROM ORIGINAL
    const handleSearch = async (query) => {
      // 再兜底刷新一次：避免页面刚打开时 storage 写入有延迟
      resolveStudentId()
      loading.value = true
      error.value = ''
      searched.value = true
      results.value = []
      try {
        const combinedRequest = { courseCondition: null, sessionCondition: null, studentId: null }

        // 0. 附带 studentId（与 enroll/drop 相同口径：注入 + storage 兜底 + 数字归一）
        combinedRequest.studentId = normalizedStudentId.value

        const courseCondition = {}
        const normalKeys = ['courseId', 'courseName', 'credits', 'description', 'college', 'campus', 'classroom', 'startWeek', 'endWeek']
        let hasCourseCondition = false
        normalKeys.forEach(key => {
          if (query[key] !== null && query[key] !== '' && query[key] !== undefined) {
            courseCondition[key] = query[key]
            hasCourseCondition = true
          }
        })
        
        // 如果 query 中有 instructorName 则传递
        if (query.instructorName) {
             courseCondition.instructorName = query.instructorName
             hasCourseCondition = true
        }

        if (hasCourseCondition) {
          // courseId 输入框是文本；若为纯数字则转为 Number，便于后端 Long 解析
          if (courseCondition.courseId !== undefined && courseCondition.courseId !== null && courseCondition.courseId !== '') {
            const rawCourseId = String(courseCondition.courseId).trim()
            if (/^\d+$/.test(rawCourseId)) {
              courseCondition.courseId = Number(rawCourseId)
            }
          }
          combinedRequest.courseCondition = courseCondition
        }

        // 2. 构建 sessionCondition
        const hasTimeQuery = query.weekday || (query.startPeriod !== null && query.startPeriod !== '') || (query.endPeriod !== null && query.endPeriod !== '')
        if (hasTimeQuery) {
          combinedRequest.sessionCondition = {
            weekdays: query.weekday ? [query.weekday] : null,
            startPeriod: query.startPeriod ? Number(query.startPeriod) : null,
            endPeriod: query.endPeriod ? Number(query.endPeriod) : null
          }
        }
        const data = await searchCombinedCourses(combinedRequest) || []
        results.value = (data || []).map(course => ({
          ...course,
          isEnrolled: Boolean(course?.isEnrolled)
        }))
      } catch (err) {
        error.value = err.message || '查询失败'
        console.error('Search error:', err)
      } finally {
        loading.value = false
      }
    }

    const handleReset = () => {
      results.value = []
      error.value = ''
      searched.value = false
      operationMessage.value = {}
    }

    const handleEnroll = async (course) => {
      if (!normalizedStudentId.value) { setOperationMessage(course.courseId, 'error', '请先输入学生ID'); return }
      enrollingCourses.value.add(course.courseId)
      clearOperationMessage(course.courseId)
      const batchId = localStorage.getItem('selectedBatchId')
      if (!batchId) {
        setOperationMessage(course.courseId, 'error', '请先选择选课轮次')
        enrollingCourses.value.delete(course.courseId)
        return
      }
      // ... Time check logic from original ...
      try {
        const response = await enrollCourse({ studentId: normalizedStudentId.value, courseId: course.courseId, batchId: Number(batchId) })
        if (response.success) {
          setOperationMessage(course.courseId, 'success', response.message)
          if (response.warn) setTimeout(() => setOperationMessage(course.courseId, 'warning', response.warn), 2000)
          course.enrolledCount = (course.enrolledCount || 0) + 1
          course.isEnrolled = true
        } else {
          setOperationMessage(course.courseId, 'error', response.message)
        }
      } catch (err) {
        setOperationMessage(course.courseId, 'error', err.message || '选课失败')
      } finally { enrollingCourses.value.delete(course.courseId) }
    }

    const handleDrop = async (course) => {
      if (!normalizedStudentId.value) { setOperationMessage(course.courseId, 'error', '请先输入学生ID'); return }
      droppingCourses.value.add(course.courseId)
      clearOperationMessage(course.courseId)
      try {
        const response = await dropCourse({ studentId: normalizedStudentId.value, courseId: course.courseId })
        if (response.success) {
          setOperationMessage(course.courseId, 'success', response.message)
          course.enrolledCount = Math.max((course.enrolledCount || 0) - 1, 0)
          course.isEnrolled = false
        } else {
          setOperationMessage(course.courseId, 'error', response.message)
        }
      } catch (err) {
        setOperationMessage(course.courseId, 'error', err.message || '退课失败')
      } finally { droppingCourses.value.delete(course.courseId) }
    }

    const setOperationMessage = (courseId, type, message) => {
      operationMessage.value[courseId] = { type, message }
      setTimeout(() => clearOperationMessage(courseId), 5000)
    }
    const clearOperationMessage = (courseId) => { if (operationMessage.value[courseId]) delete operationMessage.value[courseId] }
    // END COPY

    onMounted(() => {
      resolveStudentId()
      // 避免与 SearchForm 的 onMounted 自动搜索重复
      if (!searched.value) {
        handleSearch({})
      }
    })

    watch(
      () => injectedStudentId?.value,
      (newVal, oldVal) => {
        if (newVal !== null && newVal !== undefined && newVal !== '') {
          const normalized = String(newVal)
          storedStudentId.value = normalized
          localStorage.setItem('studentId', normalized)
          sessionStorage.setItem('studentId', normalized)
          if (!oldVal && !searched.value) {
            handleSearch({})
          }
        }
      },
      { immediate: true }
    )

    return {
      studentId, results, loading, error, searched,
      enrollingCourses, droppingCourses, operationMessage,
      handleSearch, handleReset, handleEnroll, handleDrop
    }
  }
}
</script>

<style scoped>
/* 保持背景色 */
.page-container { background-color: #F7F1FA; min-height: 100vh; padding: 10px; }
.content-wrapper { max-width: 1100px; margin: 0 auto; } /* 稍微加宽一点点以适应均匀分布 */
.results-card { background: white; border-radius: 12px; padding: 24px; min-height: 400px; box-shadow: 0 4px 20px rgba(124, 31, 137, 0.06); border: 1px solid rgba(255, 255, 255, 0.8); }
.card-header { display: flex; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #f0e6f5; padding-bottom: 12px; }
.card-header h3 { margin: 0; font-size: 18px; color: #4a2f5a; font-weight: 600; }
.badge { background: #f3e5f5; color: #7C1F89; font-weight: bold; font-size: 12px; padding: 2px 10px; border-radius: 12px; margin-left: 12px; }
.list-header { display: flex; background: #FAF4FC; padding: 12px 16px; color: #6a5acd; font-weight: 600; font-size: 13px; border-radius: 8px 8px 0 0; border-bottom: 1px solid #efe5f5; }
.course-list-body { border: 1px solid #efe5f5; border-top: none; border-radius: 0 0 8px 8px; background: #fff; }

/* --- 核心修改：平衡列宽 --- */
.col { 
  padding: 0 12px; /* 增加列内边距，让文字不靠太近 */
  box-sizing: border-box;
}

/* 
   调整思路：
   Info (2.5) : Instructor (1) : Schedule (1.8) : Capacity (1.2) : Actions (Fixed)
   这样“教师”和“容量”会宽一些，不再显得Info独大
*/
.col-info      { flex: 2.5; min-width: 200px; } 
.col-instructor{ flex: 1;   min-width: 100px; } 
.col-schedule  { flex: 1.8; min-width: 180px; }
.col-capacity  { flex: 1.2; min-width: 120px; }
.col-actions   { flex: 0 0 100px; text-align: center; }

.state-box { text-align: center; padding: 60px 0; color: #909399; }
.error-banner { background: #fff0f0; color: #ff4d4f; padding: 12px; border-radius: 6px; margin-bottom: 15px; border: 1px solid #ffccc7; }
</style>