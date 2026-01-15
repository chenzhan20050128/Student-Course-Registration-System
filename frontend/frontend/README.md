# 课程查询系统 - 前端测试页面

这是一个简单的Vue 3前端项目，用于测试后端RESTful API。

## 功能

1. **课程条件查询** - 根据课程的各种字段（课程ID、名称、学院、校区等）查询课程信息
2. **节次条件查询** - 根据上课时间（周几、节次范围）查询课程ID列表

## 技术栈

- Vue 3 (Composition API)
- Vite
- Axios

## 安装和运行

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

前端将在 `http://localhost:3000` 启动。

### 3. 确保后端服务运行

后端服务应该运行在 `http://localhost:8080`。如果后端端口不同，请修改 `vite.config.js` 中的代理配置。

## 项目结构

```
frontend/
├── src/
│   ├── api/
│   │   └── courseApi.js      # API调用封装
│   ├── components/
│   │   ├── CourseSearch.vue   # 课程查询组件
│   │   └── SessionSearch.vue # 节次查询组件
│   ├── App.vue                # 主应用组件
│   └── main.js                # 入口文件
├── index.html                 # HTML模板
├── vite.config.js             # Vite配置
└── package.json               # 项目配置
```

## API接口

### 1. 课程查询
- **URL**: `/api/courses/search`
- **方法**: POST
- **请求体**: `CourseQueryRequest` (所有字段可选)
- **响应**: `CourseWithSessionsDTO[]`

### 2. 节次查询
- **URL**: `/api/courses/search-by-session`
- **方法**: POST
- **请求体**: `SessionQueryRequest` (weekday, startPeriod, endPeriod)
- **响应**: `Long[]` (课程ID列表)

## 注意事项

- 确保后端服务已启动并运行在8080端口
- 如果遇到CORS问题，确保后端已配置跨域支持
- 前端通过Vite代理转发API请求，避免CORS问题

