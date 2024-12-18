<template>
  <div>
    <el-table :data="studentCourses" style="width: 100%">
      <el-table-column prop="cid" label="课程ID" width="100"></el-table-column>
      <el-table-column prop="cname" label="课程名称" width="150"></el-table-column>
      <el-table-column prop="teacher" label="教师" width="100"></el-table-column>
      <el-table-column prop="address" label="上课地点" width="150"></el-table-column>
      <el-table-column prop="num" label="选课人数" width="100"></el-table-column>
      <el-table-column prop="stock" label="选课容量" width="100"></el-table-column>
      <el-table-column prop="status" label="选课状态" width="100"></el-table-column>
      <el-table-column label="操作" width="100">
        <template v-slot="scope">
          <el-button @click="deselectCourse(scope.row.cid)" type="danger" size="small">退选</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handlePageChange"
      style="text-align: center; margin-top: 20px;"
    ></el-pagination>
  </div>
</template>

<script>
import axios from '@/http';
import { useStudentStore } from '@/store/student';

export default {
  name: 'CourseRecord',
  data() {
    return {
      id: 0,
      studentCourses: [],
      pageNum: 1,
      pageSize: 6,
      total: 0,
      cname: '', // 假设你有一个输入框或下拉框来选择课程名称
    };
  },
  methods: {
    async fetchStudentCourses() {
      try {
        const response3 = await axios.get('/getRoleMessage')
        this.id = response3.data.data.id
        const response2 = await axios.get('/studentCourse/listStudentCourse', {
          params: {
            sid: this.id,
          },
        });
        console.log("response2",response2)
        const response = await axios.get('/student/listMyCourse', {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            cname: this.cname,
          },
        });
        if (response.data && response.data.data) {
          this.studentCourses = response.data.data.list;
          this.total = response.data.data.total;
        }
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
        const response = await axios.post('/student/deleteMyCourse',null, {params: {
          sid: this.id,
          cid: courseId,
        },
      });
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
/* 样式 */
</style>