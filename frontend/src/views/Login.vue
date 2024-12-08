<template>
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
    <button @click="goToRegister">注册</button>
  </div>
</template>

<script>
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
        // 发送登录请求到后端
        const response = await this.$http.post('/login', loginData);
        if (response.data.success) {
          // 根据角色跳转到不同的首页视图
          const role = response.data.role;
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
        // 处理登录失败逻辑
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
/* 添加一些样式 */
.login {
  max-width: 300px;
  margin: 0 auto;
  padding: 1em;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.login h2 {
  text-align: center;
}
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
</style>