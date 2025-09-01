@echo off
chcp 65001 >nul

echo 开始清理micro_user_service分支，只保留用户服务相关文件...

:: 要删除的目录
set DIRS_TO_DELETE=ai_service api-gateway backup_earning-service common-lib content-service course-service frontend learning-service kubernetes performance-tests private_kb storage node_modules file src target

:: 要删除的文件
set FILES_TO_DELETE=AI微服务架构重构完成总结.md AI微服务通信需求分析.md AI微服务重构说明.md AI接口格式修复报告.md AI接口格式最终修复报告.md courseplatfoem.sql docker-compose.yml homework_file.txt main.yml mvnw mvnw.cmd package-lock.json package.json pom.xml run_frontend_tests.bat temp_form.html test.txt 微服务AI配置说明.md 微服务端口配置修复总结.md 拆分计划.md 数据库拆分.md 测试AI接口修复.http 配置说明和部署说明.md

:: 删除目录
for %%d in (%DIRS_TO_DELETE%) do (
    if exist "%%d" (
        echo 删除目录: %%d
        rmdir /s /q "%%d"
    )
)

:: 删除文件
for %%f in (%FILES_TO_DELETE%) do (
    if exist "%%f" (
        echo 删除文件: %%f
        del /f /q "%%f"
    )
)

echo 清理完成！
echo 保留的文件和目录：
dir

pause