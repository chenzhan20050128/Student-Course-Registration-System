<template>
  <div>
    <h2>课程管理</h2>
    <!-- 添加课程按钮 -->
    <el-button type="primary" @click="openDialog('add')" class="add-course-button">添加课程</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>

    <!-- 课程列表表格 -->
    <div class="table-container">
      <el-table
        :data="courseList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="cname" label="课程名"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
        <el-table-column prop="teacher" label="教师"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="num" label="选课人数"></el-table-column>
        <el-table-column prop="stock" label="选课容量"></el-table-column>
        <el-table-column prop="credit" label="学分"></el-table-column>
        <el-table-column prop="cimage" label="课程图片"></el-table-column>
        <el-table-column prop="cbook" label="课程书籍"></el-table-column>
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

    <!-- 添加/修改课程表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="700px">
      <el-form :model="newCourse" label-width="120px" class="course-form">
        <el-form-item label="课程名" required>
          <el-input v-model="newCourse.cname"></el-input>
        </el-form-item>
        <el-form-item label="专业" required>
          <el-select v-model="newCourse.major" placeholder="请选择专业">
            <el-option
              v-for="major in majorList"
              :key="major.id"
              :label="major.mname"
              :value="major.mname"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教师" required>
          <el-input v-model="newCourse.teacher"></el-input>
        </el-form-item>
        <el-form-item label="地址" required>
          <el-input v-model="newCourse.address"></el-input>
        </el-form-item>
        <el-form-item label="选课人数" required>
          <el-input v-model="newCourse.num" type="number"></el-input>
        </el-form-item>
        <el-form-item label="选课容量" required>
          <el-input v-model="newCourse.stock" type="number"></el-input>
        </el-form-item>
        <el-form-item label="学分" required>
          <el-input v-model="newCourse.credit" type="number"></el-input>
        </el-form-item>
        <el-form-item label="课程图片" required>
          <el-input v-model="newCourse.cimage"></el-input>
        </el-form-item>
        <el-form-item label="课程书籍" required>
          <el-input v-model="newCourse.cbook"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveCourse" v-if="!isEditing">提交</el-button>
          <el-button type="primary" @click="updateCourse" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/http'

const courseList = ref([])
const majorList = ref([])
const newCourse = ref({ cname: '', major: '', teacher: '', address: '', num: null, stock: null, credit: null, cimage: '', cbook: '' })
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const showBatchDelete = ref(false)
const selectedCourses = ref([])
const isEditing = ref(false)
const showDialog = ref(false)
const dialogTitle = ref('')

const fetchCourses = () => {
  axios.get('/course/listCourse', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  }).then((response: { data: { code: number, msg: string, data: { list: any[], total: number } } }) => {
    if (response.data.code === 1) {
      courseList.value = response.data.data.list
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取课程列表失败')
    }
  })
}

const fetchMajors = () => {
  axios.get('/major/listMajor').then((response: { data: { code: number, msg: string, data: { list: any[] } } }) => {
    if (response.data.code === 1) {
      majorList.value = response.data.data.list
    } else {
      ElMessage.error(response.data.msg || '获取专业列表失败')
    }
  })
}

const openDialog = (action: string, course: any = null) => {
  if (action === 'add') {
    dialogTitle.value = '添加课程'
    newCourse.value = { cname: '', major: '', teacher: '', address: '', num: null, stock: null, credit: null, cimage: '', cbook: '' }
    isEditing.value = false
  } else if (action === 'edit') {
    dialogTitle.value = '修改课程'
    newCourse.value = { ...course }
    isEditing.value = true
  }
  showDialog.value = true
}

const saveCourse = () => {
  axios.post('/course/saveCourse', newCourse.value).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      fetchCourses()
      ElMessage.success('课程添加成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '课程添加失败')
    }
  })
}

const updateCourse = () => {
  axios.post('/course/updateCourse', newCourse.value).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      fetchCourses()
      ElMessage.success('课程更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '课程更新失败')
    }
  })
}

const handleDelete = (id: number) => {
  axios.get(`/course/deleteCourse/${id}`).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      ElMessage.success('课程删除成功')
      fetchCourses()
    } else {
      ElMessage.error(response.data.msg || '课程删除失败')
    }
  })
}

const confirmBatchDelete = () => {
  const ids = selectedCourses.value.map(course => course.id).join(',')
  axios.post('/course/deleteBatchCourse', { ids }).then((response: { data: { code: number, msg: string } }) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除课程成功')
      fetchCourses()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除课程失败')
    }
  })
}

const handleSelectionChange = (val: any) => {
  selectedCourses.value = val
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchCourses()
}

const toggleBatchDelete = () => {
  showBatchDelete.value = !showBatchDelete.value
  selectedCourses.value = []
}

fetchCourses()
fetchMajors()
</script>

<style scoped>
.course-form {
  margin-bottom: 20px;
}
.add-course-button, .batch-delete-button, .confirm-batch-delete-button {
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