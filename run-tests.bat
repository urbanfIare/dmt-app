@echo off
echo DMT 프로젝트 테스트 실행 중...
echo.

echo 1. 테스트 컴파일...
call mvnw.cmd clean compile test-compile

echo.
echo 2. 단위 테스트 실행...
call mvnw.cmd test

echo.
echo 3. 테스트 결과 요약...
echo.
echo 테스트 완료!
pause 