# 快速参考指南 - 字段名统一

## 🔍 快速查找

### Course 对象字段名 (前端使用)
```javascript
// 基本信息
course.courseId          // 课程ID
course.courseName        // 课程名称
course.credits           // 学分
course.description       // 课程描述

// 学院和教师
course.college           // 所属学院
course.instructorName    // 授课教师

// 地点和时间
course.campus            // 教学地点 (校区)
course.classroom         // 教室号
course.startWeek         // 开始周
course.endWeek           // 结束周

// 选课信息
course.enrolledCount     // 已选人数
course.capacity          // 课程容量
course.type              // 课程类型

// 已废弃字段 (不再使用)
// course.id              ❌ 改为 course.courseId
// course.cname           ❌ 改为 course.courseName
// course.num             ❌ 改为 course.enrolledCount
// course.stock           ❌ 改为 course.capacity
// course.major           ❌ 改为 course.college
// course.teacher         ❌ 改为 course.instructorName
// course.address         ❌ 改为 course.campus
// course.credit          ❌ 改为 course.credits
// course.cimage          ❌ 已移除
// course.cbook           ❌ 已移除
```

### Student 对象字段名 (保持不变)
```javascript
student.id               // 学生ID
student.sname            // 学生姓名
student.sex              // 性别
student.age              // 年龄
student.major            // 专业
student.college          // 学院
student.simage           // 学生头像
```

### College 对象字段名 (保持不变)
```javascript
college.id               // 学院ID
college.cname            // 学院名称
```

## 📡 API 端点速查

### 获取课程列表
```javascript
// 请求
axios.get('/student/listCourseByMajorName', {
  params: {
    majorName: '计算机学院',
    pageNum: 1,
    pageSize: 6
  }
})

// 响应 (course 对象使用新字段名)
response.data.data.list[0] = {
  courseId: 1,
  courseName: "Java编程",
  enrolledCount: 30,
  capacity: 50
  // ... 更多字段
}
```

### 获取已选课程
```javascript
// 请求
axios.get('/student/listMyCourse', {
  params: {
    sid: 1,
    pageNum: 1,
    pageSize: 1000
  }
})

// 响应 (course 对象使用新字段名)
response.data.data.list[0] = {
  id: 1,
  sid: 1,
  cid: 1,
  status: 1,
  course: {
    courseId: 1,
    courseName: "Java编程",
    enrolledCount: 30,
    capacity: 50
    // ... 更多字段
  }
}
```

### 选课
```javascript
// 请求
axios.get('/student/selectCourse', {
  params: {
    sid: 1,
    cid: 1
  }
})

// 响应
response.data = {
  code: 1,
  msg: "选课成功"
}
```

## 🔄 常见修改模式

### 模板中的绑定
```vue
<!-- 旧模式 ❌ -->
<h3>{{ course.cname }}</h3>
<p>{{ course.teacher }}</p>
<span>{{ course.num }} / {{ course.stock }}</span>

<!-- 新模式 ✅ -->
<h3>{{ course.courseName }}</h3>
<p>{{ course.instructorName }}</p>
<span>{{ course.enrolledCount }} / {{ course.capacity }}</span>
```

### 条件判断
```javascript
// 旧模式 ❌
if (course.num >= course.stock) {
  // 课程已满
}

// 新模式 ✅
if (course.enrolledCount >= course.capacity) {
  // 课程已满
}
```

### 数组操作
```javascript
// 旧模式 ❌
const isFull = courses.some(c => c.num >= c.stock)
const courseIds = courses.map(c => c.id)

// 新模式 ✅
const isFull = courses.some(c => c.enrolledCount >= c.capacity)
const courseIds = courses.map(c => c.courseId)
```

### 对象赋值
```javascript
// 旧模式 ❌
const newCourse = {
  cname: 'Java',
  teacher: '张三',
  address: '教室101',
  num: 30,
  stock: 50
}

// 新模式 ✅
const newCourse = {
  courseName: 'Java',
  instructorName: '张三',
  campus: '教室101',
  enrolledCount: 30,
  capacity: 50
}
```

## 🧪 测试检查清单

### 功能测试
- [ ] 课程列表显示所有课程
- [ ] 课程名称正确显示
- [ ] 课程容量显示格式: "30 / 50"
- [ ] 容量满的课程被禁用
- [ ] 已选课程有标记
- [ ] 选课成功提示
- [ ] 退选功能正常

### 数据验证
- [ ] 所有课程都能正确显示
- [ ] 选课人数 = enrolledCount
- [ ] 课程容量 = capacity
- [ ] 学院信息 = college
- [ ] 教师信息 = instructorName

### 错误检查
- [ ] 浏览器控制台无 "undefined" 错误
- [ ] 浏览器控制台无 API 错误
- [ ] 网络请求返回 200 状态
- [ ] API 响应包含新字段名

## 🐛 常见错误排查

### 错误: "Cannot read property 'courseName' of undefined"
**原因**: course 对象为 undefined
**检查**: 
1. API 是否返回了 course 对象
2. 数据结构是否如期望

### 错误: "courseId is undefined"
**原因**: 后端返回的仍是旧字段名
**解决**:
1. 检查后端是否已编译新代码
2. 检查 Course.java 是否更新
3. 清除 Redis 缓存

### 错误: "404 Not Found"
**原因**: API 端点不存在或参数错误
**检查**:
1. 后端服务是否启动
2. API 路径是否正确
3. 请求参数是否正确

### 错误: 课程列表为空
**原因**: 数据库无数据或查询条件错误
**检查**:
1. 数据库 course 表是否有数据
2. 学生的 major/college 字段是否有值
3. 数据库字段名是否正确 (snake_case)

## 📋 修改跟踪

### 哪些文件被修改了
```
后端:
✓ backend/src/main/java/com/scrs/pojo/Course.java
✓ backend/src/main/java/com/scrs/controller/StudentCourseController.java
✓ backend/src/main/java/com/scrs/controller/CourseController.java
✓ backend/src/main/java/com/scrs/controller/StudentController.java
✓ backend/src/main/java/com/scrs/controller/TeacherCourseController.java
✓ backend/src/main/java/com/scrs/service/impl/StudentServiceImpl.java
✓ backend/src/main/java/com/scrs/service/impl/TeacherServiceImpl.java
✓ backend/src/main/java/com/scrs/service/impl/UserServiceImpl.java

前端:
✓ frontend/src/components/student-components/CourseSelection.vue
✓ frontend/src/components/student-components/CourseRecord.vue
✓ frontend/src/components/teacher-components/TeachingInfo.vue
✓ frontend/src/components/admin-components/StudentCourseSelection.vue
✓ frontend/src/components/admin-components/CourseAllocation.vue
✓ frontend/src/components/admin-components/CourseManagement.vue
```

### 哪些文件没被修改
```
不需要修改（使用不同的字段名）:
- Student 实体 (使用 major, college)
- College 实体 (使用 cname)
- Teacher 实体 (使用其他字段)
- Major 实体
- 其他组件和服务
```

## 💡 提示和技巧

### 快速搜索新字段用法
```javascript
// 搜索所有使用新字段的地方
// IDE: Ctrl+F 搜索 "course.courseName" 或 "course.enrolledCount"
```

### 快速检查是否有遗漏的旧字段
```javascript
// 搜索可疑的旧字段
// IDE: Ctrl+F 搜索 "course.cname" 或 "course.num" 等
// 应该不返回任何结果（除了注释或 College/Teacher 对象）
```

### 调试数据流
```javascript
// 在 Vue 方法中添加调试语句
console.log('收到的课程数据:', response.data.data)
console.log('课程 ID:', response.data.data.list[0].courseId)
console.log('课程名称:', response.data.data.list[0].courseName)
```

## ✅ 验证步骤

```bash
# 1. 后端编译检查
cd backend
mvn clean compile
# 应该无主要编译错误 ✓

# 2. 启动后端
mvn spring-boot:run

# 3. 启动前端（新窗口）
cd ../frontend
npm run dev

# 4. 打开浏览器
# http://localhost:5173

# 5. 打开开发者工具 (F12)
# 检查 Network 标签中 API 响应
# 确认 response 中包含 courseId, courseName 等新字段

# 6. 检查 Console 标签
# 确保没有 "Cannot read property" 错误
```

## 📞 需要帮助？

### 关键文档位置
- **详细字段映射**: `FIELD_NAMES_UPDATE.md`
- **前端更新报告**: `FRONTEND_FIELD_UPDATES_REPORT.md`
- **最终验证清单**: `FINAL_VERIFICATION_CHECKLIST.md`
- **完成工作报告**: `WORK_COMPLETION_REPORT.md`

### 快速检查清单
```
✓ 后端代码已编译
✓ 前端代码已启动
✓ 可以成功登录
✓ 课程列表显示正确
✓ 能够正常选课
✓ 浏览器无错误提示
```

---

**版本**: 1.0
**最后更新**: 2024年
**状态**: ✅ 有效
