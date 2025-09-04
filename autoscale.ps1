# autoscale.ps1
# $cpu = (Get-Counter '\Processor(_Total)\% Processor Time').CounterSamples[0].CookedValue
$cpu = 10
if ($cpu -gt 80) {
    docker compose up --scale content-service=3 -d
    Write-Host "扩容到 3 个实例"
} elseif ($cpu -lt 30) {
    docker compose up --scale content-service=1 -d
    Write-Host "缩容到 1 个实例"
} else {
    Write-Host "保持当前规模"
}