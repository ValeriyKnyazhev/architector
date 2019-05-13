#!/usr/bin/env bash

CURRENT_DIR=$(pwd)
BACKEND_DIR=${CURRENT_DIR}/backend
RESOURCES_DIR=${BACKEND_DIR}/src/main/resources
PUBLIC=${RESOURCES_DIR}/public
TEMPLATES=${RESOURCES_DIR}/templates
echo "Templates dir: ${TEMPLATES}"
echo "Public dir   : ${PUBLIC}"
echo "Backend dir  : ${BACKEND_DIR}"
#mkdir -p ${TEMPLATES}


echo ">>> yarn building & installing"
cd webclient

yarn install
yarn build


echo ">>> copying new bundle"
cp -R build/. ${PUBLIC}
cp build/index.html ${TEMPLATES}


echo ">>> building backend"
cd ${BACKEND_DIR}
gradle clean build
cp ${BACKEND_DIR}/build/libs/backend.jar ${CURRENT_DIR}/architector.jar


echo ">>> removing bundle"
rm -rf ${PUBLIC}
rm -rf ${TEMPLATES}