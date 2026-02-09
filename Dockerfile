# --- Base image con JDK 21 ---
FROM eclipse-temurin:21-jdk-jammy

# --- Directorio de trabajo dentro del contenedor ---
ENV APP_HOME=/app
WORKDIR $APP_HOME

# --- Copiamos el pom.xml para aprovechar cache de dependencias ---
COPY pom.xml .

# Instalamos Maven y descargamos dependencias offline
RUN apt-get update && \
    apt-get install -y maven && \
    mvn dependency:go-offline

# --- Copiamos el código fuente ---
COPY src ./src

# --- Compilamos el proyecto sin tests ---
RUN mvn clean package -DskipTests

# --- Clase main de prueba ---
# Ajusta "app.Main" al paquete y clase que tenga tu método main
CMD ["java", "-cp", "target/classes:target/dependency/*", "app.Main"]
