FROM openjdk:17
COPY target/*.jar eureka-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/eureka-service-1.0-SNAPSHOT.jar","--spring.profiles.active=production"]
