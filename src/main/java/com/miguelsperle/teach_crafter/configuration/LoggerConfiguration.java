package com.miguelsperle.teach_crafter.configuration;

import com.miguelsperle.teach_crafter.scheduling.ScheduledTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Essa anotação indica que a classe contém métodos que podem ser usados para definir beans do Spring
public class LoggerConfiguration {
    @Bean
    public Logger logger() { // Sempre que o Spring precisar de um Logger, ele chamará esse método para obter uma instância
        return LoggerFactory.getLogger(ScheduledTaskManager.class);
    }
}