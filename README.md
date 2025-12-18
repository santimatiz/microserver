# Microserver

A lightweight, generic REST API server for PostgreSQL and MySQL databases.

## Overview

Microserver is a Java-based REST API server that allows you to perform CRUD (Create, Read, Update, Delete) operations on any table or view in your PostgreSQL or MySQL database. It's designed to be simple to set up and use, providing a flexible and powerful way to interact with your data.

## Features

-   **Generic REST API:** No need to write custom code for each table. Microserver dynamically generates SQL queries based on the URL and request body.
-   **CRUD Operations:** Supports `GET`, `POST`, `PUT`, and `DELETE` requests for reading, creating, updating, and deleting records.
-   **PostgreSQL and MySQL Support:** Works with both PostgreSQL and MySQL databases.
-   **SSL Support:** Secure your API with SSL encryption.
-   **Configurable:** Easily configure the server port, database connection, and logging.

## How to Install

1.  **Build the project:**
    ```bash
    cd microRest
    mvn clean install
    ```
2.  **Copy the JAR file:**
    The executable JAR file, `MicroServer-jar-with-dependencies.jar`, will be located in the `microRest/target` directory.

## How to Configure

Create a configuration file (e.g., `data.conf.txt`) with the following properties:

```properties
con_url=jdbc:postgresql://localhost/mydatabase
db_user=myuser
db_password=mypassword
port=8080
timeout=5000
ssl=false
log=INFO
```

-   `con_url`: The JDBC connection URL for your database.
-   `db_user`: The username for your database.
-   `db_password`: The password for your database.
-   `port`: The port on which the server will listen.
-   `timeout`: The request timeout in milliseconds.
-   `ssl`: Set to `true` to enable SSL.
-   `log`: The logging level (`VERBOSE`, `INFO`, or `ERROR`).

If you enable SSL, you'll also need to provide the following properties:

```properties
TrustStore=/path/to/your/truststore.p12
TrustStorePassword=your_truststore_password
KeyStorePassword=your_keystore_password
KeyStore=/path/to/your/keystore.jks
```

## How to Run

Run the server from the command line, passing the path to your configuration file as an argument:

```bash
java -jar MicroServer-jar-with-dependencies.jar /path/to/your/data.conf.txt
```

## How to Use the API

The API follows a simple, RESTful URL schema:

`/api/v1/{schema}/{table}`

### GET (Read)

To get all records from a table:

`GET /api/v1/public/users`

To get a specific record by its ID:

`GET /api/v1/public/users?id=1`

### POST (Create)

To create a new record, send a `POST` request with the data in the request body:

`POST /api/v1/public/users`

**Body:**

```json
{
    "name": "John Doe",
    "email": "john.doe@example.com"
}
```

### PUT (Update)

To update a record, send a `PUT` request with the new data in the request body and the ID of the record in the URL parameters:

`PUT /api/v1/public/users?id=1`

**Body:**

```json
{
    "name": "John Smith"
}
```

### DELETE

To delete a record, send a `DELETE` request with the ID of the record in the URL parameters:

`DELETE /api/v1/public/users?id=1`

Enjoy!
