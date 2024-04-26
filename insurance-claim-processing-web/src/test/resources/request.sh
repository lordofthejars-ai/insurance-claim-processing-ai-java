curl -i -X POST http://localhost:8080/insurance/claim \                                 ─╯
  -H 'Content-Type: multipart/form-data' \
  -F 'picture=@./carImage3.jpg' \
  -F 'insuranceClaimReport=@./claim.txt'