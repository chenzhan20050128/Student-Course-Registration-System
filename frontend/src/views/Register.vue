<template>
  <div class="register-page">
    <!-- 注册部分 -->
    <div class="register">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div>
          <label for="username">用户名</label>
          <input type="text" v-model="username" required />
        </div>
        <div>
          <label for="password">密码</label>
          <input type="password" v-model="password" required />
        </div>
        <div>
          <label for="confirmPassword">确认密码</label>
          <input type="password" v-model="confirmPassword" required />
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
        <button type="submit">注册</button>
      </form>
      <button @click="goToLogin" class="login-button">返回登录</button>
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
        role: ''
      };
    },
    methods: {
      async handleRegister() {
        if (this.password !== this.confirmPassword) {
          alert('密码和确认密码不一致');
          return;
        }
        // 处理注册逻辑
        const registerData = {
          role: this.role,
          username: this.username,
          password: this.password,
          confirmPassword: this.confirmPassword,
        };
        try {
          // 发送注册请求到后端
          const response = await this.$http.get('/register', {
            params: {
            role: registerData.role,
            userName: registerData.username,
            userPwd: registerData.password,
            confirmPwd: registerData.confirmPassword }
          });
          console.log(response);
          // 处理注册成功逻辑
          console.log('注册成功', response.data);
          this.$router.push('/login');
        } catch (error) {
          // 处理注册失败逻辑
          console.error('注册失败', error);
        }
      },
      goToLogin() {
        this.$router.push('/login');
      }
    }
  };
  </script>

<style scoped>
/* 外层容器布局，复制登录页面样式 */
.register-page {
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

/* 注册表单部分，样式与登录页保持一致 */
.register {
  width: 300px;
  margin: 36px;
  padding: 36px;
  border: 2px solid #ccc;
  border-radius: 16px;
  background-color: aliceblue;
}

/* 标题样式 */
.register h2 {
  text-align: center;
}

/* 表单样式 */
.register form div {
  margin-bottom: 1em;
}

.register form label {
  display: block;
  margin-bottom: 0.5em;
}

.register form input,
.register form select {
  width: 100%;
  padding: 0.5em;
  box-sizing: border-box;
}

.register form button {
  width: 100%;
  padding: 0.5em;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.register form button:hover {
  background-color: #0056b3;
}

.login-button {
  width: 100%;
  padding: 0.5em;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 0.7em;
  margin-bottom: 1.5em;
}

.login-button:hover {
  background-color: #218838;
}
</style>