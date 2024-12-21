<template>
  <div>
    <h2>学生选课管理</h2>
    <!-- 添加选课记录按钮 -->
    <el-button type="primary" @click="openDialog('add')" class="add-student-course-button">添加选课记录</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>

    <!-- 学生选课记录表格 -->
    <div class="table-container">
      <el-table
        :data="studentCourseList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="studentName" label="学生"></el-table-column>
        <el-table-column prop="college" label="学院"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
        <el-table-column prop="course.cname" label="课程"></el-table-column>
        <el-table-column prop="course.address" label="上课地点"></el-table-column>
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

    <!-- 添加/修改选课记录表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="600px">
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
              :key="course.id"
              :label="course.cname"
              :value="course.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveStudentCourse" v-if="!isEditing">提交</el-button>
          <el-button type="primary" @click="updateStudentCourse" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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
  }).then((response: { data: { code: number, msg: string, data: { list: any[], total: number } } }) => {
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
  axios.get('/studentCourse/preSaveStudentCourse').then((response: { data: { code: number, msg: string, data: { studentList: any[], courseList: any[] } } }) => {
    if (response.data.code === 1) {
      studentList.value = response.data.data.studentList
      courseList.value = response.data.data.courseList
      fetchStudentCourses() // 确保在获取学生和课程列表后再获取选课记录
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
  axios.post('/studentCourse/saveStudentCourse', newStudentCourse.value).then((response: { data: { code: number, msg: string } }) => {
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
  axios.post('/studentCourse/updateStudentCourse', newStudentCourse.value).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      fetchStudentCourses()
      ElMessage.success('选课记录更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '选课记录更新失败')
    }
  })
}

const handleDelete = (id: number) => {
  axios.get(`/studentCourse/deleteStudentCourse/${id}`).then((response: { data: { code: number, msg: string } }) => {
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
  axios.post('/studentCourse/deleteBatchStudentCourse', { ids }).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除选课记录成功')
      fetchStudentCourses()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除选课记录失败')
    }
  })
}

const handleSelectionChange = (val: any) => {
  selectedStudentCourses.value = val
}

const handlePageChange = (page: number) => {
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
.student-course-form {
  margin-bottom: 20px;
}
.add-student-course-button, .batch-delete-button, .confirm-batch-delete-button {
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