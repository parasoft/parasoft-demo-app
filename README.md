# Parasoft Demo Application
The Parasoft Demo Application is an example Spring Boot project. The application is configurable and customizable, and is used to demonstrate functionality in a variery of Parasoft tools.

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
You can run the application either directly from sources.

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
### Importing into your IDE
If you want to import the project into your IDE, be sure to do the following:
1. Import the project as a Gradle project. You may need to synchronize or refresh the project after importing.
2. Install a Lombok plugin for your IDE, since the project uses Lombok.
#### Changing server port
When launching the app, you can specify the port to use with a command like the following:
```
./gradlew bootRun -Pport=8888'
```
## Using the Demo Application
Once started, you can access the application at [http://localhost:8080](http://localhost:8080).

Login with one of these users:
- Username `purchaser` password `password`
- Username `approver` password `password`

## HSQLDB JDBC configuration
There are four databases in parasoft-demo-app, which are corresponding to `global`, `outdoor`, `defense` and `aerospace`.

| Database  | Description                                         |
|-----------|-----------------------------------------------------|
| global    | Use to store the user, role and configuration data. |
| outdoor   | Use to store the data about outdoor.                |
| defense   | Use to store the data about defense.                |
| aerospace | Use to store the data about aerospace.              |

When using parasoft-demo-app in IDE, there are databases' configuration files (only need to configure one of them):
- /src/main/resources/application.properties
- /config/application.properties (Users can create the path and file by themselves)

When using .war package to start the parasoft-demo-app:
- {parent directory of .war package}/config/application.properties (Users can create the path and file by themselves)

### HSQLDB Embedded configuration

Should specify the path where the databases located in the file explore (absolute and relative paths are both supported). If there are no corresponding databases at the specified path when starting the project, parasoft-demo-app will create these databases automatically.
Every database has a default value in parasoft-demo-app, see the comments below for details.
Add the following code to application.properties file (would be enabled when restart the parasoft-demo-app):
```
global.datasource.configuration.url=jdbc:hsqldb:file:{path_to_database_dir}/{database_alias}    // Default as jdbc:hsqldb:file:./pda-db/global
global.datasource.configuration.driver-class-name=org.hsqldb.jdbcDriver
global.datasource.configuration.username=SA
global.datasource.configuration.password=

industry.datasource.configurations.outdoor.url=jdbc:hsqldb:file:{path_to_database_dir}/{database_alias}     // Default as jdbc:hsqldb:file:./pda-db/outdoor
industry.datasource.configurations.outdoor.driver-class-name=org.hsqldb.jdbcDriver
industry.datasource.configurations.outdoor.username=SA
industry.datasource.configurations.outdoor.password=

industry.datasource.configurations.defense.url=jdbc:hsqldb:file:{path_to_database_dir}/{database_alias}     // Default as jdbc:hsqldb:file:./pda-db/defense
industry.datasource.configurations.defense.driver-class-name=org.hsqldb.jdbcDriver
industry.datasource.configurations.defense.username=SA
industry.datasource.configurations.defense.password=

industry.datasource.configurations.aerospace.url=jdbc:hsqldb:file:{path_to_database_dir}/{database_alias}     // Default as jdbc:hsqldb:file:./pda-db/aerospace
industry.datasource.configurations.aerospace.username=SA
industry.datasource.configurations.aerospace.password=
industry.datasource.configurations.aerospace.driver-class-name=org.hsqldb.jdbcDriver
```
>Hint：When the parasoft-demo-app has started, then we can not use third-party tools to connect these databases. Also, when third-party tools has connected these databases, then the parasoft-demo-app can not been started.

### HSQLDB Server configuration
Start the HSQLDB Server first, and the server must contain the databases corresponding to global, outdoor, defense and aerospace.
Add the following code to application.properties file (would be enabled when restart the parasoft-demo-app):
```
global.datasource.configuration.driver-class-name=org.hsqldb.jdbcDriver
global.datasource.configuration.url=jdbc:hsqldb:hsql://{url_to_server}/{database_alias}  // example: jdbc:hsqldb:hsql://localhost:9001/global
global.datasource.configuration.username=SA
global.datasource.configuration.password=

industry.datasource.configurations.outdoor.driver-class-name=org.hsqldb.jdbcDriver
industry.datasource.configurations.outdoor.url=jdbc:hsqldb:hsql://{url_to_server}/{database_alias}   // example: jdbc:hsqldb:hsql://localhost:9001/outdoor
industry.datasource.configurations.outdoor.username=SA
industry.datasource.configurations.outdoor.password=

industry.datasource.configurations.defense.driver-class-name=org.hsqldb.jdbcDriver
industry.datasource.configurations.defense.url=jdbc:hsqldb:hsql://{url_to_server}/{database_alias}   // example: jdbc:hsqldb:hsql://localhost:9001/defense
industry.datasource.configurations.defense.username=SA
industry.datasource.configurations.defense.password=

industry.datasource.configurations.aerospace.driver-class-name=org.hsqldb.jdbcDriver
industry.datasource.configurations.aerospace.url=jdbc:hsqldb:hsql://{url_to_server}/{database_alias}   // example: jdbc:hsqldb:hsql://localhost:9001/aerospace
industry.datasource.configurations.aerospace.username=SA
industry.datasource.configurations.aerospace.password=
```

## Using Parasoft JDBC Proxy
1. Find the `ParasoftJDBCDriver.jar` in `{SOAtest & Virtualize installation directory}/{version}/proxies`.
2. Copy it to `{root of parasoft-demo-app}/lib`. If there is no lib folder, then create one.
3. Go to `SOATest & Virtualize`, open `JDBC Drivers` preferences(parasoft -> preferences -> JDBC Drivers), and add the path of ParasoftJDBCDriver.jar.
4. Start the server in `Virtualize Server` view.
5. Enable the `PARASOFT JDBC PROXY` in PDA `Demo Admin` page, modify `started server's URL`, `Parasoft Virtualize Server path` and `Parasoft Virtualize group ID` if necessary.
6. Go to `SOATest & Virtualize`, refresh the Server, if the `Parasoft JDBC Proxy` is enabled successfully, then there is a controller which has the same name of group ID under `JDBC Controllers`.
7. Change the settings of the controller to use.
