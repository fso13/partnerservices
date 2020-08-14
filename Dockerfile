FROM java:8
COPY build/libs/micronaut-*-all.jar micronaut-test-1.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/micronaut-test-1.0-SNAPSHOT.jar"]