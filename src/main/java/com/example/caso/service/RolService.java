package com.example.caso.service;

import com.example.caso.domain.Rol;
import com.example.caso.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(Long id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public Rol guardar(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol crear(String nombre, String descripcion) {
        if (rolRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre");
        }
        Rol rol = new Rol(nombre, descripcion);
        return rolRepository.save(rol);
    }

    public Rol actualizar(Long id, String nombre, String descripcion) {
        Optional<Rol> rolOpt = rolRepository.findById(id);
        
        if (rolOpt.isEmpty()) {
            throw new IllegalArgumentException("Rol no encontrado");
        }
        
        // Verificar si el nombre ya existe (excluyendo el rol actual)
        Optional<Rol> rolExistente = rolRepository.findByNombre(nombre);
        if (rolExistente.isPresent() && !rolExistente.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otro rol con ese nombre");
        }
        
        Rol rol = rolOpt.get();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        return rolRepository.save(rol);
    }

    public void eliminar(Long id) {
        Optional<Rol> rolOpt = rolRepository.findById(id);
        if (rolOpt.isEmpty()) {
            throw new IllegalArgumentException("Rol no encontrado");
        }
        
        Rol rol = rolOpt.get();
        if (rol.getUsuarios() != null && !rol.getUsuarios().isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el rol porque tiene usuarios asignados");
        }
        
        rolRepository.deleteById(id);
    }

    public boolean existeNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    public boolean existeNombreExcluyendoRol(String nombre, Long rolId) {
        Optional<Rol> rol = rolRepository.findByNombre(nombre);
        return rol.isPresent() && !rol.get().getId().equals(rolId);
    }
}