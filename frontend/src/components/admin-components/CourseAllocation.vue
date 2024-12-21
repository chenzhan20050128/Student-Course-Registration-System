<template>
  <div>
    <h2>课程分配管理</h2>
    <!-- 添加课程分配记录按钮 -->
    <el-button type="primary" @click="openDialog('add')" class="add-course-allocation-button">添加课程分配记录</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>

    <!-- 课程分配记录表格 -->
    <div class="table-container">
      <el-table
        :data="courseAllocationList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="teacherName" label="教师"></el-table-column>
        <el-table-column prop="phone" label="手机"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="courseName" label="课程"></el-table-column>
        <el-table-column prop="address" label="地点"></el-table-column>
        <el-table-column prop="enrollment" label="选课人数/容量"></el-table-column>
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

    <!-- 添加/修改课程分配记录表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="600px">
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
              :key="course.id"
              :label="course.cname"
              :value="course.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveCourseAllocation" v-if="!isEditing">提交</el-button>
          <el-button type="primary" @click="updateCourseAllocation" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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
  }).then((response: { data: { code: number, msg: string, data: { list: any[], total: number } } }) => {
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
          courseName: course ? course.cname : '',
          address: course ? course.address : '',
          enrollment: course ? `${course.num}/${course.stock}` : '',
          major: course ? course.major : ''
        }
      })
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取课程分配记录失败')
    }
  })
}

const fetchTeachersAndCourses = () => {
  axios.get('/teacherCourse/preSaveTeacherCourse').then((response: { data: { code: number, msg: string, data: { teacherList: any[], courseList: any[] } } }) => {
    if (response.data.code === 1) {
      teacherList.value = response.data.data.teacherList
      courseList.value = response.data.data.courseList
      fetchCourseAllocations() // 确保在获取教师和课程列表后再获取课程分配记录
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
  axios.post('/teacherCourse/saveTeacherCourse', newCourseAllocation.value).then((response: { data: { code: number, msg: string } }) => {
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
  axios.post('/teacherCourse/updateTeacherCourse', newCourseAllocation.value).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      fetchCourseAllocations()
      ElMessage.success('课程分配记录更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '课程分配记录更新失败')
    }
  })
}

const handleDelete = (id: number) => {
  axios.get(`/teacherCourse/deleteTeacherCourse/${id}`).then((response: { data: { code: number, msg: string } }) => {
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
  axios.post('/teacherCourse/deleteBatchTeacherCourse', { ids }).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除课程分配记录成功')
      fetchCourseAllocations()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除课程分配记录失败')
    }
  })
}

const handleSelectionChange = (val: any) => {
  selectedCourseAllocations.value = val
}

const handlePageChange = (page: number) => {
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
.course-allocation-form {
  margin-bottom: 20px;
}
.add-course-allocation-button, .batch-delete-button, .confirm-batch-delete-button {
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