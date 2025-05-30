# Coffee Shop API - Local Development & Testing Script
# Run this script to test the API locally before deployment

param(
    [string]$Action = "test",  # test, start, or deploy-check
    [int]$Port = 8000
)

Write-Host "🍵 Coffee Shop API - Development Helper" -ForegroundColor Green
Write-Host "=" * 50

function Test-PythonRequirements {
    Write-Host "📋 Checking Python requirements..." -ForegroundColor Yellow
    
    if (-not (Get-Command python -ErrorAction SilentlyContinue)) {
        Write-Host "❌ Python not found in PATH" -ForegroundColor Red
        return $false
    }
    
    $pythonVersion = python --version
    Write-Host "✅ $pythonVersion" -ForegroundColor Green
    
    # Check if virtual environment is activated
    if ($env:VIRTUAL_ENV) {
        Write-Host "✅ Virtual environment active: $env:VIRTUAL_ENV" -ForegroundColor Green
    } else {
        Write-Host "⚠️  No virtual environment detected. Consider using one." -ForegroundColor Yellow
    }
    
    return $true
}

function Start-LocalServer {
    Write-Host "🚀 Starting local server on port $Port..." -ForegroundColor Yellow
    
    # Change to backend directory
    Set-Location "backend"
    
    try {
        # Start server using start.py
        python start.py
    }
    catch {
        Write-Host "❌ Failed to start server: $_" -ForegroundColor Red
        exit 1
    }
}

function Test-LocalAPI {
    Write-Host "🧪 Testing local API..." -ForegroundColor Yellow
    
    # Change to backend directory
    Set-Location "backend"
    
    # Run test script
    python test_deployment.py
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ All tests passed!" -ForegroundColor Green
    } else {
        Write-Host "❌ Some tests failed!" -ForegroundColor Red
    }
}

function Show-DeploymentChecklist {
    Write-Host "📋 Pre-Deployment Checklist:" -ForegroundColor Yellow
    Write-Host ""
    
    $checks = @(
        "Environment variables set in production",
        "SUPABASE_URL and SUPABASE_ANON_KEY configured",
        "Firebase Admin SDK file uploaded",
        "JWT_SECRET_KEY changed from default",
        "Debug mode disabled (DEBUG=false)",
        "All tests passing locally"
    )
    
    foreach ($check in $checks) {
        Write-Host "  ☐ $check" -ForegroundColor Cyan
    }
    
    Write-Host ""
    Write-Host "🚀 Deployment Commands:" -ForegroundColor Yellow
    Write-Host "  Render: python app/main.py" -ForegroundColor Cyan
    Write-Host "  Alternative: python start.py" -ForegroundColor Cyan
    Write-Host "  Direct: uvicorn app.main:app --host 0.0.0.0 --port `$PORT" -ForegroundColor Cyan
}

# Main script logic
switch ($Action.ToLower()) {
    "start" {
        if (Test-PythonRequirements) {
            Start-LocalServer
        }
    }
    "test" {
        if (Test-PythonRequirements) {
            Test-LocalAPI
        }
    }
    "deploy-check" {
        if (Test-PythonRequirements) {
            Test-LocalAPI
            Show-DeploymentChecklist
        }
    }
    default {
        Write-Host "Usage: .\dev.ps1 [start|test|deploy-check]" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Commands:" -ForegroundColor Green
        Write-Host "  start       - Start local development server" -ForegroundColor Cyan
        Write-Host "  test        - Run API tests" -ForegroundColor Cyan
        Write-Host "  deploy-check - Run tests and show deployment checklist" -ForegroundColor Cyan
    }
}

Write-Host ""
Write-Host "Done! 🎉" -ForegroundColor Green
