FROM openjdk:17
COPY target/*.jar workspace-service-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/workspace-service-1.0-SNAPSHOT.jar","--spring.profiles.active=production"]
