FROM adoptopenjdk:11-jre-hotspot as builder
ARG JAR_FILE=target/clubhelper.entrypoint*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract
 
FROM adoptopenjdk:11-jre-hotspot
RUN echo "Europe/Berlin" > /etc/timezone
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENV JAVA_OPTS="-Duser.language=de -Duser.country=DE"
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
