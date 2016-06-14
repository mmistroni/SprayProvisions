# SprayProvisions
Simple Provision app using Spray

THis is a simple application to manage provisions, and it is based on Spray Framwork.
Code/Workflow has been copied from this tutorial https://danielasfregola.com/2015/02/23/how-to-build-a-rest-api-with-spray/ (many thanks!), but has been enhanced by using Slick as well as writing couple of unit tests.

Sample URS to use for testing:

CREATE A PROVISION

curl -v -H "Content-Type: application/json" 
               -X POST http://localhost:5000/provisions 
               -d "{\"user\":\"marco\", \"description\":\"testdesc\", \"amount\":1.1, \"questionDate\":\"2016-06-13\", \"provisionType\":\"COUNCIL\"}"
               
               
GET ALL PROVISIONS

curl-7.49.0-win64-mingw\bin>curl -v http://localhost:5000/provisions


GET A PROVISIONS

curl-7.49.0-win64-mingw\bin>curl -v http://localhost:5000/provisions/<ID(a number)>


UPDATE A PROVISION

curl -v -H "Content-Type:application/json" -X PUT http://localhost:5000/provisions/3 -d "{\"description\":\"UpdateDesc\", \"amount\":3.3}"

DELETE A PROVISION

curl -v -X DELETE http://localhost:5000/provisions/4