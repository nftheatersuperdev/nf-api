# Use official base image of Java Runtime
FROM openjdk:17

# Set volume point to /tmp
VOLUME /tmp

# File Author / Maintainer
MAINTAINER Veerapat Prechadech

# Set application's JAR file
ARG JAR_FILE=target/api.jar

# Add the application's JAR file to the container
ADD ${JAR_FILE} app.jar

# Expose port
EXPOSE 5001

# Run the JAR file
ENTRYPOINT java -jar -Dspring.config.location=classpath:/application.properties /app.jar
