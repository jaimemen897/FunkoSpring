# Etapa de compilación
FROM gradle:jdk17 AS build

# Directorio de trabajo
WORKDIR /app

# Copia los archivos build.gradle.kts y src
COPY build.gradle.kts .
COPY gradlew .
COPY gradle gradle
COPY src src

RUN ./gradlew build

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine AS run

WORKDIR /app

COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/my-app.jar

EXPOSE 3000

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app/my-app.jar"]