package com.example.caso.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        // Redireccionar según el rol del usuario
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_ADMIN":
                    return "redirect:/admin/dashboard";
                case "ROLE_PROFESOR":
                    return "redirect:/profesor/dashboard";
                case "ROLE_ESTUDIANTE":
                    return "redirect:/estudiante/dashboard";
            }
        }
        // Por defecto redireccionar al login si no tiene rol reconocido
        return "redirect:/login";
    }
    
    @GetMapping("/usuarios")
    public String usuarios() {
        return "redirect:/admin/usuarios";
    }
}