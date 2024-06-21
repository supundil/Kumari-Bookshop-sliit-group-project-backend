package com.g1.kumaribookshopbackend.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(contact = @Contact(name = "Kumari book shop backend api", email = "", url = ""),
                description = "OpenApi Documentation for Kumari book shop backend API",
                title = "OpenApi Specification for Kumari book shop backend",
                version = "0.1"
        ),
        servers = {
                @Server(description = "Local ENV", url = "http://localhost:8080"),
        }
)
public class OpenApiConfig {
}
