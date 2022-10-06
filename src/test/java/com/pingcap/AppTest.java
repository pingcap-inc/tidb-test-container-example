package com.pingcap;


import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.tidb.TiDBContainer;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppTest
{
    // Default TiDB version at DockerHub: https://hub.docker.com/r/pingcap/tidb
    @Container
    private static final TiDBContainer tidb = new TiDBContainer("pingcap/tidb");

    public static void printTiDBParams(TiDBContainer tidb) {
        System.out.println("db: " + tidb.getDatabaseName());
        System.out.println("username: " + tidb.getUsername());
        System.out.println("password: " + tidb.getPassword());
        System.out.println("jdbc: " + tidb.getJdbcUrl());
    }

    @BeforeAll
    public static void beforeAll() {
        tidb.start();
    }

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

    @Test
    public void printDefaultTiDBVersion() throws SQLException {
        System.out.println("------------ TiDB default version ------------");
        printTiDBParams(tidb);

        ResultSet rs = performQuery(tidb, "SELECT VERSION()");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }


    @Test
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
}
