FROM vertx/vertx3

ENV VERTX_NAME com.cotel.vertxExample.MainVerticle
ENV VERTX_JAR_FILE build/libs/vertx-example-1.0.0-SNAPSHOT.jar
ENV VERTX_HOME /usr/verticles

COPY $VERTX_JAR_FILE $VERTX_HOME/

EXPOSE 8080

WORKDIR $VERTX_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTX_NAME -cp $VERTX_HOME/*"]
