<template>
  <div>
    <!-- 添加选课记录和批量删除按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog('add')" class="add-student-course-button">添加选课记录</el-button>
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

    <!-- 学生选课记录表格 -->
    <div class="table-container">
      <el-table
        :data="studentCourseList"
        style="width: 1280px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
        <el-table-column prop="college" label="所在学院" width="240"></el-table-column>
        <el-table-column prop="major" label="专业" width="300"></el-table-column>
        <el-table-column prop="course.courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="course.campus" label="上课地点" width="260"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button size="mini" type="primary" class="edit-button" @click="openDialog('edit', scope.row)">修改</el-button>
            <el-button size="mini" type="danger" class="delete-button" @click="handleDelete(scope.row.id)">删除</el-button>
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

    <!-- 添加/修改选课记录表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
      <el-form :model="newStudentCourse" label-width="120px" class="student-course-form">
        <el-form-item label="学生" required>
          <el-select v-model="newStudentCourse.sid" placeholder="请选择学生">
            <el-option
              v-for="student in studentList"
              :key="student.id"
              :label="student.sname"
              :value="student.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程" required>
          <el-select v-model="newStudentCourse.cid" placeholder="请选择课程">
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="edit-button" @click="saveStudentCourse" v-if="!isEditing">提交</el-button>
          <el-button type="primary" class="edit-button" @click="updateStudentCourse" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/http'

const studentCourseList = ref([])
const studentList = ref([])
const courseList = ref([])
const newStudentCourse = ref({ id: null, sid: null, cid: null })
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const showBatchDelete = ref(false)
const selectedStudentCourses = ref([])
const isEditing = ref(false)
const showDialog = ref(false)
const dialogTitle = ref('')

const fetchStudentCourses = () => {
  axios.get('/studentCourse/listStudentCourse', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  }).then((response) => {
    if (response.data.code === 1) {
      const studentCourseData = response.data.data.list
      studentCourseList.value = studentCourseData.map((item) => {
        const student = studentList.value.find(student => student.id === item.sid)
        return {
          ...item,
          studentName: student ? student.sname : '',
          college: student ? student.college : '',
          major: student ? student.major : ''
        }
      })
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取选课记录失败')
    }
  })
}

const fetchStudentsAndCourses = () => {
  axios.get('/studentCourse/preSaveStudentCourse').then((response) => {
    if (response.data.code === 1) {
      studentList.value = response.data.data.studentList
      courseList.value = response.data.data.courseList
      fetchStudentCourses()
    } else {
      ElMessage.error(response.data.msg || '获取学生和课程列表失败')
    }
  })
}

const openDialog = (action: string, studentCourse: any = null) => {
  if (action === 'add') {
    dialogTitle.value = '添加选课记录'
    newStudentCourse.value = { sid: null, cid: null }
    isEditing.value = false
  } else if (action === 'edit') {
    dialogTitle.value = '修改选课记录'
    newStudentCourse.value = {
      id: studentCourse.id,
      sid: studentCourse.sid,
      cid: studentCourse.cid
    }
    isEditing.value = true
  }
  showDialog.value = true
}

const saveStudentCourse = () => {
  axios.post('/studentCourse/saveStudentCourse', newStudentCourse.value).then((response) => {
    if (response.data.code === 1) {
      fetchStudentCourses()
      ElMessage.success('选课记录添加成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '选课记录添加失败')
    }
  })
}

const updateStudentCourse = () => {
  axios.post('/studentCourse/updateStudentCourse', newStudentCourse.value).then((response) => {
    if (response.data.code === 1) {
      fetchStudentCourses()
      ElMessage.success('选课记录更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '选课记录更新失败')
    }
  })
}

const handleDelete = (id) => {
  axios.get(`/studentCourse/deleteStudentCourse/${id}`).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('选课记录删除成功')
      fetchStudentCourses()
    } else {
      ElMessage.error(response.data.msg || '选课记录删除失败')
    }
  })
}

const confirmBatchDelete = () => {
  const ids = selectedStudentCourses.value.map(studentCourse => studentCourse.id).join(',')
  axios.post('/studentCourse/deleteBatchStudentCourse', { ids }).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除选课记录成功')
      fetchStudentCourses()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除选课记录失败')
    }
  })
}

const handleSelectionChange = (val) => {
  selectedStudentCourses.value = val
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchStudentCourses()
}

const toggleBatchDelete = () => {
  showBatchDelete.value = !showBatchDelete.value
  selectedStudentCourses.value = []
}

fetchStudentsAndCourses()
</script>

<style scoped>
/* 操作按钮容器 */
.action-buttons {
  display: flex;
  margin-bottom: 15px;
  margin-left: 10px; /* 与表格左端对齐 */
}

/* 按钮样式 */
.add-student-course-button {
  background-color: #8b007a !important; /* 添加按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.add-student-course-button:hover {
  background-color: #a70c94 !important; /* 悬停颜色 */
  border-color: #a70c94 !important;
}

.edit-button {
  background-color: #8b007a !important; /* 修改按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.edit-button:hover {
  background-color: #a70c94 !important; /* 修改按钮悬停颜色 */
  border-color: #a70c94 !important;
}

.delete-button {
  background-color: #f56c6c !important; /* 删除按钮背景颜色 */
  border-color: #f56c6c !important;
  color: white !important;
}

.delete-button:hover {
  background-color: #f78989 !important; /* 删除按钮悬停颜色 */
  border-color: #f78989 !important;
}

/* 表格容器 */
.table-container {
  width: 88%;
}

/* 分页组件容器样式 */
.pagination-container {
  display: flex;
  justify-content: start; /* 左对齐 */
  align-items: center;
  margin-top: 20px;
}

/* 总页数文字样式 */
.total-pages {
  font-size: 14px;
  color: #333;
  margin-left: 20px;
}
</style>