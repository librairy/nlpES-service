#!/usr/bin/env bash
docker run -it --rm --name nlp-es-service -p 7777:7777 -p 65111:65111 librairy/nlp-es-service:latest
