FROM --platform=linux/amd64 eclipse-temurin:17-jdk-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgtk-3-0 \
    adwaita-icon-theme \
    gdk-pixbuf2.0-bin \
    librsvg2-common \
    shared-mime-info \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_linux-x64_bin-sdk.zip \
    && unzip openjfx-17.0.2_linux-x64_bin-sdk.zip \
    && mv javafx-sdk-17.0.2 /javafx-sdk \
    && rm openjfx-17.0.2_linux-x64_bin-sdk.zip

COPY target/alexandria-1.0.0.jar app.jar

ENV DISPLAY=host.docker.internal:0.0

CMD ["java", "--module-path", "/javafx-sdk/lib", "--add-modules", "javafx.controls,javafx.fxml,javafx.swing", "-jar", "app.jar"]
