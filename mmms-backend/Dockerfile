# Build stage
FROM openjdk:21-jdk-slim AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY lib ./lib

# Make mvnw executable
RUN chmod +x ./mvnw

# Install the local JAR into local Maven repository (corrected path)
RUN ./mvnw install:install-file \
    -Dfile=lib/nepali-date/nepali-english-date-converter-1.0.0.jar \
    -DgroupId=np.com.bahadur.converter.date.nepali \
    -DartifactId=nepali-english-date-converter \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -Dhttps.protocols=TLSv1.2

# Runtime stage
FROM openjdk:21-slim

WORKDIR /app

# Copy only the built jar from build stage
COPY --from=build /app/target/mmms-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]