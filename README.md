## Hoverdb

I find interacting with databases in Java annoying and filled with boilerplate. I created hoverdb to take much of that 
away.

## Getting Started

### Maven

```xml
<repositories>
    .
    .
    .
    <repository>
        <id>sultanofcardio</id>
        <url>https://repo.sultanofcardio.com/artifactory/sultanofcardio</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.sultanofcardio</groupId>
        <artifactId>hoverdb</artifactId>
    <version>1.0.0</version>
</dependency>
```
Or in your build.gradle file:
```groovy
repositories {
    mavenCentral()
    maven { url "https://repo.sultanofcardio.com/artifactory/sultanofcardio" }
}

implementation 'com.sultanofcardio:hoverdb:1.0.0'
``` 

Also be sure to add your particular database driver as a dependency. e.g. MySQL:
```xml
<dependency>
    <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    <version>5.1.45</version>
</dependency>
```
```groovy
implementation 'mysql:mysql-connector-java:5.1.45'
```

## Usage

### Querying

Get a connection to your database much the same way you usually would

```java
Database mysql = Database.connect("db", Types.MySQL, "localhost", "3306", "root", "password", "localdb");
```

This connection is then cached using the alias `localdb` and can be retrieved subsequently from anywhere in the code using `Database.getInstance("localdb");`

You can perform normal CRUD operations with the database object

#### Select
```java
ResultSet resultSet = mysql.select()
			   .from("my_table")
			    .where("id", 45)
			    .execute();
```
which is the equivalent of
```sql
SELECT * FROM my_table where id = 45;
```

#### Update
```java
long rows = mysql.update("my_table")
                   .set("description", "Hello World")
                    .where("id", 45)
                    .run();
```
which translates to
```sql
UPDATE my_table SET description = 'Hello World' where id = 45;
```

This is much more Java-like

#### Raw SQL

You can also run raw SQL directly on the database
```java
long rows = mysql.run("DELETE FROM my_table WHERE id = 45");
```

#### FileDatabase

The `FileDatabase` class can be used to connect to databases on the file system, such as SQLite databases

```java
Database sqlitedb = FileDatabase.connect("sqlite.db", Types.SQLite, "sqlitedb");
```

### ORM

I don't think this feature offers any real benefits just yet, but you may decide that you want to take advantage of it 
anyway.

Create a class that extends Entity, and implement the required methods (constructor, load, loadAll, save, update, 
delete, toString).

Because you have to implement these yourself, the value of it becomes diminished. This functionality will be coming in 
the future.

## Other Stuff

Support is built in for MySQL, SQLServer, SQLite and Oracle databases. 

You can easily add additional database support by extending `DatabaseType` and implementing the formatting methods for 
their particular syntax.

This library uses [Semantic Versioning](http://semver.org/)

