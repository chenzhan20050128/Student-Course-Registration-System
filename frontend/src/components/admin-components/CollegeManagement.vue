<template>
  <div>
    <!-- 添加学院和批量删除按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openDialog('add')" class="add-college-button">添加学院</el-button>
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

    <!-- 学院列表表格 -->
    <div class="table-container">
      <el-table
        :data="collegeList"
        style="width: 1280px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="cname" label="学院名称" width="1070"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button
              size="mini"
              type="primary"
              class="edit-college-button"
              @click="openDialog('edit', scope.row)"
            >
              修改
            </el-button>
            <el-button
              size="mini"
              type="danger"
              class="delete-college-button"
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
      <span class="total-pages">总页数: {{ Math.ceil(total / pageSize) }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/http'

const collegeList = ref([])
const newCollege = ref({ cname: '' })
const pageNum = ref(1)
const pageSize = ref(6)
const total = ref(0)
const showBatchDelete = ref(false)
const selectedColleges = ref([])
const isEditing = ref(false)

const fetchColleges = () => {
  axios.get('/college/listCollege', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
  }).then((response) => {
    if (response.data.code === 1) {
      collegeList.value = response.data.data.list
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取学院列表失败')
    }
  })
}

const openDialog = (action: string, college: any = null) => {
  if (action === 'add') {
    ElMessageBox.prompt('请输入学院名称', '添加学院', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '学院名称不能为空',
    }).then(({ value }) => {
      newCollege.value.cname = value
      saveCollege()
    }).catch(() => {
      ElMessage.info('取消输入')
    })
  } else if (action === 'edit') {
    newCollege.value = { ...college }
    ElMessageBox.prompt('请输入学院名称', '修改学院', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: newCollege.value.cname,
      inputPattern: /.+/,
      inputErrorMessage: '学院名称不能为空',
    }).then(({ value }) => {
      newCollege.value.cname = value
      updateCollege()
    }).catch(() => {
      ElMessage.info('取消输入')
    })
  }
}

const saveCollege = () => {
  axios.post('/college/saveCollege', newCollege.value).then((response) => {
    if (response.data.code === 1) {
      fetchColleges()
      ElMessage.success('学院添加成功')
    } else {
      ElMessage.error(response.data.msg || '学院添加失败')
    }
  })
}

const updateCollege = () => {
  axios.post('/college/updateCollege', newCollege.value).then((response) => {
    if (response.data.code === 1) {
      fetchColleges()
      ElMessage.success('学院更新成功')
    } else {
      ElMessage.error(response.data.msg || '学院更新失败')
    }
  })
}

const handleDelete = (id: number) => {
  axios.get(`/college/deleteCollege/${id}`).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('学院删除成功')
      fetchColleges()
    } else {
      ElMessage.error(response.data.msg || '学院删除失败')
    }
  })
}

const confirmBatchDelete = () => {
  const ids = selectedColleges.value.map(college => college.id).join(',')
  axios.post('/college/deleteBatchCollege', { ids }).then((response) => {
    if (response.data.code === 1) {
      ElMessage.success('批量删除学院成功')
      fetchColleges()
      showBatchDelete.value = false
    } else {
      ElMessage.error(response.data.msg || '批量删除学院失败')
    }
  })
}

const handleSelectionChange = (val: any) => {
  selectedColleges.value = val
}

const handlePageChange = (page: number) => {
  pageNum.value = page
  fetchColleges()
}

const toggleBatchDelete = () => {
  showBatchDelete.value = !showBatchDelete.value
  selectedColleges.value = []
}

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
.add-college-button {
  background-color: #8b007a !important; /* 添加学院按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.add-college-button:hover {
  background-color: #a70c94 !important; /* 悬停颜色 */
  border-color: #a70c94 !important;
}

.edit-college-button {
  background-color: #8b007a !important; /* 修改按钮背景颜色 */
  border-color: #8b007a !important;
  color: white !important;
}

.edit-college-button:hover {
  background-color: #a70c94 !important; /* 修改按钮悬停颜色 */
  border-color: #a70c94 !important;
}

.delete-college-button {
  background-color: #f56c6c !important; /* 删除按钮背景颜色 */
  border-color: #f56c6c !important;
  color: white !important;
}

.delete-college-button:hover {
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