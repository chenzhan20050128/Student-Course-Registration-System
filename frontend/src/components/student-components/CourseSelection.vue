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
      <el-table-column label="操作" width="100">
        <template v-slot="scope">
          <el-button @click="selectCourse(scope.row.id)" type="primary" size="small">选择</el-button>
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
  name: 'CourseSelection',
  setup() {
    const studentStore = useStudentStore();
    return { studentStore };
  },
  data() {
    return {
      id : this.studentStore.student.id,
      courses: [],
      pageNum: 1,
      pageSize: 6,
      total: 0,
      majorName: '', // 假设你有一个输入框或下拉框来选择专业
    };
  },
  methods: {
    async fetchCourses() {
      try {
        const response2 = await axios.get(`/student/preUpdateStudent/${this.id}`); 
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
        const response = await axios.get(`/student/selectCourse/${courseId}`);
        console.log("111",response)
        if (response.data && response.data.code === 1) {
          this.$message.success('选课成功');
          this.fetchCourses(); // 重新获取课程列表，更新选课人数
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
/* 样式 */
</style>