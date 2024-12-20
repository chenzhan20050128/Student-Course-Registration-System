<template>
  <div>
    <h2>专业管理</h2>
    <!-- 添加专业按钮 -->
    <el-button type="primary" @click="openDialog('add')" class="add-major-button">添加专业</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>

    <!-- 专业列表表格 -->
    <div class="table-container">
      <el-table
        :data="majorList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="mname" label="专业名"></el-table-column>
        <el-table-column prop="college" label="学院"></el-table-column>
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
          <el-button type="primary" @click="saveMajor" v-if="!isEditing">提交</el-button>
          <el-button type="primary" @click="updateMajor" v-if="isEditing">修改</el-button>
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
.major-form {
  margin-bottom: 20px;
}
.add-major-button, .batch-delete-button, .confirm-batch-delete-button {
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