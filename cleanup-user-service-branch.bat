@echo off
setlocal EnableDelayedExpansion

echo 开始清理micro_user_service分支，只保留用户服务相关文件...
echo.

REM 要删除的目录
set DIRS_TO_DELETE=ai_service api-gateway backup_earning-service common-lib content-service course-service frontend learning-service kubernetes performance-tests private_kb storage node_modules file src target

REM 要删除的文件
set FILES_TO_DELETE=courseplatfoem.sql docker-compose.yml homework_file.txt main.yml mvnw mvnw.cmd package-lock.json package.json pom.xml run_frontend_tests.bat temp_form.html test.txt

REM 要删除的Markdown文件
set MD_FILES_TO_DELETE=AI微服务架构重构完成总结.md AI微服务通信需求分析.md AI微服务重构说明.md AI接口格式修复报告.md AI接口格式最终修复报告.md 微服务AI配置说明.md 微服务端口配置修复总结.md 拆分计划.md 数据库拆分.md 配置说明和部署说明.md

REM 要删除的HTTP测试文件
set HTTP_FILES_TO_DELETE=测试AI接口修复.http

echo 删除不相关的目录...
for %%d in (%DIRS_TO_DELETE%) do (
    if exist "%%d" (
        echo 删除目录: %%d
        rmdir /s /q "%%d" 2>nul
    )
)

echo.
echo 删除不相关的文件...
for %%f in (%FILES_TO_DELETE%) do (
    if exist "%%f" (
        echo 删除文件: %%f
        del /q "%%f" 2>nul
    )
)

echo.
echo 删除不相关的Markdown文件...
for %%f in (%MD_FILES_TO_DELETE%) do (
    if exist "%%f" (
        echo 删除文件: %%f
        del /q "%%f" 2>nul
    )
)

echo.
echo 删除不相关的HTTP测试文件...
for %%f in (%HTTP_FILES_TO_DELETE%) do (
    if exist "%%f" (
        echo 删除文件: %%f
        del /q "%%f" 2>nul
    )
)

REM 清理K8s目录中的非用户服务配置
if exist "k8s" (
    echo.
    echo 清理K8s目录，只保留用户服务相关配置...
    if exist "k8s\ai-service" (
        echo 删除目录: k8s\ai-service
        rmdir /s /q "k8s\ai-service" 2>nul
    )
    if exist "k8s\cluster" (
        echo 删除目录: k8s\cluster
        rmdir /s /q "k8s\cluster" 2>nul
    )
)

REM 清理scripts目录中的非用户服务脚本
if exist "scripts" (
    echo.
    echo 清理scripts目录...
    if exist "scripts\deploy-microservices.sh" (
        echo 删除文件: scripts\deploy-microservices.sh
        del /q "scripts\deploy-microservices.sh" 2>nul
    )
)

REM 清理.github/workflows目录中的非用户服务工作流
if exist ".github\workflows" (
    echo.
    echo 清理GitHub工作流目录...
    if exist ".github\workflows\ai-service.yml" (
        echo 删除文件: .github\workflows\ai-service.yml
        del /q ".github\workflows\ai-service.yml" 2>nul
    )
)

echo.
echo ========================================
echo 清理完成！
echo ========================================
echo.
echo 保留的主要文件和目录：
dir /b
echo.
echo 用户服务相关文件：
if exist "user-service" (
    echo - user-service/
    dir /b user-service
)
echo.
if exist "sql\user.sql" (
    echo - sql\user.sql （用户数据库脚本）
)
if exist ".github\workflows\user-service.yml" (
    echo - .github\workflows\user-service.yml （用户服务CI/CD）
)
if exist "k8s\user-service" (
    echo - k8s\user-service/ （用户服务K8s配置）
)

echo.
echo 分支专用化完成！现在这个分支只包含用户服务相关的代码。
pause
