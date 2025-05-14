FROM eclipse-temurin:21 AS build-stage
WORKDIR /app
RUN apt update && apt install -y maven
COPY . .
RUN mvn clean package -DskipTests -U
#final
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build-stage /app/target/vin-contact-service-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar","/app/app.jar"]
