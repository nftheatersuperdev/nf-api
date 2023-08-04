# Use official base image of Java Runtime
        FROM openjdk:17-jdk-alpine

        # Set volume point to /tmp
        VOLUME /tmp

        # File Author / Maintainer
        MAINTAINER EVME Plus CO.,LTD.

        # Set application's JAR file
        ARG JAR_FILE=target/car.jar

        # Set size of memory
        ARG size_of_memory

        # Add the application's JAR file to the container
        ADD ${JAR_FILE} app.jar

        ENV SIZE_OF_MEMORY=$size_of_memory

        # Expose port
        EXPOSE 8006

        # Run the JAR file
        ENTRYPOINT java -jar -Dspring.config.location=file:/config/application.properties -Xmx$SIZE_OF_MEMORY /app.jar
