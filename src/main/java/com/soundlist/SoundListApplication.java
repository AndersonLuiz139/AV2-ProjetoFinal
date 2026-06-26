package com.soundlist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação SoundList.

 * @SpringBootApplication combina:
 *  - @Configuration       → classe de configuração Spring
 *  - @EnableAutoConfiguration → ativa a auto-configuração do Spring Boot
 *  - @ComponentScan       → varre os sub-pacotes em busca de beans
 */
@SpringBootApplication
public class SoundListApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoundListApplication.class, args);
    }
}
