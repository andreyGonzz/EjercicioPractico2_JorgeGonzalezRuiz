package com.example.caso.controller;

import com.example.caso.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/profesor")
@PreAuthorize("hasRole('PROFESOR')")
public class ProfesorController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "profesor/dashboard";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        // Datos simulados para reportes académicos
        Map<String, Object> reporteData = new HashMap<>();
        
        // Estadísticas generales
        reporteData.put("totalEstudiantes", 45);
        reporteData.put("estudiantesActivos", 42);
        reporteData.put("promedioGeneral", 8.5);
        
        // Datos de rendimiento por materia
        List<Map<String, Object>> materias = Arrays.asList(
            Map.of("nombre", "Matemáticas", "estudiantes", 15, "promedio", 8.2),
            Map.of("nombre", "Física", "estudiantes", 12, "promedio", 7.8),
            Map.of("nombre", "Química", "estudiantes", 18, "promedio", 8.7)
        );
        
        // Datos de asistencia por mes
        List<Map<String, Object>> asistenciaMensual = Arrays.asList(
            Map.of("mes", "Enero", "asistencia", 92),
            Map.of("mes", "Febrero", "asistencia", 88),
            Map.of("mes", "Marzo", "asistencia", 95),
            Map.of("mes", "Abril", "asistencia", 90)
        );
        
        model.addAttribute("reporteData", reporteData);
        model.addAttribute("materias", materias);
        model.addAttribute("asistenciaMensual", asistenciaMensual);
        model.addAttribute("fechaReporte", LocalDate.now());
        
        return "profesor/reportes";
    }
    
    @GetMapping("/perfil")
    public String perfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
            model.addAttribute("usuario", usuario);
            model.addAttribute("rol", usuario.getRol().getNombre());
            model.addAttribute("fechaRegistro", usuario.getFechaCreacion());
        });
        
        return "profesor/perfil";
    }
}