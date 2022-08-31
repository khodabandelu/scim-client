The purpose of this project is to simulate the SCIM protocol for provisioning users by external identity systems.

SCIM defines a flexible schema mechanism and REST API for managing identity data. The goal is to reduce the complexity of user management operations by providing patterns for exchanging schemas using HTTP.

First get secret by admin of your organization and sign your jwt token with this secret and then call provision user endpoint.

The following endpoints are implemented.

- POST for `/api/provisioner/syncProvisioning`  => Get secret key to sign jwt token

- POST for `/api/organizations/{organizationId}/provisioner/{provisionerId}/users` => Provision user with JWT token signed by secret taken from syncProvisioning endpoint

The schema of the user entry as you can provision user by provision user endpoint is:
```json
{
  "email": string,
  "name": {
    "firstName": string,
    "lastName": string,
  }
  "id": string // id in external identity system
}
```
the response in the following format:
When user is created
201 Created
```json
{
  “email”: string,
  “name”: {
    “firstName”: string,
    “lastName”: string,
  }
  "id": string // id in external identity system
  "applicationId": string // id in Happeo
}
```
## Build and Run 

- Maven -
    you can use provided maven cmd in the root of the project to build and run this project
```shell 
.\mvnw clean spring-boot:run
```
- Docker -
you can run docker compose of root of the project to build and run this project.
```shell
docker-compose up -d
```
## Test
to test this project you can use maven test command to run provided tests in test package
```shell 
.\mvnw clean test
```

## Additional documents
I recognized that this assignment is about SCIM protocol and how to provision objects from external systems like Okta
- Step 1 => My first step was to add an authentication flow with JWT for our users in Happeo, such as admins of the organization, so they can access endpoints in a secure manner.
- Step 2 => and then I added create provisioner endpoint to create new provisioner.
- Step 3 => and then I added sync provisioning like the work you are doing in Happeo for sync with Okta but with differences.
in this step I should to create secret, so I used random alphanumeric characters to make secret by Base64 encoder and store it in database.
in this step ,the question came to me ,why we should return secret key instead of api token and is it secure to share this secret key in this way.
- step 4 => During this step, I struggled with how to ensure that the token came from external systems without conflicting 
with our JWT token and how to incorporate this functionality into the token filter and exclude provision user endpoint with other endpoints 
,Ultimately, I decided to exclude this endpoint from the normal flow and check all paths of the endpoint body first.
- Step 5 => I added getAllUsers endpoint to query users ,activate user endpoint.
- STep 6 => I added multi-stage docker file and docker compose to run properly the project
