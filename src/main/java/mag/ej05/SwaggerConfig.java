package mag.ej05;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Book Advisor API")
                .version("1.0")
                .description("Documentación generada con Swagger usando Springdoc"))
                .addTagsItem(new Tag().name("Libros").description("Operaciones relacionadas con los libros"))
                .addTagsItem(new Tag().name("Usuarios").description("Gestión de usuarios y autenticación"))
                .addTagsItem(new Tag().name("Opiniones").description("Publicación y consulta de opiniones"))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación completa del proyecto")
                        .url("https://github.com/usuario/bookAdvisor"));
    }
}
