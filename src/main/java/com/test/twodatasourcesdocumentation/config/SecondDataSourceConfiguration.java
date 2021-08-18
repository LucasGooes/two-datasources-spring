package com.test.twodatasourcesdocumentation.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, 
	    DataSourceTransactionManagerAutoConfiguration.class, 
	    HibernateJpaAutoConfiguration.class})
@Configuration
@EnableJpaRepositories(basePackages = "com.test.twodatasourcesdocumentation.second.repository", entityManagerFactoryRef = "secondEntityManagerFactory", transactionManagerRef = "secondTransactionManager")
@EnableTransactionManagement
public class SecondDataSourceConfiguration {
	
	@Bean
	@ConfigurationProperties("second.datasource")
	public DataSourceProperties secondDataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean
	@ConfigurationProperties("second.datasource.configuration")
	public HikariDataSource secondDataSource(@Qualifier("secondDataSourceProperties") DataSourceProperties secondDataSourceProperties) {
		return secondDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean("secondJpaProps")
	@ConfigurationProperties("second.jpa")
	public JpaProperties secondJpaProperties() {
		return new JpaProperties();
	}
	
	
	@Bean(name = "secondEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean secondEntityManagerFactory(@Qualifier("secondDataSource") DataSource secondDataSource,@Qualifier("secondJpaProps") JpaProperties secondJpaProperties) {
		Map<String, Object> properties = new HashMap<String, Object>();
		 properties.put("hibernate.hbm2ddl.auto", "create");
		 properties.put("hibernate.show_sql", "true");
		 properties.put("hibernate.format_sql", "true");
		 EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(secondJpaProperties);
		return builder.dataSource(secondDataSource).packages("com.test.twodatasourcesdocumentation.second.domain").persistenceUnit("seconddb").properties(properties).build();
	}
	
	@Bean(name = "secondTransactionManager")
	public PlatformTransactionManager secondTransactionManager(EntityManagerFactory secondEntityManagerFactory) {
		return new JpaTransactionManager(secondEntityManagerFactory);
	}
	
	private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(@Qualifier("secondJpaProps") JpaProperties jpaProperties) {
		JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(jpaProperties);
		return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
	}
	
	private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
		return new HibernateJpaVendorAdapter();
	}

}
