# 基于 Node 构建前端产物，再用 Nginx 生产环境托管
FROM node:20 AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install --registry=https://registry.npmmirror.com
COPY . .
RUN npm run build

FROM Nginx:1.25-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]