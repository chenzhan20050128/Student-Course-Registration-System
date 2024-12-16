<template>
  <div>
    <el-table :data="courses" style="width: 100%">
      <el-table-column prop="id" label="ID" width="50"></el-table-column>
      <el-table-column prop="cname" label="课程名称" width="150"></el-table-column>
      <el-table-column prop="major" label="专业" width="100"></el-table-column>
      <el-table-column prop="teacher" label="教师" width="100"></el-table-column>
      <el-table-column prop="address" label="地址" width="150"></el-table-column>
      <el-table-column prop="num" label="选课容量" width="100"></el-table-column>
      <el-table-column prop="stock" label="选课人数" width="100"></el-table-column>
      <el-table-column prop="credit" label="学分" width="100"></el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handlePageChange"
    ></el-pagination>
  </div>
</template>

<script>
import axios from '@/http';

export default {
  name: 'CourseSelection',
  data() {
    return {
      courses: [],
      pageNum: 1,
      pageSize: 6,
      total: 0,
      major: '', // 假设你有一个输入框或下拉框来选择专业
    };
  },
  methods: {
    async fetchCourses() {
      try {
        const response2 = await axios.get('/student/preUpdateStudent/1'); 
        this.major = ("111",response2.data.data.listMajor[1].mname)

        const response = await axios.get('/student/listCourseByMajorName', {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            majorName : this.major,
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
  },
  mounted() {
    this.fetchCourses();
  },
};
</script>

<style scoped>
/* 样式 */
</style>