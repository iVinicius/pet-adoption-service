# pet-adoption-service

Setup (run below lines sequentially):
1. ./gradlew build
2. docker-compose build
3. docker-compose up

Done.

There are 3 endpoints:
1. Endpoint for indexing animals (from all partners) in the database:

2. Endpoint for searching animals that returns: name, description, image, category, creation date, and status (Available, Adopted), paginated with the following filters:

    Search term (Name or description)
    Category (Cats | Dogs)
    Status
    Creation date

3. Endpoint for updating the animal's status

See more details in the Swagger below
Swagger: http://localhost:8080/swagger-ui/index.html#/
