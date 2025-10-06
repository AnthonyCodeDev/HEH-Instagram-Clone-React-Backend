package com.example.backendstragram.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.backendstragram.adapter.out.persistence")
@EntityScan("com.example.backendstragram.adapter.out.persistence.entity")
@EnableTransactionManagement
public class PersistenceConfig {}
