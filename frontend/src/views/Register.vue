<template>
  <div class="register-page">
    <div class="register-container">
      <div class="system-title">
        <h1>学生选课管理系统</h1>
        <p class="subtitle">Student Course Registration System</p>
      </div>
      
      <div class="register-card">
        <h2 class="card-title">用户注册</h2>
        
        <form @submit.prevent="handleRegister" class="register-form">
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
            <input 
              type="password" 
              v-model="confirmPassword" 
              placeholder="请确认密码"
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
          
          <button type="submit" class="register-btn" :disabled="loading">
            {{ loading ? '注册中...' : '注册' }}
          </button>
          
          <div class="login-section">
            <span class="login-text">已有账号？</span>
            <a @click="goToLogin" class="login-link">立即登录</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

 <script>
  export default {
    name: 'Register',
    data() {
      return {
        username: '',
        password: '',
        confirmPassword: '',
        role: '',
        loading: false
      };
    },
    methods: {
      async handleRegister() {
        if (!this.username || !this.password || !this.confirmPassword || !this.role) {
          this.$message.warning('请填写完整的注册信息');
          return;
        }
        
        if (this.password !== this.confirmPassword) {
          this.$message.error('密码和确认密码不一致');
          return;
        }
        
        this.loading = true;
        const registerData = {
          role: this.role,
          username: this.username,
          password: this.password,
          confirmPassword: this.confirmPassword,
        };
        
        try {
          const response = await this.$http.get('/register', {
            params: {
              role: registerData.role,
              userName: registerData.username,
              userPwd: registerData.password,
              confirmPwd: registerData.confirmPassword
            }
          });
          
          console.log(response);
          if (response.data.code) {
            this.$message.success('注册成功');
            setTimeout(() => {
              this.$router.push('/login');
            }, 500);
          } else {
            this.$message.error(response.data.msg || '注册失败，请重试');
          }
        } catch (error) {
          console.error('注册失败', error);
          this.$message.error('注册失败，请稍后重试');
        } finally {
          this.loading = false;
        }
      },
      goToLogin() {
        this.$router.push('/login');
      }
    }
  };
  </script>

<style scoped>
/* 页面整体布局 */
.register-page {
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
.register-page::before {
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

.register-container {
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

/* 注册卡片 */
.register-card {
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
.register-form {
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

/* 注册按钮 */
.register-btn {
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

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(139, 0, 122, 0.4);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 登录区域 */
.login-section {
  margin-top: 20px;
  text-align: center;
}

.login-text {
  color: #666;
  font-size: 14px;
  margin-right: 5px;
}

.login-link {
  font-size: 14px;
  font-weight: 600;
  color: #8b007a;
  cursor: pointer;
  text-decoration: none;
}

.login-link:hover {
  color: #5a004f;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
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