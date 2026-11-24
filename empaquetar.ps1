[CmdletBinding()]
param(
    [ValidateSet('exe', 'app-image')]
    [string]$Tipo = 'exe',
    [ValidatePattern('^\d+\.\d+\.\d+$')]
    [string]$Version = '1.0.0',
    [string]$JavaHome,
    [string]$WixBin,
    [switch]$SinCompilar,
    [switch]$SinConexion
)

$ErrorActionPreference = 'Stop'
$javaHomeAnterior = $env:JAVA_HOME
$pathAnterior = $env:PATH
$ubicacionAnterior = Get-Location

try {
    Set-Location -LiteralPath $PSScriptRoot
    $candidatosJava = @()
    if ($JavaHome) {
        $candidatosJava += $JavaHome
    } else {
        if ($env:JAVA_HOME) { $candidatosJava += $env:JAVA_HOME }
        $carpetaJava = Join-Path $env:ProgramFiles 'Java'
        if (Test-Path -LiteralPath $carpetaJava) {
            $candidatosJava += Get-ChildItem -LiteralPath $carpetaJava -Directory -Filter 'jdk-21*' |
                Sort-Object Name -Descending | Select-Object -ExpandProperty FullName
        }
    }
    $jdkElegido = $null
    foreach ($candidato in $candidatosJava) {
        $archivoRelease = Join-Path $candidato 'release'
        if ((Test-Path -LiteralPath $archivoRelease) -and
            ((Get-Content -LiteralPath $archivoRelease -Raw) -match 'JAVA_VERSION="21[."]') -and
            (Test-Path -LiteralPath (Join-Path $candidato 'bin\jpackage.exe'))) {
            $jdkElegido = (Resolve-Path -LiteralPath $candidato).Path
            break
        }
    }
    if (-not $jdkElegido) {
        throw 'Se necesita JDK 21. Indica su carpeta con -JavaHome o configura JAVA_HOME.'
    }
    $env:JAVA_HOME = $jdkElegido
    $env:PATH = (Join-Path $jdkElegido 'bin') + ';' + $env:PATH

    if ($Tipo -eq 'exe') {
        $candidatosWix = @()
        if ($WixBin) {
            $candidatosWix += $WixBin
        } else {
            $candle = Get-Command candle.exe -ErrorAction SilentlyContinue
            if ($candle) { $candidatosWix += Split-Path -Parent $candle.Source }
            if (${env:ProgramFiles(x86)}) {
                $candidatosWix += Join-Path ${env:ProgramFiles(x86)} 'WiX Toolset v3.14\bin'
                $candidatosWix += Join-Path ${env:ProgramFiles(x86)} 'WiX Toolset v3.11\bin'
            }
        }
        $wixElegido = $null
        foreach ($candidato in $candidatosWix) {
            if ((Test-Path -LiteralPath (Join-Path $candidato 'candle.exe')) -and
                (Test-Path -LiteralPath (Join-Path $candidato 'light.exe'))) {
                $wixElegido = (Resolve-Path -LiteralPath $candidato).Path
                break
            }
        }
        if (-not $wixElegido) {
            throw 'Instala WiX Toolset 3.14 o indica la carpeta de candle.exe y light.exe con -WixBin.'
        }
        $env:PATH = $wixElegido + ';' + $env:PATH
    }

    if (-not $SinCompilar) {
        Write-Host 'Compilando FitLab y copiando dependencias a target/libs...'
        $argumentosMaven = @(
            "-Dmaven.multiModuleProjectDirectory=$PSScriptRoot",
            '-cp', (Join-Path $PSScriptRoot '.mvn\wrapper\maven-wrapper.jar'),
            'org.apache.maven.wrapper.MavenWrapperMain'
        )
        if ($SinConexion) { $argumentosMaven += '-o' }
        $argumentosMaven += @('clean', 'package')
        & (Join-Path $jdkElegido 'bin\java.exe') @argumentosMaven
        if ($LASTEXITCODE -ne 0) { throw "Maven termino con codigo $LASTEXITCODE." }
    }

    [xml]$pom = Get-Content -LiteralPath (Join-Path $PSScriptRoot 'pom.xml') -Raw
    $jar = Join-Path $PSScriptRoot "target\$($pom.project.artifactId)-$($pom.project.version).jar"
    $librerias = Join-Path $PSScriptRoot 'target\libs'
    if (-not (Test-Path -LiteralPath $jar)) { throw 'Falta el JAR del proyecto. Ejecuta el script sin -SinCompilar.' }
    if (-not (Test-Path -LiteralPath $librerias)) { throw 'Falta target/libs. Ejecuta el script sin -SinCompilar.' }
    $destino = Join-Path $PSScriptRoot 'target\dist'
    if ($Tipo -eq 'app-image') { $destino = Join-Path $PSScriptRoot 'target\app-image' }
    $salida = Join-Path $destino "FitLab-$Version.exe"
    if ($Tipo -eq 'app-image') { $salida = Join-Path $destino 'FitLab' }
    if (Test-Path -LiteralPath $salida) {
        throw 'La salida ya existe. Ejecuta sin -SinCompilar para reconstruir target, o elige otra -Version para el instalador.'
    }

    $argumentosPackage = @(
        '--type', $Tipo,
        '--name', 'FitLab',
        '--app-version', $Version,
        '--vendor', 'FitLab',
        '--description', 'FitLab - Gestion de gimnasio',
        '--module-path', "$jar;$librerias",
        '--module', 'pe.edu.upeu.fitlab_final/pe.edu.upeu.fitlab_final.Launcher',
        '--dest', $destino
    )
    if ($Tipo -eq 'exe') {
        $argumentosPackage += @(
            '--win-shortcut', '--win-menu', '--win-dir-chooser',
            '--win-menu-group', 'FitLab',
            '--win-upgrade-uuid', '649ab54a-402c-4704-90a8-345f8b7fa0b6'
        )
    }
    Write-Host "Empaquetando FitLab ($Tipo) con Java 21..."
    & (Join-Path $jdkElegido 'bin\jpackage.exe') @argumentosPackage
    if ($LASTEXITCODE -ne 0) { throw "jpackage termino con codigo $LASTEXITCODE." }
    if (-not (Test-Path -LiteralPath $salida)) { throw 'jpackage no genero la salida esperada.' }
    Write-Host "Generado: $salida"
} finally {
    $env:JAVA_HOME = $javaHomeAnterior
    $env:PATH = $pathAnterior
    Set-Location -LiteralPath $ubicacionAnterior.Path
}
