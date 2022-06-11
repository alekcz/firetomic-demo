#!/usr/bin/env bash

docker run \
  --env FIRETOMIC_NAME=clojured \
  --env FIRETOMIC_DB=http://host.docker.internal:9000 \
  --env FIRETOMIC_KEEP_HISTORY=true \
  --env FIRETOMIC_PORT=4000 \
  --env FIRETOMIC_TOKEN=foshizzle \
  --env FIRETOMIC_DEV_MODE=true \
  -p 4000:4000 \
  --add-host host.docker.internal:host-gateway \
  alekcz/firetomic:latest 
