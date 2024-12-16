以下是针对`AccountController`类中每个方法的文档，遵照示例的格式：

---

### 7.1. 用户登录 - 用户登录接口

**地址**

POST `/login`

**请求类型**

`application/json`

**参数**

- `loginRequest`: 包含用户名、密码和角色的信息的请求对象

**返回**

- `R<Map<String, Object>>`: 返回用户的登录结果和角色信息，成功则返回角色信息，失败返回错误信息。

**说明**

此接口处理用户登录请求。根据请求中的用户名、密码和角色验证用户，并将相关信息存储在会话中。

---

### 7.2. 用户登出 - 用户登出接口

**地址**

GET `/logout`

**请求类型**

`n/a`

**参数**

- `HttpSession session`: 用于管理用户会话的HttpSession对象

**返回**

- `String`: 返回重定向至的页面路径。

**说明**

该接口处理用户登出请求，会话被置为无效，并重定向至主页。

---

### 7.3. 获取用户名 - 获取当前登录用户名

**地址**

GET `/getUserName`

**请求类型**

`n/a`

**参数**

- `HttpSession session`: 用于管理用户会话的HttpSession对象

**返回**

- `R<User>`: 返回当前会话中存储的用户信息，包括用户名和头像。

**说明**

此接口用于获取当前登录用户的用户名信息。

---

### 7.4. 用户个人中心 - 获取用户个人信息

**地址**

GET `/profile`

**请求类型**

`n/a`

**参数**

- `HttpSession session`: 用于管理用户会话的HttpSession对象
- `Model model`: 用于交互数据的Model对象

**返回**

- `R`: 返回当前用户的详细角色信息（管理员、老师、学生），未登录则返回错误信息。

**说明**

此接口用于获取用户个人中心信息，具体取决于登录用户的角色。

---

### 7.5. 更新管理员资料 - 更新管理员信息

**地址**

POST `/updateAdminProfile`

**请求类型**

`application/json`

**参数**

- `User user`: 包含更新信息的User对象
- `MultipartFile file`: 用户上传的文件

**返回**

- `R<String>`: 返回更新结果信息。

**说明**

该接口用于更新管理员的个人资料，如有文件上传则会处理文件。

---

### 7.6. 更新教师资料 - 更新教师信息

**地址**

POST `/updateTeacherProfile`

**请求类型**

`application/json`

**参数**

- `Teacher teacher`: 包含更新信息的Teacher对象
- `MultipartFile file`: 用户上传的文件

**返回**

- `R<String>`: 返回更新结果信息。

**说明**

此接口用于更新教师个人资料，如有文件上传则会处理文件。

---

### 7.7. 更新学生资料 - 更新学生信息

**地址**

POST `/updateStudentProfile`

**请求类型**

`application/json`

**参数**

- `Student student`: 包含更新信息的Student对象
- `MultipartFile file`: 用户上传的文件

**返回**

- `R<String>`: 返回更新结果信息。

**说明**

此接口用于更新学生个人资料，如有文件上传则会处理文件。

---

### 7.8. 注册 - 用户注册接口

**地址**

GET `/register`

**请求类型**

`application/x-www-form-urlencoded`

**参数**

- `Integer role`: 注册角色
- `String userName`: 用户名
- `String userPwd`: 用户密码
- `String confirmPwd`: 确认密码

**返回**

- `R<String>`: 返回注册结果，成功或失败的信息。

**说明**

此接口用于新用户注册，根据提供的角色（管理员、教师、学生）进行不同的注册逻辑处理。

---

### 7.9. 转换文件方法 - 处理文件上传

**说明**

`transfileAdmin`、`transfileTeacher` 和 `transfileStudent` 方法是为不同角色处理文件上传的辅助方法，用来处理文件存储路径和重命名。

**参数**

- `User/Teacher/Student`: 角色对象
- `MultipartFile file`: 上传的文件

**返回**

- `void`: 不返回值，内部修改传递对象的文件字段。

**说明**

方法内部根据不同角色专用字段设置文件名称，通过时间戳生成唯一文件名，并将文件存储至指定路径。

---

### 7.10. 不再需要的页面跳转方法

以下两个方法被注释为不再需要：

- `toRegister`: 跳转到注册页面，不再需要，前端直接跳转。
- `toLogin`: 跳转到登录页面，不再需要，前端直接跳转。