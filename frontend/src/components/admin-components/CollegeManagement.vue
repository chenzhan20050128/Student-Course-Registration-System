<template>
  <div>
    <h2>学院管理</h2>
    <!-- 添加学院按钮 -->
    <el-button type="primary" @click="toggleForm" class="add-college-button">添加学院</el-button>
    <!-- 批量删除按钮 -->
    <el-button type="danger" @click="toggleBatchDelete" class="batch-delete-button">批量删除</el-button>
    <!-- 确定批量删除按钮 -->
    <el-button type="danger" @click="confirmBatchDelete" v-if="showBatchDelete" class="confirm-batch-delete-button">确定批量删除</el-button>
    <!-- 添加学院表单 -->
    <el-form v-if="showForm" :model="newCollege" label-width="120px" class="college-form">
      <el-form-item label="学院名" required>
        <el-input v-model="newCollege.cname"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveCollege">提交</el-button>
        <el-button type="primary" @click="updateCollege" v-if="isEditing">修改</el-button>
      </el-form-item>
    </el-form>

    <!-- 学院列表表格 -->
    <div class="table-container">
      <el-table
        :data="collegeList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="showBatchDelete"></el-table-column>
        <el-table-column prop="cname" label="学院名"></el-table-column>
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
  name: 'CollegeManagement',
  data() {
    return {
      collegeList: [], // 学院列表
      newCollege: {
        cname: '',
      },
      pageNum: 1, // 当前页码
      pageSize: 6, // 每页显示条数
      total: 0, // 总条数
      showForm: false, // 控制表单显示和隐藏
      isEditing: false, // 控制是否为编辑状态
      showBatchDelete: false, // 控制批量删除模式
      selectedColleges: [], // 选中的学院
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
      this.selectedColleges = []; // 重置选中的学院
    },
    // 获取学院列表
    fetchColleges() {
      axios.get('/college/listCollege', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
      }).then((response) => {
        if (response.data.code === 1) {
          this.collegeList = response.data.data.list;
          this.total = response.data.data.total;
        } else {
          this.$message.error(response.data.msg || '获取学院列表失败');
        }
      });
    },
    // 添加学院
    saveCollege() {
      axios.post('/college/saveCollege', this.newCollege).then((response) => {
        if (response.data.code === 1) {
          this.fetchColleges(); // 重新获取学院列表
          this.$message.success('学院添加成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
        } else {
          this.$message.error(response.data.msg || '学院添加失败');
        }
      });
    },
    // 更新学院
    updateCollege() {
      axios.post('/college/updateCollege', this.newCollege).then((response) => {
        if (response.data.code === 1) {
          this.fetchColleges(); // 重新获取学院列表
          this.$message.success('学院更新成功');
          this.resetForm();
          this.showForm = false; // 提交后隐藏表单
          this.isEditing = false; // 重置编辑状态
        } else {
          this.$message.error(response.data.msg || '学院更新失败');
        }
      });
    },
    // 重置表单
    resetForm() {
      this.newCollege = {
        cname: '',
      };
    },
    // 处理分页变化
    handlePageChange(page) {
      this.pageNum = page;
      this.fetchColleges();
    },
    // 处理修改操作
    handleEdit(id) {
      // 调用 /preUpdateCollege/{id} 接口
      axios.get(`/college/preUpdateCollege/${id}`).then((response) => {
        if (response.data.code === 1) {
          const data = response.data.data;
          this.newCollege = data.college;
          this.showForm = true; // 显示表单
          this.isEditing = true; // 设置编辑状态
        } else {
          this.$message.error(response.data.msg || '获取学院信息失败');
        }
      });
    },
    // 处理删除操作
    handleDelete(id) {
      // 调用 /deleteCollege/{id} 接口
      axios.get(`/college/deleteCollege/${id}`).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('学院删除成功');
          this.fetchColleges(); // 重新获取学院列表
        } else {
          this.$message.error(response.data.msg || '学院删除失败');
        }
      });
    },
    // 处理批量删除操作
    confirmBatchDelete() {
      const ids = this.selectedColleges.map(college => college.id).join(',');
      axios.post('/college/deleteBatchCollege', { ids }).then((response) => {
        if (response.data.code === 1) {
          this.$message.success('批量删除学院成功');
          this.fetchColleges(); // 重新获取学院列表
          this.showBatchDelete = false; // 退出批量删除模式
        } else {
          this.$message.error(response.data.msg || '批量删除学院失败');
        }
      });
    },
    // 处理选择变化
    handleSelectionChange(val) {
      this.selectedColleges = val;
    },
  },
  mounted() {
    this.fetchColleges();
  },
};
</script>

<style scoped>
.college-form {
  margin-bottom: 20px;
}
.add-college-button, .batch-delete-button, .confirm-batch-delete-button {
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