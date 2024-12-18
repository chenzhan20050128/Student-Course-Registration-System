<template>
    <div>
      <el-form :model="teacher" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="teacher.tname"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="teacher.sex" placeholder="请选择">
            <el-option label="男" value="Male"></el-option>
            <el-option label="女" value="Female"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="teacher.age" type="number"></el-input>
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="teacher.major"></el-input>
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
          <el-button type="primary" @click="updateTeacherInfo">保存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </template>
  
  <script>
  import axios from '@/http';
  
  export default {
    name: 'TeachingInfo',
    data() {
      return {
        teacher: {
          id: 0,
          tname: '',
          sex: '',
          age: null,
          major: '',
          college: '',
          timage: '',
          password: '',
        },
        fileList: [],
      };
    },
    methods: {
      async fetchTeacherInfo() {
        try {
          const response2 = await axios.get('/getRoleMessage')
          this.teacher.id = response2.data.data.id
          const response = await axios.get(`/teacher/preUpdateTeacher/${this.teacher.id}`);
          if (response.data && response.data.data) {
            this.teacher = response.data.data.teacher;
          }
        } catch (error) {
          console.error('获取教师信息失败', error);
        }
      },
      handleUploadSuccess(response, file, fileList) {
        this.teacher.timage = file.name;
      },
      handleRemove(file, fileList) {
        this.teacher.timage = '';
      },
      handlePictureCardPreview(file) {
        this.$alert(`<img src="${file.url}" style="width: 100%;">`, '图片预览', {
          dangerouslyUseHTMLString: true,
        });
      },
      async updateTeacherInfo() {
        try {
          const response = await axios.post('/teacher/updateTeacher', this.teacher);
          if (response.data && response.data.code === 1) {
            this.$message.success('教师信息更新成功');
          } else {
            this.$message.error('教师信息更新失败');
          }
        } catch (error) {
          console.error('更新教师信息失败', error);
          this.$message.error('更新教师信息失败');
        }
      },
    },
    mounted() {
      this.fetchTeacherInfo();
    },
  };
  </script>
  
  <style scoped>
  /* 样式 */
  </style>