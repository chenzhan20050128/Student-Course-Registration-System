# 前端字段映射修复说明

## 修复时间
2026-01-22 13:50

## 问题描述
管理员页面的"课程管理"表格无法显示数据，虽然后端成功返回了数据。

## 根本原因
前端表格列的 `prop` 属性仍然使用旧字段名，无法从后端返回的新字段名数据中提取值。

## 已修复文件
- `frontend/src/components/admin-components/CourseManagement.vue`

## 字段映射对照表

| 旧字段名 (前端) | 新字段名 (后端) | 说明 |
|----------------|----------------|------|
| `cname` | `courseName` | 课程名称 |
| `major` | `college` | 学院/专业 |
| `teacher` | `instructorName` | 授课教师 |
| `address` | `campus` | 校区位置 |
| `num` | `enrolledCount` | 已选人数 |
| `stock` | `capacity` | 课程容量 |
| `credit` | `credits` | 学分 |
| `cimage` | (移除) | 课程图片字段已废弃 |
| `cbook` | (移除) | 课程书籍字段已废弃 |
| `id` | `courseId` | 课程ID |
| - | `classroom` | 教室号（新增） |
| - | `description` | 课程描述（新增） |
| - | `startWeek` | 开始周（新增） |
| - | `endWeek` | 结束周（新增） |
| - | `type` | 课程类型（新增） |

## 修复内容

### 1. 表格列映射更新
```vue
<!-- 修改前 -->
<el-table-column prop="cname" label="课程名"></el-table-column>
<el-table-column prop="major" label="专业"></el-table-column>
<el-table-column prop="teacher" label="教师"></el-table-column>

<!-- 修改后 -->
<el-table-column prop="courseName" label="课程名"></el-table-column>
<el-table-column prop="college" label="学院"></el-table-column>
<el-table-column prop="instructorName" label="教师"></el-table-column>
```

### 2. 数据对象初始化更新
```typescript
// 修改前
const newCourse = ref({ 
  cname: '', major: '', teacher: '', address: '', 
  num: null, stock: null, credit: null 
})

// 修改后
const newCourse = ref({ 
  courseName: '', college: '', instructorName: '', campus: '', 
  classroom: '', enrolledCount: 0, capacity: null, credits: null,
  description: '', startWeek: 1, endWeek: 16, type: 'Required'
})
```

### 3. 表单绑定更新
所有 `v-model` 绑定已更新为新字段名。

### 4. 删除操作ID更新
```typescript
// 修改前
handleDelete(scope.row.id)

// 修改后
handleDelete(scope.row.courseId)
```

## 验证步骤

1. ✅ 保存文件
2. ✅ 前端会自动热重载（Vite）
3. ✅ 刷新管理员课程管理页面
4. ✅ 应该能看到6门课程数据

## 预期结果

表格应显示以下数据：
- Data Structure (计算机科学学院, Li Hua, 50人容量)
- Advanced Mathematics (数学学院, Xiao Yuanming, 60人容量)
- Software Engineering and Computing (软件学院, Liu Qin, 40人容量)
- Linear Algebra (数学学院, Nie Ziwei, 60人容量)
- Operating System (计算机科学学院, Zhang Wei, 30人容量)
- Demand and Business Model Analysis (软件学院, Liu Tao, 45人容量)

## 相关文件
- 后端Mapper: `backend/src/main/resources/com/scrs/mapper/StudentCourseMapper.xml`
- 数据库Schema: `backend/src/main/resources/db_scrs.sql`
- 前端组件: `frontend/src/components/admin-components/CourseManagement.vue`
