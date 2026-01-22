@echo off
echo ============================================
echo 正在执行数据库修复脚本...
echo ============================================

mysql -u root -p --default-character-set=utf8mb4 < backend\database_field_fix.sql

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo 数据库修复成功！
    echo ============================================
) else (
    echo.
    echo ============================================
    echo 数据库修复失败！请检查MySQL连接
    echo ============================================
)

pause
