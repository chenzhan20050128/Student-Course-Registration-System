<template>
  <div>
    <el-table :data="courses" style="width: 88%">
      <el-table-column prop="id" label="ID" width="75"></el-table-column>
      <el-table-column prop="cname" label="课程名称" width="175"></el-table-column>
      <el-table-column prop="major" label="开设专业" width="225"></el-table-column>
      <el-table-column prop="teacher" label="授课教师" width="125"></el-table-column>
      <el-table-column prop="address" label="教学地点" width="175"></el-table-column>
      <el-table-column prop="num" label="当前选课人数" width="150"></el-table-column>
      <el-table-column prop="stock" label="课程容量" width="145"></el-table-column>
      <el-table-column prop="credit" label="学分" width="125"></el-table-column>
      <el-table-column label="操作" width="80">
        <template v-slot="scope">
          <el-button
            @click="selectCourse(scope.row.id)"
            type="primary"
            size="small"
            class="select-course-button"
          >
            选择
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handlePageChange"
      class="custom-pagination"
      style="text-align: center; margin-top: 20px;"
    ></el-pagination>
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
    };
  },
  methods: {
    async fetchCourses() {
      try {
        const response3 = await axios.get('/getRoleMessage');
        this.id = response3.data.data.id;
        const response2 = await axios.get(
          `/student/preUpdateStudent/${this.id}`
        );
        this.majorName = response2.data.data.student.major;
        const response = await axios.get('/student/listCourseByMajorName', {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            majorName: this.majorName,
          },
        });
        if (response.data && response.data.data) {
          this.courses = response.data.data.list;
          this.total = response.data.data.total;
        }
      } catch (error) {
        console.error('获取课程列表失败', error);
      }
    },
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchCourses();
    },
    async selectCourse(courseId) {
      try {
        const response = await axios.get('/student/selectCourse', {
          params: {
            sid: this.id,
            cid: courseId,
          },
        });
        if (response.data && response.data.code === 1) {
          this.$message.success('选课成功');
          this.fetchCourses();
        } else {
          this.$message.error(response.data.message || '选课失败');
        }
      } catch (error) {
        console.error('选课失败', error);
        this.$message.error('选课失败');
      }
    },
  },
  mounted() {
    this.fetchCourses();
  },
};
</script>

<style scoped>
/* 调整操作列中按钮的样式 */
.select-course-button {
  margin-left: -10px; /* 通过负的 margin-left 向左移动按钮 */
  background-color: #8b007a !important; /* 正常状态背景颜色 */
  border-color: #8b007a !important; /* 正常状态边框颜色 */
  color: white !important; /* 保持文字为白色 */
}

.select-course-button:hover {
  margin-left: -10px; /* 通过负的 margin-left 向左移动按钮 */
  background-color: #a70c94 !important; /* 正常状态背景颜色 */
  border-color: #a70c94 !important; /* 正常状态边框颜色 */
  color: white !important; /* 保持文字为白色 */
}

/* 自定义分页组件样式 */
::v-deep(.custom-pagination .el-pager li) {
  background-color: #8b007a !important; /* 页码背景颜色 */
  border-color: #8b007a !important; /* 页码边框颜色 */
  color: white !important; /* 页码文字颜色保持白色 */
  border-radius: 5px; /* 设置圆角 */
}

::v-deep(.custom-pagination .el-pagination__button) {
  background-color: #8b007a !important; /* 翻页按钮背景颜色 */
  border-color: #8b007a !important; /* 翻页按钮边框颜色 */
  color: white !important; /* 翻页按钮文字颜色 */
}
</style>