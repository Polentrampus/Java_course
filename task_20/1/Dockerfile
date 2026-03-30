# Используем официальный образ Tomcat с Java 17
FROM tomcat:10-jdk17

# Удаляем стандартные приложения Tomcat (необязательно)
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем ваш war-файл в правильное место внутри контейнера
# Путь /usr/local/tomcat/webapps/ - это стандартный путь в образе tomcat
COPY hotel-app/target/hotel-app.war /usr/local/tomcat/webapps/ROOT.war

# Открываем порт Tomcat
EXPOSE 8080

# Tomcat запускается автоматически, CMD уже определен в базовом образе
# Можно не писать CMD, или переопределить:
CMD ["catalina.sh", "run"]