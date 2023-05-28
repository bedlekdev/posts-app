# Posts Application

Simple appliaction to manage posts.
This application is made of 3 separate Docker containers that holds:

- PostgreSQL database
- Java backend (Spring Boot)
- Angular frontend

Website which is available under the address: **http://localhost:4200/**

---

### Prerequisites

In order to run this application you need to install two tools: **Docker** & **Docker Compose**.

### How to run it?

An entire application can be ran with a single command in a terminal:

```
$ docker-compose up -d
```

If you want to stop it use following command:

```
$ docker-compose down
```


---

#### Postgres (Database)

PostgreSQL database contains only single schema with one table - post.

After running the app it can be accessible using this connectors:


- Host: *localhost*
- Database: *postsdb*
- User: *posts*
- Password: *postspwd*

#### Posts-api (REST API)

This is a Spring Boot (Java) based application that connects with a
database and expose the REST endpoints that can be consumed by
frontend. It supports multiple HTTP REST methods like GET, POST, PUT and
DELETE for post resource.

Full list of available REST endpoints could be found in Swagger UI,
which could be called using link: **http://localhost:8080/api/swagger-ui.html**

#### Posts-app (Frontend)

This is a real endpoint for a user where they can manipulate their 
posts. It consumes the REST API endpoints provided by
*Posts-api*.

It can be entered using link: **http://localhost:4200/**