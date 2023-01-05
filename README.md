# Parasoft Demo Application
The Parasoft Demo Application is an example Spring Boot project. The application is configurable and customizable, and is used to demonstrate functionality in a variety of Parasoft tools.

Docker Hub repository: https://hub.docker.com/r/parasoft/demo-app
## Getting Started
### Building .war from sources
Once you download the sources, build the project as a .war file using the Gradle wrapper.

In Linux / Cygwin:
```
./gradlew bootWar
```
In Windows:
```
gradlew.bat bootWar
```
The file parasoft-demo-app-1.1.0.war can be found in build/libs after building.
### Running
You can run the application either directly from sources,

In Linux / Cygwin:
```
./gradlew bootRun
```
In Windows:
```
gradlew.bat bootRun
```
Or as a .war file with Java (after building):
```
java -jar build/libs/parasoft-demo-app-1.1.0.war
```
Port can be specified when running .war file:
```
java -jar build/libs/parasoft-demo-app-1.1.0.war --server.port=8888
```
### Importing into your IDE
If you want to import the project into your IDE, be sure to do the following:
1. Import the project as a Gradle project. You may need to synchronize or refresh the project after importing.
2. Install a Lombok plugin for your IDE since the project uses Lombok.
#### Changing server port
When launching the app, you can specify the port to use with a command like the following:
```
./gradlew bootRun -Pport=8888
```
## Using the Demo Application
Once started, you can access the application at [http://localhost:8080](http://localhost:8080).

Login with one of these users:
- Username `purchaser` password `password`
- Username `approver` password `password`

## Connect to embedded HSQLDB server instance
There are four databases (one for global and three for industries) in Parasoft Demo Application, which are **global**, **outdoor**, **defense** and **aerospace**.

| Database name | Description                                          |
|---------------|------------------------------------------------------|
| global        | Used to store the user, role and configuration data. |
| outdoor       | Used to store the data about outdoor industry.       |
| defense       | Used to store the data about defense industry.       |
| aerospace     | Used to store the data about aerospace industry.     |

### Connection configuration
Parasoft Demo Application exposes port 9001 for the user to connect to the HSQLDB database remotely.

- Global database

| Option   | Value                                      |
|----------|--------------------------------------------|
| Driver   | `org.hsqldb.jdbcDriver`                    |
| URL      | `jdbc:hsqldb:hsql://localhost:9001/global` |
| Username | `SA`                                       |
| Password | `pass`                                     |

- Industry database

| Option   | Value                                               |
|----------|-----------------------------------------------------|
| Driver   | `org.hsqldb.jdbcDriver`                             |
| URL      | `jdbc:hsqldb:hsql://localhost:9001/{database name}` |
| Username | `SA`                                                |
| Password | `pass`                                              |

## Using Parasoft JMS Proxy and Virtual Asset with message queue
There are two main services for order management in PDA, **order service** and **inventory service**. After an order is submitted, order service sends
a request through message queue to check and decrease the inventory. After the operation is done, inventory service sends a response through message queue which includes the information of the operation result.

### Configuration
PDA uses two default queues/topics to support messaging between **order service** and **inventory service**.
The configuration for queues/topics can be changed or reset to default on PDA Demo Administration page.

<img src="src/main/resources/static/common/images/mq_default_mode_diagram.png" alt="mq default mode diagram">

**Configuration details for embedded ActiveMQ server**

| Option                            | Value                                                    |
|-----------------------------------|----------------------------------------------------------|
| Provider URL                      | `tcp://localhost:61626`                                  |
| Initial context class             | `org.apache.activemq.jndi.ActiveMQInitialContextFactory` |
| Connection factory                | `ConnectionFactory`                                      |
| Username                          | `admin`                                                  |
| Password                          | `admin`                                                  |
| Inventory service request queue   | `inventory.request`                                      |
| Inventory service response queue  | `inventory.response`                                     |

**Configuration details for external Kafka server (default)**

|    Option   |             Value              |
|-------------|--------------------------------|
| Broker URL  |       `localhost:9092`         |
| Group ID    |       `inventory-operation`    |

**Configuration details for external RabbitMQ server (default)**

| Option                     | Value                      |
|----------------------------|----------------------------|
| Host                       | `localhost`                |
| Port                       | `5672`                     |
| Username                   | `guest`                    |
| Password                   | `guest`                    |
| Exchange                   | `inventory.direct`         |
| Request queue routing key  | `inventory.queue.request`  |
| Response queue routing key | `inventory.queue.response` |

This configuration can be changed in **application.properties** file.

### Using JMS Proxy
To use the queueing system with JMS proxy, you can change **Destination queue** and **Reply to queue** to customized queue names.
The **Client Connection** in message proxy should be configured with the two customized queues.
The **Server Connection** in message proxy should be configured with the two default queues.

<img src="src/main/resources/static/common/images/mq_proxy_mode_diagram.png" alt="mq proxy mode diagram">

### Using virtual asset with JMS and RabbitMQ
To use the queueing system with virtual asset, you can change **Inventory service request queue** to a customized destination queue name.
The virtual asset deployment should be configured to listen to the customized destination queue and reply to the default response queue.

<img src="src/main/resources/static/common/images/mq_virtual_asset_mode_diagram.png" alt="mq virtual asset mode diagram">

### Using virtual asset with Kafka
To use Kafka with virtual asset, you can change **Inventory service request topic**
to a customized request topic name. The virtual asset deployment should be configured to listen to the customized request topic and produce messages to the default response topic.

<img src="src/main/resources/static/common/images/Kafka_virtual_asset_mode_diagram.png" alt="Kafka virtual asset mode diagram">

### Using external Kafka server with PDA

1. Download, install and start a Kafka server (0.10.0.0 or later) using default settings.
2. Set Kafka broker URL and consumer group ID in **application.properties** file.
3. Start PDA and change queue type to Kafka in **PARASOFT QUEUE CONFIGURATION** section of PDA Demo Administration page.
4. To test connection with Kafka server, either use **Test Connection** button in **Kafka configuration details** link or save changes in PDA Demo Administration page.

### Using external RabbitMQ server with PDA
1. Download, install Erlang and RabbitMQ, start the RabbitMQ server  using default settings.
> Note: <br>
> RabbitMQ version should be compatible with Erlang version.
> Demo Application supports the minimal RabbitMQ version is 2.0.0 and the compatible Erlang version is R13B.
> We recommend you to see the [RabbitMQ and Erlang/OTP Compatibility Matrix](https://www.rabbitmq.com/which-erlang.html#compatibility-matrix) section on RabbitMQ official website to find compatible versions.
2. Set RabbitMQ host, port, username, password in **application.properties** file.
3. Start PDA and change queue type to RabbitMQ in **PARASOFT QUEUE CONFIGURATION** section of PDA Demo Administration page.
4. To test connection with RabbitMQ server, either use **Test Connection** button in **RabbitMQ configuration details** link or save changes in PDA Demo Administration page.
## Using Parasoft JDBC Proxy
1. Find the **ParasoftJDBCDriver.jar** in **{SOAtest & Virtualize installation directory}/{version}/proxies**.
2. Copy it to **{root directory of parasoft-demo-app}/lib**. (Create the folder if it does not already exist.)
3. Open **SOAtest & Virtualize** desktop, add the **ParasoftJDBCDriver.jar** to **Parasoft > Preferences > JDBC Drivers**.
4. Start Virtualize server in **Virtualize Server** view.
5. Enable the **PARASOFT JDBC PROXY** in PDA **Demo Administration** page, modify started server's **URL**, **Parasoft Virtualize Server path**, and **Parasoft Virtualize group ID** if necessary.
6. Go to **SOAtest & Virtualize** desktop and refresh the Server. If the **Parasoft JDBC Proxy** is enabled successfully, there will be a controller which has the same name as group ID under **JDBC Controllers**.
7. Change the settings of the controller.

## Using SOAtest DB Tool
1. Open **SOAtest & Virtualize** desktop, add the hsqldb driver to **Parasoft > Preferences > JDBC Drivers**.
2. Create a tst file with **DB Tool**.
3. Open the **DB Tool** and open the **Connection** tab. Select **Local** option and fill in **Driver**, **URL**, **Username**, and **Password** for the database.

| Option   | Value                                               |
|----------|-----------------------------------------------------|
| Driver   | `org.hsqldb.jdbcDriver`                             |
| URL      | `jdbc:hsqldb:hsql://localhost:9001/{database name}` |
| Username | `SA`                                                |
| Password | `pass`                                              |

4. Write SQL statement in **SQL Query** tab and run the test. The query results will be shown in **Traffic Object**.