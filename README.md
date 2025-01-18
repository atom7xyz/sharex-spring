
## sharex-spring

ShareX Spring Boot server with support for: image, text and file upload (and retrieval), URL shortener.

---

### ⚠️ IMPORTANT SECURITY NOTICE

**It is strongly recommended to run the software behind a reverse proxy, such as [nginx](https://nginx.org/en/),**
since `sharex-spring` lacks built-in HTTPS support.
Running without HTTPS exposes your instance to security vulnerabilities.
To protect your instance, ensure SSL/TLS is enabled and traffic is securely routed.

Additionally, **port `9007` will be exposed to the internet** by default when running the application.
To further secure your instance **close port `9007` to non-`localhost` connections** either by configuring 
`server.address` to `127.0.0.1` or by configuring your firewall (`ufw`, `iptables` and alike).

#### Guides:
- For detailed instructions on setting up nginx with SSL, refer to [this guide](https://nginx.org/en/docs/http/configuring_https_servers.html).
- To run nginx in a Docker container, follow [this guide](https://www.digitalocean.com/community/tutorials/how-to-run-nginx-in-a-docker-container-on-ubuntu-22-04).

---

### Installing using Docker Compose
To run your `sharex-spring` instance using Docker Compose, follow these steps:

1. **Ensure that you have Docker and Docker Compose installed on your system.**
2. **Clone the repository:**

   ```bash
   git clone https://github.com/atom7xyz/sharex-sping && \
   cd sharex-spring
   ```
3. **Start your instance in the background:**

   ```bash
   docker compose up sharex-spring -d
   ```

   > **Note:** To refresh the configuration append `--force-recreate` to the previous command.

4. **Configuration:**

   From the provided [docker-compose.yml](https://github.com/atom7xyz/sharex-spring/docker-compose.yml) file, 
   edit it using an editor like `vim` or `nano`:
   ```bash
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

---

### Installing from Source

To run your `sharex-spring` instance from source, follow these steps:

1. **Clone the repository:**

   ```bash
   git clone https://github.com/atom7xyz/sharex-spring && \
   cd sharex-spring
   ```

2. **Retrieve dependencies and compile the project:**

   ```bash
   chmod +x ./gradlew && \
   ./gradlew bootJar -Pjava.version=21
   ```

   > **Note:** You can modify the `java.version` flag if you need a different Java version.

3. **Move the compiled JAR file to a convenient directory:**

   ```bash
   mkdir ~/tools && mkdir ~/tools/sharex-spring \
   mv ./build/libs/sharex-spring-0.0.1-SNAPSHOT.jar ~/tools/sharex-spring/ && \
   mv ./start.sh ~/tools/sharex-spring/ && \
   cd ~/tools/sharex-spring/
   ```

   > **Note:** You **can** have a different directory from `/tools/sharex-spring/`.

4. **Run the JAR file:**

   ```bash
   java -jar sharex-spring-0.0.1-SNAPSHOT.jar
   ```
   or
   ```bash
   chmod +x ./start.sh && \
   ./start.sh
   ```

   > **Note:** The application will automatically create (and use) these directories: `./data/` and `./uploads/`.

5. **Configuration:**

   From command line (not recommended):
   ```bash
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
     -jar sharex-spring-0.0.1-SNAPSHOT.jar
   ```

   From the provided [start.sh](https://github.com/atom7xyz/sharex-spring/start.sh) file, edit it using an editor like `vim` or `nano`:
   ```bash
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
      -jar sharex-spring-0.0.1-SNAPSHOT.jar
   "
   
   echo "The application is now running in the screen named `sharex-spring`"
   echo "Use `screen -x sharex-spring` to access the terminal."
   ```

6. **(Optional) Automatically start the instance at server startup.**

   Using the `start.sh` file:
   ```bash
   (crontab -l 2>/dev/null; echo "@reboot java -jar ~/tools/sharex-spring/start.sh") | crontab -
   ```

---
