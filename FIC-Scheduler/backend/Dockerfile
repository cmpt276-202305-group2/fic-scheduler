FROM ubuntu:22.04 AS build
ENV PATH=/usr/local/bin:$PATH
RUN apt-get update -qq && DEBIAN_FRONTEND=noninteractive apt-get install -yq default-jdk maven && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
ENV JAVA_HOME=/usr/lib/jvm/default-java
WORKDIR /home/project
COPY . .
RUN mvn clean package -DskipTests

FROM ubuntu:22.04
COPY --from=build /home/project/target/server-0.0.1-SNAPSHOT.jar server.jar
RUN apt-get update -qq && DEBIAN_FRONTEND=noninteractive apt-get install -yq default-jdk && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
EXPOSE 8080
ENTRYPOINT ["java","-jar","server.jar"]
