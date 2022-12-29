FROM openjdk:17-alpine
VOLUME /tmp
ARG JAR_FILE
COPY /target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE dev
ENV SPRING_PROFILES_ACTIVE="dev"
EXPOSE 8080
# runs application
CMD ["-Dspring.profiles.active=dev"]
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar $ARGS