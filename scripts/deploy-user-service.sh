#!/bin/bash

# EduSoft 用户服务专用部署脚本
# 适用于 micro_user_service 分支

set -e

# 配置变量
NAMESPACE="edusoft"
SERVICE_NAME="user-service"
IMAGE_NAME="crpi-z38aw29its2zwb1p.cn-beijing.personal.cr.aliyuncs.com/sjy330/edusoft-user-service"
SERVICE_PORT="8081"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 显示脚本头部信息
show_header() {
    echo "=================================="
    echo "  EduSoft 用户服务部署脚本"
    echo "  分支: micro_user_service"
    echo "  服务: User Service"
    echo "=================================="
    echo
}

# 检查前置条件
check_prerequisites() {
    log_step "检查前置条件..."
    
    # 检查kubectl
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl 未安装或不在PATH中"
        exit 1
    fi
    
    # 检查kubectl连接
    if ! kubectl cluster-info > /dev/null 2>&1; then
        log_error "无法连接到Kubernetes集群"
        exit 1
    fi
    
    # 检查Docker（如果需要本地构建）
    if ! command -v docker &> /dev/null; then
        log_warn "Docker 未安装，将跳过本地构建步骤"
    fi
    
    log_info "前置条件检查通过"
}

# 创建命名空间
create_namespace() {
    log_step "创建命名空间..."
    kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
    log_info "命名空间 $NAMESPACE 已就绪"
}

# 创建数据库Secret
create_database_secret() {
    log_step "配置数据库连接..."
    
    # 检查是否已存在Secret
    if kubectl get secret user-service-secret -n $NAMESPACE > /dev/null 2>&1; then
        log_info "数据库Secret已存在"
        return
    fi
    
    # 提示用户输入数据库配置
    read -p "请输入数据库用户名 [root]: " DB_USERNAME
    DB_USERNAME=${DB_USERNAME:-root}
    
    read -s -p "请输入数据库密码: " DB_PASSWORD
    echo
    
    read -p "请输入JWT密钥 [edusoft-user-jwt-secret]: " JWT_SECRET
    JWT_SECRET=${JWT_SECRET:-edusoft-user-jwt-secret}
    
    # 创建Secret
    kubectl create secret generic user-service-secret \
        --from-literal=db-username="$DB_USERNAME" \
        --from-literal=db-password="$DB_PASSWORD" \
        --from-literal=jwt-secret="$JWT_SECRET" \
        --namespace=$NAMESPACE
    
    log_info "数据库Secret创建完成"
}

# 创建镜像拉取Secret
create_image_pull_secret() {
    log_step "配置镜像拉取凭证..."
    
    # 检查是否已存在Secret
    if kubectl get secret aliyun-registry-secret -n $NAMESPACE > /dev/null 2>&1; then
        log_info "镜像拉取Secret已存在"
        return
    fi
    
    read -p "请输入阿里云镜像仓库用户名: " ACR_USERNAME
    read -s -p "请输入阿里云镜像仓库密码: " ACR_PASSWORD
    echo
    
    kubectl create secret docker-registry aliyun-registry-secret \
        --docker-server=crpi-z38aw29its2zwb1p.cn-beijing.personal.cr.aliyuncs.com \
        --docker-username=$ACR_USERNAME \
        --docker-password=$ACR_PASSWORD \
        --namespace=$NAMESPACE
    
    log_info "镜像拉取Secret创建完成"
}

# 本地构建（可选）
build_local() {
    log_step "本地构建用户服务..."
    
    if [ ! -d "user-service" ]; then
        log_error "user-service目录不存在"
        exit 1
    fi
    
    cd user-service
    
    # Maven构建
    log_info "执行Maven构建..."
    mvn clean package -DskipTests
    
    # Docker构建
    if command -v docker &> /dev/null; then
        log_info "构建Docker镜像..."
        docker build -t $IMAGE_NAME:latest .
        
        read -p "是否推送镜像到仓库? (y/N): " PUSH_IMAGE
        if [[ $PUSH_IMAGE == [yY] ]]; then
            docker push $IMAGE_NAME:latest
            log_info "镜像推送完成"
        fi
    fi
    
    cd ..
    log_info "本地构建完成"
}

# 部署用户服务
deploy_user_service() {
    log_step "部署用户服务到Kubernetes..."
    
    if [ ! -f "k8s/user-service/deployment.yaml" ]; then
        log_error "K8s配置文件不存在: k8s/user-service/deployment.yaml"
        exit 1
    fi
    
    # 应用K8s配置
    kubectl apply -f k8s/user-service/deployment.yaml
    
    log_info "等待用户服务就绪..."
    kubectl rollout status deployment/edusoft-user-service -n $NAMESPACE --timeout=300s
    
    log_info "用户服务部署完成"
}

# 执行健康检查
health_check() {
    log_step "执行健康检查..."
    
    # 检查Pod状态
    log_info "Pod状态:"
    kubectl get pods -n $NAMESPACE -l app=user-service
    
    # 检查Service状态
    log_info "Service状态:"
    kubectl get services -n $NAMESPACE -l app=user-service
    
    # 等待Pod就绪
    log_info "等待Pod完全就绪..."
    kubectl wait --for=condition=ready pod -l app=user-service -n $NAMESPACE --timeout=180s
    
    # 获取Service信息
    SERVICE_IP=$(kubectl get service user-service -n $NAMESPACE -o jsonpath='{.spec.clusterIP}')
    
    if [ ! -z "$SERVICE_IP" ]; then
        log_info "服务内部地址: http://$SERVICE_IP:$SERVICE_PORT"
        
        # 尝试健康检查
        log_info "执行健康检查..."
        kubectl run health-check --rm -i --restart=Never --image=curlimages/curl -- \
            curl -f http://$SERVICE_IP:$SERVICE_PORT/actuator/health || \
            log_warn "健康检查失败，请检查服务状态"
    fi
    
    log_info "健康检查完成"
}

# 显示访问信息
show_access_info() {
    log_step "部署完成信息"
    
    echo "========================================="
    echo "  用户服务部署成功！"
    echo "========================================="
    echo
    echo "服务信息:"
    echo "- 命名空间: $NAMESPACE"
    echo "- 服务名称: $SERVICE_NAME"
    echo "- 端口: $SERVICE_PORT"
    echo
    echo "管理命令:"
    echo "- 查看Pod: kubectl get pods -n $NAMESPACE -l app=user-service"
    echo "- 查看日志: kubectl logs -f deployment/edusoft-user-service -n $NAMESPACE"
    echo "- 查看服务: kubectl get services -n $NAMESPACE"
    echo
    echo "如需外部访问，请配置Ingress或使用端口转发："
    echo "kubectl port-forward service/user-service $SERVICE_PORT:$SERVICE_PORT -n $NAMESPACE"
    echo
    echo "健康检查地址: http://localhost:$SERVICE_PORT/actuator/health"
}

# 清理部署
cleanup() {
    log_warn "清理用户服务部署..."
    read -p "确定要清理用户服务部署吗? (y/N): " confirm
    if [[ $confirm == [yY] ]]; then
        kubectl delete -f k8s/user-service/deployment.yaml 2>/dev/null || true
        kubectl delete secret user-service-secret -n $NAMESPACE 2>/dev/null || true
        kubectl delete secret aliyun-registry-secret -n $NAMESPACE 2>/dev/null || true
        log_info "清理完成"
    else
        log_info "取消清理"
    fi
}

# 更新部署
update_deployment() {
    log_step "更新用户服务部署..."
    
    read -p "请输入新的镜像标签 [latest]: " IMAGE_TAG
    IMAGE_TAG=${IMAGE_TAG:-latest}
    
    kubectl set image deployment/edusoft-user-service \
        user-service=$IMAGE_NAME:$IMAGE_TAG \
        -n $NAMESPACE
    
    log_info "等待滚动更新完成..."
    kubectl rollout status deployment/edusoft-user-service -n $NAMESPACE --timeout=300s
    
    log_info "更新完成"
}

# 主函数
main() {
    show_header
    
    case "$1" in
        "deploy")
            log_info "开始完整部署用户服务..."
            check_prerequisites
            create_namespace
            create_database_secret
            create_image_pull_secret
            deploy_user_service
            health_check
            show_access_info
            ;;
        "build")
            log_info "本地构建用户服务..."
            check_prerequisites
            build_local
            ;;
        "update")
            log_info "更新用户服务部署..."
            check_prerequisites
            update_deployment
            health_check
            ;;
        "health")
            log_info "执行健康检查..."
            check_prerequisites
            health_check
            ;;
        "cleanup")
            cleanup
            ;;
        "info")
            show_access_info
            ;;
        *)
            echo "用法: $0 {deploy|build|update|health|cleanup|info}"
            echo ""
            echo "命令说明:"
            echo "  deploy  - 完整部署用户服务"
            echo "  build   - 本地构建和打包"
            echo "  update  - 更新现有部署"
            echo "  health  - 执行健康检查"
            echo "  cleanup - 清理所有部署"
            echo "  info    - 显示访问信息"
            exit 1
            ;;
    esac
}

main "$@"
