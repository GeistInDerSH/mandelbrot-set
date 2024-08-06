FROM eclipse-temurin:21.0.4_7-jre-jammy

RUN mkdir -pv /opt/mandelbrot
COPY target/mandelbrot.jar /opt/mandelbrot
CMD ["java", "-jar", "/opt/mandelbrot/mandelbrot.jar" ]