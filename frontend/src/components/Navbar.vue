<template>
  <el-header class="navbar-wrapper">
    <div class="navbar">
      <div class="navbar-left">
        <span class="welcome-text">欢迎, <strong>{{ username }}</strong></span>
      </div>
      <div class="navbar-right">
        <span class="page-name">{{ currentPageName }}系统</span>
        <el-button 
          type="primary" 
          class="logout-button" 
          @click="logout"
          size="small"
        >
          退出登录
        </el-button>
      </div>
    </div>
  </el-header>
</template>

<script>
import axios from '@/http';

export default {
  name: 'Navbar',
  props: {
    currentPageName: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      username: '',
    };
  },
  methods: {
    fetchUserInfo() {
      axios.get('/getRoleMessage', { params: { inDatabase: 0 } }).then((response) => {
        if (response.data.code === 1) {
          this.username = response.data.data.username || response.data.data.tname || response.data.data.sname;
        } else {
          this.$message.error(response.data.msg || '获取用户信息失败');
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
    this.fetchUserInfo();
  },
};
</script>

<style scoped>
.navbar-wrapper {
  padding: 0 !important;
  background-color: #ffffff !important;
  border-bottom: 1px solid #e0e0e0;
  height: auto !important;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  height: 60px;
}

.navbar-left {
  flex: 1;
  display: flex;
  align-items: center;
}

.welcome-text {
  font-size: 15px;
  color: #333;
}

.welcome-text strong {
  color: #667eea;
  font-weight: 600;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-name {
  font-size: 14px;
  color: #666;
}

.logout-button {
  background-color: #667eea !important;
  border-color: #667eea !important;
  color: white !important;
  padding: 6px 16px !important;
  font-size: 14px !important;
  border-radius: 4px !important;
}

.logout-button:hover {
  background-color: #5568d3 !important;
  border-color: #5568d3 !important;
}
</style>