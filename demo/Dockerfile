# Multi-stage build para optimizar el tamaño de la imagen

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Maven primero (para aprovechar cache)
COPY pom.xml .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar y empaquetar la aplicación
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar curl para healthchecks
RUN apk add --no-cache curl

# Crear usuario no-root para ejecutar la aplicación
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR compilado desde el stage de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Configuración de JVM optimizada
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
