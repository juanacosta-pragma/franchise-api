FROM eclipse-temurin:25-jdk-alpine
VOLUME /tmp
#COPY *.jar ScaffoldQuickStart.jar
COPY applications/app-service/build/libs/*.jar app.jar
ENV JAVA_OPTS=" -Xshareclasses:name=cacheapp,cacheDir=/cache,nonfatal -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
# Replace with a non-root user to avoid running the container with excessive privileges
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -D appuser

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
