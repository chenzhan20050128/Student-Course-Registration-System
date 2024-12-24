<template>
  <div>
    <el-table
      :data="studentCourses"
      class="course-record-table"
      header-align="left"
      align="left"
      style="width: 88%"
    >
      <el-table-column prop="course.id" label="ID" width="75"></el-table-column>
      <el-table-column prop="course.cname" label="课程名称" width="225"></el-table-column>
      <el-table-column prop="course.teacher" label="授课教师" width="175"></el-table-column>
      <el-table-column prop="course.address" label="教学地点" width="300"></el-table-column>
      <el-table-column prop="course.num" label="当前选课人数" width="150"></el-table-column>
      <el-table-column prop="course.stock" label="课程容量" width="120"></el-table-column>
      <el-table-column label="选课状态" width="150">
        <template v-slot="scope">
          <span :style="{ color: scope.row.status === 1 ? 'green' : 'red' }">
            {{ scope.row.status === 1 ? '已选课' : '已退选' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button
            @click="deselectCourse(scope.row.cid)"
            type="danger"
            size="small"
            class="deselect-course-button"
          >
            退选
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
      class="pagination-component"
      style="text-align: center; margin-top: 20px;"
    ></el-pagination>
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
      cname: '', // 假设你有一个输入框或下拉框来选择课程名称
    };
  },
  methods: {
    async fetchStudentCourses() {
      try {
        const response3 = await axios.get('/getRoleMessage');
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
        const response = await axios.post(
          '/student/deleteMyCourse',
          null,
          {
            params: {
              sid: this.id,
              cid: courseId,
            },
          }
        );
        if (response.data && response.data.code === 1) {
          this.$message.success('退选成功');
          this.fetchStudentCourses(); // 重新获取课程列表，更新选课记录
        } else {
          this.$message.error(response.data.message || '退选失败');
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
/* 表格样式调整 */
.course-record-table .cell {
  text-align: left; /* 保证所有单元格文字左对齐 */
  padding-left: 10px; /* 保证列内容与单元格左边距保持一致 */
}

.course-record-table th {
  text-align: left; /* 表头文字左对齐 */
  padding-left: 10px; /* 保证表头文字与单元格内容一致 */
}

/* 调整“退选”按钮的样式 */
.deselect-course-button {
  margin-left: -10px; /* 微调按钮位置 */
}

/* 翻页组件样式 */
.pagination-component {
  margin-top: 35px; /* 调整翻页组件与表格之间的间距 */
  text-align: center; /* 保持居中对齐 */
}

/* 自定义分页组件样式 */
::v-deep(.pagination-component .el-pager li) {
  background-color: #8b007a !important; /* 页码背景颜色 */
  border-color: #8b007a !important; /* 页码边框颜色 */
  color: white !important; /* 页码文字颜色保持白色 */
  border-radius: 5px; /* 设置圆角 */
}

::v-deep(.pagination-component .el-pagination__button) {
  background-color: #8b007a !important; /* 翻页按钮背景颜色 */
  border-color: #8b007a !important; /* 翻页按钮边框颜色 */
  color: white !important; /* 翻页按钮文字颜色 */
}
</style>