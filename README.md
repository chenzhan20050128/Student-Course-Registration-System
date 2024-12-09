## 启动前端步骤:
端口号：5173
1. 在./frontend/目录下打开终端
2. 首次启动先，输入 npm install 来安装需要的依赖
3. 随后的每次启动，输入 npm run dev ，等待几秒后点击弹出的(http://localhost:5173/)

## 启动后端步骤：
端口号：8080
1. 启动本地的MySQL，输入密码登录
2. 在MySQLsh输入以下命令来使用本数据库（数据库的路径要替换成绝对路径or相对路径）
```bash 
source db_scrs.sql
```

3. 在backend\src\main\resources\application.yml文件中把密码修改成本地的密码（待改进）
4. backend\src\main\java\com\scrs\ScrsApplication.java文件下通过main函数启动