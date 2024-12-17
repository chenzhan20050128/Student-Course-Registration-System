<template>
    <el-header>
      <div class="navbar">
        <div class="navbar-left">
          <span>当前用户: {{ username }}</span>
        </div>
        <div class="navbar-right">
          <el-button type="primary" @click="logout">登出</el-button>
        </div>
      </div>
    </el-header>
  </template>
  
  <script>
  import axios from '@/http';
  
  export default {
    name: 'Navbar',
    data() {
      return {
        username: '',
      };
    },
    methods: {
      fetchUsername() {
        axios.get('/getUserName').then((response) => {
          if (response.data.code === 1) {
            this.username = response.data.data.username;
          } else {
            this.$message.error(response.data.msg || '获取用户名失败');
          }
        });
      },
      logout() {
        axios.get('/logout').then((response) => {
          if (response.data.code === 1) {
            this.$message.success('登出成功');
            this.$router.push('/login');
          } else {
            this.$message.error(response.data.msg || '登出失败');
          }
        });
      },
    },
    mounted() {
      this.fetchUsername();
    },
  };
  </script>
  
  <style scoped>
  .navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .navbar-left {
    font-size: 16px;
  }
  .navbar-right {
    margin-right: 20px;
  }
  </style>