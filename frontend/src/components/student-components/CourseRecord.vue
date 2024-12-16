<template>
    <div>
      <el-table :data="studentCourses" style="width: 100%">
        <el-table-column prop="id" label="ID" width="50"></el-table-column>
        <el-table-column prop="sid" label="学生ID" width="100"></el-table-column>
        <el-table-column prop="cid" label="课程ID" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="100"></el-table-column>
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
    name: 'CourseRecord',
    data() {
      return {
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
    },
    mounted() {
      this.fetchStudentCourses();
    },
  };
  </script>
  
  <style scoped>
  /* 样式 */
  </style>