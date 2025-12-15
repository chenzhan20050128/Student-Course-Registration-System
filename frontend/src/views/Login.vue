<template>
  <div class="login-page">
    <div class="login-container">
      <div class="system-title">
        <h1>学生选课管理系统</h1>
        <p class="subtitle">Student Course Registration System</p>
      </div>
      
      <div class="login-card">
        <h2 class="card-title">用户登录</h2>
        
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-item">
            <input 
              type="text" 
              v-model="username" 
              placeholder="请输入用户名"
              required
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <input 
              type="password" 
              v-model="password" 
              placeholder="请输入密码"
              required
              class="form-input"
            />
          </div>
          
          <div class="form-item">
            <select v-model="role" required class="form-select">
              <option value="" disabled>请选择角色</option>
              <option value="1">管理员</option>
              <option value="2">教师</option>
              <option value="3">学生</option>
            </select>
          </div>
          
          <button type="submit" class="login-btn" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
          
          <div class="register-section">
            <span class="register-text">还没有账号？</span>
            <a @click="goToRegister" class="register-link">立即注册</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      role: '',
      loading: false
    };
  },
  methods: {
    async handleLogin() {
      if (!this.username || !this.password || !this.role) {
        this.$message.warning('请填写完整的登录信息');
        return;
      }
      
      this.loading = true;
      const loginData = {
        username: this.username,
        password: this.password,
        role: this.role
      };
      
      try {
        const response = await this.$http.post('/login', loginData);
        if (response.data.code) {
          this.$message.success('登录成功');
          const role = response.data.data.role;
          
          setTimeout(() => {
            if (role === 1) {
              this.$router.push('/admin-home');
            } else if (role === 2) {
              this.$router.push('/teacher-home');
            } else if (role === 3) {
              this.$router.push('/student-home');
            }
          }, 500);
        } else {
          this.$message.error(response.data.msg || '登录失败，请检查用户名和密码');
        }
      } catch (error) {
        console.error('登录失败', error);
        this.$message.error('登录失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
};
</script>

<style scoped>
/* 页面整体布局 */
.login-page {
  background: linear-gradient(135deg, #8b007a 0%, #5a004f 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100vw;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

/* 背景动画 */
.login-page::before {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: moveBackground 20s linear infinite;
}

@keyframes moveBackground {
  0% { transform: translate(0, 0); }
  100% { transform: translate(50px, 50px); }
}

.login-container {
  position: relative;
  z-index: 1;
  text-align: center;
}

/* 系统标题 */
.system-title {
  margin-bottom: 40px;
  color: white;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
}

.system-title h1 {
  font-size: 42px;
  font-weight: 700;
  margin: 0 0 10px 0;
  letter-spacing: 2px;
}

.system-title .subtitle {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 1px;
  margin: 0;
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 45px 40px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  width: 420px;
  max-width: 100%;
  animation: slideIn 0.6s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-title {
  color: #333;
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 35px 0;
  text-align: center;
}

/* 表单样式 */
.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 24px;
}

.form-input,
.form-select {
  width: 100%;
  height: 48px;
  padding: 0 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 15px;
  box-sizing: border-box;
  transition: all 0.3s;
  background: #fff;
}

.form-input:hover,
.form-select:hover {
  border-color: #c0c4cc;
}

.form-input:focus,
.form-select:focus {
  border-color: #8b007a;
  outline: none;
}

.form-input::placeholder {
  color: #c0c4cc;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #8b007a 0%, #5a004f 100%);
  border: none;
  color: white;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(139, 0, 122, 0.4);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 注册区域 */
.register-section {
  margin-top: 20px;
  text-align: center;
}

.register-text {
  color: #666;
  font-size: 14px;
  margin-right: 5px;
}

.register-link {
  font-size: 14px;
  font-weight: 600;
  color: #8b007a;
  cursor: pointer;
  text-decoration: none;
}

.register-link:hover {
  color: #5a004f;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    padding: 35px 25px;
  }
  
  .system-title h1 {
    font-size: 32px;
  }
  
  .system-title .subtitle {
    font-size: 14px;
  }
}
</style>