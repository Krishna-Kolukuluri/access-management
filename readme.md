# Access Management API

Access Management API is a Rest based service that enables creating, updating and deleting users, groups and their relationships.

Using H2 database for temporary storage of data. Enabled encryption of sensitive data like `password` when saved to database.

* **Users API** can be accessed while service is running from [Users](http://localhost:8080/am/swagger-ui/index.html#/user-controller)
* **Groups API** can be accessed while service is running from [Groups](http://localhost:8080/am/swagger-ui/index.html#/group-controller)
* **API Schema** can be accessed while service is running from [Schema](http://localhost:8080/am/swagger-ui/index.html#/)

## Prerequisites
* **OpenJDK 17** or **later** versions can be downloaded from [JDK](https://jdk.java.net/17/)
* **Maven** 
* **Docker**
* **
Before proceeding to next steps, please verify prerequisites are installed/setup and accessible via cmd/terminal.

## Steps to Build/Test Access Management API
From cmd/terminal navigate to ~/access-management folder or can be built from Intellij, Eclipse or any other supported IDE.
* **Build** `mvn clean compile`
* **Unit/Integration Test** `mvn test`
* **Code Coverage Report** will be available under `~\target\site\jacoco\index.html` or [report](.\target\site\jacoco\index.html)
* **

## Steps to Build and Run Access Management API
After installing/setting-up all prerequisites follow below steps to build and launch Access Management API Server in docker container.
* **Step 1** Clone latest code from [Access-Management](https://github.com/Krishna-Kolukuluri/access-management) GitHub repo
* **Step 2** From cmd/terminal navigate to ~/access-management folder and  Build code using `mvn clean install`
* **Step 3** From same folder run `docker-compose --file .\docker-compose.yml up -d --build`
* **
If all above steps are successful then 
* **API Server** will be available [api](http://localhost:8080/am/swagger-ui/index.html#/)
* **API Documentation** will be available [docs](http://localhost:8080/am/swagger-ui/index.html#/)
* **API Server Logs** will be available [logs](http://localhost:8080/am/actuator/logfile)
* **API Database** will be available [db](http://localhost:8080/am/h2-console/) change JDBC URL to `jdbc:h2:file:~/data/access-management.db`

## Steps to Stop and Remove running API Server containers
* **Step 1** `Docker ps` list all running containers
* **Step 2** Identify and get `containerid` from **Step 1** and run `docker stop containerid` to stop.
* **Step 2** Identify and get `containerid` from **Step 1** and run `docker rm containerid` to delete.


##  Sample User Payload Messages

* **User** Payload sample json for creating without group `/am/users/createUser`
```
  {
    "firstName":"Krishna_Two",
    "lastName":"Krishna_Two",
    "userName":"Krishna_Two",
    "userRole":"ADMIN",
    "dob":"2010-12-14",
    "address":"111 Address Cary, NC",
    "password":"KrishnaKra@1234"
  }
```

* **User** Payload sample json for creating with group `/am/users/createUser`
```
{
  "firstName": "Krishna",
  "lastName": "Kolukuluri",
  "userName": "KrishnaRead",
  "userRole": "NONE_ADMIN",
  "dob": "2010-12-14",
  "address": "111 Address Cary, NC",
  "password": "KrishnaKra@1234",
  "groups": [
    {
      "groupName": "READ"
    }
  ]
}
```
* **User** Payload sample for all users `/am/users/all`
```
[
    {
        "firstName": "Krishna",
        "lastName": "Kolukuluri",
        "userName": "KrishnaWrite",
        "dob": "2010-12-13",
        "userRole": "NON_ADMIN",
        "address": "111 Address Cary, NC",
        "groups": [
            {
                "groupName": "WRITE",
                "groupDescription": "Default Read Group",
                "groupRole": "NON_ADMIN",
                "groupPermission": "WRITE"
            }
        ]
    },
    {
        "firstName": "KrishnaNewOne",
        "lastName": "KrishnaNewOne",
        "userName": "KrishnaNewOne",
        "dob": "2010-12-13",
        "userRole": "NON_ADMIN",
        "address": "111 Address Cary, NC",
        "groups": []
    }
]
```
* **User** Payload sample for single user `/am/users/userName`
```
{
    "firstName": "KrishnaNewOne",
    "lastName": "KrishnaNewOne",
    "userName": "KrishnaNewOne",
    "dob": "2010-12-13",
    "userRole": "NON_ADMIN",
    "address": "111 Address Cary, NC",
    "groups": []
}
```
* **User** Payload sample for delete user `/am/users/KrishnaWrite` `DELETE`
```
{
    "httpStatus": "OK",
    "message": "User found and deleted.",
    "status": true
}
```
* **User** Payload sample for update user `/am/users/KrishnaNewOne` `PUT`
```
PUT:
{
  "firstName": "KrishnaNewOne",
  "lastName": "KrishnaNewOne",
  "userName": "KrishnaNewOne",
  "userRole": "NON_ADMIN",
  "dob": "2010-12-14",
  "address": "111 Address Cary, NC",
  "password": "Krishna@12",
  "groups": [
    {
      "groupName": "READ"
    }
  ]
}
Response:
{
    "firstName": "KrishnaNewOne",
    "lastName": "KrishnaNewOne",
    "userName": "KrishnaNewOne",
    "dob": "2010-12-14",
    "userRole": "NON_ADMIN",
    "address": "111 Address Cary, NC",
    "groups": [
        {
            "groupName": "READ",
            "groupDescription": "Default Read Only Group Updated groupDescription",
            "groupRole": "NON_ADMIN",
            "groupPermission": "READ"
        }
    ]
}
```



## Sample Group Payload Messages

* **Group** Payload sample json for creating group `/am/groups/createGroup`

```
POST
{
  "groupName": "WRITE",
  "groupDescription": "Default Read Group",
  "groupRole": "NON_ADMIN",
  "groupPermission": "WRITE"
}
Response:
{
    "httpStatus": "CREATED",
    "message": "Created Group with GroupName: 'WRITE'",
    "status": true
}
```
* **Group** Payload sample json for get group with users `/am/groups/WRITE`

```
{
    "groupName": "WRITE",
    "groupDescription": "Default Read Group",
    "groupRole": "NON_ADMIN",
    "groupPermission": "WRITE",
    "users": [
        {
            "firstName": "Krishna",
            "lastName": "Kolukuluri",
            "userName": "KrishnaWrite",
            "userRole": "NON_ADMIN"
        }
    ]
}
```

* **Group** Payload sample json for get all group `/am/groups/all`

```
[
    {
        "groupName": "READ",
        "groupDescription": "Default Read Group",
        "groupRole": "NON_ADMIN",
        "groupPermission": "READ"
    },
    {
        "groupName": "WRITE",
        "groupDescription": "Default Read Group",
        "groupRole": "NON_ADMIN",
        "groupPermission": "WRITE"
    },
    {
        "groupName": "NONE",
        "groupDescription": "Default None Group",
        "groupRole": "NON_ADMIN",
        "groupPermission": "NONE"
    },
    {
        "groupName": "ADMIN_ALL",
        "groupDescription": "Default Admin Group, Not be deleted",
        "groupRole": "ADMIN",
        "groupPermission": "ALL"
    }
]
```

* **Group** Payload sample json for delete group `/am/groups/WRITE` `DELETE`

```
{
    "httpStatus": "OK",
    "message": "WRITE Group found and deleted",
    "status": true
}
```

* **Group** Payload sample json for update group `//am/groups/READ` `PUT`

```
PUT:
{
  "groupName": "READ",
  "groupDescription": "Default Read Only Group Updated groupDescription",
  "groupRole": "NON_ADMIN",
  "groupPermission": "READ",
  "users": [
    {
      "userName": "Krishna_Two"
    }
  ]
}
Response:
{
    "httpStatus": "OK",
    "message": "Updated Group: 'READ'",
    "status": true
}
```