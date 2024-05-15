#!/bin/bash

read -p "***** MongoDB, MinIO, Mail server *****"

printf "\n"

kubectl apply -f 1_mailhog.yaml
kubectl apply -f 1_minio.yaml
kubectl apply -f 1_mongodb.yaml

kubectl wait --for=condition=ready pod -l app=mailhog
kubectl wait --for=condition=ready pod -l app=mongodb
kubectl wait --for=condition=ready pod -l app=minio

printf "\n"

read -p "***** Installing Damage Detection *****"

kubectl apply -f 2_damage_detection.yml

kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=damage-detection

printf "\n"

read -p "***** Installing Insurance Web *****∫"

kubectl apply -f 3_insurance_claim.yml

kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=insurance-claim-processing-web

mailhogHost=`oc get route mailhog -o jsonpath={.spec.host}`
mailUrl="https://$mailhogHost"

printf "\n"

echo "* Mail server: $mailUrl"

insuranceHost=`oc get route insurance-web-console -o jsonpath={.spec.host}`
insuranceUrl="https://$insuranceHost"

printf "\n"

read -p "* Sending the following claim text:"

cat ./claim.txt

printf "\n"

./imgcat ./carImage3.jpg

claimId=`curl -s -X POST $insuranceUrl/insurance/claim -H 'Content-Type: multipart/form-data' -F 'picture=@./carImage3.jpg' -F 'insuranceClaimReport=@./claim.txt'`

printf "\n"

read -p "* Getting The Result"

curl -s "$insuranceUrl/insurance/claim/$claimId" | python3 -m json.tool

printf "\n"

./imgcat -u "$insuranceUrl/insurance/claim/image/$claimId"

printf "\n"

read -p "Done :)"