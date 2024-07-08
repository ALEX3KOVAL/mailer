FROM bellsoft/liberica-openjdk-alpine:21

RUN mkdir /mailer
WORKDIR /mailer

ADD ../build/libs /mailer/libs
ADD ../build/mailer.jar .

ENTRYPOINT ["java","-jar","mailer.jar"]