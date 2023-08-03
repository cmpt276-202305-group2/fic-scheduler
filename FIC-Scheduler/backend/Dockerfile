FROM maven:3.8.5-openjdk-17 AS build
COPY . . 
RUN mvn clean package -DskipTests
# Extract the native libraries
RUN mkdir -p native-libs && (cd native-libs; jar xf ../target/server-0.0.1-SNAPSHOT.jar BOOT-INF/lib/ortools-linux-x86-64-9.6.2534.jar)
RUN (cd native-libs/BOOT-INF/lib; jar xf ortools-linux-x86-64-9.6.2534.jar)

FROM openjdk:17.0.1-jdk-slim
# Copy the main JAR
COPY --from=build /target/server-0.0.1-SNAPSHOT.jar server.jar
# Copy the extracted native libraries
COPY --from=build /native-libs/BOOT-INF/lib/ortools-linux-x86-64/libjniortools.so /lib/
COPY --from=build /native-libs/BOOT-INF/lib/ortools-linux-x86-64/libortools.so.9 /lib/
EXPOSE 8080
# Add the directory containing the native library to java.library.path
ENTRYPOINT ["java","-Djava.library.path=/lib","-jar","server.jar"]
