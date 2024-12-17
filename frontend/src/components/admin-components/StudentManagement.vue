<template>
  <div>
    <h2>学生管理</h2>
    <!-- 添加学生按钮 -->
    <el-button type="primary" @click="toggleForm" class="add-student-button">添加学生</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>
    <!-- 添加学生表单 -->
    <el-form v-if="showForm" :model="newStudent" label-width="120px" class="student-form">
      <el-form-item label="姓名" required>
        <el-input v-model="newStudent.sname"></el-input>
      </el-form-item>
      <el-form-item label="性别" required>
        <el-select v-model="newStudent.sex" placeholder="请选择性别">
          <el-option label="男" value="男"></el-option>
          <el-option label="女" value="女"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="年龄" required>
        <el-input v-model="newStudent.age" type="number"></el-input>
      </el-form-item>
      <el-form-item label="专业" required>
        <el-select v-model="newStudent.major" placeholder="请选择专业">
          <el-option
            v-for="major in majorList"
            :key="major.id"
            :label="major.mname"
            :value="major.mname"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="学院" required>
        <el-select v-model="newStudent.college" placeholder="请选择学院">
          <el-option
            v-for="college in collegeList"
            :key="college.id"
            :label="college.cname"
            :value="college.cname"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="密码" required>
        <el-input :type="password" 
          v-model="newStudent.password"
          placeholder="Please input password"
          show-password>
        </el-input>
      </el-form-item>
      <el-form-item label="学生图片">
        <el-input v-model="newStudent.simage"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveStudent">提交</el-button>
        <el-button type="primary" @click="updateStudent" v-if="isEditing">修改</el-button>
      </el-form-item>
    </el-form>

    <!-- 学生列表表格 -->
    <div class="table-container">
      <el-table
        :data="studentList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="sname" label="姓名"></el-table-column>
        <el-table-column prop="sex" label="性别"></el-table-column>
        <el-table-column prop="age" label="年龄"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
        <el-table-column prop="college" label="学院"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row.id)">修改</el-button>
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
  name: 'StudentManagement',
  data() {
    return {
      studentList: [], // 学生列表
      majorList: [], // 专业列表
      collegeList: [], // 学院列表
      newStudent: {
        sname: '',
        sex: '',
        age: 0,
        major: '',
        college: '',
        password: '',
        simage: '',
      },
      pageNum: 1, // 当前页码
      pageSize: 6, // 每页显示条数
      total: 0, // 总条数
      showForm: false, // 控制表单显示和隐藏
      isEditing: false, // 控制是否为编辑状态
      showBatchDelete: false, // 控制批量删除模式
      selectedStudents: [], // 选中的学生
      passwordFieldType: 'password', // 控制密码字段的类型
    };
  },
  methods: {
    // 切换表单显示和隐藏
    toggleForm() {
      this.showForm = !this.showForm;
      this.isEditing = false; // 重置编辑状态
    },
    // 切换批量删除模式
    toggleBatchDelete() {
      this.showBatchDelete = !this.showBatchDelete;
      this.selectedStudents = []; // 重置选中的学生
    },
    // 获取学生列表
    fetchStudents() {
      axios.get('/student/listStudent', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
      }).then((response) => {
        if (response.data.code === 1) {
          this.studentList = response.data.data.list;
          this.total = response.data.data.total;
        } else {
          this.$message.error(response.data.msg || '获取学生列表失败');
        }
      });
    },
    // 获取专业列表
    fetchMajors() {
      axios.get('/student/preSaveStudent').then((response) => {
        if (response.data.code === 1) {
          this.majorList = response.data.data.listMajor;
          this.collegeList = response.data.data.listCollege;
        } else {
          this.$message.error(response.data.msg || '获取专业列表失败');
        }
      });
    },
    // 添加学生
    saveStudent() {
      axios.post('/student/saveStudent', this.newStudent).then((response) => {
        if (response.data.code === 1) {
          this.fetchStudents(); // 重新获取学生列表
          this.$message.success('学生添加成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
        } else {
          this.$message.error(response.data.msg || '学生添加失败');
        }
      });
    },
    // 更新学生
    updateStudent() {
      axios.post('/student/updateStudent', this.newStudent).then((response) => {
        if (response.data.code === 1) {
          this.fetchStudents(); // 重新获取学生列表
          this.$message.success('学生更新成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
          this.isEditing = false; // 重置编辑状态
        } else {
          this.$message.error(response.data.msg || '学生更新失败');
        }
      });
    },
    // 重置表单
    resetForm() {
      this.newStudent = {
        sname: '',
        sex: '',
        age: 0,
        major: '',
        college: '',
        password: '',
        simage: '',
      };
    },
    // 切换密码字段的显示和隐藏
    togglePasswordVisibility() {
      this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
    },
    // 处理分页变化
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchStudents();
    },
    // 处理修改操作
    handleEdit(id) {
      // 调用 /preUpdateStudent/{id} 接口
      axios.get(`/student/preUpdateStudent/${id}`).then((response) => {
        if (response.data.code === 1) {
          const data = response.data.data;
          this.newStudent = data.student;
          this.majorList = data.listMajor;
          this.collegeList = data.listCollege;
          this.showForm = true; // 显示表单
          this.isEditing = true; // 设置编辑状态
        } else {
          this.$message.error(response.data.msg || '获取学生信息失败');
        }
      });
    },
    // 处理删除操作
    handleDelete(id) {
      // 调用 /deleteStudent/{id} 接口
      axios.get(`/student/deleteStudent/${id}`).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('学生删除成功');
          this.fetchStudents(); // 重新获取学生列表
        } else {
          this.$message.error(response.data.msg || '学生删除失败');
        }
      });
    },
    // 处理批量删除操作
    confirmBatchDelete() {
      const ids = this.selectedStudents.map(student => student.id).join(',');
      axios.post('/student/deleteBatchStudent', { ids }).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('批量删除学生成功');
          this.fetchStudents(); // 重新获取学生列表
          this.showBatchDelete = false; // 退出批量删除模式
        } else {
          this.$message.error(response.data.msg || '批量删除学生失败');
        }
      });
    },
    // 处理选择变化
    handleSelectionChange(val) {
      this.selectedStudents = val;
    },
  },
  mounted() {
    this.fetchStudents();
    this.fetchMajors();
  },
};
</script>

<style scoped>
.student-form {
  margin-bottom: 20px;
}
.add-student-button, .batch-delete-button, .confirm-batch-delete-button {
  margin-bottom: 20px;
  margin-right: 10px;
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