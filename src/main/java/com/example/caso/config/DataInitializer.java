package com.example.caso.config;

import com.example.caso.domain.Rol;
import com.example.caso.domain.Usuario;
import com.example.caso.repository.RolRepository;
import com.example.caso.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("=== INICIANDO CONFIGURACIÓN DE DATOS ===");
        
        // Crear roles si no existen
        Rol adminRole = rolRepository.findByNombre("ADMIN").orElse(null);
        Rol profesorRole = rolRepository.findByNombre("PROFESOR").orElse(null);
        Rol estudianteRole = rolRepository.findByNombre("ESTUDIANTE").orElse(null);

        if (adminRole == null) {
            adminRole = new Rol("ADMIN", "Administrador del sistema");
            adminRole = rolRepository.save(adminRole);
            logger.info("Rol ADMIN creado con ID: {}", adminRole.getId());
        }

        if (profesorRole == null) {
            profesorRole = new Rol("PROFESOR", "Profesor del sistema");
            profesorRole = rolRepository.save(profesorRole);
            logger.info("Rol PROFESOR creado con ID: {}", profesorRole.getId());
        }

        if (estudianteRole == null) {
            estudianteRole = new Rol("ESTUDIANTE", "Estudiante del sistema");
            estudianteRole = rolRepository.save(estudianteRole);
            logger.info("Rol ESTUDIANTE creado con ID: {}", estudianteRole.getId());
        }

        // Crear usuarios de prueba si no existen
        if (!usuarioRepository.existsByEmail("admin@test.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActivo(true);
            admin.setRol(adminRole);
            admin = usuarioRepository.save(admin);
            logger.info("Usuario ADMIN creado: {} con rol {}", admin.getEmail(), admin.getRol().getNombre());
        }

        if (!usuarioRepository.existsByEmail("profesor@test.com")) {
            Usuario profesor = new Usuario();
            profesor.setNombre("Juan Carlos");
            profesor.setApellido("García");
            profesor.setEmail("profesor@test.com");
            profesor.setPassword(passwordEncoder.encode("prof123"));
            profesor.setActivo(true);
            profesor.setRol(profesorRole);
            profesor = usuarioRepository.save(profesor);
            logger.info("Usuario PROFESOR creado: {} con rol {}", profesor.getEmail(), profesor.getRol().getNombre());
        }

        if (!usuarioRepository.existsByEmail("estudiante@test.com")) {
            Usuario estudiante = new Usuario();
            estudiante.setNombre("María");
            estudiante.setApellido("López");
            estudiante.setEmail("estudiante@test.com");
            estudiante.setPassword(passwordEncoder.encode("est123"));
            estudiante.setActivo(true);
            estudiante.setRol(estudianteRole);
            estudiante = usuarioRepository.save(estudiante);
            logger.info("Usuario ESTUDIANTE creado: {} con rol {}", estudiante.getEmail(), estudiante.getRol().getNombre());
        }

        logger.info("=== CONFIGURACIÓN DE DATOS COMPLETADA ===");
        logger.info("Usuarios disponibles para login:");
        logger.info("  • admin@test.com / admin123 (ADMIN)");
        logger.info("  • profesor@test.com / prof123 (PROFESOR)");
        logger.info("  • estudiante@test.com / est123 (ESTUDIANTE)");
    }
}