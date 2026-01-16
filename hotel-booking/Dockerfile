FROM eclipse-temurin:17-jre

WORKDIR /app
ENV SERVER_PORT=8081

COPY target/*.jar /app/
RUN set -e; \
  jar_path="$(ls -1 /app/*.jar | grep -v 'original' | head -n 1)"; \
  if [ -z "${jar_path}" ]; then \
    echo "No runnable jar found in /app"; \
    ls -la /app; \
    exit 1; \
  fi; \
  mv "${jar_path}" /app/app.jar; \
  find /app -maxdepth 1 -name "*.jar" ! -name "app.jar" -delete

EXPOSE 8081
CMD ["java","-jar","/app/app.jar"]
