package mag.ej05.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Necesario incluir
@EnableWebSecurity // Necesario incluir
@EnableMethodSecurity // Necesario incluir
public class SecurityConfig {
        // Se encarga de la gestión de autentificación
        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        // Bean para encriptar constraseñas
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // Define los permisos y/o roles necesarios para acceder a cada ruta
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.headers(headersConfigurer -> headersConfigurer
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

                http.authorizeHttpRequests(
                                auth -> auth
                                                // Permitir el acceso a recursos css
                                                .requestMatchers(
                                                                "/css/**", "/js/**", "/images/**",
                                                                "/public/**", "/webjars/**").permitAll()
                                                .requestMatchers("/api/**", "/api/genre").permitAll()

                                                // Acesso a la consola h2 a todos
                                                .requestMatchers("/h2-console/**").permitAll()
                                                // Acesso a a todos para registrase
                                                .requestMatchers("/usuarios/registro/**").permitAll()
                                                // Public y Libros accesible a todos
                                                .requestMatchers("/public/**").permitAll() // RTutas públicas
                                                .requestMatchers("/libros/").permitAll() // catálogo de libros
                                                .requestMatchers("/libros/listone/**").permitAll() // Vista individual
                                                                                                   // libro
                                                // Buscadores
                                                .requestMatchers("/libros/findByTitle/**").permitAll()
                                                .requestMatchers("/libros/findByGenero/**").permitAll()
                                                // Votacion
                                                .requestMatchers("/libros/votacion/**")
                                                .hasAnyRole("MANAGER", "ADMIN", "USER")
                                                // CRUD Libros
                                                .requestMatchers("/libros/addBook/**", "/libros/delete/**",
                                                                "libros/edit/**")
                                                .hasAnyRole("MANAGER", "ADMIN")
                                                // CRUD Generos
                                                .requestMatchers("/libros/addGenero/")
                                                .hasAnyRole("MANAGER", "ADMIN")
                                                // Usuarios
                                                .requestMatchers("/usuarios/**").hasRole("ADMIN")

                                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                                                .permitAll()
                                                .anyRequest().authenticated()) // Para cualquier petición no contemplada
                                                                               // anteriormente
                                // .formLogin(formLogin -> formLogin
                                // .defaultSuccessUrl("/", true)
                                // .permitAll())
                                // .logout(logout -> logout
                                // .permitAll())
                                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                                                .loginPage("/public/signin").permitAll() // mapping par mostrar
                                                                                         // formulario de login
                                                .loginProcessingUrl("/login").permitAll() // ruta post de /signin
                                                .failureUrl("/public/signin?error") // vuelve a signin con mensaje de
                                                                                    // error
                                                .defaultSuccessUrl("/public/", true).permitAll())
                                .logout((logout) -> logout
                                                .logoutSuccessUrl("/public/signin?logout").permitAll()) // o bien
                                                                                                        // ‘/home’, etc.

                                // .csrf(csrf -> csrf.disable())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**") // solo desactivar CSRF para
                                                                                           // h2
                                )
                                .httpBasic(Customizer.withDefaults()); // import org.springframework.security.config
                // Para redireccionar a una página específica si se lanza un error
                http.exceptionHandling(exceptions -> exceptions.accessDeniedPage("/public/accessError"));
                return http.build();
        }

}
