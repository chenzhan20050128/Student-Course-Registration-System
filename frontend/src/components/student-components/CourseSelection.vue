<template>
  <div class="course-selection-container">
    <div class="course-header">
      <h2>课程列表</h2>
      <span class="course-count">共 <strong>{{ total }}</strong> 门课程</span>
    </div>

    <!-- 搜索框区域 -->
    <div class="search-bar">
      <el-input
        v-model="searchCourseName"
        placeholder="请输入课程名称搜索"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <i class="el-icon-search"></i>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch" class="search-button">
        搜索
      </el-button>
    </div>

    <div v-if="courses.length === 0" class="empty-state">
      <p>暂无课程数据</p>
    </div>

    <div v-else class="courses-grid">
      <div v-for="course in courses" :key="course.courseId" class="course-card">
        <!-- 已选标签 -->
        <div v-if="selectedCourseIds.includes(parseInt(course.courseId))" class="selected-badge">
          已选
        </div>
        
        <div class="course-card-header">
          <h3 class="course-name">{{ course.courseName }}</h3>
          <span class="credit-badge">{{ course.credits }}分</span>
        </div>
        
        <div class="course-meta">
          <div class="meta-row">
            <span class="meta-label">课程编号:</span>
            <span class="meta-value">{{ course.courseId }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">开设专业:</span>
            <span class="meta-value">{{ course.college }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">授课教师:</span>
            <span class="meta-value">{{ course.instructorName }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">教学地点:</span>
            <span class="meta-value">{{ course.campus }}</span>
          </div>
        </div>

        <div class="course-capacity">
          <div class="capacity-header">
            <span>容量</span>
            <span class="capacity-text">{{ course.enrolledCount }} / {{ course.capacity }}</span>
          </div>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ 
                width: capacityPercent(course.enrolledCount, course.capacity) + '%',
                backgroundColor: capacityColor(course.enrolledCount, course.capacity)
              }"
            ></div>
          </div>
        </div>

        <button 
          @click="selectCourse(course.courseId)"
          :class="['select-btn', { 
            disabled: course.enrolledCount >= course.capacity || selectedCourseIds.includes(parseInt(course.courseId))
          }]"
          :disabled="course.enrolledCount >= course.capacity || selectedCourseIds.includes(parseInt(course.courseId))"
        >
          {{ selectedCourseIds.includes(parseInt(course.courseId)) ? '已选' : (course.enrolledCount >= course.capacity ? '已满课' : '选课') }}
        </button>
      </div>
    </div>

    <div class="pagination-wrapper">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="handlePageChange"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import axios from '@/http';
export default {
  name: 'CourseSelection',
  data() {
    return {
      id: 0,
      courses: [],
      pageNum: 1,
      pageSize: 6,
      total: 0,
      majorName: '',
      selectedCourseIds: [],
      selectingCourseId: null, // 正在选课的课程ID，防止重复提交
      searchCourseName: '', // 搜索的课程名
    };
  },
  methods: {
    isSelected(courseId) {
      return this.selectedCourseIds.includes(courseId);
    },
    capacityPercent(num, stock) {
      if (stock === 0) return 0;
      return Math.min((num / stock) * 100, 100);
    },
    capacityColor(num, stock) {
      const percent = this.capacityPercent(num, stock);
      if (percent >= 100) return '#ff4d4f';
      if (percent >= 80) return '#faad14';
      return '#52c41a';
    },
    async fetchSelectedCourses() {
      try {
        console.log('开始获取已选课程，学生ID:', this.id);
        const response = await axios.get('/student/listMyCourse', {
          params: {
            sid: this.id,
            pageNum: 1,
            pageSize: 1000,
          },
        });
        
        console.log('已选课程API响应:', response.data);
        
        if (response.data && response.data.data && response.data.data.list) {
          console.log('已选课程列表:', response.data.data.list);
          
          // 提取已选课程的ID，确保类型一致
          const backendSelectedIds = response.data.data.list
            .filter(item => {
              console.log('检查课程项:', item, 'status:', item.status);
              return item.status === 1;
            })
            .map(item => {
              const courseId = item.course?.courseId || item.courseId;
              console.log('提取课程ID:', item, '-> courseId:', courseId);
              // 统一转换为数字类型
              return parseInt(courseId);
            })
            .filter(id => !isNaN(id));
          
          // 合并前端已选和后端已选（去重）
          const mergedIds = [...new Set([...this.selectedCourseIds, ...backendSelectedIds])];
          this.selectedCourseIds = mergedIds;
          
          console.log('后端已选课程IDs:', backendSelectedIds);
          console.log('合并后已选课程IDs:', this.selectedCourseIds);
        } else {
          console.log('没有已选课程数据');
        }
      } catch (error) {
        console.error('获取已选课程失败', error);
      }
    },
    async fetchCourses() {
      try {
        const response3 = await axios.get('/getRoleMessage');
        if (!response3.data || !response3.data.data || !response3.data.data.id) {
          console.error('获取用户信息失败', response3.data);
          this.$message.error('获取用户信息失败，请重新登录');
          return;
        }
        this.id = response3.data.data.id;
        
        const response2 = await axios.get(
          `/student/preUpdateStudent/${this.id}`
        );
        if (!response2.data || !response2.data.data || !response2.data.data.student) {
          console.error('获取学生信息失败', response2.data);
          this.$message.error('获取学生信息失败');
          return;
        }
        this.majorName = response2.data.data.student.college;
        
        const response = await axios.get('/student/listCourseByMajorName', {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            majorName: this.majorName,
            courseName: this.searchCourseName || undefined, // 添加课程名搜索参数
          },
        });
        
        if (response.data && response.data.data) {
          const allCourses = response.data.data.list;
          this.total = response.data.data.total;
          
          // 获取已选课程列表
          await this.fetchSelectedCourses();
          
          console.log('所有课程数量:', allCourses.length);
          console.log('已选课程IDs:', this.selectedCourseIds);
          
          // 显示所有课程，不过滤（通过标签和按钮状态区分已选）
          this.courses = allCourses;
          
          console.log('显示课程数量:', this.courses.length);
          
          console.log('过滤后剩余课程数:', this.courses.length);
        }
      } catch (error) {
        console.error('获取课程列表失败', error);
        this.$message.error('获取课程列表失败');
      }
    },
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchCourses();
    },
    handleSearch() {
      // 搜索时重置到第一页
      this.pageNum = 1;
      this.fetchCourses();
    },
    async selectCourse(courseId) {
      try {
        if (!this.id) {
          this.$message.error('请先登录');
          return;
        }
        
        // 防止重复提交
        if (this.selectingCourseId === courseId) {
          console.log('重复提交，已忽略');
          return;
        }
        
        // 检查是否已选
        if (this.selectedCourseIds.includes(parseInt(courseId))) {
          this.$message.warning('该课程已选，无需重复选课');
          return;
        }
        
        // 前端检查容量
        const course = this.courses.find(c => c.courseId === courseId);
        if (course && course.enrolledCount >= course.capacity) {
          this.$message.warning('该课程已满，无法选课');
          console.log('前端检查：课程已满', course.enrolledCount, '/', course.capacity);
          return;
        }
        
        this.selectingCourseId = courseId;
        console.log('开始选课，课程ID:', courseId, '学生ID:', this.id, '当前容量:', course?.enrolledCount, '/', course?.capacity);
        
        const response = await axios.get('/student/selectCourse', {
          params: {
            sid: this.id,
            cid: courseId,
          },
        });
        
        console.log('选课响应:', response.data);
        
        if (response.data && response.data.code === 1) {
          this.$message.success(response.data.msg || '选课成功');
          // 立即添加到已选列表（立即更新UI状态）
          if (!this.selectedCourseIds.includes(parseInt(courseId))) {
            this.selectedCourseIds.push(parseInt(courseId));
          }
          console.log('选课成功，当前已选:', this.selectedCourseIds);
          
          // Kafka异步处理，延迟刷新课程容量（但不重新获取已选列表，避免状态丢失）
          setTimeout(async () => {
            // 只刷新课程列表的容量信息，保留已选状态
            await this.fetchCourses();
          }, 1500);
        } else {
          // 选课失败，刷新课程列表
          this.$message.error(response.data.msg || '选课失败');
          console.error('选课失败:', response.data.msg);
          // 延迟刷新以获取最新的课程容量信息
          setTimeout(async () => {
            await this.fetchCourses();
          }, 800);
        }
      } catch (error) {
        console.error('选课失败，异常:', error);
        this.$message.error('选课失败: ' + (error.response?.data?.msg || error.message));
      } finally {
        this.selectingCourseId = null;
      }
    },
  },
  mounted() {
    this.fetchCourses();
  },
};
</script>

<style scoped>
.course-selection-container {
  padding: 0;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.course-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.course-count {
  font-size: 14px;
  color: #666;
}

.course-count strong {
  color: #667eea;
  font-weight: 600;
}

/* 搜索框样式 */
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  align-items: center;
}

.search-input {
  flex: 1;
  max-width: 400px;
}

.search-button {
  height: 40px;
  padding: 0 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-weight: 500;
}

.search-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3f91 100%);
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.course-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  position: relative;
}

.course-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
  transform: translateY(-2px);
}

.selected-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #52c41a;
  color: white;
  padding: 4px 12px;
  border-radius: 0 8px 0 8px;
  font-size: 12px;
  font-weight: 600;
  z-index: 1;
  box-shadow: 0 2px 4px rgba(82, 196, 26, 0.3);
}

.course-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 8px;
}

.course-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  flex: 1;
  word-break: break-word;
}

.credit-badge {
  background-color: #667eea;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.course-meta {
  margin-bottom: 16px;
  font-size: 13px;
}

.meta-row {
  display: flex;
  margin-bottom: 6px;
  line-height: 1.4;
}

.meta-label {
  color: #999;
  width: 70px;
  flex-shrink: 0;
}

.meta-value {
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-capacity {
  margin-bottom: 16px;
}

.capacity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  font-size: 13px;
  color: #666;
}

.capacity-text {
  font-weight: 600;
  color: #333;
}

.progress-bar {
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.3s ease;
}

.select-btn {
  width: 100%;
  padding: 10px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: auto;
}

.select-btn:hover:not(.disabled) {
  background-color: #5568d3;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.select-btn.disabled {
  background-color: #d9d9d9;
  color: #999;
  cursor: not-allowed;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

::v-deep(.el-pagination) {
  justify-content: center;
}

::v-deep(.el-pagination .btn) {
  color: #667eea;
}

::v-deep(.el-pagination .active) {
  background-color: #667eea;
  color: white;
}
</style>
