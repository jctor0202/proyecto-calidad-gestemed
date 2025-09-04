package com.calidad.gestemed.config;

import com.calidad.gestemed.domain.RolePolicy;
import com.calidad.gestemed.service.impl.AuthzService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

import java.util.function.Predicate;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Servicio que lee los flags de la tabla role_policies
    private final AuthzService authz;

    // Usuarios en memoria
    // Cuando el programa está corriendo la información de los usuarios y roles se guardan directamente en la memoria de la aplicación
    // esto es un error de seguridad ya que los usuarios deberían estar en la base de datos protegidos
    // en futuras versiones esto se corrigirá paa mejorar la calidad y seguridad del código

    // el {noop} significa que la contra no esta encriptada, lo que representa un error en la seguridad de la app

    // el admin tiene todos los roles, es decir, puede ejecutar todas las funcionalidades.

    //Por ejemplo, cuando en el login se pone user=admin y password=admin entonces automáticamente se cargan los roles ADMIN, LEASING, WAREHOUSE, TECH, AUDIT Y MANAGER.
    @Bean
    public InMemoryUserDetailsManager users() {
        UserDetails admin     = User.withUsername("admin").password("{noop}admin").roles("ADMIN","LEASING","WAREHOUSE","TECH","AUDIT","MANAGER").build();
        UserDetails leasing   = User.withUsername("leasing").password("{noop}123").roles("LEASING").build();
        UserDetails tech      = User.withUsername("tech").password("{noop}123").roles("TECH").build();
        UserDetails wh        = User.withUsername("warehouse").password("{noop}123").roles("WAREHOUSE").build();
        UserDetails audit     = User.withUsername("audit").password("{noop}123").roles("AUDIT").build();
        UserDetails manager   = User.withUsername("manager").password("{noop}123").roles("MANAGER").build();
        return new InMemoryUserDetailsManager(admin, leasing, tech, wh, audit, manager);
    }

    //Autorización dinámica por flags

    //HttpSecurity: Es el objeto principal de Spring Security que se usa para configurar la protección de las solicitudes HTTP.

    // authorizeHttpRequests(...): Esta sección define las reglas de acceso a las diferentes URLs de la aplicación.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        //todos los roles pueden acceder a esto
                        .requestMatchers("/login","/h2-console/**","/","/files/**","/css/**","/js/**").permitAll()


                        //A estas URLs solo pueden acceder usuarios con el rol ADMIN o SUPPORT
                        .requestMatchers("/tracking", "/api/gps/**").hasAnyRole("ADMIN","SUPPORT")

                        // admin UI para editar roles/políticas
                        //La sección de administración solo es accesible para usuarios con el rol ADMIN.
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ---- ASSETS ----
                        .requestMatchers(HttpMethod.GET,    "/assets/**")
                        .access((authSupplier, ctx) -> decision(authSupplier.get(), RolePolicy::isCanAssetsRead))
                        .requestMatchers(HttpMethod.POST,   "/assets/**")
                        .access((authSupplier, ctx) -> decision(authSupplier.get(), RolePolicy::isCanAssetsWrite))
                        .requestMatchers(HttpMethod.PUT,    "/assets/**")
                        .access((authSupplier, ctx) -> decision(authSupplier.get(), RolePolicy::isCanAssetsWrite))
                        .requestMatchers(HttpMethod.DELETE, "/assets/**")
                        .access((authSupplier, ctx) -> decision(authSupplier.get(), RolePolicy::isCanAssetsWrite))

                        // ---- CONTRACTS ----
                        .requestMatchers(HttpMethod.GET,    "/contracts/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanContractsRead))
                        .requestMatchers(HttpMethod.POST,   "/contracts/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanContractsWrite))
                        .requestMatchers(HttpMethod.PUT,    "/contracts/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanContractsWrite))
                        .requestMatchers(HttpMethod.DELETE, "/contracts/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanContractsWrite))

                        // ---- INVENTORY ----
                        .requestMatchers(HttpMethod.GET,    "/inventory/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanInventoryRead))
                        .requestMatchers(HttpMethod.POST,   "/inventory/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanInventoryWrite))
                        .requestMatchers(HttpMethod.PUT,    "/inventory/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanInventoryWrite))
                        .requestMatchers(HttpMethod.DELETE, "/inventory/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanInventoryWrite))

                        // ---- MAINTENANCE ----
                        .requestMatchers(HttpMethod.GET,    "/maintenance/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanMaintenanceRead))
                        .requestMatchers(HttpMethod.POST,   "/maintenance/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanMaintenanceWrite))
                        .requestMatchers(HttpMethod.PUT,    "/maintenance/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanMaintenanceWrite))
                        .requestMatchers(HttpMethod.DELETE, "/maintenance/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanMaintenanceWrite))

                        // ---- REPORTS (solo lectura) ----
                        .requestMatchers("/reports/**")
                        .access((a, c) -> decision(a.get(), RolePolicy::isCanReportsRead))

                        .anyRequest().authenticated()
                )
                // esto es para abrir el formulario de inicio de sesion
                .formLogin(Customizer.withDefaults())

                //esto es para la base de datos H2. Para desabilitar la seguridad y entrar a la consola de H2.
                //no es necesario si se está usando postgres
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }


    // este metodo AuthorizationDecision es el corazon de la logica de autorizacion.
    // este método determina si el usuario que está autenticado tiene permiso para realizar la acción
    // toma el rol del usuario autenticado y busca en la base de datos si ese rol tiene permiso para hacer determinada acción
    // Admin siempre pasa; para otros, miramos su primer rol y validamos contra role_policies
    private AuthorizationDecision decision(Authentication auth, Predicate<RolePolicy> checker) {

        // si el objeto auth es nulo significa que no hay usuario autenticado y devuelve el AuthorizationDecision con el valor false
        if (auth == null) return new AuthorizationDecision(false);

        // En caso de que el usuario autenticado tenga el role de admin entonces se permite hacer todo de una vez
        // auth.getAuthorities().stream lo que hace es traer todos los roles que tiene el usuario autenticado.
        //se recorren esos roles y si alguno coincide con el role de admin entonces el método nmediatamente devuelve un AuthorizationDecision con el valor true.
        // Esto significa que un administrador siempre tiene acceso completo, sin importar las demás políticas.
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(ga -> "ROLE_ADMIN".equals(ga.getAuthority()));
        if (isAdmin) return new AuthorizationDecision(true);

        // si el usuario autenticado no tiene el rol de admin
        String role = auth.getAuthorities().stream()

                // spring le coloca a cada rol el prefijo "ROLE_" por ejemplo, si el rol es ADMIN entonces se le pone ROLE_ADMIN
                // con la función .map se le está quitando a todos los roles ese prefijo.
                // esto es necesario hacerlo porque los roles definidos son estos "ADMIN","LEASING","WAREHOUSE","TECH","AUDIT","MANAGER" y es necesario comparar
                .map(ga -> ga.getAuthority().replace("ROLE_", ""))

                // en dado caso que un role registrado sea ADMIN entonces se quita, porque en isAdmin ya debería haber pasado ese filtro
                .filter(r -> !"ADMIN".equals(r))

                // se toma el primer role que se encuentra
                .findFirst().orElse(null);


        // el role se envía a la función has para determinar si tiene permiso o no para realizar la acción.
        boolean allowed = (role != null) && authz.has(role, checker);
        return new AuthorizationDecision(allowed);



    }
}
