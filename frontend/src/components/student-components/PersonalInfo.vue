<template>
  <div class="personal-info-container">
    <div class="info-header">
      <h2>个人信息</h2>
      <span class="info-tip">管理你的账户信息和头像</span>
    </div>

    <div class="info-form-wrapper">
      <div class="form-left">
        <div class="form-group">
          <label class="form-label">姓名</label>
          <input v-model="student.sname" class="form-input" type="text" />
        </div>

        <div class="form-group">
          <label class="form-label">性别</label>
          <select v-model="student.sex" class="form-input">
            <option value="">请选择</option>
            <option value="Male">男</option>
            <option value="Female">女</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">年龄</label>
          <input v-model.number="student.age" class="form-input" type="number" />
        </div>

        <div class="form-group">
          <label class="form-label">专业</label>
          <select v-model="student.major" class="form-input">
            <option value="">请选择专业</option>
            <option v-for="major in majorList" :key="major.id" :value="major.mname">
              {{ major.mname }}
            </option>
          </select>
        </div>

        <div class="form-actions">
          <button @click="updateStudentInfo" class="save-btn">
            保存修改
          </button>
        </div>
      </div>

      <div class="form-right">
        <div class="avatar-section">
          <h3>头像</h3>
          <div class="avatar-preview">
            <img 
              v-if="student.simage" 
              :src="`http://localhost:8080/public/${student.simage}`"
              alt="头像"
              class="avatar-image"
            />
            <div v-else class="avatar-placeholder">
              <span>暂无头像</span>
            </div>
          </div>
          <el-upload
            action="http://localhost:8080/student/uploadStudentImage"
            list-type="picture"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :file-list="fileList"
            :on-success="handleUploadSuccess"
            class="upload-box"
          >
            <el-button type="primary" size="small">上传头像</el-button>
          </el-upload>
        </div>
      </div>
    </div>
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
      majorList: [],
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
      this.student.simage = response.data;
      this.fileList.push({
        name: file.name,
        url: `/${response.data}`,
      });
      this.$message.success('头像上传成功');
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
.personal-info-container {
  padding: 0;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.info-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.info-tip {
  font-size: 14px;
  color: #999;
}

.info-form-wrapper {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 40px;
  max-width: 900px;
}

.form-left {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-input {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-input:hover {
  border-color: #667eea;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.form-actions {
  margin-top: 10px;
}

.save-btn {
  width: 100%;
  padding: 10px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-btn:hover {
  background-color: #5568d3;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.form-right {
  display: flex;
  flex-direction: column;
}

.avatar-section {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
}

.avatar-section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.avatar-preview {
  width: 150px;
  height: 150px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  background-color: #fafafa;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.avatar-placeholder {
  text-align: center;
  color: #999;
  font-size: 12px;
}

.upload-box {
  margin-top: 12px;
}

::v-deep(.el-upload--picture .el-upload-list__item) {
  margin-right: 8px;
  margin-bottom: 8px;
}

@media (max-width: 768px) {
  .info-form-wrapper {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}
</style>
