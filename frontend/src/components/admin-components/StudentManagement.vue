<template>
  <div>
    <h2>学生管理</h2>
    <!-- 添加学生按钮 -->
    <el-button type="primary" @click="openDialog('add')" class="add-student-button">添加学生</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>

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
        <el-table-column prop="college" label="学院"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="mini" type="primary" @click="openDialog('edit', scope.row)">修改</el-button>
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
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
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
          <el-button type="primary" @click="saveStudent" v-if="!isEditing">提交</el-button>
          <el-button type="primary" @click="updateStudent" v-if="isEditing">修改</el-button>
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