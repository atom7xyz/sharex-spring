![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![License](https://img.shields.io/github/license/atom7xyz/sharex-spring)

# sharex-spring

A robust and efficient self-hosted server for ShareX, supporting image uploads, text sharing, file hosting, and URL shortening.

## Features
- 🚀 Modern Spring Boot server implementation
- 🔒 Secure API key authentication
- 🛡️ Built-in rate limiting protection
- 📁 File upload support with customizable limits
- 🔗 URL shortening service
- 📝 Text/code sharing capabilities
- 🐳 Easy deployment with Docker
- ⚡ Efficient caching system
- 🔧 Configurable via environment variables

---

### ⚠️ IMPORTANT SECURITY NOTICE

**It is strongly recommended to run the software behind a reverse proxy, such as [nginx](https://nginx.org/en/),**
since `sharex-spring` lacks built-in HTTPS support.
Running without HTTPS exposes your instance to security vulnerabilities.
To protect your instance, ensure SSL/TLS is enabled and traffic is securely routed.

Additionally, **port `9007` will be exposed to the internet** by default when running the application.
To further secure your instance **close port `9007` to non-`localhost` connections** either by configuring
`server.address` to `127.0.0.1` or by configuring your firewall (`ufw`, `iptables` and alike).

---

### Simple deploy using Docker Compose

1. **Ensure that you have Docker and Docker Compose installed on your system.**
2. **Download the docker-compose and the application file:**

   ```shell
   wget https://raw.githubusercontent.com/atom7xyz/sharex-spring/refs/heads/master/docker-compose.yml && \
   wget https://github.com/atom7xyz/sharex-spring/releases/download/0.0.1/sharex-spring-0.0.2-SNAPSHOT.jar
   ```

3. **Move the application JAR file to a convenient directory:**

   ```shell
   mkdir -p ~/tools/sharex-spring && \
   mv ./sharex-spring-0.0.2-SNAPSHOT.jar ~/tools/sharex-spring/ && \
   mv ./docker-compose.yml ~/tools/sharex-spring/ && \
   cd ~/tools/sharex-spring/
   ```

   > **Note:** You **can** have a different directory from `~/tools/sharex-spring/`.

4. **Start your instance in the background:**

   ```shell
   docker compose up sharex-spring -d
   ```

   > **Note:** To refresh the configuration append `--force-recreate` to this command.

5. **Configuration:**

   From the provided [docker-compose.yml](https://github.com/atom7xyz/sharex-spring/docker-compose.yml) file,
   edit it using an editor like `vim` or `nano`:
   ```yml
   ...
   environment:
      - APP_SECURITY_API_KEY=changeme
      - APP_RATE_LIMIT_ACTION=50
      - APP_RATE_LIMIT_WRONG_API_KEY=1
      - APP_PUBLIC_UPLOADED_FILES=https://YOUR_DOMAIN/share/u/
      - APP_PUBLIC_SHORTENED_URLS=https://YOUR_DOMAIN/share/s/
      - APP_FILE_UPLOAD_DIRECTORY=./uploads/
      - APP_LIMITS_FILE_UPLOADER_GENERATED_NAME_LENGTH=8
      - APP_LIMITS_FILE_UPLOADER_SIZE=-1
      - APP_LIMITS_URL_SHORTENER_GENERATED_NAME_LENGTH=4
      - SERVER_PORT=9007
   ports:
      - "9007:9007"
   ...
   ```
   To no longer expose the application port to the internet, set `ports` like this:
   ```yml
    ports:
      - "127.0.0.1:9007:9007"
   ```
   To allow connections from/to the nginx container, uncomment this `#` part:
   ```yml
      ...
      user: "1000:1000"
      # networks:
      #   - shared-nginx

   #networks:
   #  shared-nginx:
   #    external: true
   ```
   and set the name of the external network to fit your needs.

----

### Compiling and Installing using Docker Compose
To compile and run your `sharex-spring` instance using Docker Compose, follow these steps:

1. **Ensure that you have Docker and Docker Compose installed on your system.**
2. **Clone the repository:**

   ```shell
   git clone https://github.com/atom7xyz/sharex-sping && \
   cd sharex-spring
   ```

3. **Retrieve dependencies and compile the project:**
   ```shell
   chmod +x ./gradlew && \
   ./gradlew bootJar -Pjava.version=17
   ```

   > **Note:** Supports Java versions 17-23. Default is Java 21 if not specified.

4. **Move the compiled JAR file to a convenient directory:**

   ```shell
   mkdir -p ~/tools/sharex-spring && \
   mv ./build/libs/sharex-spring-0.0.2-SNAPSHOT.jar ~/tools/sharex-spring/ && \
   mv ./docker-compose.yml ~/tools/sharex-spring/ && \
   cd ~/tools/sharex-spring/
   ```

   > **Note:** You **can** have a different directory from `~/tools/sharex-spring/`.

5. **Start your instance in the background:**

   ```shell
   docker compose up sharex-spring -d
   ```

   > **Note:** To refresh the configuration append `--force-recreate` to this command.

6. **Configuration:**

   From the provided [docker-compose.yml](https://github.com/atom7xyz/sharex-spring/docker-compose.yml) file,
   edit it using an editor like `vim` or `nano`:
   ```yml
   ...
   environment:
      - APP_SECURITY_API_KEY=changeme
      - APP_RATE_LIMIT_ACTION=50
      - APP_RATE_LIMIT_WRONG_API_KEY=1
      - APP_PUBLIC_UPLOADED_FILES=https://YOUR_DOMAIN/share/u/
      - APP_PUBLIC_SHORTENED_URLS=https://YOUR_DOMAIN/share/s/
      - APP_FILE_UPLOAD_DIRECTORY=./uploads/
      - APP_LIMITS_FILE_UPLOADER_GENERATED_NAME_LENGTH=8
      - APP_LIMITS_FILE_UPLOADER_SIZE=-1
      - APP_LIMITS_URL_SHORTENER_GENERATED_NAME_LENGTH=4
      - SERVER_PORT=9007
   ports:
      - "9007:9007"
   ...
   ```
   To no longer expose the application port to the internet, set `ports` like this:
   ```yml
    ports:
      - "127.0.0.1:9007:9007"
   ```
   To allow connections from/to the nginx container, uncomment this `#` part:
   ```yml
      ...
      user: "1000:1000"
      # networks:
      #   - shared-nginx

   #networks:
   #  shared-nginx:
   #    external: true
   ```
   and set the name of the external network to fit your needs.

----

### Installing from Source

To run your `sharex-spring` instance from source, follow these steps:

1. **Clone the repository:**

   ```shell
   git clone https://github.com/atom7xyz/sharex-spring && \
   cd sharex-spring
   ```

2. **Retrieve dependencies and compile the project:**

   ```shell
   chmod +x ./gradlew && \
   ./gradlew bootJar -Pjava.version=17
   ```

   > **Note:** Supports Java versions 17-23. Default is Java 21 if not specified.

3. **Move the compiled JAR file to a convenient directory:**

   ```shell
   mkdir -p ~/tools/sharex-spring && \
   mv ./build/libs/sharex-spring-0.0.2-SNAPSHOT.jar ~/tools/sharex-spring/ && \
   mv ./start.sh ~/tools/sharex-spring/ && \
   cd ~/tools/sharex-spring/
   ```

   > **Note:** You **can** have a different directory from `~/tools/sharex-spring/`.

4. **Run the JAR file:**

   ```shell
   java -jar sharex-spring-0.0.2-SNAPSHOT.jar
   ```
   or
   ```shell
   chmod +x ./start.sh && \
   ./start.sh
   ```

   > **Note:** The application will automatically create (and use) these directories: `./data/` and `./uploads/`.

5. **Configuration:**

   From command line (not recommended):
   ```shell
   java \
     -Dapp.security.api-key=changeme \
     -Dapp.rate.limit.action=50 \
     -Dapp.rate.limit.wrong-api-key=1 \
     -Dapp.public.uploaded-files=https://YOUR_DOMAIN/share/u/ \
     -Dapp.public.shortened-urls=https://YOUR_DOMAIN/share/s/ \
     -Dapp.file.upload-directory=./uploads/ \
     -Dapp.limits.file-uploader.generated-name-length=8 \
     -Dapp.limits.file-uploader.size=-1 \
     -Dapp.limits.url-shortener.generated-name-length=4 \
     -Dserver.address=0.0.0.0 \
     -Dserver.port=9007 \
     -jar sharex-spring-0.0.2-SNAPSHOT.jar
   ```

   From the provided [start.sh](https://github.com/atom7xyz/sharex-spring/start.sh) file, edit it using an editor like `vim` or `nano`:
   ```shell
   #!/bin/bash

   screen -S sharex-spring -dm bash -c "
   java \
      -Dapp.security.api.key=changeme \
      -Dapp.rate.limit.action=50 \
      -Dapp.rate.limit.wrong.api.key=1 \
      -Dapp.public.uploaded.files=https://YOUR_DOMAIN/share/u/ \
      -Dapp.public.shortened.urls=https://YOUR_DOMAIN/share/s/ \
      -Dapp.file.upload.directory=./uploads/ \
      -Dapp.limits.file.uploader.generated.name.length=8 \
      -Dapp.limits.file.uploader.size=-1 \
      -Dapp.limits.url.shortener.generated.name.length=4 \
      -Dserver.address=0.0.0.0 \
      -Dserver.port=9007 \
      -jar sharex-spring-0.0.2-SNAPSHOT.jar
   "
   
   echo "The application is now running in the screen named `sharex-spring`"
   echo "Use `screen -x sharex-spring` to access the terminal."
   ```
   To no longer expose the application port to the internet, set `server.address` like this:
   ```yml
    -Dserver.address=127.0.0.1
   ```

6. **(Optional) Automatically start the instance at server startup.**

   Using the `start.sh` file:
   ```shell
   (crontab -l 2>/dev/null; echo "@reboot java -jar ~/tools/sharex-spring/start.sh") | crontab -
   ```

---

## License
This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
