# Models and Hibernate Documentation

Initial Notes:

Documenting the role of hibernate in the application and the models:

- How hibernate creates the tables in the database
- How the models are setup for hibernate and spring boot

Relevant file in the osm-to-kuwaiba project:
- For Hibernate: [application.properties](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/resources/application.properties)
- [All Models](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/models/)

Each table in the schema, as seen in Figure 6 above, is represented by a corresponding Java entity class/model within the application. Models define the structure of the data and act as an object-oriented representation of the database tables. The classes are annotated using Java Persistence API (JPA), which defines how class fields map to relational database columns. Hibernation is used as the JPA provider to manage ORM. The main role of Hibernate is to connect the app and the database by automatically translating Java objects into SQL queries. This removes the need for manual SQL in standard operations and allows interaction with the database using Java objects instead. Hibernate also manages the persistence lifecycle of entities, including creating, reading, updating and deleting records. Application’s configuration is defined in the application.properties file. The spring.jpa.hibernate.ddl-auto=update allows Hibernate to automatically create or update database tables at application startup. 
