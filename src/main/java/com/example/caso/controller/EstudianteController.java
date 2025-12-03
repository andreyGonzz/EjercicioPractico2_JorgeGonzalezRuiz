package com.example.caso.controller;

import com.example.caso.domain.Usuario;
import com.example.caso.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/estudiante")
@PreAuthorize("hasRole('ESTUDIANTE')")
public class EstudianteController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "estudiante/dashboard";
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
        
        return "estudiante/perfil";
    }
    
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                  @RequestParam String apellido,
                                  RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
                usuario.setNombre(nombre);
                usuario.setApellido(apellido);
                usuarioService.guardar(usuario);
            });
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:/estudiante/perfil";
    }
    
    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                 @RequestParam String passwordNueva,
                                 @RequestParam String passwordConfirmar,
                                 RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            // Validar que las nuevas contraseñas coincidan
            if (!passwordNueva.equals(passwordConfirmar)) {
                redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden");
                return "redirect:/estudiante/perfil";
            }
            
            // Validar longitud mínima
            if (passwordNueva.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres");
                return "redirect:/estudiante/perfil";
            }
            
            usuarioService.buscarPorEmail(email).ifPresent(usuario -> {
                // Verificar contraseña actual
                if (passwordEncoder.matches(passwordActual, usuario.getPassword())) {
                    // Actualizar contraseña
                    usuario.setPassword(passwordEncoder.encode(passwordNueva));
                    usuarioService.guardar(usuario);
                    redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
                } else {
                    redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
                }
            });
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
        }
        
        return "redirect:/estudiante/perfil";
    }
}