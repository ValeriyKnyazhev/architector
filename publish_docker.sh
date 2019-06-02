#!/usr/bin/env bash

#TODO paste version here
version=3

DOCKER_NAME=architector
IMAGE_NAME=valeriyknyazhev/architector:${version}
LOGIN=valeriyknyazhev

echo ">>> Building local image: ${DOCKER_NAME}"
docker build -t ${DOCKER_NAME} .

echo ">>> Tagging image to new name: ${IMAGE_NAME}"
docker tag ${DOCKER_NAME} ${IMAGE_NAME}

echo ">>> Logging into Docker Hub"
cat docker-credentials.conf | docker login --username ${LOGIN} --password-stdin

echo ">>> Pushing image"
docker push ${IMAGE_NAME}

echo ">>> Image published into Docker Hub"

echo ">>> Deleting local resources"
docker rmi ${IMAGE_NAME}
docker image rm ${DOCKER_NAME}