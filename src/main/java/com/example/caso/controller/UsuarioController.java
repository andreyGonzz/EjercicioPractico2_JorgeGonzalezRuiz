package com.example.caso.controller;

import com.example.caso.domain.Usuario;
import com.example.caso.domain.Rol;
import com.example.caso.service.UsuarioService;
import com.example.caso.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RolService rolService;

    // Listar usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios/lista";
    }

    // Mostrar formulario de creación
    @GetMapping("/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        Usuario usuario = new Usuario();
        List<Rol> roles = rolService.listarTodos();
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", roles);
        return "admin/usuarios/formulario";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
        
        Usuario usuario = usuarioOpt.get();
        List<Rol> roles = rolService.listarTodos();
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", roles);
        return "admin/usuarios/editar";
    }

    // Mostrar detalle de usuario
    @GetMapping("/detalle/{id}")
    public String mostrarDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
        
        model.addAttribute("usuario", usuarioOpt.get());
        return "admin/usuarios/detalle";
    }

    // Guardar usuario (crear o actualizar)
    @PostMapping("/guardar")
    public String guardarUsuario(@RequestParam String nombre,
                                @RequestParam String apellido,
                                @RequestParam String email,
                                @RequestParam String password,
                                @RequestParam Long rolId,
                                @RequestParam(required = false) Boolean activo,
                                @RequestParam(required = false) Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            // Si activo es null (checkbox no marcado), establecer como false
            if (activo == null) {
                activo = false;
            }
            
            if (id == null) {
                // Crear nuevo usuario
                if (usuarioService.existeEmail(email)) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe un usuario con ese email");
                    return "redirect:/admin/usuarios/nuevo";
                }
                
                usuarioService.crear(nombre, apellido, email, password, rolId);
                redirectAttributes.addFlashAttribute("success", "Usuario creado exitosamente");
                
            } else {
                // Actualizar usuario existente
                if (usuarioService.existeEmailExcluyendoUsuario(email, id)) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe otro usuario con ese email");
                    return "redirect:/admin/usuarios/editar/" + id;
                }
                
                usuarioService.actualizar(id, nombre, apellido, email, password, rolId, activo);
                redirectAttributes.addFlashAttribute("success", "Usuario actualizado exitosamente");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
            if (id == null) {
                return "redirect:/admin/usuarios/nuevo";
            } else {
                return "redirect:/admin/usuarios/editar/" + id;
            }
        }
        
        return "redirect:/admin/usuarios";
    }

    // Método alternativo para crear usuario (por si hay problemas con /guardar)
    @PostMapping
    public String crearUsuario(@RequestParam String nombre,
                              @RequestParam String apellido,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam Long rolId,
                              @RequestParam(required = false) Boolean activo,
                              RedirectAttributes redirectAttributes) {
        try {
            // Si activo es null (checkbox no marcado), establecer como true por defecto para nuevos usuarios
            if (activo == null) {
                activo = true;
            }
            
            // Crear nuevo usuario
            if (usuarioService.existeEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un usuario con ese email");
                return "redirect:/admin/usuarios/nuevo";
            }
            
            usuarioService.crear(nombre, apellido, email, password, rolId);
            redirectAttributes.addFlashAttribute("success", "Usuario creado exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
        
        return "redirect:/admin/usuarios";
    }

    // Eliminar usuario
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
            
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/usuarios";
            }
            
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }
}