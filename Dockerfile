FROM openjdk:17-slim AS base
RUN apt-get update && \
    apt-get install -y wget unzip git curl && \
    # Установка Gradle
    wget https://services.gradle.org/distributions/gradle-8.5-bin.zip -P /tmp && \
    unzip /tmp/gradle-8.5-bin.zip -d /opt && \
    ln -s /opt/gradle-8.5/bin/gradle /usr/bin/gradle && \
    # Установка Maven
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/* /tmp/*
WORKDIR /app
COPY . .
RUN for d in */ ; do \
      cd "$d" && \
      if [ -f "pom.xml" ]; then \
        echo "Maven build for $d" && mvn clean package -DskipTests ; \
      elif [ -f "build.gradle" ]; then \
        echo "🔧 Gradle build for $d" && gradle build -x test ; \
      else \
        echo "Skipping $d — no known build file." ; \
      fi && \
      cd .. ; \
    done
CMD ["sh"]