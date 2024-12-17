<template>
  <div>
    <h2>教师管理</h2>
    <!-- 添加教师按钮 -->
    <el-button type="primary" @click="toggleForm" class="add-teacher-button">添加教师</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>
    <!-- 添加教师表单 -->
    <el-form v-if="showForm" :model="newTeacher" label-width="120px" class="teacher-form">
      <el-form-item label="教师名" required>
        <el-input v-model="newTeacher.tname"></el-input>
      </el-form-item>
      <el-form-item label="性别" required>
        <el-select v-model="newTeacher.sex" placeholder="请选择性别">
          <el-option label="男" value="男"></el-option>
          <el-option label="女" value="女"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="年龄" required>
        <el-input v-model="newTeacher.age" type="number"></el-input>
      </el-form-item>
      <el-form-item label="学院" required>
        <el-select v-model="newTeacher.college" placeholder="请选择学院">
          <el-option
            v-for="college in collegeList"
            :key="college.id"
            :label="college.cname"
            :value="college.cname"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="专业" required>
        <el-select v-model="newTeacher.major" placeholder="请选择专业">
          <el-option
            v-for="major in majorList"
            :key="major.id"
            :label="major.mname"
            :value="major.mname"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="密码" required>
        <el-input v-model="newTeacher.password" type="password" placeholder="请输入密码" show-password></el-input>
      </el-form-item>
      <el-form-item label="教师图片">
        <el-input v-model="newTeacher.timage"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveTeacher">提交</el-button>
        <el-button type="primary" @click="updateTeacher" v-if="isEditing">修改</el-button>
      </el-form-item>
    </el-form>

    <!-- 教师列表表格 -->
    <div class="table-container">
      <el-table
        :data="teacherList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="tname" label="教师名"></el-table-column>
        <el-table-column prop="sex" label="性别"></el-table-column>
        <el-table-column prop="age" label="年龄"></el-table-column>
        <el-table-column prop="college" label="学院"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
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
  name: 'TeacherManagement',
  data() {
    return {
      teacherList: [], // 教师列表
      collegeList: [], // 学院列表
      majorList: [], // 专业列表
      newTeacher: {
        tname: '',
        sex: '',
        age: 0,
        college: '',
        major: '',
        password: '',
        timage: '',
      },
      pageNum: 1, // 当前页码
      pageSize: 6, // 每页显示条数
      total: 0, // 总条数
      showForm: false, // 控制表单显示和隐藏
      isEditing: false, // 控制是否为编辑状态
      showBatchDelete: false, // 控制批量删除模式
      selectedTeachers: [], // 选中的教师
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
      this.selectedTeachers = []; // 重置选中的教师
    },
    // 获取教师列表
    fetchTeachers() {
      axios.get('/teacher/listTeacher', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
      }).then((response) => {
        if (response.data.code === 1) {
          this.teacherList = response.data.data.list;
          this.total = response.data.data.total;
        } else {
          this.$message.error(response.data.msg || '获取教师列表失败');
        }
      });
    },
    // 获取专业和学院列表
    fetchMajorsColleges() {
      axios.get('/student/preSaveStudent').then((response) => {
        if (response.data.code === 1) {
          this.majorList = response.data.data.listMajor;
          this.collegeList = response.data.data.listCollege;
        } else {
          this.$message.error(response.data.msg || '获取专业和学院列表失败');
        }
      });
    },
    // 添加教师
    saveTeacher() {
      axios.post('/teacher/saveTeacher', this.newTeacher).then((response) => {
        if (response.data.code === 1) {
          this.fetchTeachers(); // 重新获取教师列表
          this.$message.success('教师添加成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
        } else {
          this.$message.error(response.data.msg || '教师添加失败');
        }
      });
    },
    // 更新教师
    updateTeacher() {
      axios.post('/teacher/updateTeacher', this.newTeacher).then((response) => {
        if (response.data.code === 1) {
          this.fetchTeachers(); // 重新获取教师列表
          this.$message.success('教师更新成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
          this.isEditing = false; // 重置编辑状态
        } else {
          this.$message.error(response.data.msg || '教师更新失败');
        }
      });
    },
    // 重置表单
    resetForm() {
      this.newTeacher = {
        tname: '',
        sex: '',
        age: 0,
        college: '',
        major: '',
        password: '',
        timage: '',
      };
    },
    // 处理分页变化
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchTeachers();
    },
    // 处理修改操作
    handleEdit(id) {
      // 调用 /preUpdateTeacher/{id} 接口
      axios.get(`/teacher/preUpdateTeacher/${id}`).then((response) => {
        if (response.data.code === 1) {
          const data = response.data.data;
          this.newTeacher = data.teacher;
          this.collegeList = data.collegeList;
          this.majorList = data.majorList;
          this.showForm = true; // 显示表单
          this.isEditing = true; // 设置编辑状态
        } else {
          this.$message.error(response.data.msg || '获取教师信息失败');
        }
      });
    },
    // 处理删除操作
    handleDelete(id) {
      // 调用 /deleteTeacher/{id} 接口
      axios.get(`/teacher/deleteTeacher/${id}`).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('教师删除成功');
          this.fetchTeachers(); // 重新获取教师列表
        } else {
          this.$message.error(response.data.msg || '教师删除失败');
        }
      });
    },
    // 处理批量删除操作
    confirmBatchDelete() {
      const ids = this.selectedTeachers.map(teacher => teacher.id).join(',');
      axios.post('/teacher/deleteBatchTeacher', { ids }).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('批量删除教师成功');
          this.fetchTeachers(); // 重新获取教师列表
          this.showBatchDelete = false; // 退出批量删除模式
        } else {
          this.$message.error(response.data.msg || '批量删除教师失败');
        }
      });
    },
    // 处理选择变化
    handleSelectionChange(val) {
      this.selectedTeachers = val;
    },
  },
  mounted() {
    this.fetchTeachers();
    this.fetchMajorsColleges();
  },
};
</script>

<style scoped>
.teacher-form {
  margin-bottom: 20px;
}
.add-teacher-button, .batch-delete-button, .confirm-batch-delete-button {
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