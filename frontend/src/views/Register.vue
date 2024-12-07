<template>
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
          username: this.username,
          password: this.password,
          role: this.role
        };
        try {
          // 发送注册请求到后端
          const response = await this.$http.post('/register', registerData);
          // 处理注册成功逻辑
          console.log('注册成功', response.data);
          this.$router.push('/login');
        } catch (error) {
          // 处理注册失败逻辑
          console.error('注册失败', error);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  /* 添加一些样式 */
  .register {
    max-width: 300px;
    margin: 0 auto;
    padding: 1em;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  .register h2 {
    text-align: center;
  }
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
  </style>