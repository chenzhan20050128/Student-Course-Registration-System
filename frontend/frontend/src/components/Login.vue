<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo-section">
          <img src="../assets/校标-透明.png" alt="南京大学" class="logo-image" />
          <h2 class="system-title">蓝鲸选课系统</h2>
        </div>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="input-group">
          <label class="input-label">学号</label>
          <input 
            type="text" 
            id="username" 
            v-model="loginForm.username" 
            placeholder="请输入学号" 
            required
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="loginForm.password" 
            placeholder="请输入密码" 
            required
          />
        </div>
        
        <div class="input-group">
          <label class="input-label">选课轮次</label>
          <select v-model="selectedBatchId" required>
            <option value="">请选择选课轮次</option>
            <option v-for="batch in electiveBatches" :key="batch.batchId" :value="batch.batchId">
              {{ batch.batchName }} - {{ batch.roundName }}
            </option>
          </select>
        </div>
        
        <div class="captcha-container">
          <div class="input-group captcha-input">
            <label class="input-label">验证码</label>
            <input 
              type="text" 
              v-model="loginForm.captcha" 
              placeholder="验证码" 
              maxlength="4"
              required
            />
          </div>
          <div class="captcha-wrapper">
            <label class="input-label" style="visibility: hidden;">占位</label>
            <div class="captcha-image" @click="refreshCaptcha">
              <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
              <span v-else class="captcha-loading">加载中...</span>
            </div>
          </div>
        </div>
        
        <div v-if="errorMessage" class="error-message">
          ⚠️ {{ errorMessage }}
        </div>
        
        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? '登录中...' : '登  录' }}
        </button>
        
        <div class="register-link">
          没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </form>
      
      <div class="login-footer">
        <p>© 2025 蓝鲸选课系统</p>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '../api/authApi';
import { getAllBatches } from '../api/electiveBatchApi';

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        userType: 'student',
        captcha: '',
        captchaToken: ''
      },
      electiveBatches: [],
      selectedBatchId: '',
      captchaImage: '',
      errorMessage: '',
      loading: false
    };
  },
  mounted() {
    this.refreshCaptcha();
    this.loadElectiveBatches();
  },
  methods: {
    async loadElectiveBatches() {
      try {
        const response = await getAllBatches();
        // 只显示进行中和未开始的轮次
        this.electiveBatches = response.data.filter(
          batch => batch.status === '进行中' || batch.status === '未开始'
        );
        
        // 如果有进行中的轮次，自动选择第一个
        const currentBatch = this.electiveBatches.find(batch => batch.status === '进行中');
        if (currentBatch) {
          this.selectedBatchId = currentBatch.batchId;
        } else if (this.electiveBatches.length > 0) {
          this.selectedBatchId = this.electiveBatches[0].batchId;
        }
      } catch (error) {
        console.error('获取选课轮次失败:', error);
      }
    },
    
    async refreshCaptcha() {
      try {
        const response = await fetch('http://localhost:8080/api/auth/captcha');
        const data = await response.json();
        this.captchaImage = data.image;
        this.loginForm.captchaToken = data.token;
      } catch (error) {
        console.error('获取验证码失败:', error);
      }
    },
    
    async handleLogin() {
      // 验证是否选择了选课轮次
      if (!this.selectedBatchId) {
        this.errorMessage = '请选择选课轮次';
        return;
      }
      
      this.errorMessage = '';
      this.loading = true;

      try {
        const response = await login(this.loginForm);
        
        if (response.data.success) {
          // 保存用户信息和选课轮次到localStorage
          localStorage.setItem('user', JSON.stringify(response.data.user));
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('selectedBatchId', this.selectedBatchId);

          // 保存 studentId（后端返回的 user.userId 即 studentId）
          const studentId = response.data.user?.userId;
          if (studentId !== null && studentId !== undefined && studentId !== '') {
            localStorage.setItem('studentId', String(studentId));
            sessionStorage.setItem('studentId', String(studentId));
          }
          
          // 保存选中的轮次信息
          const selectedBatch = this.electiveBatches.find(
            batch => batch.batchId === this.selectedBatchId
          );
          if (selectedBatch) {
            localStorage.setItem('selectedBatch', JSON.stringify(selectedBatch));
          }
          
          // 登录成功，跳转到主页
          this.$router.push('/home');
        } else {
          this.errorMessage = response.data.message || '登录失败';
          // 登录失败刷新验证码
          this.refreshCaptcha();
          this.loginForm.captcha = '';
        }
      } catch (error) {
        console.error('登录错误:', error);
        this.errorMessage = error.response?.data?.message || '登录失败，请检查网络连接';
        // 登录失败刷新验证码
        this.refreshCaptcha();
        this.loginForm.captcha = '';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f5f5;
}

.login-box {
  background: white;
  padding: 25px 35px 20px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(124, 31, 137, 0.3);
  width: 100%;
  max-width: 420px;
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

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 0;
}

.logo-image {
  width: 65px;
  height: 65px;
  margin-bottom: 10px;
  object-fit: contain;
}

.system-title {
  color: #7C1F89;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

.login-subtitle {
  color: #7C1F89;
  font-size: 18px;
  font-weight: 600;
  margin: 15px 0 8px;
  letter-spacing: 3px;
}

.login-hint {
  color: #7f8c8d;
  font-size: 13px;
  margin: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.input-group {
  position: relative;
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.input-label {
  font-size: 13px;
  font-weight: 600;
  color: #7C1F89;
  margin-bottom: 5px;
  display: block;
}

.input-group input,
.input-group select {
  width: 100%;
  padding: 10px 12px;
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

.input-group select {
  cursor: pointer;
}

.input-group input::placeholder {
  color: #bdc3c7;
}

.input-group select option {
  background: white;
  color: #2c3e50;
  padding: 10px;
}

.error-message {
  color: #e74c3c;
  background: #fee;
  padding: 12px 15px;
  border-radius: 8px;
  margin-bottom: 20px;
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

.login-button {
  width: 100%;
  padding: 11px;
  background: linear-gradient(135deg, #7C1F89 0%, #5A1566 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(124, 31, 137, 0.4);
  margin-bottom: 12px;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(124, 31, 137, 0.5);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.register-link {
  text-align: center;
  margin-top: 15px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
  color: #7f8c8d;
  font-size: 14px;
}

.register-link a {
  color: #7C1F89;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #5A1566;
  text-decoration: underline;
}

.login-footer {
  text-align: center;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.login-footer p {
  color: #95a5a6;
  font-size: 12px;
  margin: 0;
}

.captcha-container {
  display: flex;
  gap: 18px;
  margin-bottom: 20px;
  align-items: flex-start;
}

.captcha-input {
  flex: 0 1 auto;
  margin-bottom: 0;
  max-width: calc(100% - 130px);
}

.captcha-wrapper {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.captcha-image {
  width: 120px;
  height: 42px;
  border: 2px solid #e0d4e8;
  border-radius: 8px;
  cursor: pointer;
  display: block;
  background: white;
  transition: all 0.3s ease;
  overflow: hidden;
  padding: 0;
  line-height: 0;
}

.captcha-image:hover {
  border-color: #7C1F89;
  box-shadow: 0 0 0 3px rgba(124, 31, 137, 0.1);
}

.captcha-image img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: fill;
  border-radius: 6px;
}

.captcha-loading {
  color: #95a5a6;
  font-size: 12px;
}
</style>
