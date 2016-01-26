#!/bin/bash
source ./params.cfg
echo "starting git pull from branch: $git_branch ..."
git pull origin $git_branch
echo "git pull: completed"
echo "starting: maven build"
mvn clean install
echo "maven build: completed"
echo "deploying on tomcat"
cp target/$snapshot_name $deploy_location/$snapshot_name
echo ".WAR files copied."
echo "Deploy complete!"
