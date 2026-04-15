package rinhacampusiv.api.v2.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfigurations {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Rinha Campus IV API")
                        .version("v2")
                        .description("API REST da aplicação Rinha Campus IV, contendo funcionalidades de autenticação, gerenciamento de torneios, times, jogadores e pagamentos.")
                        .contact(new Contact()
                                .name("Time Backend")
                                .email("rinhaufpb@gmail.com")));
    }
}