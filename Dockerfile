FROM adoptopenjdk
RUN mkdir /opt/exchange-service-app
COPY eurofxref-hist.csv /opt/exchange-service-app/
COPY eurofxref-hist.zip /opt/exchange-service-app/
COPY target/exchange-rate-service-0.0.1-SNAPSHOT.jar /opt/exchange-service-app
EXPOSE 8081
CMD ["java", "-jar", "/opt/exchange-service-app/exchange-rate-service-0.0.1-SNAPSHOT.jar"]
