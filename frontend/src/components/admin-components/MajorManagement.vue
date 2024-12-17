<template>
  <div>
    <h2>专业管理</h2>
    <!-- 添加专业按钮 -->
    <el-button type="primary" @click="toggleForm" class="add-major-button">添加专业</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>
    <!-- 添加专业表单 -->
    <el-form v-if="showForm" :model="newMajor" label-width="120px" class="major-form">
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
        <el-button type="primary" @click="saveMajor">提交</el-button>
        <el-button type="primary" @click="updateMajor" v-if="isEditing">修改</el-button>
      </el-form-item>
    </el-form>

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
            <el-button size="mini" type="primary" @click="handleEdit(scope.row.id)">修改</el-button>
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
  </div>
</template>

<script>
import axios from '@/http';

export default {
  name: 'MajorManagement',
  data() {
    return {
      majorList: [], // 专业列表
      collegeList: [], // 学院列表
      newMajor: {
        mname: '',
        college: '',
      },
      pageNum: 1, // 当前页码
      pageSize: 6, // 每页显示条数
      total: 0, // 总条数
      showForm: false, // 控制表单显示和隐藏
      isEditing: false, // 控制是否为编辑状态
      showBatchDelete: false, // 控制批量删除模式
      selectedMajors: [], // 选中的专业
    };
  },
  methods: {
    // 切换表单显示和隐藏
    toggleForm() {
      this.showForm = !this.showForm;
      this.isEditing = false; // 重置编辑状态
    },
    // 切换批量删除模式
    toggleBatchDelete() {
      this.showBatchDelete = !this.showBatchDelete;
      this.selectedMajors = []; // 重置选中的专业
    },
    // 获取专业列表
    fetchMajors() {
      axios.get('/major/listMajor', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
      }).then((response) => {
        if (response.data.code === 1) {
          this.majorList = response.data.data.list;
          this.total = response.data.data.total;
        } else {
          this.$message.error(response.data.msg || '获取专业列表失败');
        }
      });
    },
    // 获取学院列表
    fetchColleges() {
      axios.get('/major/preSaveMajor').then((response) => {
        if (response.data.code === 1) {
          this.collegeList = response.data.data.collegeList;
        } else {
          this.$message.error(response.data.msg || '获取专业和学院列表失败');
        }
      });
    },
    // 添加专业
    saveMajor() {
      axios.post('/major/saveMajor', this.newMajor).then((response) => {
        if (response.data.code === 1) {
          this.fetchMajors(); // 重新获取专业列表
          this.$message.success('专业添加成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
        } else {
          this.$message.error(response.data.msg || '专业添加失败');
        }
      });
    },
    // 更新专业
    updateMajor() {
      axios.post('/major/updateMajor', this.newMajor).then((response) => {
        if (response.data.code === 1) {
          this.fetchMajors(); // 重新获取专业列表
          this.$message.success('专业更新成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
          this.isEditing = false; // 重置编辑状态
        } else {
          this.$message.error(response.data.msg || '专业更新失败');
        }
      });
    },
    // 重置表单
    resetForm() {
      this.newMajor = {
        mname: '',
        college: '',
      };
    },
    // 处理分页变化
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchMajors();
    },
    // 处理修改操作
    handleEdit(id) {
      // 调用 /preUpdateMajor/{id} 接口
      axios.get(`/major/preUpdateMajor/${id}`).then((response) => {
        if (response.data.code === 1) {
          const data = response.data.data;
          this.newMajor = data.major;
          this.collegeList = data.collegeList;
          this.showForm = true; // 显示表单
          this.isEditing = true; // 设置编辑状态
        } else {
          this.$message.error(response.data.msg || '获取专业信息失败');
        }
      });
    },
    // 处理删除操作
    handleDelete(id) {
      // 调用 /deleteMajor/{id} 接口
      axios.get(`/major/deleteMajor/${id}`).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('专业删除成功');
          this.fetchMajors(); // 重新获取专业列表
        } else {
          this.$message.error(response.data.msg || '专业删除失败');
        }
      });
    },
    // 处理批量删除操作
    confirmBatchDelete() {
      const ids = this.selectedMajors.map(major => major.id).join(',');
      axios.post('/major/deleteBatchMajor', { ids }).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('批量删除专业成功');
          this.fetchMajors(); // 重新获取专业列表
          this.showBatchDelete = false; // 退出批量删除模式
        } else {
          this.$message.error(response.data.msg || '批量删除专业失败');
        }
      });
    },
    // 处理选择变化
    handleSelectionChange(val) {
      this.selectedMajors = val;
    },
  },
  mounted() {
    this.fetchMajors();
    this.fetchColleges();
  },
};
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