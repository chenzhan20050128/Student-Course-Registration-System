<template>
  <div>
    <!-- 添加课程分配记录和批量删除按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog('add')" class="add-course-allocation-button">添加课程分配记录</el-button>
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

    <!-- 课程分配记录表格 -->
    <div class="table-container">
      <el-table
        :data="courseAllocationList"
        style="width: 1280px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="teacherName" label="教师姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" width="150"></el-table-column>
        <el-table-column prop="address" label="上课地点" width="150"></el-table-column>
        <el-table-column prop="enrollment" label="选课人数/容量" width="120"></el-table-column>
        <el-table-column prop="major" label="专业" width="260"></el-table-column>
        <el-table-column label="操作" width="200">
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

    <!-- 添加/修改课程分配记录表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
      <el-form :model="newCourseAllocation" label-width="120px" class="course-allocation-form">
        <el-form-item label="教师" required>
          <el-select v-model="newCourseAllocation.tid" placeholder="请选择教师">
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.tname"
              :value="teacher.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程" required>
          <el-select v-model="newCourseAllocation.cid" placeholder="请选择课程">
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="edit-button" @click="saveCourseAllocation" v-if="!isEditing">提交</el-button>
          <el-button type="primary" class="edit-button" @click="updateCourseAllocation" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/http'

const courseAllocationList = ref([])
const teacherList = ref([])
const courseList = ref([])
const newCourseAllocation = ref({ id: null, tid: null, cid: null })
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const showBatchDelete = ref(false)
const selectedCourseAllocations = ref([])
const isEditing = ref(false)
const showDialog = ref(false)
const dialogTitle = ref('')

const fetchCourseAllocations = () => {
  axios.get('/teacherCourse/listTeacherCourse', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  }).then((response) => {
    if (response.data.code === 1) {
      const courseAllocationData = response.data.data.list
      courseAllocationList.value = courseAllocationData.map((item) => {
        const teacher = teacherList.value.find(teacher => teacher.id === item.tid)
        const course = courseList.value.find(course => course.id === item.cid)
        return {
          ...item,
          teacherName: teacher ? teacher.tname : '',
          phone: teacher ? teacher.phone : '',
          email: teacher ? teacher.email : '',
          courseName: course ? course.courseName : '',
          campus: course ? course.campus : '',
          enrollment: course ? `${course.enrolledCount}/${course.capacity}` : '',
          college: course ? course.college : ''
        }
      })
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取课程分配记录失败')
    }
  })
}

const fetchTeachersAndCourses = () => {
  axios.get('/teacherCourse/preSaveTeacherCourse').then((response) => {
    if (response.data.code === 1) {
      teacherList.value = response.data.data.teacherList
      courseList.value = response.data.data.courseList
      fetchCourseAllocations()
    } else {
      ElMessage.error(response.data.msg || '获取教师和课程列表失败')
    }
  })
}

const openDialog = (action: string, courseAllocation: any = null) => {
  if (action === 'add') {
    dialogTitle.value = '添加课程分配记录'
    newCourseAllocation.value = { tid: null, cid: null }
    isEditing.value = false
  } else if (action === 'edit') {
    dialogTitle.value = '修改课程分配记录'
    newCourseAllocation.value = {
      id: courseAllocation.id,
      tid: courseAllocation.tid,
      cid: courseAllocation.cid
    }
    isEditing.value = true
  }
  showDialog.value = true
}

const saveCourseAllocation = () => {
  axios.post('/teacherCourse/saveTeacherCourse', newCourseAllocation.value).then((response) => {
    if (response.data.code === 1) {
      fetchCourseAllocations()
      ElMessage.success('课程分配记录添加成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '课程分配记录添加失败')
    }
  })
}

const updateCourseAllocation = () => {
  axios.post('/teacherCourse/updateTeacherCourse', newCourseAllocation.value).then((response) => {
    if (response.data.code === 1) {
      fetchCourseAllocations()
      ElMessage.success('课程分配记录更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '课程分配记录更新失败')
    }
  })
}

const handleDelete = (id) => {
  axios.get(`/teacherCourse/deleteTeacherCourse/${id}`).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('课程分配记录删除成功')
      fetchCourseAllocations()
    } else {
      ElMessage.error(response.data.msg || '课程分配记录删除失败')
    }
  })
}

const confirmBatchDelete = () => {
  const ids = selectedCourseAllocations.value.map(courseAllocation => courseAllocation.id).join(',')
  axios.post('/teacherCourse/deleteBatchTeacherCourse', { ids }).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除课程分配记录成功')
      fetchCourseAllocations()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除课程分配记录失败')
    }
  })
}

const handleSelectionChange = (val) => {
  selectedCourseAllocations.value = val
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchCourseAllocations()
}

const toggleBatchDelete = () => {
  showBatchDelete.value = !showBatchDelete.value
  selectedCourseAllocations.value = []
}

fetchTeachersAndCourses()
</script>

<style scoped>
/* 操作按钮容器 */
.action-buttons {
  display: flex;
  margin-bottom: 15px;
  margin-left: 10px; /* 与表格左端对齐 */
}

/* 按钮样式 */
.add-course-allocation-button {
  background-color: #8b007a !important; /* 添加按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.add-course-allocation-button:hover {
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