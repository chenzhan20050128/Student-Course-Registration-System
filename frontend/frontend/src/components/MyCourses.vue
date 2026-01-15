<template>
  <div class="my-courses">
    <div v-if="!studentId" class="no-student-id">
      <p>请先在上方输入学生ID</p>
    </div>

    <div v-if="displayError" class="error-message">
      {{ displayError }}
    </div>

    <div v-if="successMessage" class="success-message-toast">
      {{ successMessage }}
    </div>

    <div v-if="loading && courses.length === 0" class="loading">
      加载中...
    </div>

    <!-- 课程表 -->
    <div v-if="courses.length > 0" class="schedule-layout">
      <div class="schedule-main">
        <div class="schedule-container">
          <div class="schedule-wrapper">
            <div class="schedule-grid">
              <!-- 表头：周几 -->
              <div class="grid-header time-header"></div>
              <div 
                v-for="(day, index) in weekdays" 
                :key="index" 
                class="grid-header day-header"
                :class="{ 'today': isToday(index) }"
              >
                <div class="day-name">{{ day }}</div>
              </div>

              <!-- 课程表主体 -->
              <template v-for="period in periods" :key="period.period">
                <!-- 上午时段标签（第1节） -->
                <div v-if="period.period === 1" class="time-period-label morning-label">
                  <span class="period-label-text">上午</span>
                </div>
                
                <!-- 午间分隔行（在第4节后） -->
                <div v-if="period.period === 5" class="time-break time-break-with-text">
                  <span class="break-text">午休</span>
                </div>
                
                <!-- 下午时段标签（第5节） -->
                <div v-if="period.period === 5" class="time-period-label afternoon-label">
                  <span class="period-label-text">下午</span>
                </div>
                
                <!-- 傍晚分隔行（在第8节后） -->
                <div v-if="period.period === 9" class="time-break time-break-with-text">
                  <span class="break-text">傍晚</span>
                </div>
                
                <!-- 晚间时段标签（第9节） -->
                <div v-if="period.period === 9" class="time-period-label evening-label">
                  <span class="period-label-text">晚间</span>
                </div>

                <!-- 左侧节次和时间（不显示时段标签的行） -->
                <div v-if="![1, 5, 9].includes(period.period)" class="period-header" :data-period="period.period">
                  <div class="period-number">第{{ period.period }}节</div>
                  <div class="period-time">{{ period.time }}</div>
                </div>
                
                <!-- 有时段标签的行，节次时间部分 -->
                <div v-else class="period-header period-header-with-label" :data-period="period.period">
                  <div class="period-number">第{{ period.period }}节</div>
                  <div class="period-time">{{ period.time }}</div>
                </div>

                <!-- 每天的单元格 -->
                <div 
                  v-for="(day, dayIndex) in weekdays" 
                  :key="`${period.period}-${dayIndex}`"
                  class="schedule-cell"
                  :class="{ 
                    'empty-cell': getCellBlocks(day, period.period).length === 0,
                    'show-confirm': isShowingConfirm(day, period.period)
                  }"
                  :data-day="day"
                  :data-period="period.period"
                  @mouseenter="handleCellHover(day, period.period)"
                  @mouseleave="handleCellLeave(day, period.period)"
                >
                  <!-- 该单元格的所有课程块 -->
                  <div 
                    v-for="(block, blockIndex) in getCellBlocks(day, period.period)"
                    :key="`block-${block.course.courseId}-${block.session.sessionId}-${blockIndex}`"
                    class="course-block"
                    :class="{
                      'has-conflict': getCellConflictCount(block, day, period.period) > 1
                    }"
                    :style="getBlockStyle(block)"
                    @click.stop="showCourseDetail(block.course)"
                  >
                    <div v-if="hasMultipleCampuses" class="campus-badge" :style="getCampusBadgeStyle(block.course.courseId)">{{ (block.course.campus || '未知').replace('校区', '') }}</div>
                    <div v-if="block.session.weekType && block.session.weekType !== 0" class="week-type-badge" :style="getCampusBadgeStyle(block.course.courseId)">{{ block.session.weekType === 1 ? '单' : '双' }}</div>
                    <div class="course-name-text">{{ block.course.courseName }}</div>
                    <div class="course-location">{{ block.course.classroom }}</div>
                  </div>
                  
                  <!-- 空单元格的确认按钮 -->
                  <div 
                    v-if="isShowingConfirm(day, period.period)" 
                    class="confirm-buttons"
                    @click.stop
                  >
                    <p class="confirm-text">查询可选课程？</p>
                    <button class="btn-confirm-small" @click.stop="confirmSearchCourses">确认</button>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!loading && courses.length === 0 && studentId && !displayError" class="no-results">
      您还没有选课
    </div>

    <!-- 课程详情对话框 -->
    <div v-if="selectedCourse" class="dialog-overlay" @click="closeDialog">
      <div class="dialog-content course-detail-dialog results-card" @click.stop>
        <button class="dialog-close" @click="closeDialog">×</button>
        <div class="dialog-header">
          <h3>课程详情</h3>
        </div>
        <div class="dialog-body" style="padding: 0;">
          <div class="results-table">
            <div class="list-header">
              <div class="col col-info">课程信息</div>
              <div class="col col-instructor">教师</div>
              <div class="col col-schedule">时间 / 地点</div>
              <div class="col col-capacity">课余量</div>
              <div class="col col-actions">操作</div>
            </div>
            <div class="course-list-body">
              <CourseCard 
                :course="selectedCourse"
                :student-id="studentId"
                :is-enrolled="true"
                :is-dropping="droppingCourses.has(selectedCourse.courseId)"
                :message="operationMessage[selectedCourse.courseId]"
                @drop="handleDrop"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 可选课程列表对话框 -->
    <div v-if="showAvailableCourses" class="dialog-overlay" @click="closeAvailableCoursesDialog">
      <div class="dialog-content available-courses-dialog results-card" @click.stop>
        <button class="dialog-close" @click="closeAvailableCoursesDialog">×</button>
        <div class="dialog-header">
          <h3>{{ queryTimeInfo }} 可选课程</h3>
        </div>
        <div class="dialog-body" style="padding: 0;">
          <div v-if="loadingAvailableCourses" class="loading">
            查询中...
          </div>
          <div v-else-if="availableCoursesError" class="error-message">
            {{ availableCoursesError }}
          </div>
          <div v-else-if="availableCourses.length === 0" class="no-results">
            该时间段没有可选课程
          </div>
          <div v-else class="results-table">
            <div class="list-header">
              <div class="col col-info">课程信息</div>
              <div class="col col-instructor">教师</div>
              <div class="col col-schedule">时间 / 地点</div>
              <div class="col col-capacity">课余量</div>
              <div class="col col-actions">操作</div>
            </div>
            <div class="course-list-body">
              <CourseCard 
                v-for="course in availableCourses" 
                :key="course.courseId" 
                :course="course"
                :student-id="studentId"
                :is-enrolled="courses.some(c => c.courseId === course.courseId)"
                :is-enrolling="enrollingCourses.has(course.courseId)"
                :is-dropping="droppingCourses.has(course.courseId)"
                :message="operationMessage[course.courseId]"
                @enroll="handleEnrollFromDialog"
                @drop="handleDrop"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject, onMounted } from 'vue'
import { dropCourse, searchCoursesBySession, enrollCourse } from '../api/courseApi'
import CourseCard from './CourseCard.vue'

export default {
  name: 'MyCourses',
  components: { CourseCard },
  props: {
    courses: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    error: {
      type: String,
      default: ''
    }
  },
  emits: ['refresh'],
  setup(props, { emit }) {
    // 获取学生ID
    const studentId = inject('studentId')
    
    const courses = computed(() => props.courses || [])
    const loading = computed(() => props.loading)
    const displayError = computed(() => props.error || '')
    const selectedCourse = ref(null)
    const successMessage = ref('')
    
    // 退课相关状态
    const droppingCourses = ref(new Set())
    const operationMessage = ref({})

    // 可选课程相关状态
    const showAvailableCourses = ref(false)
    const availableCourses = ref([])
    const loadingAvailableCourses = ref(false)
    const availableCoursesError = ref('')
    const queryTimeInfo = ref('')
    const queryWeekday = ref('')
    const queryPeriod = ref(null)
    const enrollingCourses = ref(new Set())
    
    // 确认对话框状态
    const showConfirmDialog = ref(false)
    const confirmDialogInfo = ref({ weekday: '', period: null })

    // 周几数组
    const weekdays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

    // 节次和时间映射
    const periods = [
      { period: 1, time: '08:00-08:50' },
      { period: 2, time: '09:00-09:50' },
      { period: 3, time: '10:10-11:00' },
      { period: 4, time: '11:10-12:00' },
      { period: 5, time: '14:00-14:50' },
      { period: 6, time: '15:00-15:50' },
      { period: 7, time: '16:10-17:00' },
      { period: 8, time: '17:10-18:00' },
      { period: 9, time: '18:30-19:20' },
      { period: 10, time: '19:30-20:20' },
      { period: 11, time: '20:30-21:20' }
    ]

    /**
     * 检测是否有跨校区课程
     */
    const hasMultipleCampuses = computed(() => {
      const campuses = new Set()
      courses.value.forEach(course => {
        if (course.campus) {
          campuses.add(course.campus)
        }
      })
      return campuses.size > 1
    })

    /**
     * 生成用于渲染课程表的课程块（处理跨节次/同一时间冲突）
     */
    const generateCourseBlocks = computed(() => {
      // 第一步：收集所有节次块
      const sessionBlocks = []
      courses.value.forEach(course => {
        if (!course.sessions || !Array.isArray(course.sessions)) return
        course.sessions.forEach(session => {
          const sessionBlock = {
            course,
            session,
            weekday: session.weekday,
            startPeriod: session.startPeriod,
            endPeriod: session.endPeriod,
            span: session.endPeriod - session.startPeriod + 1,
            id: `${course.courseId}-${session.sessionId}`
          }
          sessionBlocks.push(sessionBlock)
        })
      })

      // 第二步：按weekday分组处理
      const weekdayGroups = new Map()
      sessionBlocks.forEach(block => {
        if (!weekdayGroups.has(block.weekday)) {
          weekdayGroups.set(block.weekday, [])
        }
        weekdayGroups.get(block.weekday).push(block)
      })

      // 第三步：为每个weekday的课程分配列位置
      weekdayGroups.forEach((blocks) => {
        // 检测冲突关系
        const conflicts = new Map() // 存储每个块与哪些块冲突
        blocks.forEach(block => {
          conflicts.set(block.id, new Set())
        })

        // 构建冲突图
        for (let i = 0; i < blocks.length; i++) {
          for (let j = i + 1; j < blocks.length; j++) {
            const b1 = blocks[i]
            const b2 = blocks[j]
            // 检查时间是否重叠
            if (!(b1.endPeriod < b2.startPeriod || b2.endPeriod < b1.startPeriod)) {
              conflicts.get(b1.id).add(b2.id)
              conflicts.get(b2.id).add(b1.id)
            }
          }
        }

        // 使用贪心算法分配列位置
        blocks.forEach(block => {
          const conflictingBlocks = conflicts.get(block.id)
          const usedColumns = new Set()

          // 找出冲突块已使用的列
          conflictingBlocks.forEach(conflictId => {
            const conflictBlock = blocks.find(b => b.id === conflictId)
            if (conflictBlock && conflictBlock.columnIndex !== undefined) {
              usedColumns.add(conflictBlock.columnIndex)
            }
          })

          // 分配第一个未使用的列
          let columnIndex = 0
          while (usedColumns.has(columnIndex)) {
            columnIndex++
          }
          block.columnIndex = columnIndex

          // 计算该块需要的总列数
          let maxColumn = columnIndex
          conflictingBlocks.forEach(conflictId => {
            const conflictBlock = blocks.find(b => b.id === conflictId)
            if (conflictBlock && conflictBlock.columnIndex !== undefined) {
              maxColumn = Math.max(maxColumn, conflictBlock.columnIndex)
            }
          })
          block.totalColumns = maxColumn + 1
        })

        // 更新所有冲突块的totalColumns为最大值
        blocks.forEach(block => {
          const conflictingBlocks = conflicts.get(block.id)
          let maxColumns = block.totalColumns || 1
          conflictingBlocks.forEach(conflictId => {
            const conflictBlock = blocks.find(b => b.id === conflictId)
            if (conflictBlock && conflictBlock.totalColumns) {
              maxColumns = Math.max(maxColumns, conflictBlock.totalColumns)
            }
          })
          block.totalColumns = maxColumns
        })
      })

      return sessionBlocks
    })

    // 统计：冲突时间槽数量（同一 weekday+period 上出现 >=2 门课）
    const conflictCount = computed(() => {
      const cellCourseSetMap = new Map()
      generateCourseBlocks.value.forEach(block => {
        const start = Number(block.startPeriod)
        const end = Number(block.endPeriod)
        for (let p = start; p <= end; p++) {
          const key = `${block.weekday}-${p}`
          let set = cellCourseSetMap.get(key)
          if (!set) {
            set = new Set()
            cellCourseSetMap.set(key, set)
          }
          set.add(block.course.courseId)
        }
      })
      let count = 0
      cellCourseSetMap.forEach(set => {
        if (set.size > 1) count++
      })
      return count
    })

    /**
     * 获取指定单元格的所有课程块
     * 只返回在该单元格第一行显示的块（跨多节的课程只在第一行显示）
     */
    const getCellBlocks = (weekday, period) => {
      return generateCourseBlocks.value.filter(block => {
        // 只显示在该单元格第一行的块（即startPeriod等于当前period的块）
        return block.weekday === weekday && block.startPeriod === period
      })
    }

    /**
     * 获取课程块的样式
     * 对于跨多节的课程，使用绝对定位和计算高度来跨越多行
     */
    const getBlockStyle = (block) => {
      const color = getCourseColor(block.course.courseId)

      // 使用图着色算法分配的列信息
      const totalColumns = block.totalColumns || 1
      const columnIndex = block.columnIndex || 0

      let style = {
        background: color.bg,
        borderLeftColor: color.border,
        color: color.text,
        position: 'absolute',
        top: '3px',
        bottom: '3px',
        zIndex: totalColumns > 1 ? 10 : 5
      }

      // 跨多节的课程，设置高度
      if (block.span > 1) {
        style.height = `calc(${block.span} * 100% + ${(block.span - 1) * 2}px - 6px)`
        style.display = 'flex'
        style.flexDirection = 'column'
        style.justifyContent = 'center'
      }

      // 计算宽度和位置
      if (totalColumns > 1) {
        const widthPercent = 100 / totalColumns
        const leftPercent = columnIndex * widthPercent
        style.width = `calc(${widthPercent}% - 4px)`
        style.left = `calc(${leftPercent}% + 2px)`
      } else {
        style.width = 'calc(100% - 6px)'
        style.left = '3px'
      }

      return style
    }

    /**
     * 获取校区标识的样式（使用课程颜色的深色版本）
     */
    const getCampusBadgeStyle = (courseId) => {
      const color = getCourseColor(courseId)
      // 使用border颜色作为校区标识的背景色（通常是较深的颜色）
      return {
        background: color.border,
        color: 'white'
      }
    }

    /**
     * 获取单元格的冲突数量
     */
    const getCellConflictCount = (block, weekday, period) => {
      return block.totalColumns || 1
    }

    /**
     * 判断是否是今天（简化版，实际应该根据当前日期计算）
     */
    const isToday = (dayIndex) => {
      // 这里简化处理，实际应该根据当前日期判断
      return false
    }

    /**
     * 显示课程详情
     */
    const showCourseDetail = (course) => {
      selectedCourse.value = course
    }

    /**
     * 关闭对话框
     */
    const closeDialog = () => {
      selectedCourse.value = null
    }

    /**
     * 处理单元格鼠标悬停事件
     * 如果单元格为空，在单元格内显示确认按钮
     */
    const handleCellHover = (weekday, period) => {
      // 检查该单元格是否有课程
      const cellBlocks = getCellBlocks(weekday, period)
      if (cellBlocks.length > 0) {
        // 如果有课程，不处理
        return
      }

      // 如果单元格为空，在单元格内显示确认按钮
      confirmDialogInfo.value = { weekday, period }
      showConfirmDialog.value = true
    }

    /**
     * 处理单元格鼠标离开事件
     */
    const handleCellLeave = (weekday, period) => {
      // 鼠标离开时关闭确认按钮
      if (showConfirmDialog.value && 
          confirmDialogInfo.value.weekday === weekday && 
          confirmDialogInfo.value.period === period) {
        cancelSearchCourses()
      }
    }

    /**
     * 判断是否显示确认按钮
     */
    const isShowingConfirm = (weekday, period) => {
      return showConfirmDialog.value && 
             confirmDialogInfo.value.weekday === weekday && 
             confirmDialogInfo.value.period === period
    }

    /**
     * 确认查询课程
     */
    const confirmSearchCourses = async () => {
      const { weekday, period } = confirmDialogInfo.value
      showConfirmDialog.value = false
      
      queryWeekday.value = weekday
      queryPeriod.value = period
      queryTimeInfo.value = `${weekday} 第${period}节`
      showAvailableCourses.value = true
      availableCourses.value = []
      availableCoursesError.value = ''
      loadingAvailableCourses.value = true

      try {
        const data = await searchCoursesBySession({
          weekdays: [weekday],
          startPeriod: period,
          endPeriod: period
        })
        availableCourses.value = data || []
      } catch (err) {
        availableCoursesError.value = err.message || '查询失败'
        console.error('Search available courses error:', err)
      } finally {
        loadingAvailableCourses.value = false
      }
    }

    /**
     * 取消查询
     */
    const cancelSearchCourses = () => {
      showConfirmDialog.value = false
      confirmDialogInfo.value = { weekday: '', period: null }
    }

    /**
     * 关闭可选课程对话框
     */
    const closeAvailableCoursesDialog = () => {
      showAvailableCourses.value = false
      availableCourses.value = []
      availableCoursesError.value = ''
      queryTimeInfo.value = ''
      queryWeekday.value = ''
      queryPeriod.value = null
      // 清除操作消息
      operationMessage.value = {}
    }

    /**
     * 从对话框选课
     */
    const handleEnrollFromDialog = async (course) => {
      if (!studentId.value) {
        setOperationMessage(course.courseId, 'error', '请先输入学生ID')
        return
      }

      enrollingCourses.value.add(course.courseId)
      clearOperationMessage(course.courseId)

      // 获取当前选中的选课轮次ID
      const batchId = localStorage.getItem('selectedBatchId')
      if (!batchId) {
        setOperationMessage(course.courseId, 'error', '请先选择选课轮次')
        enrollingCourses.value.delete(course.courseId)
        return
      }

      // 检查选课轮次是否在有效时间内
      const selectedBatchStr = localStorage.getItem('selectedBatch')
      if (selectedBatchStr) {
        try {
          const selectedBatch = JSON.parse(selectedBatchStr)
          const now = new Date()
          const startTime = new Date(selectedBatch.startTime)
          const endTime = new Date(selectedBatch.endTime)
          
          if (now < startTime) {
            setOperationMessage(course.courseId, 'error', `选课轮次尚未开始，开始时间：${selectedBatch.startTime}`)
            enrollingCourses.value.delete(course.courseId)
            return
          }
          
          if (now > endTime) {
            setOperationMessage(course.courseId, 'error', `选课轮次已结束，结束时间：${selectedBatch.endTime}`)
            enrollingCourses.value.delete(course.courseId)
            return
          }
        } catch (err) {
          console.error('解析选课轮次信息失败:', err)
        }
      }

      try {
        const response = await enrollCourse({
          studentId: studentId.value,
          courseId: course.courseId,
          batchId: Number(batchId)
        })

        if (response.success) {
          setOperationMessage(course.courseId, 'success', response.message)
          if (response.warn) {
            // 如果有警告信息，也显示出来
            setTimeout(() => {
              setOperationMessage(course.courseId, 'warning', response.warn)
            }, 2000)
          }
          // 更新课程的已选人数
          course.enrolledCount = (course.enrolledCount || 0) + 1
          // 延迟刷新课程表
          setTimeout(() => {
            emit('refresh')
          }, 1000)
        } else {
          setOperationMessage(course.courseId, 'error', response.message)
        }
      } catch (err) {
        setOperationMessage(course.courseId, 'error', err.message || '选课失败')
        console.error('Enroll error:', err)
      } finally {
        enrollingCourses.value.delete(course.courseId)
      }
    }

    /**
     * 设置操作消息
     */
    const setOperationMessage = (courseId, type, message) => {
      operationMessage.value[courseId] = { type, message }
      // 5秒后自动清除消息
      setTimeout(() => {
        clearOperationMessage(courseId)
      }, 5000)
    }

    /**
     * 清除操作消息
     */
    const clearOperationMessage = (courseId) => {
      if (operationMessage.value[courseId]) {
        delete operationMessage.value[courseId]
      }
    }

    /**
     * 处理退课
     */
    const handleDrop = async (course) => {
      if (!studentId.value) {
        return
      }

      droppingCourses.value.add(course.courseId)

      try {
        const response = await dropCourse({
          studentId: studentId.value,
          courseId: course.courseId
        })

        if (response.success) {
          setOperationMessage(course.courseId, 'success', response.message)
          // 更新课程的已选人数
          course.enrolledCount = Math.max((course.enrolledCount || 0) - 1, 0)
          // 退课成功后，如果它在 selectedCourse 中，也触发一下提示或关闭
          if (selectedCourse.value && selectedCourse.value.courseId === course.courseId) {
             // 稍后自动关闭
             setTimeout(closeDialog, 1500)
          }
          setTimeout(() => {
            emit('refresh')
          }, 300)
        } else {
          setOperationMessage(course.courseId, 'error', response.message || '退课失败')
        }
      } catch (err) {
        setOperationMessage(course.courseId, 'error', err.message || '退课失败')
        console.error('Drop error:', err)
      } finally {
        droppingCourses.value.delete(course.courseId)
      }
    }

    /**
     * 计算总学分
     */
    const totalCredits = computed(() => {
      return courses.value.reduce((sum, course) => {
        return sum + (course.credits || 0)
      }, 0)
    })

    // 监听ESC键关闭对话框
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') {
        if (showConfirmDialog.value) {
          cancelSearchCourses()
        } else if (selectedCourse.value) {
          closeDialog()
        } else if (showAvailableCourses.value) {
          closeAvailableCoursesDialog()
        }
      }
    }
    onMounted(() => {
      window.addEventListener('keydown', handleKeyDown)
    })

    // 课程颜色缓存（避免重复计算，且保证同一课程颜色稳定一致）
    const courseColorCache = new Map()

    const hashStringToUint32 = (input) => {
      const str = String(input ?? '')
      // FNV-1a 32-bit
      let hash = 2166136261
      for (let i = 0; i < str.length; i++) {
        hash ^= str.charCodeAt(i)
        hash = Math.imul(hash, 16777619)
      }
      return hash >>> 0
    }

    /**
     * 获取课程颜色
     */
    const getCourseColor = (courseId) => {
      const key = String(courseId ?? '')
      const cached = courseColorCache.get(key)
      if (cached) return cached

      // 原来的做法是 parseInt(courseId) % N：当 courseId 不是纯数字、或数字分布集中时会高频撞色。
      // 改为：对 courseId 做稳定哈希 → 生成 HSL 颜色（可组合维度更多，重复概率显著降低）。
      const hash = hashStringToUint32(key)
      const hue = hash % 360
      const borderSat = 62 + ((hash >>> 8) % 18) // 62-79
      const borderLight = 40 + ((hash >>> 16) % 12) // 40-51
      const bgSat = 78
      const bgLight = 92

      const color = {
        bg: `hsl(${hue}, ${bgSat}%, ${bgLight}%)`,
        border: `hsl(${hue}, ${borderSat}%, ${borderLight}%)`,
        text: '#333'
      }
      courseColorCache.set(key, color)
      return color
    }

    return {
      studentId,
      courses,
      loading,
      displayError,
      successMessage,
      droppingCourses,
      totalCredits,
      weekdays,
      periods,
      selectedCourse,
      handleDrop,
      getCellBlocks,
      getBlockStyle,
      getCellConflictCount,
      isToday,
      showCourseDetail,
      closeDialog,
      showAvailableCourses,
      availableCourses,
      loadingAvailableCourses,
      availableCoursesError,
      queryTimeInfo,
      enrollingCourses,
      operationMessage,
      handleCellHover,
      handleCellLeave,
      hasMultipleCampuses,
      getCampusBadgeStyle,
      closeAvailableCoursesDialog,
      handleEnrollFromDialog,
      showConfirmDialog,
      confirmDialogInfo,
      confirmSearchCourses,
      cancelSearchCourses,
      isShowingConfirm,
      getCourseColor
    }
  }
}
</script>

<style scoped>
.my-courses {
  padding: 0px;
  background: #f8f5fa;
}
.no-student-id {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 12px;
  color: #909399;
  border: 1px dashed #dcdfe6;
  margin: 20px 0;
}

.error-message {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe0e0 100%);
  border: 1px solid #ffb3b3;
  color: #d63031;
  padding: 14px 18px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(214, 48, 49, 0.1);
}

.loading {
  text-align: center;
  padding: 40px;
  color: #667eea;
  font-size: 16px;
  font-weight: 500;
}

/* 引入首页选课页面的样式 */
.results-card { background: white; border-radius: 12px; padding: 0; min-height: auto; box-shadow: 0 4px 20px rgba(124, 31, 137, 0.06); border: 1px solid rgba(255, 255, 255, 0.8); overflow: hidden; }
.list-header { display: flex; background: #FAF4FC; padding: 12px 16px; color: #6a5acd; font-weight: 600; font-size: 13px; border-bottom: 1px solid #efe5f5; }
.course-list-body { border: none; background: #fff; }

.col { 
  padding: 0 12px;
  box-sizing: border-box;
}
.col-info      { flex: 2.5; min-width: 200px; } 
.col-instructor{ flex: 1;   min-width: 100px; } 
.col-schedule  { flex: 1.8; min-width: 180px; }
.col-capacity  { flex: 1.2; min-width: 120px; }
.col-actions   { flex: 0 0 100px; text-align: center; }

.schedule-layout {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  overflow: auto;
}

.schedule-main {
  flex: 1 1 100%;
  max-width: 100%;
  overflow: auto;
}

.schedule-sidebar {
  flex: 1;
  max-width: 35%;
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 100%;
}

.sidebar-section {
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  padding: 16px;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.sidebar-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.sidebar-section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.btn-link {
  border: 1px solid #7C1F89;
  background: #7C1F89;
  color: #fff;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 6px;
  transition: opacity 0.2s;
}

.btn-link:hover:not(:disabled) {
  opacity: 0.85;
}

.btn-link:disabled {
  background: #ccc;
  border-color: #ccc;
  color: #fff;
  cursor: not-allowed;
}

.sidebar-hint,
.sidebar-empty,
.sidebar-loading {
  text-align: center;
  color: #888;
  font-size: 13px;
  padding: 12px 4px;
}

.selected-course-list {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.selected-course-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.selected-course-main {
  margin-bottom: 6px;
}

.selected-course-name {
  font-weight: 600;
  color: #333;
}

.selected-course-meta {
  display: flex;
  gap: 10px;
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
}

.selected-course-sessions {
  font-size: 12px;
  color: #555;
  margin-bottom: 8px;
}

.selected-course-actions {
  text-align: right;
}

.schedule-container {
  width: 100%;
  overflow-x: hidden;
  overflow-y: hidden;
  background: white;
  padding: 4px 4px 55px 4px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.schedule-wrapper {
  min-width: 1000px;
  max-width: 1200px;
  margin: 0 auto;
}

.schedule-grid {
  display: grid;
  grid-template-columns: 35px 55px repeat(7, 1fr);
  grid-template-rows: 35px repeat(4, 57px) 16px repeat(4, 57px) 16px repeat(3, 57px);
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transform: scale(0.9);
  transform-origin: top center;
  margin-bottom: -10%;
}

.grid-header:first-child {
  grid-column: 1 / 3;
  background: #7C1F89;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 13px;
}

.grid-header.day-header {
  background: #7C1F89;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.5px;
}

.time-header {
  background: #7C1F89;
  border-right: 2px solid rgba(255,255,255,0.3);
}

.day-header {
  border-right: 1px solid rgba(255,255,255,0.2);
  padding: 8px;
}

.day-header.today {
  background: #9C27B0;
  box-shadow: inset 0 0 0 2px rgba(255,255,255,0.3);
}

.day-name {
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

/* 时段标签（上午/下午/晚间） */
.time-period-label {
  background: linear-gradient(180deg, #f3e5f5 0%, #e1bee7 100%);
  color: #6a1b9a;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: vertical-rl;
  text-orientation: upright;
  border-right: 2px solid #dee2e6;
  border-bottom: 1px solid #e9ecef;
  font-weight: 700;
  font-size: 13px;
  letter-spacing: 3px;
  padding: 8px 0;
}

.morning-label {
  grid-row: 2 / 6;
}

.afternoon-label {
  grid-row: 7 / 11;
}

.evening-label {
  grid-row: 12 / 15;
}

.period-label-text {
  display: inline-block;
}

.period-header {
  background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%);
  border: none;
  border-right: 2px solid #dee2e6;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4px 2px;
}

.period-header-with-label {
  background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%);
}

.period-number {
  font-size: 11px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 2px;
}

.period-time {
  font-size: 9px;
  color: #6c757d;
  line-height: 1.2;
}

.schedule-cell {
  border-right: 1px solid #e9ecef;
  border-bottom: 1px solid #e9ecef;
  position: relative;
  padding: 0px;
  min-height: 57px;
  background: #fff;
  overflow: visible;
  grid-column: auto;
  transition: all 0.2s ease;
}

/* 时间分隔行 */
.time-break-with-text {
  grid-column: 1 / -1;
  background: white;
  border-top: 1px solid #dee2e6;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.break-text {
  font-size: 12px;
  font-weight: 600;
  color: #333;
  letter-spacing: 30px;
}

.schedule-cell.empty-cell {
  cursor: pointer;
}

.schedule-cell.empty-cell:hover {
  background: #f8f5fa;
  box-shadow: inset 0 0 0 1px #7C1F89;
}

.schedule-cell.show-confirm {
  background: #f8f5fa;
  box-shadow: inset 0 0 0 2px #7C1F89;
}

/* 单元格内确认按钮 - 自然融入样式 */
.confirm-buttons {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: linear-gradient(135deg, rgba(124, 31, 137, 0.05) 0%, rgba(156, 39, 176, 0.05) 100%);
  backdrop-filter: blur(2px);
  z-index: 15;
  padding: 8px;
}

.confirm-text {
  margin: 0;
  font-size: 10px;
  color: #7C1F89;
  font-weight: 600;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

.button-group {
  display: flex;
  gap: 4px;
}

.btn-confirm-small {
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 10px;
  font-weight: 600;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  background: #7C1F89;
  color: white;
}

.btn-confirm-small:hover {
  background: #9C27B0;
  box-shadow: 0 2px 5px rgba(124, 31, 137, 0.3);
}

.course-block {
  border: none;
  border-left: none;
  border-radius: 4px;
  padding: 6px 8px;
  margin: 1px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 13px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
  word-break: break-word;
  box-sizing: border-box;
  min-height: 0;
  backdrop-filter: blur(10px);
  position: relative;
}

/* 校区标识 */
.campus-badge {
  position: absolute;
  top: 0;
  right: 0;
  font-size: 13px;
  font-weight: 700;
  padding: 2px 5px;
  border-bottom-left-radius: 6px;
  box-shadow: -1px 1px 2px rgba(0,0,0,0.1);
  z-index: 2;
}

/* 单双周标识 */
.week-type-badge {
  position: absolute;
  top: 0;
  left: 0;
  font-size: 13px;
  font-weight: 700;
  padding: 2px 5px;
  border-bottom-right-radius: 4px;
  box-shadow: 1px 1px 2px rgba(0,0,0,0.1);
  z-index: 2;
}

.course-block:hover .campus-badge {
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.3);
  transform: translateY(-1px);
}

.course-block:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  z-index: 20;
}

.course-block.has-conflict {
  position: absolute;
  top: 3px;
  bottom: 3px;
  left: 3px;
  right: 3px;
  margin: 0;
}

/* 跨多节的课程块样式 */
.course-block {
  margin: 0;
}

.course-name-text {
  font-weight: bold;
}

/* 课程列表对话框样式 */
.course-list-dialog {
  max-width: 800px;
}

.course-list-table-container {
  overflow-x: auto;
}

.course-list-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.course-list-table th {
  background: #f8f9fa;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
}

.course-list-table td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  vertical-align: middle;
}

.course-name-main {
  font-weight: 600;
  color: #333;
}

.course-id-sub {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.session-item {
  margin-bottom: 2px;
  font-size: 13px;
  color: #666;
}

.week-tag {
  display: inline-block;
  font-size: 10px;
  padding: 0 4px;
  border-radius: 3px;
  margin-left: 4px;
  color: white;
}

.week-tag.single {
  background-color: #7C1F89;
}

.week-tag.double {
  background-color: #1976D2;
}

.btn-drop-small {
  padding: 4px 10px;
  background: white;
  color: #ff4d4f;
  border: 1px solid #ff4d4f;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.btn-drop-small:hover:not(:disabled) {
  background: #fff1f0;
}

.btn-drop-small:disabled {
  border-color: #ccc;
  color: #ccc;
  cursor: not-allowed;
}

/* ont-weight: 600;
  margin-bottom: 3px;
  line-height: 1.3;
  font-size: 11px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}

.course-location {
  font-size: 10px;
  opacity: 0.85;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #6c757d;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  font-size: 15px;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s;
}

.dialog-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 75vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.2);
  animation: slideUp 0.3s;
}

.dialog-close {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  cursor: pointer;
  font-size: 24px;
  line-height: 1;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  z-index: 10;
}

.dialog-close:hover {
  background: #e0e0e0;
  color: #333;
}

.dialog-header {
  padding: 15px 40px 12px 15px;
  border-bottom: 1px solid #eee;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.dialog-body {
  padding: 15px;
}

.detail-item {
  margin-bottom: 15px;
  font-size: 14px;
  line-height: 1.6;
}

.detail-item strong {
  color: #333;
  margin-right: 8px;
}

.sessions-list {
  list-style: none;
  padding: 0;
  margin: 10px 0 0 0;
}

.sessions-list li {
  padding: 5px 0;
  color: #666;
}

.dialog-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
}

.btn-drop-dialog {
  padding: 8px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-drop-dialog:hover:not(:disabled) {
  background: #ff7875;
}

.btn-drop-dialog:disabled {
  background: #ccc;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 对话框特定样式 */
.course-detail-dialog {
  max-width: 780px !important;
  width: 85% !important;
  max-height: 60vh !important;
}


.available-courses-dialog {
  max-width: 780px !important;
  width: 85% !important;
  max-height: 65vh !important;
}

.results-table {
  width: 100%;
}

.no-results {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 12px;
  color: #909399;
  border: 1px dashed #dcdfe6;
  margin: 20px 0;
}

@media (max-width: 1024px) {
  .my-courses {
    padding: 15px;
  }

  .schedule-layout {
    flex-direction: column;
    overflow: hidden;
  }

  .schedule-main {
    flex: 1 1 100%;
    max-width: 100%;
    overflow: auto;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header h2 {
    font-size: 20px;
  }
  
  .schedule-wrapper {
    min-width: 650px;
  }
  
  .schedule-grid {
    grid-template-columns: 32px 50px repeat(7, 1fr);
    grid-template-rows: 42px repeat(4, 60px) 16px repeat(4, 60px) 16px repeat(3, 60px);
  }
  
  .time-period-label {
    font-size: 12px;
  }
  
  .period-header {
    padding: 3px 2px;
  }
  
  .period-number {
    font-size: 10px;
  }
  
  .period-time {
    font-size: 8px;
  }
  
  .course-block {
    font-size: 10px;
    padding: 5px 6px;
  }
  
  .campus-badge {
    font-size: 8px;
    padding: 1px 4px;
  }

  .week-type-badge {
    font-size: 8px;
    padding: 1px 4px;
  }
  
  .course-name-text {
    font-size: 10px;
    line-clamp: 4;
    -webkit-line-clamp: 4;
  }
  
  .course-location {
    font-size: 8px;
  }
}

@media (max-width: 768px) {
  .my-courses {
    padding: 10px;
  }
  
  .header h2 {
    font-size: 18px;
  }
  
  .summary {
    padding: 10px 14px;
    font-size: 13px;
  }
  
  .schedule-wrapper {
    min-width: 550px;
  }
  
  .schedule-grid {
    grid-template-columns: 30px 45px repeat(7, 1fr);
    grid-template-rows: 38px repeat(4, 55px) 14px repeat(4, 55px) 14px repeat(3, 55px);
  }
  
  .time-period-label {
    font-size: 11px;
  }
  
  .grid-header {
    font-size: 11px;
    padding: 5px;
  }
  
  .day-name {
    font-size: 12px;
  }
  
  .period-header {
    padding: 2px 1px;
  }
  
  .period-number {
    font-size: 9px;
  }
  
  .period-time {
    font-size: 7px;
  }
  
  .schedule-cell {
    min-height: 55px;
    padding: 2px;
  }
  
  .course-block {
    font-size: 9px;
    padding: 4px 5px;
    border-left-width: 2px;
  }
  
  .campus-badge {
    font-size: 7px;
    padding: 1px 3px;
  }

  .week-type-badge {
    font-size: 7px;
    padding: 1px 3px;
  }
  
  .course-name-text {
    font-size: 9px;
    margin-bottom: 2px;
    line-clamp: 3;
    -webkit-line-clamp: 3;
  }
  
  .course-location {
    font-size: 8px;
  }
  
  /* 对话框在手机上的优化 */
  .dialog-content {
    width: 95%;
    max-width: none;
    max-height: 90vh;
    margin: 10px;
  }
  
  .dialog-header {
    padding: 15px 40px 12px 15px;
  }
  
  .dialog-header h3 {
    font-size: 18px;
  }
  
  .dialog-body {
    padding: 15px;
  }
  
  .detail-item {
    font-size: 13px;
    margin-bottom: 12px;
  }
  
  .btn-drop-dialog {
    width: 100%;
    padding: 10px;
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .my-courses {
    padding: 8px;
  }
  
  .header h2 {
    font-size: 16px;
  }
  
  .btn-refresh {
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .summary {
    padding: 8px 12px;
    font-size: 12px;
  }
  
  .summary strong {
    font-size: 16px;
  }
  
  .schedule-wrapper {
    min-width: 480px;
  }
  
  .schedule-grid {
    grid-template-columns: 28px 42px repeat(7, 1fr);
    grid-template-rows: 32px repeat(4, 50px) 12px repeat(4, 50px) 12px repeat(3, 50px);
  }
  
  .time-period-label {
    font-size: 10px;
  }
  
  .grid-header {
    font-size: 10px;
    padding: 3px;
  }
  
  .day-name {
    font-size: 11px;
  }
  
  .period-header {
    padding: 1px;
  }
  
  .period-number {
    font-size: 8px;
  }
  
  .period-time {
    font-size: 7px;
  }
  
  .schedule-cell {
    min-height: 50px;
    padding: 2px;
  }
  
  .course-block {
    font-size: 8px;
    padding: 3px 4px;
    border-left-width: 2px;
  }
  
  .campus-badge {
    font-size: 7px;
    padding: 1px 2px;
  }

  .week-type-badge {
    font-size: 7px;
    padding: 1px 2px;
  }
  
  .course-name-text {
    font-size: 8px;
    margin-bottom: 1px;
    line-clamp: 2;
    -webkit-line-clamp: 2;
  }
  
  .course-location {
    font-size: 7px;
  }
  
  .dialog-content {
    width: 98%;
    margin: 5px;
    border-radius: 6px;
  }
  
  .dialog-header {
    padding: 12px 35px 10px 12px;
  }
  
  .dialog-header h3 {
    font-size: 16px;
  }
  
  .dialog-body {
    padding: 12px;
  }
  
  .detail-item {
    font-size: 12px;
    margin-bottom: 10px;
  }
  
  .dialog-close {
    width: 28px;
    height: 28px;
    font-size: 20px;
    top: 8px;
    right: 8px;
  }
}

.category-title {
  margin: 20px 0 10px;
  padding-left: 10px;
  border-left: 4px solid #7C1F89;
  color: #333;
  font-size: 18px;
}

.recommendation-group:first-child .category-title {
  margin-top: 0;
}

/* 进度条列表样式 */
.progress-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px 5px;
}

.progress-item-container {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 15px;
}

.progress-item-container:last-child {
  border-bottom: none;
}

.progress-row {
  display: flex;
  align-items: center;
  gap: 15px;
  height: 40px;
}

.progress-label {
  width: 100px; /* 固定宽度，对齐 */
  font-weight: 600;
  color: #333;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.progress-track-wrapper {
  flex: 1.5; /* 占据更多空间，让进度条更长 */
  min-width: 200px;
  display: flex;
  align-items: center;
}

.progress-track {
  width: 100%;
  height: 12px;
  background-color: #e9ecef;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
}

.progress-bar {
  height: 100%;
  border-radius: 6px;
  transition: width 0.6s ease;
}

.bar-green {
  background: linear-gradient(90deg, #52c41a 0%, #73d13d 100%);
}

.bar-red {
  background: linear-gradient(90deg, #ff4d4f 0%, #ff7875 100%);
}

.progress-value {
  width: 60px;
  text-align: right;
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.status-ok {
  color: #52c41a;
  font-size: 12px;
}

.progress-action {
  width: 80px;
  display: flex;
  justify-content: flex-end;
}

.btn-recommend {
  padding: 4px 10px;
  background-color: white;
  color: #1890ff;
  border: 1px solid #1890ff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.btn-recommend:hover {
  background-color: #e6f7ff;
}

/* 推荐课程下拉区域 */
.recommendation-dropdown {
  margin-top: 15px;
  background: #fcfcfc;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 15px;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.no-recs-hint {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 10px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 15px;
}

/* 迷你课程卡片样式调整 */
.course-card.mini-card {
  margin-bottom: 0;
  padding: 12px;
  background: white;
  border: 1px solid #e8e8e8;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.course-card.mini-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border-color: #1890ff;
}

.course-info.compact p {
  margin: 3px 0;
  font-size: 12px;
}

.btn-enroll.small {
  padding: 4px 12px;
  font-size: 12px;
}

/* 移动端适配调整 */
@media (max-width: 600px) {
  .progress-row {
    flex-wrap: wrap;
    height: auto;
    gap: 8px;
  }
  
  .progress-label {
    width: 100%;
    margin-bottom: 4px;
  }
  
  .progress-track-wrapper {
    min-width: 150px;
  }
}
</style>
