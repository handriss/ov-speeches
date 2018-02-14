FROM java:8

WORKDIR /

USER daemon

ADD target/scala-2.11/docker-spray-http-server-assembly-1.0.jar /app/server.jar

ENTRYPOINT [ "java", "-jar", "/app/server.jar" ]

EXPOSE 8080