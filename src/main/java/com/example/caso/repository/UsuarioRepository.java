package com.example.caso.repository;

import com.example.caso.domain.Usuario;
import com.example.caso.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);
    
    List<Usuario> findByActivo(Boolean activo);
    
    List<Usuario> findByRol(Rol rol);
    
    List<Usuario> findByRolNombre(String nombreRol);
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
}