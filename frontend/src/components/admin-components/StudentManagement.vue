<template>
  <div>
    <!-- 添加学生和批量删除按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog('add')" class="add-student-button">添加学生</el-button>
      <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
      <el-button
        type="danger"
        @click="confirmBatchDelete"
        v-if="showBatchDelete"
        class="confirm-batch-delete-button"
      >
        确定批量删除
      </el-button>
    </div>

    <!-- 学生列表表格 -->
    <div class="table-container">
      <el-table
        :data="studentList"
        style="width: 1280px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="sname" label="学生姓名" width="180"></el-table-column>
        <el-table-column prop="sex" label="性别" width="120"></el-table-column>
        <el-table-column prop="age" label="年龄" width="90"></el-table-column>
        <el-table-column prop="college" label="所在学院" width="300"></el-table-column>
        <el-table-column prop="major" label="专业" width="380"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="mini" type="primary" class="edit-button" @click="openDialog('edit', scope.row)">修改</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        background
        @current-change="handlePageChange"
        :current-page="pageNum"
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="total"
        class="pagination"
      >
      </el-pagination>
      <span class="total-pages">总页数：{{ Math.ceil(total / pageSize) }}</span>
    </div>

    <!-- 添加/修改学生表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
      <el-form :model="newStudent" label-width="120px" class="student-form">
        <el-form-item label="姓名" required>
          <el-input v-model="newStudent.sname"></el-input>
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="newStudent.password" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="性别" required>
          <el-select v-model="newStudent.sex" placeholder="请选择性别">
            <el-option label="Male" value="Male"></el-option>
            <el-option label="Female" value="Female"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄" required>
          <el-input v-model="newStudent.age" type="number"></el-input>
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
        <el-form-item>
          <el-button type="primary" class="edit-button" @click="saveStudent" v-if="!isEditing">提交</el-button>
          <el-button type="primary" class="edit-button" @click="updateStudent" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/http'

const studentList = ref([])
const majorList = ref([])
const collegeList = ref([])
const newStudent = ref({ sname: '', password: '', sex: '', age: null, college: '', major: '' })
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const showBatchDelete = ref(false)
const selectedStudents = ref([])
const isEditing = ref(false)
const showDialog = ref(false)
const dialogTitle = ref('')

const fetchStudents = () => {
  axios.get('/student/listStudent', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  }).then((response: { data: { code: number, msg: string, data: { list: any[], total: number } } }) => {
    if (response.data.code === 1) {
      studentList.value = response.data.data.list
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取学生列表失败')
    }
  })
}

const fetchMajorsColleges = () => {
  axios.get('/student/preSaveStudent').then((response: { data: { code: number, msg: string, data: { listMajor: any[], listCollege: any[] } } }) => {
    if (response.data.code === 1) {
      majorList.value = response.data.data.listMajor
      collegeList.value = response.data.data.listCollege
    } else {
      ElMessage.error(response.data.msg || '获取专业和学院列表失败')
    }
  })
}

const openDialog = (action: string, student: any = null) => {
  if (action === 'add') {
    dialogTitle.value = '添加学生'
    newStudent.value = { sname: '', password: '', sex: '', age: null, college: '', major: '' }
    isEditing.value = false
  } else if (action === 'edit') {
    dialogTitle.value = '修改学生'
    newStudent.value = { ...student }
    isEditing.value = true
  }
  showDialog.value = true
}

const saveStudent = () => {
  axios.post('/student/saveStudent', newStudent.value).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      fetchStudents()
      ElMessage.success('学生添加成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '学生添加失败')
    }
  })
}

const updateStudent = () => {
  axios.post('/student/updateStudent', newStudent.value).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      fetchStudents()
      ElMessage.success('学生更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '学生更新失败')
    }
  })
}

const handleDelete = (id: number) => {
  axios.get(`/student/deleteStudent/${id}`).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      ElMessage.success('学生删除成功')
      fetchStudents()
    } else {
      ElMessage.error(response.data.msg || '学生删除失败')
    }
  })
}

const confirmBatchDelete = () => {
  const ids = selectedStudents.value.map(student => student.id).join(',')
  axios.post('/student/deleteBatchStudent', { ids }).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除学生成功')
      fetchStudents()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除学生失败')
    }
  })
}

const handleSelectionChange = (val: any) => {
  selectedStudents.value = val
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchStudents()
}

const toggleBatchDelete = () => {
  showBatchDelete.value = !showBatchDelete.value
  selectedStudents.value = []
}

fetchStudents()
fetchMajorsColleges()
</script>

<style scoped>
/* 操作按钮容器 */
.action-buttons {
  display: flex;
  margin-bottom: 15px;
  margin-left: 10px; /* 与表格左端对齐 */
}

/* 添加学生按钮样式 */
.add-student-button {
  background-color: #8b007a !important; /* 默认颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.add-student-button:hover {
  background-color: #a70c94 !important; /* 悬停颜色 */
  border-color: #a70c94 !important;
}

/* 操作按钮样式 */
.batch-delete-button,
.confirm-batch-delete-button {
  margin-right: 0px;
}

/* 表格容器 */
.table-container {
  width: 88%;
}

/* 分页组件容器样式 */
.pagination-container {
  display: flex;
  justify-content: start; /* 左右对齐，翻页组件在左，总页数在右 */
  align-items: center;
  margin-top: 20px;
  width: 100%;
}

/* 总页数文字样式 */
.total-pages {
  font-size: 14px;
  color: #333;
  margin-left: 20px;
}

/* 自定义分页组件样式 */
/* 选中页码（当前页码）样式 */
::v-deep(.pagination .el-pager li:not(.disabled).active) {
  background-color: #8b007a !important; /* 当前页码背景颜色 */
  border-color: #8b007a !important; /* 当前页码边框颜色 */
  color: white !important; /* 当前页码文字颜色 */
  font-weight: bold; /* 当前页码文字加粗 */
  border-radius: 5px; /* 设置圆角 */
}
/* 鼠标悬停页码样式 */
::v-deep(.pagination .el-pager li:hover) {
  color: #8b007a !important; /* 悬停时文字变为紫色 */
}

/* 表格中“修改”按钮的样式 */
.edit-button {
  background-color: #8b007a !important; /* 紫色背景 */
  border-color: #8b007a !important; /* 紫色边框 */
  color: white !important; /* 文字颜色为白色 */
}

.edit-button:hover {
  background-color: #a70c94 !important; /* 悬停时背景颜色稍微变亮 */
  border-color: #a70c94 !important; /* 悬停时边框颜色稍微变亮 */
}
</style>