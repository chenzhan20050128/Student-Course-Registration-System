<template>
  <div>
    <el-form :model="teacher" label-width="50px" class="personal-info-form">
      <el-form-item label="姓名">
        <el-input v-model="teacher.tname" class="short-input"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="teacher.sex" placeholder="请选择" class="short-input">
          <el-option label="男" value="Male"></el-option>
          <el-option label="女" value="Female"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="teacher.age" type="number" class="short-input"></el-input>
      </el-form-item>
      <el-form-item label="专业">
        <el-input v-model="teacher.major" class="short-input"></el-input>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          action="http://localhost:8080/teacher/uploadTeacherImage"
          list-type="picture-card"
          :on-preview="handlePictureCardPreview"
          :on-remove="handleRemove"
          :file-list="fileList"
          :on-success="handleUploadSuccess"
          class="upload-box"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
      </el-form-item>
      <el-form-item class="button-container">
        <el-button type="primary" @click="updateTeacherInfo" class="custom-button">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import axios from '@/http';

export default {
  name: 'TeacherPersonalInfo',
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
        const response2 = await axios.get('/getRoleMessage');
        this.teacher.id = response2.data.data.id;
        const response = await axios.get(`/teacher/preUpdateTeacher/${this.teacher.id}`);
        if (response.data && response.data.data) {
          this.teacher = response.data.data.teacher;
          if (this.teacher.timage) {
            this.fileList.push({
              name: this.teacher.timage,
              url: `http://localhost:8080/public/${this.teacher.timage}`,
            });
          }
        }
      } catch (error) {
        console.error('获取教师信息失败', error);
      }
    },
    handleUploadSuccess(response, file, fileList) {
      this.teacher.timage = response.data; // 假设后端返回的是文件名
      this.fileList.push({
        name: file.name,
        url: `/${response.data}`,
      });
    },
    handleRemove(file, fileList) {
      this.teacher.timage = '';
      this.fileList = fileList;
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
/* 限制输入框和选择框的宽度 */
.short-input {
  max-width: 600px; /* 设置最大宽度 */
  width: 100%; /* 保持自适应 */
  margin-left: 5px; /* 向右移动5px */
}

.personal-info-form {
  margin-top: 3px;
  max-width: 900px; /* 表单整体宽度控制 */
}

/* 为图片上传框设置右移样式 */
.upload-box {
  margin-top: 10px;
  margin-left: 5px; /* 向右移动5px */
  margin-bottom: 10px;
}

/* 为按钮容器设置右移样式 */
.button-container {
  margin-left: 5px; /* 按钮向右移动5px */
}

/* 自定义按钮样式 */
.custom-button {
  background-color: #8b007a !important; /* 按钮正常状态背景颜色 */
  border-color: #8b007a !important; /* 按钮正常状态边框颜色 */
  color: white !important; /* 按钮文字颜色保持白色 */
  border-radius: 5px; /* 添加圆角效果 */
}

/* 按钮悬停状态 */
.custom-button:hover {
  background-color: #a70c94 !important; /* 悬停状态背景颜色 */
  border-color: #a70c94 !important; /* 悬停状态边框颜色 */
}
</style>