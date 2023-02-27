#!/bin/bash

mvn clean install -f ../../../chat/pom.xml

cp ../../../chat/target/chat-0.0.1-SNAPSHOT.jar ./

docker build . -t socialnetwork-app-chat
