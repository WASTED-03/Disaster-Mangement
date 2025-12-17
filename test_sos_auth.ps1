$ErrorActionPreference = "Stop"

function Test-Auth {
    $email = "testuser" + (Get-Random) + "@example.com"
    Write-Host "1. Registering user: $email"
    $regUrl = "http://localhost:8080/auth/register"
    $regBody = @{
        email = $email
        name = "Test User"
        city = "TestCity"
        state = "TestState"
        password = "password123"
    } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri $regUrl -Body $regBody -ContentType "application/json"

    Write-Host "`n2. Requesting OTP"
    $otpUrl = "http://localhost:8080/auth/request-otp"
    $otpBody = @{ email = $email } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri $otpUrl -Body $otpBody -ContentType "application/json"

    Write-Host "`n3. Waiting for Log Flush and Extracting OTP..."
    Start-Sleep -Seconds 5
    $logContent = Get-Content "c:\Disaster Mangement\disaster-management\startup_debug.log" -Tail 100
    $otpLine = $logContent | Select-String "DEBUG OTP: (\d{6})" | Select-Object -Last 1
    if (-not $otpLine) {
        Write-Error "Could not find OTP in logs!"
    }
    $otp = $otpLine.Matches.Groups[1].Value
    Write-Host "Found OTP: $otp"

    Write-Host "`n4. Verifying OTP and Getting Token"
    $verifyUrl = "http://localhost:8080/auth/verify-otp"
    $verifyBody = @{ email = $email; otp = $otp } | ConvertTo-Json
    $response = Invoke-RestMethod -Method Post -Uri $verifyUrl -Body $verifyBody -ContentType "application/json"
    $token = $response.token
    Write-Host "Token received. Length: $($token.Length)"

    Write-Host "`n5. Accessing /sos/my with Token"
    $sosUrl = "http://localhost:8080/sos/my"
    try {
        $sosResponse = Invoke-RestMethod -Method Get -Uri $sosUrl -Headers @{ Authorization = "Bearer $token" }
        Write-Host "SUCCESS! /sos/my Response:"
        $sosResponse | ConvertTo-Json -Depth 5
    } catch {
        Write-Host "FAILED! Status: $($_.Exception.Response.StatusCode)"
        Write-Host "Error Details: $($_.ErrorDetails.Message)"
        exit 1
    }
}

Test-Auth
