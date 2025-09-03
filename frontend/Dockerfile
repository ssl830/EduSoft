# 生产阶段
FROM nginx:alpine

# 直接复制本地的 dist 目录
COPY dist/ /usr/share/nginx/html/

# 复制 nginx 配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
