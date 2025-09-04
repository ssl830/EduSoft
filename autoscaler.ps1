# autoscaler.ps1
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# Configuration
$services = @("user-service", "course-service", "learning-service", "content-service", "ai-service", "frontend")
$minReplicas = 1
$maxReplicas = 5
$scaleUpThreshold = 2       # 扩容（可按需调整）
$scaleDownThreshold = 1   # 缩容（可按需调整）
$checkInterval = 30         # 每 30 秒检测一次

# Compose 网络名（你本地实际网络名）
$networkName = "edusoft-network"
Write-Host "Using network: $networkName"

# 新增：AI服务停用逻辑
function Stop-AIServiceIfNeeded {
    param (
        [array]$serviceNames,
        [int]$cpuThreshold = 2
    )
    $shouldStopAI = $false
    foreach ($svc in $serviceNames) {
        $containerIds = docker ps --filter "name=$svc" -q
        if (-not $containerIds) { continue }
        $cpuList = @()
        $memList = @()
        foreach ($id in $containerIds) {
            $cpuStr = docker stats --no-stream --format "{{.CPUPerc}}" $id
            $memStr = docker stats --no-stream --format "{{.MemPerc}}" $id
            if ($cpuStr) { $cpuList += [int]($cpuStr.Trim().TrimEnd('%')) }
            if ($memStr) { $memList += [int]($memStr.Trim().TrimEnd('%')) }
        }
        if ($cpuList.Count -gt 0 -and ($cpuList | Measure-Object -Average).Average -ge $cpuThreshold) {
            $shouldStopAI = $true
            break
        }
    }
    if ($shouldStopAI) {
        Write-Host "资源占用过高，自动停止 ai-service 及其扩容实例"
        $aiContainers = docker ps --filter "name=ai-service" -q
        foreach ($id in $aiContainers) {
            docker rm -f $id
        }
    }
}

while ($true) {
    # 检查是否需要停掉 ai-service
    Stop-AIServiceIfNeeded -serviceNames @("user-service", "course-service", "learning-service", "content-service") -cpuThreshold 2

    foreach ($service in $services) {
        try {
            # 获取所有容器 ID
            $containerIds = docker ps --filter "name=$service" -q

            if (-not $containerIds) {
                Write-Host "Service $service is not running, skip"
                continue
            }

            # 收集每个容器的 CPU
            $cpuList = @()
            foreach ($id in $containerIds) {
                $cpuStr = docker stats --no-stream --format "{{.CPUPerc}}" $id
                if ($cpuStr) {
                    $cpuVal = [int]($cpuStr.Trim().TrimEnd('%'))
                    $cpuList += $cpuVal
                }
            }

            if ($cpuList.Count -eq 0) {
                Write-Host "Failed to get CPU for $service, skip"
                continue
            }

            # 计算平均 CPU
            $avgCpu = [math]::Round(($cpuList | Measure-Object -Average).Average)

            # 当前副本数
            $currentScale = $containerIds.Count

            Write-Host "Service: $service | Current replicas: $currentScale | Average CPU: $avgCpu%"

            # ---------------- 扩容 ----------------
            if ($avgCpu -gt $scaleUpThreshold -and $currentScale -lt $maxReplicas) {
                $newId = $currentScale + 1
                Write-Host "Scaling up $service to $newId (+1) "

                # 使用第一个正在运行的容器获取镜像名
                $imageName = docker inspect --format '{{.Config.Image}}' ($containerIds | Select-Object -First 1)
                if (-not $imageName) {
                    Write-Host "Cannot find image for $service, skip"
                    continue
                }


                # 随机端口启动新副本
                docker run -d -P --name "$service-$newId" --network $networkName $imageName
            }

            # ---------------- 缩容 ----------------
            elseif ($avgCpu -lt $scaleDownThreshold -and $currentScale -gt $minReplicas) {
                $removeId = $currentScale
                Write-Host "Scaling down $service, removing $service-$removeId (-1)"

                # 删除最后一个副本
                docker rm -f "$service-$removeId"
            }

        } catch {
            Write-Host "Exception handling $service, skip"
            continue
        }
    }

    Start-Sleep -Seconds $checkInterval
}
