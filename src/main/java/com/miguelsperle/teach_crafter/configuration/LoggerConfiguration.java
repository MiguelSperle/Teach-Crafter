package com.miguelsperle.teach_crafter.configuration;

import com.miguelsperle.teach_crafter.scheduling.ScheduledTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // As classes que contém essa anotação, elas definem e configuram os beans e são gerencidas automaticamente pelo spring
public class LoggerConfiguration {
    @Bean
    public Logger logger() { // Sempre que o Spring precisar de um Logger, ele chamará esse método para obter uma instância
        return LoggerFactory.getLogger(ScheduledTaskManager.class);
    }
}