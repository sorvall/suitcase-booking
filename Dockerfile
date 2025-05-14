# Используем официальный образ OpenJDK
FROM openjdk:21-jdk-slim

ENV TZ=Europe/Moscow

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем JAR файл в контейнер
COPY build/libs/suitcase-booking-0.0.1-SNAPSHOT.jar /app/suitcase-booking.jar

# Открываем порт, если приложение использует порты (например, 8080)
EXPOSE 8081

# Запускаем JAR файл
CMD ["java", "-jar", "suitcase-booking.jar"]