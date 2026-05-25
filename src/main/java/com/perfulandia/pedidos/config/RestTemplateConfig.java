package com.perfulandia.pedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// Esta clase le dice a Spring que cree el objeto RestTemplate
// para que podamos usarlo en el Service con @Autowired
// sin esta clase Spring no sabria como crear el RestTemplate
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}