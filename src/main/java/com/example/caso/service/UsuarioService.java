package com.example.caso.service;

import com.example.caso.domain.Usuario;
import com.example.caso.domain.Rol;
import com.example.caso.repository.UsuarioRepository;
import com.example.caso.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> buscarPorRol(String nombreRol) {
        return usuarioRepository.findByRolNombre(nombreRol);
    }

    public List<Usuario> buscarActivos() {
        return usuarioRepository.findByActivo(true);
    }
    
    public List<Usuario> buscarPorActivo(Boolean activo) {
        return usuarioRepository.findByActivo(activo);
    }
    
    public List<Usuario> buscarPorNombreOApellido(String termino) {
        return usuarioRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino);
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario crear(String nombre, String apellido, String email, String password, Long rolId) {
        Optional<Rol> rol = rolRepository.findById(rolId);
        if (rol.isPresent()) {
            // Encriptar la contraseña antes de guardar
            String passwordEncriptada = passwordEncoder.encode(password);
            Usuario usuario = new Usuario(nombre, apellido, email, passwordEncriptada, rol.get());
            return usuarioRepository.save(usuario);
        }
        throw new IllegalArgumentException("Rol no encontrado");
    }

    public Usuario actualizar(Long id, String nombre, String apellido, String email, String password, Long rolId, Boolean activo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        Optional<Rol> rolOpt = rolRepository.findById(rolId);
        
        if (usuarioOpt.isPresent() && rolOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            if (password != null && !password.trim().isEmpty()) {
                // Encriptar la nueva contraseña antes de guardar
                usuario.setPassword(passwordEncoder.encode(password));
            }
            usuario.setRol(rolOpt.get());
            usuario.setActivo(activo);
            return usuarioRepository.save(usuario);
        }
        throw new IllegalArgumentException("Usuario o Rol no encontrado");
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existeEmailExcluyendoUsuario(String email, Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.isPresent() && !usuario.get().getId().equals(usuarioId);
    }
}