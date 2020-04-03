FROM ubuntu

RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y unzip
RUN apt-get install -y git
RUN apt-get install -y openjdk-8-jdk
RUN cd /usr/local  && \
    curl -L https://services.gradle.org/distributions/gradle-5.6.4-bin.zip -o gradle-5.6.4-bin.zip && \
    unzip gradle-5.6.4-bin.zip && \
    rm gradle-5.6.4-bin.zip && \
    cd .. && \
    cd ..

ENV GRADLE_HOME=/usr/local/gradle-5.6.4
ENV PATH=$PATH:$GRADLE_HOME/bin
COPY . hyh-zuul-api-gateway
RUN cd /hyh-zuul-api-gateway && ls && chmod +x gradle  && gradle build -x test
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/hyh-zuul-api-gateway/build/libs/hyh-zuul-api-gateway-0.0.1-SNAPSHOT.jar"]