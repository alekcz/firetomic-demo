FROM openjdk:8-alpine

COPY target/uberjar/firetomic-demo.jar /firetomic-demo/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/firetomic-demo/app.jar"]
