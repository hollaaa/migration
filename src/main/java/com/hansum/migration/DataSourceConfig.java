package com.hansum.migration;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashSet;

@Configuration
@MapperScan(basePackages = {"com.hansum.migration"})
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource1")
    public DataSource mysql1DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource2")
    public DataSource mysql2DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactoryMain(@Autowired @Qualifier("mysql1DataSource") DataSource dataSource) throws Exception
    {
        System.out.println("sqlSessionFactoryMain Start");

        org.apache.ibatis.session.Configuration configuration = this.getMyBatisConfig();

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setConfiguration(configuration);
        factoryBean.setTypeHandlersPackage("com.commax.tool.framework.mybatis.typehandler");

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resource = resolver.getResources("mybatis/h2/*.xml");
        factoryBean.setMapperLocations(resource);

        return factoryBean.getObject();

    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Autowired @Qualifier("mysql2DataSource") DataSource dataSource) throws Exception
    {
        System.out.println("sqlSessionFactory Sub Start");

        org.apache.ibatis.session.Configuration configuration = this.getMyBatisConfig();

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setConfiguration(configuration);
        factoryBean.setTypeHandlersPackage("com.commax.tool.framework.mybatis.typehandler");

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resource = resolver.getResources("mybatis/mysql/*.xml");
        factoryBean.setMapperLocations(resource);

        return factoryBean.getObject();

    }

    @Bean
    @Primary
    public SqlSession sqlSessionMain(@Autowired  @Qualifier("sqlSessionFactoryMain") SqlSessionFactory factory)
    {
        return new SqlSessionTemplate(factory);
    }

    @Bean
    public SqlSession sqlSessionSub(@Autowired  @Qualifier("sqlSessionFactory") SqlSessionFactory factory)
    {
        return new SqlSessionTemplate(factory);
    }

    private org.apache.ibatis.session.Configuration getMyBatisConfig() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(true);
        configuration.setAggressiveLazyLoading(false);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setUseColumnLabel(true);
        configuration.setAutoMappingBehavior(AutoMappingBehavior.PARTIAL);
        configuration.setDefaultStatementTimeout(25000);
        configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.NVARCHAR);
        configuration.setLazyLoadTriggerMethods(new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString")));
        configuration.setLogPrefix("[SQL]");

        return configuration;
    }

}
