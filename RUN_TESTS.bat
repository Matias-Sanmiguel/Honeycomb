@echo off
REM ====================================================================
REM Script de Testing Automatizado - Windows
REM Sistema de Análisis Forense de Criptomonedas
REM ====================================================================

setlocal EnableDelayedExpansion

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   🧪 Sistema de Testing Automatizado                          ║
echo ║   Análisis Forense de Criptomonedas                           ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0demo"

REM Colores (simulados con echo)
set GREEN=[32m
set BLUE=[34m
set YELLOW=[33m
set RED=[31m
set NC=[0m

REM ====================================================================
REM 1. VERIFICAR PREREQUISITOS
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%1. VERIFICANDO PREREQUISITOS%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo %RED%❌ Java no encontrado. Instala Java 17+%NC%
    pause
    exit /b 1
) else (
    echo %GREEN%✅ Java detectado%NC%
    java -version
)

echo.

REM Verificar Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo %RED%❌ Maven no encontrado. Instala Maven 3.8+%NC%
    pause
    exit /b 1
) else (
    echo %GREEN%✅ Maven detectado%NC%
    mvn -version | findstr "Apache Maven"
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 2. COMPILAR PROYECTO
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%2. COMPILANDO PROYECTO%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.

mvn clean compile -DskipTests

if errorlevel 1 (
    echo %RED%❌ Error en compilación%NC%
    pause
    exit /b 1
) else (
    echo %GREEN%✅ Compilación exitosa%NC%
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 3. TESTS UNITARIOS - Backtracking
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%3. TESTS UNITARIOS - BacktrackingAlgorithm%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.
echo %YELLOW%Ejecutando 12 tests...%NC%
echo.

mvn test -Dtest=BacktrackingAlgorithmTest

if errorlevel 1 (
    echo %RED%❌ Algunos tests fallaron%NC%
) else (
    echo %GREEN%✅ Todos los tests pasaron%NC%
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 4. TESTS UNITARIOS - Branch & Bound
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%4. TESTS UNITARIOS - BranchAndBoundAlgorithm%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.
echo %YELLOW%Ejecutando 14 tests...%NC%
echo.

mvn test -Dtest=BranchAndBoundAlgorithmTest

if errorlevel 1 (
    echo %RED%❌ Algunos tests fallaron%NC%
) else (
    echo %GREEN%✅ Todos los tests pasaron%NC%
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 5. TESTS DE SERVICIOS - Backtracking
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%5. TESTS DE SERVICIOS - BacktrackingService%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.
echo %YELLOW%Ejecutando 5 tests...%NC%
echo.

mvn test -Dtest=BacktrackingServiceTest

if errorlevel 1 (
    echo %RED%❌ Algunos tests fallaron%NC%
) else (
    echo %GREEN%✅ Todos los tests pasaron%NC%
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 6. TESTS DE SERVICIOS - Branch & Bound
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%6. TESTS DE SERVICIOS - BranchBoundService%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.
echo %YELLOW%Ejecutando 7 tests...%NC%
echo.

mvn test -Dtest=BranchBoundServiceTest

if errorlevel 1 (
    echo %RED%❌ Algunos tests fallaron%NC%
) else (
    echo %GREEN%✅ Todos los tests pasaron%NC%
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 7. TESTS DE INTEGRACIÓN
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%7. TESTS DE INTEGRACIÓN - Endpoints REST%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.
echo %YELLOW%Ejecutando 20 tests...%NC%
echo %YELLOW%NOTA: Requiere aplicación Spring Boot iniciada%NC%
echo.

mvn test -Dtest=AlgorithmEndpointsIntegrationTest

if errorlevel 1 (
    echo %RED%❌ Algunos tests fallaron%NC%
    echo %YELLOW%NOTA: Tests de integración requieren Neo4j y Spring Boot activos%NC%
) else (
    echo %GREEN%✅ Todos los tests pasaron%NC%
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 8. EJECUTAR TODOS LOS TESTS
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%8. EJECUTANDO TODOS LOS TESTS%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.
echo %YELLOW%Ejecutando suite completa (58 tests)...%NC%
echo.

mvn clean test

if errorlevel 1 (
    echo %RED%❌ Algunos tests fallaron%NC%
) else (
    echo %GREEN%✅ ¡TODOS LOS TESTS PASARON! 🎉%NC%
)

echo.
echo %YELLOW%Presiona cualquier tecla para continuar...%NC%
pause >nul
echo.

REM ====================================================================
REM 9. GENERAR REPORTE DE COBERTURA
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%9. GENERANDO REPORTE DE COBERTURA%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.

mvn jacoco:report

if errorlevel 1 (
    echo %RED%❌ Error generando reporte%NC%
) else (
    echo %GREEN%✅ Reporte generado: target\site\jacoco\index.html%NC%

    REM Intentar abrir el reporte
    if exist "target\site\jacoco\index.html" (
        echo.
        echo %YELLOW%¿Abrir reporte de cobertura en navegador? (S/N)%NC%
        set /p OPEN_REPORT=
        if /i "!OPEN_REPORT!"=="S" (
            start target\site\jacoco\index.html
        )
    )
)

echo.

REM ====================================================================
REM RESUMEN FINAL
REM ====================================================================

echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo %GREEN%✅ TESTING COMPLETADO%NC%
echo %BLUE%━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%NC%
echo.

echo %YELLOW%Resumen de Tests:%NC%
echo.
echo   - BacktrackingAlgorithmTest      : 12 tests
echo   - BranchAndBoundAlgorithmTest    : 14 tests
echo   - BacktrackingServiceTest        : 5 tests
echo   - BranchBoundServiceTest         : 7 tests
echo   - AlgorithmEndpointsIntegrationTest : 20 tests
echo   ────────────────────────────────────────
echo   TOTAL                            : 58 tests
echo.

echo %YELLOW%Documentación:%NC%
echo   - Guía completa: TESTING_GUIDE.md
echo   - Reporte cobertura: target\site\jacoco\index.html
echo.

echo %YELLOW%Comandos útiles:%NC%
echo   mvn test                         # Ejecutar todos los tests
echo   mvn test -Dtest=*Backtracking*   # Solo Backtracking
echo   mvn test -Dtest=*BranchBound*    # Solo Branch ^& Bound
echo   mvn jacoco:report                # Generar cobertura
echo.

echo %GREEN%¡Testing completado exitosamente! 🧪✅%NC%
echo.

pause

