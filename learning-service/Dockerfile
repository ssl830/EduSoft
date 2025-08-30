# 使用官方的 OpenJDK 8 作为基础镜像
FROM openjdk:8-jdk-alpine

# 将应用的 jar 文件添加到容器中
ADD target/learning-service-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8082

# 运行应用的命令
ENTRYPOINT ["java","-jar","/app.jar"]
