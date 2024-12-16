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
        <el-form-item label="学院">
          <el-input v-model="student.college"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="#"
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
  
  export default {
    name: 'PersonalInfo',
    data() {
      return {
        student: {
          id : 1,  
          sname: '',
          sex: '',
          age: null,
          major: '',
          college: '',
          simage: '',
          password: '',
        },
        fileList: [],
      };
    },
    methods: {
      async fetchStudentInfo() {
        try {
            const response = await axios.get('/student/preUpdateStudent/1'); // 假设学生ID为1 TODO 修改为真实的学生ID
          if (response.data && response.data.data) {
            this.student = response.data.data.student;
          }
        } catch (error) {
          console.error('获取学生信息失败', error);
        }
      },
      handleUploadSuccess(response, file, fileList) {
        this.student.simage = file.name;
      },
      handleRemove(file, fileList) {
        this.student.simage = '';
      },
      handlePictureCardPreview(file) {
        this.$alert(`<img src="${file.url}" style="width: 100%;">`, '图片预览', {
          dangerouslyUseHTMLString: true,
        });
      },
      async updateStudentInfo() {
        try {
          const response = await axios.post('/student/updateStudent', this.student);
          console.log(111)

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