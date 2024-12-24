<template>
  <el-header>
    <div class="navbar">
      <div class="navbar-content">
        <span class="username-label">当前用户：</span>
        <span>{{ username }}</span>
        <el-button 
          type="primary" 
          class="logout-button" 
          @click="logout"
        >
          登出
        </el-button>
        <!-- 新增动态欢迎信息 -->
        <span class="welcome-message">
          欢迎使用{{ currentPageName }}系统
        </span>
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
      required: true, // 父组件传递的当前页面名称
    },
  },
  data() {
    return {
      username: '', // 用户名
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
.navbar {
  display: flex;
  justify-content: flex-start; /* 左对齐 */
  align-items: center;
  padding: 0px 20px;
}

.navbar-content {
  display: flex;
  align-items: center;
}

.username-label {
  font-weight: bold; /* 加粗字体 */
  font-size: 16px;
}

.navbar-content span {
  font-size: 16px;
}

/* 新增欢迎信息样式 */
.welcome-message {
  margin-left: 20px; /* 欢迎信息与登出按钮之间的间距 */
  font-weight: bold; /* 加粗文字 */
  font-size: 16px; /* 与用户名一致的字号 */
  color: #6a005f; /* 紫色字体，和整体风格一致 */
}

/* 自定义登出按钮样式 */
.logout-button {
  margin-left: 15px; /* 控制按钮与文字之间的间距 */
  background-color: #8b007a !important; /* 按钮背景设置为南大紫 */
  border-color: #8b007a !important; /* 按钮边框设置为南大紫 */
  color: white !important; /* 按钮文字颜色设置为白色 */
}

.logout-button:hover {
  background-color: #a70c94 !important; /* 悬停时背景颜色稍微变亮 */
  border-color: #a70c94 !important; /* 悬停时边框颜色一致 */
}
</style>