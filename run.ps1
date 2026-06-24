# Startup script for SmartCampus Connect microservices (local H2 + RabbitMQ)
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "          Starting SmartCampus Connect Stack...          " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Docker not found. Ensure RabbitMQ is running on localhost:5672" -ForegroundColor Yellow
} else {
    $rabbitRunning = docker ps --filter "name=scc-rabbitmq" --filter "status=running" -q
    if (-not $rabbitRunning) {
        Write-Host "Starting RabbitMQ via Docker..." -ForegroundColor Yellow
        docker compose up -d rabbitmq
        Start-Sleep -Seconds 8
    } else {
        Write-Host "RabbitMQ container already running." -ForegroundColor Green
    }
}

$services = @{
    "Student Profile Service (Port 8081)" = "student-service/target/student-service-1.0.0-SNAPSHOT.jar"
    "Course Enrolment Service (Port 8082)" = "enrolment-service/target/enrolment-service-1.0.0-SNAPSHOT.jar"
    "Notification Service (Port 8083)" = "notification-service/target/notification-service-1.0.0-SNAPSHOT.jar"
    "Library/Booking Service (Port 8084, SOAP 8088)" = "library-service/target/library-service-1.0.0-SNAPSHOT.jar"
    "Reporting/Analytics Service (Port 8085)" = "reporting-service/target/reporting-service-1.0.0-SNAPSHOT.jar"
}

foreach ($name in $services.Keys) {
    $path = $services[$name]
    if (-not (Test-Path $path)) {
        Write-Host "Error: $path not found. Please run .\build.ps1 first!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "Launching Notification Service first (RabbitMQ consumer)..." -ForegroundColor Yellow
$notifJar = $services["Notification Service (Port 8083)"]
Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$Host.UI.RawUI.WindowTitle='Notification Service'; `$env:SPRING_RABBITMQ_HOST='localhost'; java -jar $notifJar"
Start-Sleep -Seconds 4

Write-Host "Launching Reporting Service (RabbitMQ consumer)..." -ForegroundColor Yellow
$reportJar = $services["Reporting/Analytics Service (Port 8085)"]
Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$Host.UI.RawUI.WindowTitle='Reporting Service'; `$env:SPRING_RABBITMQ_HOST='localhost'; java -jar $reportJar"
Start-Sleep -Seconds 3

foreach ($name in $services.Keys) {
    if ($name -like "*Notification*" -or $name -like "*Reporting*") { continue }
    Write-Host "Launching $name..." -ForegroundColor Yellow
    $jar = $services[$name]
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$Host.UI.RawUI.WindowTitle='$name'; `$env:SPRING_RABBITMQ_HOST='localhost'; java -jar $jar"
    Start-Sleep -Seconds 2
}

Write-Host "`nAll services launched!" -ForegroundColor Green
Write-Host "RabbitMQ Management UI: http://localhost:15672 (guest/guest)" -ForegroundColor Magenta
Write-Host "SOAP WSDL: http://localhost:8088/ws/library?wsdl" -ForegroundColor Magenta
Write-Host "Reporting Dashboard: http://localhost:8085/api/reports/enrolment-summary" -ForegroundColor Magenta
Write-Host "Full Docker stack: docker compose up --build" -ForegroundColor Magenta
Write-Host "Load test: mvn exec:java -pl load-test" -ForegroundColor Green
Write-Host "Press any key to stop all Java processes..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
Get-Process | Where-Object { $_.ProcessName -eq "java" } | Stop-Process -Force
Write-Host "Cleaned up." -ForegroundColor Green
