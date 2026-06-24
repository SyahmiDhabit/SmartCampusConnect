Write-Host "Building SmartCampus Connect..." -ForegroundColor Cyan
mvn clean install -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful." -ForegroundColor Green
} else {
    Write-Host "Build failed." -ForegroundColor Red
    exit $LASTEXITCODE
}
