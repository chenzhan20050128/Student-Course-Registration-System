<template>
    <div>
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="cid" label="课程ID" width="100"></el-table-column>
        <el-table-column prop="course.cname" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="course.address" label="上课地点" width="150"></el-table-column>
        <el-table-column prop="course.num" label="选课人数" width="100"></el-table-column>
        <el-table-column prop="course.stock" label="选课容量" width="100"></el-table-column>
        <el-table-column prop="teacher.major" label="所属专业" width="150"></el-table-column>
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
  
  export default {
    name: 'TeachingInfo',
    data() {
      return {
        courses: [],
        pageNum: 1,
        pageSize: 6,
        total: 0,
        cname: '', // 假设你有一个输入框或下拉框来选择课程名称
      };
    },
    methods: {
      async fetchCourses() {
        try {
          const response = await axios.get('/teacher/listMyCourse', {
            params: {
              pageNum: this.pageNum,
              pageSize: this.pageSize,
              cname: this.cname,
            },
          });
          console.log("response",response)
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