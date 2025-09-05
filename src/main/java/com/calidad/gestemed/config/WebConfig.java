package com.calidad.gestemed.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//esta clase es para decirle a Spring dónde encontrar archivos estáticos (como imágenes, CSS y JavaScript) en el sistema de archivos local del servidor.
//como se está usando el servicio de azure blob para subir imagenes entonces esta clase se hace innecesaria
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // No es necesario añadir un ResourceHandler para archivos locales,
        // ya que ahora se gestionan con Azure Blob Storage.
    }
}
