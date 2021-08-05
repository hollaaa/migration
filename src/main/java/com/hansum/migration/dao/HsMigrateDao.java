package com.hansum.migration.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
@Slf4j
public class HsMigrateDao {

    @Autowired
    DataSource dataSource;

    public void testConnection() throws Exception
    {
        Connection connection = dataSource.getConnection();
        log.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        log.debug("url=" + connection.getMetaData().getURL());
        log.debug("username=" + connection.getMetaData().getUserName());
        log.debug("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

    }

}
