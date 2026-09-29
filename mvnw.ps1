# Minimal Maven bootstrapper: downloads Apache Maven 3.9.6 if missing, then runs it.
$ErrorActionPreference = "Stop"

$mavenVersion = "3.9.6"
$root = $PSScriptRoot
$toolsDir = Join-Path $root ".mvn\tools"
$mavenDir = Join-Path $toolsDir "apache-maven-$mavenVersion"
$mavenBat = Join-Path $mavenDir "bin\mvn.cmd"

if (-not (Test-Path $mavenBat)) {
    New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null
    $zipUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"
    $zipPath = Join-Path $toolsDir "maven.zip"
    Write-Host "Maven が見つからないためダウンロードします: $zipUrl"
    Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath
    Expand-Archive -Path $zipPath -DestinationPath $toolsDir -Force
    Remove-Item $zipPath
}

& $mavenBat @args
exit $LASTEXITCODE
