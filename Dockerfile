FROM eclipse-temurin:17
EXPOSE 8080
ADD build/libs/wareshop-0.0.1-SNAPSHOT.jar /app/wareshop.jar
ENTRYPOINT ["java","-jar","/app/wareshop.jar"]