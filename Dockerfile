FROM openjdk:24-oraclelinux9
COPY build/libs/*.jar ems-system.jar
ENTRYPOINT ["java", "-jar", "/ems-system.jar"]