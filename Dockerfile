FROM centos

RUN yum install -y java

VOLUME /tmp
ADD /target/partnerservices-1.0.0-SNAPSHOT.jar myapp.jar
RUN sh -c 'touch /myapp.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-XX:MaxPermSize=128m", "-jar","/myapp.jar"]
EXPOSE 33500