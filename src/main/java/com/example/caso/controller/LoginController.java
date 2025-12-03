package com.example.caso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos.");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión exitosamente.");
        }
        
        return "login";
    }

    @GetMapping("/acceso_denegado")
    public String accessDenied() {
        return "error/acceso_denegado";
    }
}