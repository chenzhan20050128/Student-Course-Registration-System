<template>
  <div>
    <el-table :data="courses" style="width: 88%">
      <el-table-column prop="cid" label="ID" width="75"></el-table-column>
      <el-table-column prop="cname" label="课程名称" width="350"></el-table-column>
      <el-table-column prop="address" label="上课地点" width="175"></el-table-column>
      <el-table-column prop="num" label="选课人数" width="175"></el-table-column>
      <el-table-column prop="stock" label="课程容量" width="150"></el-table-column>
      <el-table-column prop="major" label="所属专业" width="350"></el-table-column>
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
  name: 'TeachingInfo',
  data() {
    return {
      courses: [],
      pageNum: 1,
      pageSize: 6,
      total: 0,
    };
  },
  methods: {
    async fetchCourses() {
      try {
        const response = await axios.get('/teacher/listMyCourse', {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
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