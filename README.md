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
There are four databases in parasoft-demo-app, which are **global**, **outdoor**, **defense** and **aerospace**.

| Database  | Description                                          |
|-----------|------------------------------------------------------|
| global    | Used to store the user, role and configuration data. |
| outdoor   | Used to store the data about outdoor.                |
| defense   | Used to store the data about defense.                |
| aerospace | Used to store the data about aerospace.              |

When running parasoft-demo-app from source, one of the following files should be configured for databases:
- /src/main/resources/application.properties
- /config/application.properties (Create the folder and file if not already exists)

When running parasoft-demo-app with WAR file, the following file should be configured for databases:
- {parent directory of .war package}/config/application.properties (Create the folder and file if not already exists)

### HSQLDB Embedded configuration

Configure **application.properties** with your databases information as below and restart the application. 
The database directory can be either absolute path and relative path. Please note that if no databases specified, parasoft-demo-app will create the databases with default value as commented when the application started.
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
>Hint: Once application started (and connected to these databases), third-party tools will not be able to connect and access (and vice versa).

### HSQLDB Server configuration
The HSQLDB Server should be started first, which should have **global**, **outdoor**, **defense** and **aerospace** databases exist already.
Then configure **application.properties** with your databases information as below and restart the application:
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
1. Find the **ParasoftJDBCDriver.jar** in **{SOAtest & Virtualize installation directory}/{version}/proxies**.
2. Copy it to **{root directory of parasoft-demo-app}/lib**. (Create the folder if not already exists)
3. Open **SOAtest & Virtualize** desktop, add the **ParasoftJDBCDriver.jar** to **Parasoft -> Preferences -> JDBC Drivers**.
4. Start Virtualize server in **Virtualize Server** view.
5. Enable the **PARASOFT JDBC PROXY** in PDA **Demo Admin** page, modify **started server's URL**, **Parasoft Virtualize Server path** and **Parasoft Virtualize group ID** if necessary.
6. Go to **SOAtest & Virtualize** desktop and refresh the Server. If the **Parasoft JDBC Proxy** is enabled successfully, there will be a controller which has the same name as group ID under **JDBC Controllers**.
7. Change the settings of the controller.

## Using SOAtest DB Tool
1. Open **SOAtest & Virtualize** desktop, add the hsqldb driver to **Parasoft -> Preferences -> JDBC Drivers**.
2. Create a tst file with **DB Tool**.
3. Open the **DB Tool** and open **Connection** tab.
   - If **File** is selected, a configuration file with connection details need to be specified.
   - If **Local** is selected, **Driver**, **URL**, **Username**, and **Password** for the database need to be specified.

| Option   | Value                                                                                                                                                                                                                                      |
|----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Driver   | `org.hsqldb.jdbcDriver`                                                                                                                                                                                                                    |
| URL      | Should be the same as the configuration of [HSQLDB JDBC configuration](#hsqldb-jdbc-configuration) section. <br/>If you use embedded database, the URL should be <br/>`jdbc:hsqldb:file:{absoulte_path_to_database_dir}/{database_alias}`. |
| Username | Should be the same as the configuration of [HSQLDB JDBC configuration](#hsqldb-jdbc-configuration) section. <br/>The default value is `SA` if the username is not changed.                                                                 |
| Password | Should be the same as the configuration of [HSQLDB JDBC configuration](#hsqldb-jdbc-configuration) section. <br/>The default value is `''` if the password is not changed.                                                                 |

>Hint1: Database cannot be connected if parasoft-demo-app started with embedded database since database files are locked.

>Hint2: Configuration settings specified in **Local** section can be exported as a properties file by clicking the **Export Configuration Settings** button. This file can be used as **Input file** for **File** option.

>Hint3: If **Close connection** is enabled, connection will be closed right after the query is finished for current DB Tool. 
> Best practice for running multiple DB tools with same configuration is to disable this option. In that way the connection will be shared to improve efficiency.


4. Write SQL statement in **SQL Query** tab, and run the test, the query results will be showed in **Traffic Object**.
