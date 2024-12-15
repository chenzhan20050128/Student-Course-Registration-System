<template>
  <div>
    <h2>课程管理</h2>
    <!-- 添加课程表单 -->
    <el-form :model="newCourse" label-width="120px" class="course-form">
      <el-form-item label="课程名">
        <el-input v-model="newCourse.cname"></el-input>
      </el-form-item>
      <el-form-item label="专业">
        <el-select v-model="newCourse.major" placeholder="请选择专业">
          <el-option
            v-for="major in majorList"
            :key="major.id"
            :label="major.mname"
            :value="major.mname"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="教师">
        <el-input v-model="newCourse.teacher"></el-input>
      </el-form-item>
      <el-form-item label="地址">
        <el-input v-model="newCourse.address"></el-input>
      </el-form-item>
      <el-form-item label="选课容量">
        <el-input v-model="newCourse.num" type="number"></el-input>
      </el-form-item>
      <el-form-item label="学分">
        <el-input v-model="newCourse.credit" type="number"></el-input>
      </el-form-item>
      <el-form-item label="课程图片">
        <el-input v-model="newCourse.cimage"></el-input>
      </el-form-item>
      <el-form-item label="课程书籍">
        <el-input v-model="newCourse.cbook"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveCourse">添加课程</el-button>
      </el-form-item>
    </el-form>

    <!-- 课程列表表格 -->
    <el-table :data="courseList" style="width: 100%">
      <el-table-column prop="cname" label="课程名"></el-table-column>
      <el-table-column prop="major" label="专业"></el-table-column>
      <el-table-column prop="teacher" label="教师"></el-table-column>
      <el-table-column prop="address" label="地址"></el-table-column>
      <el-table-column
        label="选课人数/选课容量"
        :formatter="formatCourseCapacity"
      ></el-table-column>
      <el-table-column prop="credit" label="学分"></el-table-column>
      <el-table-column prop="cimage" label="课程图片"></el-table-column>
      <el-table-column prop="cbook" label="课程书籍"></el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <el-pagination
      @current-change="handlePageChange"
      :current-page="pageNum"
      :page-size="pageSize"
      layout="total, prev, pager, next"
      :total="total"
      class="pagination"
    >
      <template #total="{ total }">
        总页数: {{ Math.ceil(total / pageSize) }}
      </template>
    </el-pagination>
  </div>
</template>

<script>
import axios from '@/http';

export default {
  name: 'CourseManagement',
  data() {
    return {
      courseList: [], // 课程列表
      majorList: [], // 专业列表
      newCourse: {
        cname: '',
        major: '',
        teacher: '',
        address: '',
        num: 0, // 选课容量
        stock: 0, // 选课人数
        credit: '', // 学分
        cimage: '', // 课程图片
        cbook: '', // 课程书籍
      },
      pageNum: 1, // 当前页码
      pageSize: 6, // 每页显示条数
      total: 0, // 总条数
    };
  },
  methods: {
    // 获取课程列表
    fetchCourses() {
      axios.get('/course/listCourse', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
      }).then((response) => {
        if (response.data.code === 1) {
          this.courseList = response.data.data.list;
          this.total = response.data.data.total;
        } else {
          this.$message.error(response.data.msg || '获取课程列表失败');
        }
      });
    },
    // 获取专业列表
    fetchMajors() {
      axios.get('/course/preSaveCourse').then((response) => {
        if (response.data.code === 1) {
          this.majorList = response.data.data;
        } else {
          this.$message.error(response.data.msg || '获取专业列表失败');
        }
      });
    },
    // 添加课程
    saveCourse() {
      axios.post('/course/saveCourse', this.newCourse).then((response) => {
        if (response.data.code === 1) {
          this.fetchCourses(); // 重新获取课程列表
          this.$message.success('课程添加成功');
          this.resetForm();
        } else {
          this.$message.error(response.data.msg || '课程添加失败');
        }
      });
    },
    // 重置表单
    resetForm() {
      this.newCourse = {
        cname: '',
        major: '',
        teacher: '',
        address: '',
        num: 0, // 选课容量
        stock: 0, // 选课人数
        credit: '', // 学分
        cimage: '', // 课程图片
        cbook: '', // 课程书籍
      };
    },
    // 格式化课程容量
    formatCourseCapacity(row) {
      return `${row.stock} / ${row.num}`;
    },
    // 处理分页变化
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchCourses();
    },
  },
  mounted() {
    this.fetchCourses();
    this.fetchMajors();
  },
};
</script>

<style scoped>
.course-form {
  margin-bottom: 20px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>