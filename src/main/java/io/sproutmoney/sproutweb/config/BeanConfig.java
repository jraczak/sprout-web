package io.sproutmoney.sproutweb.config;

//  Created by Justin on 12/14/17

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "io.sproutmoney.sproutweb")
public class BeanConfig {

    Logger logger = LoggerFactory.getLogger(BeanConfig.class.getSimpleName());

    @Autowired
    Environment environment;

    //TODO: Figure out how to get this reading from local variables
    @Value("${SPRING_DATASOURCE_URL}")
    String databaseUrl;

    @Value("${SPRING_DATASOURCE_USERNAME}")
    String databaseUsername;

    @Value("${SPRING_DATASOURCE_PASSWORD}")
    String databasePassword;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        logger.info(System.getenv("SPRING_PROFILES_ACTIVE"));
        logger.info(System.getenv("SPRING_DATASOURCE_URL"));
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(new String[] {"io.sproutmoney.sproutweb.models"});
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        logger.info("Setting database URL as " + databaseUrl);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        return dataSource;
    }

    Properties hibernateProperties() {
        return new Properties() {
            {
                setProperty("hibernate.ddl-auto", "update");
                setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                setProperty("hibernate.show_sql", "true");
            }
        };
    }
}
