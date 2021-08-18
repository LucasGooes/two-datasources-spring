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
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = "com.test.twodatasourcesdocumentation.first.repository", entityManagerFactoryRef = "firstEntityManagerFactory", transactionManagerRef = "firstTransactionManager")
@EnableTransactionManagement
public class FirstDataSourceConfiguration {
	
	@Bean
	@Primary
	@ConfigurationProperties("first.datasource")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean
	@Primary
	@ConfigurationProperties("first.datasource.configuration")
	public HikariDataSource firstDataSource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Bean("jpaProps")
	@Primary
	@ConfigurationProperties("first.jpa")
	public JpaProperties firstJpaProperties() {
		return new JpaProperties();
	}
	
	@Bean(name = "firstEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(DataSource firstDataSource,@Qualifier("jpaProps") JpaProperties firstJpaProperties) {
		 Map<String, Object> properties = new HashMap<String, Object>();
		 properties.put("hibernate.hbm2ddl.auto", "create");
		 properties.put("hibernate.show_sql", "true");
		 properties.put("hibernate.format_sql", "true");
		 EntityManagerFactoryBuilder builder = createEntityManagerFactoryBuilder(firstJpaProperties);
		return builder.dataSource(firstDataSource).packages("com.test.twodatasourcesdocumentation.first.domain").persistenceUnit("firstdb").properties(properties).build();
	}
	
	@Bean(name = "firstTransactionManager")
	@Primary
	public PlatformTransactionManager firstTransactionManager(EntityManagerFactory firstEntityManagerFactory) {
		return new JpaTransactionManager(firstEntityManagerFactory);
	}
	
	private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(@Qualifier("jpaProps") JpaProperties jpaProperties) {
		JpaVendorAdapter jpaVendorAdapter = createJpaVendorAdapter(jpaProperties);
		return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
	}
	
	private JpaVendorAdapter createJpaVendorAdapter(JpaProperties jpaProperties) {
		return new HibernateJpaVendorAdapter();
	}
}
