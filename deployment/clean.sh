#!/bin/bash

kubectl delete -f 3_insurance_claim.yml
kubectl delete -f 2_damage_detection.yml
kubectl delete -f 1_mailhog.yaml
kubectl delete -f 1_minio.yaml
kubectl delete -f 1_mongodb.yaml