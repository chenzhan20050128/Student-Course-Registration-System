<template>
  <div class="login-page">
    <!-- 登录部分 -->
    <div class="login">
      <h2>登录</h2>
      <form @submit.prevent="handleLogin">
        <div>
          <label for="username">用户名</label>
          <input type="text" v-model="username" required />
        </div>
        <div>
          <label for="password">密码</label>
          <input type="password" v-model="password" required />
        </div>
        <div>
          <label for="role">角色</label>
          <select v-model="role" required>
            <option value="" disabled>请选择角色</option>
            <option value="1">管理员</option>
            <option value="2">老师</option>
            <option value="3">学生</option>
          </select>
        </div>
        <button type="submit">登录</button>
      </form>
      <button @click="goToRegister" class="register-button">注册</button>
    </div>
  </div>
</template>

<script>
import { useStudentStore } from '@/store/student';
export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      role: ''
    };
  },
  methods: {
    async handleLogin() {
      // 处理登录逻辑
      const loginData = {
        username: this.username,
        password: this.password,
        role: this.role
      };
      try {
        console.log("loginData", loginData);
        const response = await this.$http.post('/login', loginData);
        if (response.data.code) {
          const role = response.data.data.role;
          if (role === 1) {
            this.$router.push('/admin-home');
          } else if (role === 2) {
            this.$router.push('/teacher-home');
          } else if (role === 3) {
            this.$router.push('/student-home');
          }
        } else {
          alert(response.data.message);
        }
      } catch (error) {
        console.error('登录失败', error);
      }
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
};
</script>

<style scoped>
/* 外层容器布局 */
.login-page {
  background: url('public/background_2.png') no-repeat center center;
  background-size: contain; /* 改为 contain，缩小图片大小并展示更多内容 */
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  box-sizing: border-box;
  padding: 0px;
}

/* 登录表单部分 */
.login {
  width: 300px;
  margin: 36px;
  padding: 36px;
  border: 2px solid #ccc;
  border-radius: 16px;
  background-color: aliceblue;
}

/* 标题样式 */
.login h2 {
  text-align: center;
}

/* 表单样式 */
.login form div {
  margin-bottom: 1em;
}

.login form label {
  display: block;
  margin-bottom: 0.5em;
}

.login form input,
.login form select {
  width: 100%;
  padding: 0.5em;
  box-sizing: border-box;
}

.login form button {
  width: 100%;
  padding: 0.5em;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.login form button:hover {
  background-color: #0056b3;
}

.register-button {
  width: 100%;
  padding: 0.5em;
  background-color: #28a745;
  color: rgb(255, 255, 255);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 0.7em;
  margin-bottom: 1.5em;
}
.register-button:hover {
  background-color: #218838;
}
</style>