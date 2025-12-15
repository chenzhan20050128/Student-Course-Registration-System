<template>
  <div>
    <el-form :model="student" label-width="50px" class="personal-info-form">
      <el-form-item label="姓名">
        <el-input v-model="student.sname" class="short-input"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="student.sex" placeholder="请选择" class="short-input">
          <el-option label="男" value="Male"></el-option>
          <el-option label="女" value="Female"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="student.age" type="number" class="short-input"></el-input>
      </el-form-item>
      <el-form-item label="专业">
        <el-select v-model="student.major" placeholder="请选择专业" class="short-input">
          <el-option
            v-for="major in majorList"
            :key="major.id"
            :label="major.mname"
            :value="major.id.toString()"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload
          action="http://localhost:8080/student/uploadStudentImage"
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
        <el-button type="primary" @click="updateStudentInfo" class="custom-button">保存</el-button>
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
      majorList: [], // 专业列表
    };
  },
  methods: {
    async fetchMajorList() {
      try {
        const response = await axios.get('/major/listMajor');
        if (response.data && response.data.data) {
          this.majorList = response.data.data.list || response.data.data;
        }
      } catch (error) {
        console.error('获取专业列表失败', error);
      }
    },
    async fetchStudentInfo() {
      try {
        const response2 = await axios.get('/getRoleMessage');
        this.student.id = response2.data.data.id;
        const response = await axios.get(`/student/preUpdateStudent/${this.student.id}`);
        if (response.data && response.data.data) {
          this.student = response.data.data.student;
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
    this.fetchMajorList();
    this.fetchStudentInfo();
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