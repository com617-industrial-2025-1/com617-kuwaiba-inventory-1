# readme osm-to-kuwaiba

this application is written in spring boot

to build the application without tests use

```
 mvn clean install -DskipTests
```
note that if you don't have maven installed on your PC, there is a maven runner included with  the project.

You can use the runner on windows instead of `mvn`

```
 ./mvnw.cmd
```

To build with tests use

```
 mvn clean install
```

to run the tests you will need to have the database already turned using the database docker project

The database credentials are set in the application.properties file

To run as a an application use

```
 mvn spring-boot:run
```

