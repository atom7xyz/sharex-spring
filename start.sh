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
   -jar sharex-spring-0.0.4-SNAPSHOT.jar
"

echo "The application is now running in the screen named `sharex-spring`"
echo "Use `screen -x sharex-spring` to access the terminal."
