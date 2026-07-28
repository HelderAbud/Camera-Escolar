@echo off
cd /d C:\Users\Pessoal\Desktop\Camera-Escolar
set JWT_SECRET_BASE64=dGVzdGVTZWNyZXRLZXlGYWNlTG9nQUlUZXN0ZVNlY3JldEtleUZhY2VMb2dBSQ==
set FACELOGAI_SEED_ADMIN_ENABLED=true
set FACELOGAI_SEED_ADMIN_EMAIL=admin@facelogai.local
set FACELOGAI_SEED_ADMIN_PASSWORD=TestOnly-Admin-Password-2026!
set SPRING_DATASOURCE_URL=jdbc:h2:mem:shot;DB_CLOSE_DELAY=-1;MODE=MySQL
set SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
set SPRING_DATASOURCE_USERNAME=sa
set SPRING_DATASOURCE_PASSWORD=
set SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
set SPRING_FLYWAY_ENABLED=false
set SERVER_PORT=8082
"C:\Users\Pessoal\Desktop\apache-maven-3.9.12\bin\mvn.cmd" -q spring-boot:run -Dspring-boot.run.useTestClasspath=true
