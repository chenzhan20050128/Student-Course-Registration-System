<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <div class="logo-section">
          <img src="../assets/校标-透明.png" alt="南京大学" class="logo-image" />
          <h2 class="system-title">学生注册</h2>
        </div>
      </div>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="input-group">
          <label class="input-label">学号</label>
          <input 
            type="text" 
            id="studentId" 
            v-model="registerForm.studentId" 
            placeholder="请输入学号" 
            pattern="\d+"
            required
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">姓名</label>
          <input 
            type="text" 
            id="studentName" 
            v-model="registerForm.studentName" 
            placeholder="请输入真实姓名" 
            required
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="registerForm.password" 
            placeholder="请设置密码（至少6位）" 
            minlength="6"
            required
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">确认密码</label>
          <input 
            type="password" 
            id="confirmPassword" 
            v-model="registerForm.confirmPassword" 
            placeholder="请再次输入密码" 
            required
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">学院</label>
          <select id="college" v-model="registerForm.college" required>
            <option value="">请选择学院</option>
            <option value="计算机学院">计算机学院</option>
            <option value="软件学院">软件学院</option>
            <option value="信息工程学院">信息工程学院</option>
            <option value="数学学院">数学学院</option>
            <option value="文学院">文学院</option>
            <option value="历史学院">历史学院</option>
            <option value="哲学学院">哲学学院</option>
            <option value="新闻传播学院">新闻传播学院</option>
            <option value="法学院">法学院</option>
            <option value="商学院">商学院</option>
            <option value="物理学院">物理学院</option>
            <option value="化学学院">化学学院</option>
            <option value="生命科学学院">生命科学学院</option>
          </select>
        </div>
        
        <div class="input-group">
          <label class="input-label">出生日期（可选）</label>
          <input 
            type="date" 
            id="birthDate" 
            v-model="registerForm.birthDate"
          />
        </div>
        
        <div v-if="errorMessage" class="error-message">
          ⚠️ {{ errorMessage }}
        </div>
        
        <div v-if="successMessage" class="success-message">
          ✅ {{ successMessage }}
        </div>
        
        <button type="submit" class="register-button" :disabled="loading">
          {{ loading ? '注册中...' : '立即注册' }}
        </button>
        
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </form>
      
      <div class="register-footer">
        <p>© 2025 蓝鲸选课系统</p>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '../api/authApi';

export default {
  name: 'Register',
  data() {
    return {
      registerForm: {
        studentId: '',
        studentName: '',
        password: '',
        confirmPassword: '',
        college: '',
        birthDate: ''
      },
      errorMessage: '',
      successMessage: '',
      loading: false
    };
  },
  methods: {
    async handleRegister() {
      this.errorMessage = '';
      this.successMessage = '';
      
      // 前端验证
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.errorMessage = '两次输入的密码不一致';
        return;
      }
      
      if (this.registerForm.password.length < 6) {
        this.errorMessage = '密码长度至少6位';
        return;
      }
      
      this.loading = true;

      try {
        const response = await register(this.registerForm);
        
        if (response.data.success) {
          this.successMessage = `注册成功！请使用学号：${response.data.studentId} 登录`;
          
          // 3秒后跳转到登录页
          setTimeout(() => {
            this.$router.push('/login');
          }, 3000);
        } else {
          this.errorMessage = response.data.message || '注册失败';
        }
      } catch (error) {
        console.error('注册错误:', error);
        this.errorMessage = error.response?.data?.message || '注册失败，请检查网络连接';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
}

.register-box {
  background: white;
  padding: 20px 30px 18px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(124, 31, 137, 0.3);
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-header {
  text-align: center;
  margin-bottom: 15px;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 0;
}

.logo-image {
  width: 60px;
  height: 60px;
  margin-bottom: 8px;
  object-fit: contain;
}

.system-title {
  color: #7C1F89;
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

.register-subtitle {
  color: #7C1F89;
  font-size: 16px;
  font-weight: 600;
  margin: 12px 0 6px;
  letter-spacing: 3px;
}

.register-hint {
  color: #7f8c8d;
  font-size: 13px;
  margin: 0;
}

.register-form {
  display: flex;
  flex-direction: column;
}

.input-group {
  position: relative;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.input-label {
  font-size: 13px;
  font-weight: 600;
  color: #7C1F89;
  margin-bottom: 4px;
  display: block;
}

.input-group input,
.input-group select {
  width: 100%;
  padding: 9px 12px;
  border: 2px solid #e0d4e8;
  border-radius: 8px;
  font-size: 14px;
  color: #2c3e50;
  outline: none;
  background: white;
  transition: all 0.3s ease;
}

.input-group input:focus,
.input-group select:focus {
  border-color: #7C1F89;
  box-shadow: 0 0 0 3px rgba(124, 31, 137, 0.1);
}

.input-group input::placeholder {
  color: #bdc3c7;
}

.input-group select {
  cursor: pointer;
}

.input-group input[type="date"] {
  color: #7f8c8d;
}

.error-message {
  color: #e74c3c;
  background: #fee;
  padding: 12px 15px;
  border-radius: 8px;
  margin-bottom: 16px;
  text-align: center;
  font-size: 14px;
  border-left: 4px solid #e74c3c;
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10px); }
  75% { transform: translateX(10px); }
}

.success-message {
  color: #27ae60;
  background: #d5f4e6;
  padding: 12px 15px;
  border-radius: 8px;
  margin-bottom: 16px;
  text-align: center;
  font-size: 14px;
  border-left: 4px solid #27ae60;
  animation: slideDown 0.5s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-button {
  width: 100%;
  padding: 11px;
  background: linear-gradient(135deg, #7C1F89 0%, #5A1566 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 3px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(124, 31, 137, 0.4);
  margin-bottom: 12px;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(124, 31, 137, 0.5);
}

.register-button:active:not(:disabled) {
  transform: translateY(0);
}

.register-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.login-link {
  text-align: center;
  margin-top: 15px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
  color: #7f8c8d;
  font-size: 14px;
}

.login-link a {
  color: #7C1F89;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #5A1566;
  text-decoration: underline;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.register-footer p {
  color: #95a5a6;
  font-size: 12px;
  margin: 0;
}

/* 自定义滚动条样式 */
.register-box::-webkit-scrollbar {
  width: 6px;
}

.register-box::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.register-box::-webkit-scrollbar-thumb {
  background: #667eea;
  border-radius: 10px;
}

.register-box::-webkit-scrollbar-thumb:hover {
  background: #764ba2;
}
</style>
