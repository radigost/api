FROM amazoncorretto:19

COPY chat-0.0.1-SNAPSHOT.jar .

RUN java --version

CMD ["java","-jar","chat-0.0.1-SNAPSHOT.jar","--persistence.messages.url=jdbc:postgresql://172.18.0.9:5432/postgres"]


