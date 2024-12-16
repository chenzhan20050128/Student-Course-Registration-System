<template>
  <div>
    <h2>课程管理</h2>
    <!-- 添加课程按钮 -->
    <el-button type="primary" @click="toggleForm" class="add-course-button">添加课程</el-button>
    <!-- 添加课程表单 -->
    <el-form v-if="showForm" :model="newCourse" label-width="120px" class="course-form">
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
        <el-button type="primary" @click="saveCourse">提交</el-button>
        <el-button type="primary" @click="updateCourse" v-if="isEditing">修改</el-button>
      </el-form-item>
    </el-form>

    <!-- 课程列表表格 -->
    <div class="table-container">
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
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="mini" type="primary"@click="handleEdit(scope.row.id)">修改</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        @current-change="handlePageChange"
        :current-page="pageNum"
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="total"
        class="pagination"
      >
      </el-pagination>
      <span class="total-pages">总页数: {{ Math.ceil(total / pageSize) }}</span>
    </div>
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
      showForm: false, // 控制表单显示和隐藏
      isEditing: false, // 控制是否为编辑状态
    };
  },
  methods: {
    // 切换表单显示和隐藏
    toggleForm() {
      this.showForm = !this.showForm;
      this.isEditing = false; // 重置编辑状态
    },
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
          this.showForm = false; // 提交后隐藏表单
        } else {
          this.$message.error(response.data.msg || '课程添加失败');
        }
      });
    },
    // 更新课程
    updateCourse() {
      axios.post('/course/updateCourse', this.newCourse).then((response) => {
        if (response.data.code === 1) {
          this.fetchCourses(); // 重新获取课程列表
          this.$message.success('课程更新成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
          this.isEditing = false; // 重置编辑状态
        } else {
          this.$message.error(response.data.msg || '课程更新失败');
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
    // 处理修改操作
    handleEdit(id) {
      // 调用 /preUpdateCourse/{id} 接口
      axios.get(`/course/preUpdateCourse/${id}`).then((response) => {
        if (response.data.code === 1) {
          const data = response.data.data;
          this.newCourse = data.course;
          this.majorList = data.majorList;
          this.showForm = true; // 显示表单
          this.isEditing = true; // 设置编辑状态
        } else {
          this.$message.error(response.data.msg || '获取课程信息失败');
        }
      });
    },
    // 处理删除操作
    handleDelete(id) {
      // 调用 /deleteCourse/{id} 接口
      axios.get(`/course/deleteCourse/${id}`).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('课程删除成功');
          this.fetchCourses(); // 重新获取课程列表
        } else {
          this.$message.error(response.data.msg || '课程删除失败');
        }
      });
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
.add-course-button {
  margin-bottom: 20px;
}
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}
.total-pages {
  margin-left: 20px;
}
</style>