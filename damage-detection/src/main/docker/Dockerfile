FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /opt/app

COPY . .

RUN mvn clean package

#FROM registry.access.redhat.com/ubi8/openjdk-21:1.18

#ENV LANGUAGE='en_US:en'
#ARG TARGETPLATFORM

# add DJL stuff respecting the target platform
#COPY --chown=185:0 --from=builder /home/jboss/.djl.ai/$TARGETPLATFORM/pytorch/ /home/jboss/.djl.ai/pytorch/

# We make four distinct layers so if there are application changes the library layers can be re-used
#COPY --chown=185 --from=builder /home/jboss/target/quarkus-app/lib/ /deployments/lib/
#COPY --chown=185 --from=builder /home/jboss/quarkus-app/*.jar /deployments/
#COPY --chown=185 --from=builder /home/jboss/quarkus-app/app/ /deployments/app/
#COPY --chown=185 --from=builder /home/jboss/quarkus-app/quarkus/ /deployments/quarkus/

#EXPOSE 8080
#USER 185
#ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
#ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

#ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]