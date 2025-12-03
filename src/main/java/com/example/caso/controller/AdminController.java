package com.example.caso.controller;

import com.example.caso.domain.Usuario;
import com.example.caso.domain.Rol;
import com.example.caso.service.UsuarioService;
import com.example.caso.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RolService rolService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String dashboard(Model model) {
        // Estadísticas para el dashboard
        long totalUsuarios = usuarioService.listarTodos().size();
        long totalRoles = rolService.listarTodos().size();
        long usuariosActivos = usuarioService.buscarActivos().size();
        
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalRoles", totalRoles);
        model.addAttribute("usuariosActivos", usuariosActivos);
        
        return "admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboardAlternative(Model model) {
        return dashboard(model);
    }
    
    // === GESTIÓN DE ROLES ===
    
    @GetMapping("/roles")
    public String listarRoles(Model model) {
        List<Rol> roles = rolService.listarTodos();
        model.addAttribute("roles", roles);
        return "admin/roles/lista";
    }
    
    @GetMapping("/roles/nuevo")
    public String mostrarFormularioNuevoRol(Model model) {
        model.addAttribute("rol", new Rol());
        return "admin/roles/formulario";
    }
    
    @PostMapping("/roles")
    public String crearRol(@RequestParam String nombre,
                          @RequestParam String descripcion,
                          RedirectAttributes redirectAttributes) {
        try {
            rolService.crear(nombre, descripcion);
            redirectAttributes.addFlashAttribute("success", "Rol creado exitosamente");
            return "redirect:/admin/roles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el rol: " + e.getMessage());
            return "redirect:/admin/roles/nuevo";
        }
    }
    
    @GetMapping("/roles/{id}")
    public String verRol(@PathVariable Long id, Model model) {
        Optional<Rol> rol = rolService.buscarPorId(id);
        if (rol.isPresent()) {
            model.addAttribute("rol", rol.get());
            List<Usuario> usuariosConRol = usuarioService.buscarPorRol(rol.get().getNombre());
            model.addAttribute("usuariosConRol", usuariosConRol);
            return "admin/roles/detalle";
        } else {
            return "redirect:/admin/roles";
        }
    }
    
    @GetMapping("/roles/{id}/editar")
    public String mostrarFormularioEditarRol(@PathVariable Long id, Model model) {
        Optional<Rol> rol = rolService.buscarPorId(id);
        if (rol.isPresent()) {
            model.addAttribute("rol", rol.get());
            return "admin/roles/editar";
        } else {
            return "redirect:/admin/roles";
        }
    }
    
    @PostMapping("/roles/{id}")
    public String actualizarRol(@PathVariable Long id,
                               @RequestParam String nombre,
                               @RequestParam String descripcion,
                               RedirectAttributes redirectAttributes) {
        try {
            rolService.actualizar(id, nombre, descripcion);
            redirectAttributes.addFlashAttribute("success", "Rol actualizado exitosamente");
            return "redirect:/admin/roles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el rol: " + e.getMessage());
            return "redirect:/admin/roles/" + id + "/editar";
        }
    }
    
    @PostMapping("/roles/{id}/eliminar")
    public String eliminarRol(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            rolService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Rol eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el rol: " + e.getMessage());
        }
        return "redirect:/admin/roles";
    }
    
    // === CONSULTAS AVANZADAS ===
    
    @GetMapping("/consultas")
    public String mostrarConsultas(Model model) {
        // Obtener todas las consultas disponibles del repository
        List<Usuario> todosUsuarios = usuarioService.listarTodos();
        List<Usuario> usuariosActivos = usuarioService.buscarActivos();
        
        // Consultas por rol
        List<Usuario> administradores = usuarioService.buscarPorRol("ADMIN");
        List<Usuario> profesores = usuarioService.buscarPorRol("PROFESOR");
        List<Usuario> estudiantes = usuarioService.buscarPorRol("ESTUDIANTE");
        
        // Estadísticas
        model.addAttribute("totalUsuarios", todosUsuarios.size());
        model.addAttribute("usuariosActivos", usuariosActivos.size());
        model.addAttribute("totalAdministradores", administradores.size());
        model.addAttribute("totalProfesores", profesores.size());
        model.addAttribute("totalEstudiantes", estudiantes.size());
        
        // Listas para mostrar
        model.addAttribute("todosUsuarios", todosUsuarios);
        model.addAttribute("usuariosActivos", usuariosActivos);
        model.addAttribute("administradores", administradores);
        model.addAttribute("profesores", profesores);
        model.addAttribute("estudiantes", estudiantes);
        
        return "admin/consultas";
    }
}
