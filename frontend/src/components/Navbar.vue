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
    fetchUserInfo() {
      axios.get('/getRoleMessage', { params: { inDatabase: 0 } }).then((response) => {
        if (response.data.code === 1) {
          console.log('Session Data:', response.data.data); // 打印会话变量
          this.username = response.data.data.username || response.data.data.tname || response.data.data.sname;
        } else {
          this.$message.error(response.data.msg || '获取用户信息失败');
        }
      });
    },
    // fetchSessionAttributes() {
    //   axios.get('/getSessionAttributes').then((response) => {
    //     if (response.data.code === 1) {
    //       console.log('Session Attributes:', response.data.data); // 打印会话属性
    //     } else {
    //       this.$message.error(response.data.msg || '获取会话属性失败');
    //     }
    //   });
    // },
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
    this.fetchUserInfo();
    //this.fetchSessionAttributes(); // 获取并打印会话属性
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