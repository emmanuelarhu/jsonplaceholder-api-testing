@echo off
echo ====================================
echo JSONPlaceholder API Tests
echo ====================================

echo.
echo Cleaning and compiling...
call mvn clean compile test-compile

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Running tests...
call mvn test

if %errorlevel% neq 0 (
    echo WARNING: Some tests may have failed. Check the output above.
) else (
    echo SUCCESS: All tests passed!
)

echo.
echo Generating Allure report...
call mvn allure:serve

echo.
echo Done!
pause