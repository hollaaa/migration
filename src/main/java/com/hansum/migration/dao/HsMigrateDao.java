package com.hansum.migration.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class HsMigrateDao {

    @Autowired
    DataSource dataSource;

    public void testConnection() throws Exception
    {
        Connection connection = dataSource.getConnection();
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("url=" + connection.getMetaData().getURL());
        System.out.println("username=" + connection.getMetaData().getUserName());
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

    }

}
