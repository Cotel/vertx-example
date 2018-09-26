FROM java:8-jre

ENV VERTX_JAR_FILE vertx-example-1.0.0-SNAPSHOT-fat.jar
ENV VERTX_HOME /usr/verticles

COPY build/libs/$VERTX_JAR_FILE $VERTX_HOME/

EXPOSE 8080

WORKDIR $VERTX_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTX_JAR_FILE"]
