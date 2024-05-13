#!/bin/bash

curl -i -X POST https://insurance-web-console-asotobue-dev.apps.sandbox-m4.g2pi.p1.openshiftapps.com/insurance/claim \
  -H 'Content-Type: multipart/form-data' \
  -F 'picture=@./carImage3.jpg' \
  -F 'insuranceClaimReport=@./claim.txt'
