# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy built artifact
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8082

# JVM options for container (larger memory for video streaming)
ENV JAVA_OPTS="-Xmx1024m -Xms512m"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
