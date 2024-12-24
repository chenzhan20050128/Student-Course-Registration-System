<template>
  <div>
    <!-- 添加教师和批量删除按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog('add')" class="add-teacher-button">添加教师</el-button>
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

    <!-- 教师列表表格 -->
    <div class="table-container">
      <el-table
        :data="teacherList"
        style="width: 1280px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="tname" label="教师姓名" width="180"></el-table-column>
        <el-table-column prop="sex" label="性别" width="120"></el-table-column>
        <el-table-column prop="age" label="年龄" width="90"></el-table-column>
        <el-table-column prop="college" label="所在学院" width="300"></el-table-column>
        <el-table-column prop="major" label="专业" width="380"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              size="mini"
              type="primary"
              class="edit-teacher-button"
              @click="openDialog('edit', scope.row)"
            >
              修改
            </el-button>
            <el-button
              size="mini"
              type="danger"
              class="delete-teacher-button"
              @click="handleDelete(scope.row.id)"
            >
              删除
            </el-button>
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
        class="custom-pagination"
      >
      </el-pagination>
      <span class="total-pages">总页数：{{ Math.ceil(total / pageSize) }}</span>
    </div>

    <!-- 添加/修改教师表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
      <el-form :model="newTeacher" label-width="120px" class="teacher-form">
        <el-form-item label="姓名" required>
          <el-input v-model="newTeacher.tname"></el-input>
        </el-form-item>
        <el-form-item label="密码" required>
          <el-input v-model="newTeacher.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="性别" required>
          <el-select v-model="newTeacher.sex" placeholder="请选择性别">
            <el-option label="Male" value="Male"></el-option>
            <el-option label="Female" value="Female"></el-option>
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
        <el-form-item>
          <el-button type="primary" class="edit-button" @click="saveTeacher" v-if="!isEditing">提交</el-button>
          <el-button type="primary" class="edit-button" @click="updateTeacher" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/http'

const teacherList = ref([])
const majorList = ref([])
const collegeList = ref([])
const newTeacher = ref({ tname: '', password: '', sex: '', age: null, college: '', major: '' })
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const showBatchDelete = ref(false)
const selectedTeachers = ref([])
const isEditing = ref(false)
const showDialog = ref(false)
const dialogTitle = ref('')

const fetchTeachers = () => {
  axios.get('/teacher/listTeacher', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  }).then((response) => {
    if (response.data.code === 1) {
      teacherList.value = response.data.data.list
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取教师列表失败')
    }
  })
}

const fetchMajorsColleges = () => {
  axios.get('/student/preSaveStudent').then((response) => {
    if (response.data.code === 1) {
      majorList.value = response.data.data.listMajor
      collegeList.value = response.data.data.listCollege
    } else {
      ElMessage.error(response.data.msg || '获取专业和学院列表失败')
    }
  })
}

const openDialog = (action: string, teacher: any = null) => {
  if (action === 'add') {
    dialogTitle.value = '添加教师'
    newTeacher.value = { tname: '', password: '', sex: '', age: null, college: '', major: '' }
    isEditing.value = false
  } else if (action === 'edit') {
    dialogTitle.value = '修改教师'
    newTeacher.value = { ...teacher }
    isEditing.value = true
  }
  showDialog.value = true
}

const saveTeacher = () => {
  axios.post('/teacher/saveTeacher', newTeacher.value).then((response) => {
    if (response.data.code === 1) {
      fetchTeachers()
      ElMessage.success('教师添加成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '教师添加失败')
    }
  })
}

const updateTeacher = () => {
  axios.post('/teacher/updateTeacher', newTeacher.value).then((response) => {
    if (response.data.code === 1) {
      fetchTeachers()
      ElMessage.success('教师更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '教师更新失败')
    }
  })
}

const handleDelete = (id: number) => {
  axios.get(`/teacher/deleteTeacher/${id}`).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('教师删除成功')
      fetchTeachers()
    } else {
      ElMessage.error(response.data.msg || '教师删除失败')
    }
  })
}

const confirmBatchDelete = () => {
  const ids = selectedTeachers.value.map(teacher => teacher.id).join(',')
  axios.post('/teacher/deleteBatchTeacher', { ids }).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除教师成功')
      fetchTeachers()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除教师失败')
    }
  })
}

const handleSelectionChange = (val: any) => {
  selectedTeachers.value = val
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchTeachers()
}

const toggleBatchDelete = () => {
  showBatchDelete.value = !showBatchDelete.value
  selectedTeachers.value = []
}

fetchTeachers()
fetchMajorsColleges()
</script>

<style scoped>
/* 操作按钮容器 */
.action-buttons {
  display: flex;
  margin-bottom: 15px;
  margin-left: 10px; /* 与表格左端对齐 */
}

/* 按钮样式调整 */
.add-teacher-button {
  background-color: #8b007a !important; /* 添加教师按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.add-teacher-button:hover {
  background-color: #a70c94 !important; /* 悬停颜色 */
  border-color: #a70c94 !important;
}

.edit-teacher-button {
  background-color: #8b007a !important; /* 修改按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.edit-teacher-button:hover {
  background-color: #a70c94 !important; /* 修改按钮悬停颜色 */
  border-color: #a70c94 !important;
}

.delete-teacher-button {
  background-color: #f56c6c !important; /* 删除按钮背景颜色 */
  border-color: #f56c6c !important;
  color: white !important;
}

.delete-teacher-button:hover {
  background-color: #f78989 !important; /* 删除按钮悬停颜色 */
  border-color: #f78989 !important;
}

/* 表格容器 */
.table-container {
  width: 88%;
}

/* 分页组件样式 */
.pagination-container {
  display: flex;
  justify-content: start;
  align-items: center;
  margin-top: 20px;
}

.total-pages {
  font-size: 14px;
  color: #333;
  margin-left: 20px;
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