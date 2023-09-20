# Utiliza una imagen de OpenJDK 11 como base
FROM openjdk:11-jre-slim

# Copia tu archivo JAR al contenedor
COPY plataformanc.jar /app.jar

# Expone el puerto en el que se ejecutar� la aplicaci�n
EXPOSE 8080

# Comando para ejecutar la aplicaci�n al iniciar el contenedor
CMD ["java", "-jar", "/app.jar"]