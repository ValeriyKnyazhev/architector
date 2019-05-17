FROM openjdk:11-jre-slim-stretch
MAINTAINER valeriyknyazhev
RUN mkdir /app
COPY architector.jar /app/architector.jar
CMD ["java", "-jar", "/app/architector.jar"]
EXPOSE 8080