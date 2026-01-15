<template>
  <div class="course-record-container">
    <div class="record-header">
      <h2>选课记录</h2>
      <span class="record-count">共 <strong>{{ total }}</strong> 门课程</span>
    </div>

    <div v-if="studentCourses.length === 0" class="empty-state">
      <p>暂无选课记录</p>
    </div>

    <div v-else class="records-list">
      <div v-for="record in studentCourses" :key="record.course.id" class="record-card">
        <div class="record-card-header">
          <div class="header-left">
            <h3 class="record-course-name">{{ record.course.cname }}</h3>
            <span :class="['status-badge', { active: record.status === 1, dropped: record.status === 0 }]">
              {{ record.status === 1 ? '已选课' : '已退选' }}
            </span>
          </div>
        </div>

        <div class="record-meta">
          <div class="meta-row">
            <span class="meta-label">课程编号:</span>
            <span class="meta-value">{{ record.course.id }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">授课教师:</span>
            <span class="meta-value">{{ record.course.teacher }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">教学地点:</span>
            <span class="meta-value">{{ record.course.address }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">选课人数:</span>
            <span class="meta-value">{{ record.course.num }} / {{ record.course.stock }}</span>
          </div>
        </div>

        <div class="record-actions">
          <button 
            v-if="record.status === 1"
            @click="deselectCourse(record.course.id)"
            class="deselect-btn"
          >
            退选
          </button>
          <span v-else class="status-text">已退选</span>
        </div>
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
  name: 'CourseRecord',
  data() {
    return {
      id: 0,
      studentCourses: [],
      studentCoursesAll: [],
      pageNum: 1,
      pageSize: 6,
      total: 0,
      cname: '',
    };
  },
  methods: {
    async fetchStudentCourses() {
      try {
        const response3 = await axios.get('/getRoleMessage');
        if (!response3.data || !response3.data.data || !response3.data.data.id) {
          console.error('获取用户信息失败', response3.data);
          this.$message.error('获取用户信息失败，请重新登录');
          return;
        }
        this.id = response3.data.data.id;
        
        const response = await axios.get('/student/listMyCourse', {
          params: {
            sid: this.id,
            pageNum: this.pageNum,
            pageSize: this.pageSize,
          },
        });
        const response4 = await axios.get('/student/listCourseByMajorName', {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            majorName: this.majorName,
          },
        }); 
        this.studentCoursesAll = response4.data.data.list;
        if (response.data && response.data.data) {
          this.studentCourses = response.data.data.list;
          this.total = response.data.data.total;
        }
        this.studentCourses.forEach(course => {
          const matchingCourse = this.studentCoursesAll.find(c => c.cname === course.course.cname);
          if (matchingCourse) {
            course.course.teacher = matchingCourse.teacher;
            course.course.id = matchingCourse.id;
          }
        });
      } catch (error) {
        console.error('获取学生选课记录失败', error);
      }
    },
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchStudentCourses();
    },
    async deselectCourse(courseId) {
      try {
        if (!this.id) {
          this.$message.error('请先登录');
          return;
        }
        
        const response = await axios.post(
          `/studentCourse/deleteMyCourse?sid=${this.id}&cid=${courseId}`
        );
        
        if (response.data && response.data.code === 1) {
          this.$message.success('退选成功');
          this.fetchStudentCourses();
        } else {
          this.$message.error(response.data.msg || '退选失败');
        }
      } catch (error) {
        console.error('退选失败', error);
        this.$message.error('退选失败');
      }
    },
  },
  mounted() {
    this.fetchStudentCourses();
  },
};
</script>

<style scoped>
.course-record-container {
  padding: 0;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.record-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.record-count {
  font-size: 14px;
  color: #666;
}

.record-count strong {
  color: #667eea;
  font-weight: 600;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.records-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.record-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.record-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  border-color: #667eea;
  transform: translateY(-2px);
}

.record-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 8px;
}

.header-left {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  flex: 1;
}

.record-course-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  flex: 1;
  word-break: break-word;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-badge.active {
  background-color: #52c41a;
  color: white;
}

.status-badge.dropped {
  background-color: #ff4d4f;
  color: white;
}

.record-meta {
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

.record-actions {
  margin-top: auto;
}

.deselect-btn {
  width: 100%;
  padding: 10px;
  background-color: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.deselect-btn:hover {
  background-color: #ff7875;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.4);
}

.status-text {
  display: block;
  text-align: center;
  padding: 10px;
  color: #999;
  font-size: 14px;
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
