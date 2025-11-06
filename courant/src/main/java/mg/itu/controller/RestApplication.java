package mg.itu.controller;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration JAX-RS: tous les endpoints sous /api/*
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
