FROM eclipse-temurin:21.0.4_7-jre-jammy

RUN apt update -y && \
    apt upgrade -y && \
    apt install -y libgl1 libegl1
RUN mkdir -pv /opt/mandelbrot
COPY target/mandelbrot.jar /opt/mandelbrot
CMD ["java", "-jar", "/opt/mandelbrot/mandelbrot.jar" ]