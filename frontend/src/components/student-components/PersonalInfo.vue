<template>
  <div>
    <el-form :model="student" label-width="120px">
      <el-form-item label="姓名">
        <el-input v-model="student.sname"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="student.sex" placeholder="请选择">
          <el-option label="男" value="Male"></el-option>
          <el-option label="女" value="Female"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="student.age" type="number"></el-input>
      </el-form-item>
      <el-form-item label="专业">
        <el-input v-model="student.major"></el-input>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          action="http://localhost:8080/student/uploadStudentImage"
          list-type="picture-card"
          :on-preview="handlePictureCardPreview"
          :on-remove="handleRemove"
          :file-list="fileList"
          :on-success="handleUploadSuccess"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updateStudentInfo">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from '@/http';
import { useStudentStore } from '@/store/student';

export default {
  name: 'PersonalInfo',
  setup() {
    const studentStore = useStudentStore();
    return { studentStore };
  },
  data() {
    return {
      student: {
        id: this.studentStore.student.id,
        sname: '',
        sex: '',
        age: null,
        major: '',
        simage: '',
        password: '',
      },
      fileList: [],
    };
  },
  methods: {
    async fetchStudentInfo() {
      try {
        const response2 = await axios.get('/getRoleMessage');
        this.student.id = response2.data.data.id;
        const response = await axios.get(`/student/preUpdateStudent/${this.student.id}`);
        if (response.data && response.data.data) {
          this.student = response.data.data.student;
          // 如果有头像，添加到 fileList 中
          if (this.student.simage) {
            this.fileList.push({
              name: this.student.simage,
              url: `http://localhost:8080/public/${this.student.simage}`,
            });
          }
        }
      } catch (error) {
        console.error('获取学生信息失败', error);
      }
    },
    handleUploadSuccess(response, file, fileList) {
      this.student.simage = response.data; // 假设后端返回的是文件名
      // 更新 fileList 以便预览
      this.fileList.push({
        name: file.name,
        url: `/${response.data}`,
      });
    },
    handleRemove(file, fileList) {
      this.student.simage = '';
      this.fileList = fileList;
    },
    handlePictureCardPreview(file) {
      this.$alert(`<img src="${file.url}" style="width: 100%;">`, '图片预览', {
        dangerouslyUseHTMLString: true,
      });
    },
    async updateStudentInfo() {
      try {
        const response = await axios.post('/student/updateStudent', this.student);
        if (response.data && response.data.code === 1) {
          this.$message.success('学生信息更新成功');
        } else {
          this.$message.error('学生信息更新失败');
        }
      } catch (error) {
        console.error('更新学生信息失败', error);
        this.$message.error('更新学生信息失败');
      }
    },
  },
  mounted() {
    this.fetchStudentInfo();
  },
};
</script>

<style scoped>
/* 样式 */
</style>