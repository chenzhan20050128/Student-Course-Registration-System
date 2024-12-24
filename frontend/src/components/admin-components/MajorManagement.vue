<template>
  <div>
    <!-- 添加专业和批量删除按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog('add')" class="add-major-button">添加专业</el-button>
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

    <!-- 专业列表表格 -->
    <div class="table-container">
      <el-table
        :data="majorList"
        style="width: 1280px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="mname" label="专业名称" width="690"></el-table-column>
        <el-table-column prop="college" label="所属学院" width="380"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              size="mini"
              type="primary"
              class="edit-major-button"
              @click="openDialog('edit', scope.row)"
            >
              修改
            </el-button>
            <el-button
              size="mini"
              type="danger"
              class="delete-major-button"
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

    <!-- 添加/修改专业表单弹出框 -->
    <el-dialog v-model="showDialog" :title="dialogTitle" width="500px">
      <el-form :model="newMajor" label-width="120px" class="major-form">
        <el-form-item label="专业名" required>
          <el-input v-model="newMajor.mname"></el-input>
        </el-form-item>
        <el-form-item label="学院" required>
          <el-select v-model="newMajor.college" placeholder="请选择学院">
            <el-option
              v-for="college in collegeList"
              :key="college.id"
              :label="college.cname"
              :value="college.cname"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="edit-button" @click="saveMajor" v-if="!isEditing">提交</el-button>
          <el-button type="primary" class="edit-button" @click="updateMajor" v-if="isEditing">修改</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/http'

const majorList = ref([])
const collegeList = ref([])
const newMajor = ref({ mname: '', college: '' })
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const showBatchDelete = ref(false)
const selectedMajors = ref([])
const isEditing = ref(false)
const showDialog = ref(false)
const dialogTitle = ref('')

const fetchMajors = () => {
  axios.get('/major/listMajor', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  }).then((response) => {
    if (response.data.code === 1) {
      majorList.value = response.data.data.list
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取专业列表失败')
    }
  })
}

const fetchColleges = () => {
  axios.get('/college/listCollege').then((response) => {
    if (response.data.code === 1) {
      collegeList.value = response.data.data.list; // 确保提取 list 数据
    } else {
      ElMessage.error(response.data.msg || '获取学院列表失败')
    }
  })
}

const openDialog = (action: string, major: any = null) => {
  if (action === 'add') {
    dialogTitle.value = '添加专业'
    newMajor.value = { mname: '', college: '' }
    isEditing.value = false
  } else if (action === 'edit') {
    dialogTitle.value = '修改专业'
    newMajor.value = { ...major }
    isEditing.value = true
  }
  showDialog.value = true
}

const saveMajor = () => {
  axios.post('/major/saveMajor', newMajor.value).then((response) => {
    if (response.data.code === 1) {
      fetchMajors()
      ElMessage.success('专业添加成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '专业添加失败')
    }
  })
}

const updateMajor = () => {
  axios.post('/major/updateMajor', newMajor.value).then((response) => {
    if (response.data.code === 1) {
      fetchMajors()
      ElMessage.success('专业更新成功')
      showDialog.value = false
    } else {
      ElMessage.error(response.data.msg || '专业更新失败')
    }
  })
}

const handleDelete = (id: number) => {
  axios.get(`/major/deleteMajor/${id}`).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('专业删除成功')
      fetchMajors()
    } else {
      ElMessage.error(response.data.msg || '专业删除失败')
    }
  })
}

const confirmBatchDelete = () => {
  const ids = selectedMajors.value.map(major => major.id).join(',')
  axios.post('/major/deleteBatchMajor', { ids }).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除专业成功')
      fetchMajors()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除专业失败')
    }
  })
}

const handleSelectionChange = (val: any) => {
  selectedMajors.value = val
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchMajors()
}

const toggleBatchDelete = () => {
  showBatchDelete.value = !showBatchDelete.value
  selectedMajors.value = []
}

fetchMajors()
fetchColleges()
</script>

<style scoped>
/* 操作按钮容器 */
.action-buttons {
  display: flex;
  margin-bottom: 15px;
  margin-left: 10px; /* 与表格左端对齐 */
}

/* 按钮样式 */
.add-major-button {
  background-color: #8b007a !important; /* 添加专业按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.add-major-button:hover {
  background-color: #a70c94 !important; /* 悬停颜色 */
  border-color: #a70c94 !important;
}

.edit-major-button {
  background-color: #8b007a !important; /* 修改按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.edit-major-button:hover {
  background-color: #a70c94 !important; /* 修改按钮悬停颜色 */
  border-color: #a70c94 !important;
}

.delete-major-button {
  background-color: #f56c6c !important; /* 删除按钮背景颜色 */
  border-color: #f56c6c !important;
  color: white !important;
}

.delete-major-button:hover {
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
</style>