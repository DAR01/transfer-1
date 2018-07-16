## Money Transfer

Implementation of a RESTful API for creating accounts and money transfers between accounts.

Run app by `build_run.sh` or make jar by yourself.
Running needs port 8080 exposed, you can also edit port at `config.json`.
Http server starts on localhost.

**Packaging**:

    mvn clean package
    
**Running**:
   
    java -jar target/transfer-1.0.0-SNAPSHOT-fat.jar

**API**:

**Get account**:

    GET localhost:8080/account/{id}

**Create account**: 

    POST localhost:8080/account/
    Example body: {"amount":100}
    
**Money transfer**:

    PATCH localhost:8080/transfer/{fromId}/{toId}/{amount}

**Tools**:
Java 8, Vert.X, Susom, HSQLDB, JUnit, Git

**TODO**:
- Add event bus
