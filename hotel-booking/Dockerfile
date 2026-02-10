FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /workspace

# чтобы mvn/spring-boot:run нормально видел исходники из volume
# (команда задаётся в ENTRYPOINT, но можно переопределять в compose)
ENTRYPOINT ["bash","-lc","mvn -q -DskipTests spring-boot:run"]