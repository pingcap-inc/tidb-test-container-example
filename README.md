# tidb-test-container-example

This is an example for [testcontainers-java](https://github.com/testcontainers/testcontainers-java) and TiDB.

## Usage

1. `mvn clean test`
2. And there is **NOT** the second step.

## Output

```
------------ TiDB default version ------------
db: test
username: root
password: 
jdbc: jdbc:mysql://localhost:56464/test
5.7.25-TiDB-v6.1.1
------------ TiDB v6.2.0 version ------------
db: test
username: root
password: 
jdbc: jdbc:mysql://localhost:56470/test
5.7.25-TiDB-v6.2.0
```

## Code

### Define a query function named `performQuery`

```java
protected ResultSet performQuery(TiDBContainer container, String sql) throws SQLException {
    DataSource ds = getDataSource(container);
    Statement statement = ds.getConnection().createStatement();
    statement.execute(sql);
    return statement.getResultSet();
}

protected DataSource getDataSource(TiDBContainer container) throws SQLException {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL(container.getJdbcUrl());
    dataSource.setUser(container.getUsername());
    dataSource.setPassword(container.getPassword());
    dataSource.setUseSSL(false);
    return dataSource;
}
```

### And create a container to run SQL

```java
public void printParticularTiDBVersion() throws SQLException {
    // Appoint TiDB version to v6.2.0
    try (TiDBContainer tidb62 = new TiDBContainer("pingcap/tidb:v6.2.0")) {
        tidb62.start();

        System.out.println("------------ TiDB v6.2.0 version ------------");
        printTiDBParams(tidb62);

        ResultSet rs = performQuery(tidb62, "SELECT VERSION()");
        while (rs.next()) {
        System.out.println(rs.getString(1));
        }
    }
}
```

You can get more information at [AppTest.java](/src/test/java/com/pingcap/AppTest.java).
