package com.example.caso.controller;

import com.example.caso.domain.Rol;
import com.example.caso.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@RequestMapping("/admin/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    // Listar todos los roles
    @GetMapping
    public String listarRoles(Model model) {
        List<Rol> roles = rolService.listarTodos();
        model.addAttribute("roles", roles);
        model.addAttribute("nuevoRol", new Rol()); // Para el modal de creación
        return "admin/roles";
    }

    // Crear nuevo rol
    @PostMapping("/crear")
    public String crearRol(@RequestParam String nombre, 
                          @RequestParam String descripcion,
                          RedirectAttributes redirectAttributes) {
        try {
            rolService.crear(nombre.trim(), descripcion.trim());
            redirectAttributes.addFlashAttribute("success", "Rol creado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el rol: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }

    // Actualizar rol
    @PostMapping("/actualizar")
    public String actualizarRol(@RequestParam Long id,
                               @RequestParam String nombre,
                               @RequestParam String descripcion,
                               RedirectAttributes redirectAttributes) {
        try {
            rolService.actualizar(id, nombre.trim(), descripcion.trim());
            redirectAttributes.addFlashAttribute("success", "Rol actualizado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el rol: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }

    // Eliminar rol
    @PostMapping("/eliminar/{id}")
    public String eliminarRol(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rolService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Rol eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el rol: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }

    // Obtener rol por ID (para edición via AJAX)
    @GetMapping("/obtener/{id}")
    @ResponseBody
    public Rol obtenerRol(@PathVariable Long id) {
        Optional<Rol> rol = rolService.buscarPorId(id);
        return rol.orElse(null);
    }

    // Verificar si existe un nombre de rol
    @GetMapping("/verificar-nombre")
    @ResponseBody
    public boolean verificarNombre(@RequestParam String nombre, 
                                  @RequestParam(required = false) Long excluirId) {
        if (excluirId != null) {
            return !rolService.existeNombreExcluyendoRol(nombre, excluirId);
        }
        return !rolService.existeNombre(nombre);
    }
}