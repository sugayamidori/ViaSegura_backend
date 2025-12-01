# build
FROM maven:3.8.8-amazoncorretto-21-al2023 as build
WORKDIR /build

# Copie apenas os arquivos de configuração Maven primeiro
COPY ./pom.xml .
COPY ./src ./src

# Resolve dependências e compila
RUN mvn clean package -DskipTests

# run
FROM amazoncorretto:21.0.5
WORKDIR /app

COPY --from=build /build/target/*.jar ./viaseguraapi.jar

EXPOSE 8080
EXPOSE 9090

ENV DATASOURCE_URL=''
ENV DATASOURCE_USERNAME=''
ENV DATASOURCE_PASSWORD=''
ENV JWT_SECRET=''
ENV JWT_EXPIRE=''
ENV ORIGIN_PATTERNS=''

ENV SPRING_PROFILES_ACTIVE='prod'
ENV TZ='America/Sao_Paulo'

ENTRYPOINT exec java -jar viaseguraapi.jar