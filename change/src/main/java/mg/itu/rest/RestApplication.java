package mg.itu.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration JAX-RS
 * Tous les endpoints REST seront accessibles sous /api/*
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // Cette classe configure l'application REST
    // Les endpoints seront accessibles à : http://localhost:8080/change/api/*
}
