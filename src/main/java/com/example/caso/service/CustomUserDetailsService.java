package com.example.caso.service;

import com.example.caso.domain.Usuario;
import com.example.caso.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Intentando autenticar usuario con email: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("Usuario no encontrado con email: {}", email);
                return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
            });
        
        if (!usuario.getActivo()) {
            logger.warn("Usuario desactivado intentó acceder: {}", email);
            throw new UsernameNotFoundException("Usuario desactivado: " + email);
        }

        // Forzar la carga del rol dentro de la transacción
        String roleName = usuario.getRol().getNombre();
        logger.info("Usuario encontrado: {} con rol: {}", email, roleName);

        Collection<? extends GrantedAuthority> authorities = mapRolesToAuthorities(roleName);
        logger.info("Autoridades asignadas a {}: {}", email, authorities);

        return new User(
            usuario.getEmail(),
            usuario.getPassword(),
            authorities
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(String roleName) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Agregar el rol del usuario con el prefijo ROLE_
        String roleWithPrefix = "ROLE_" + roleName;
        authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
        
        logger.debug("Rol mapeado: {} -> {}", roleName, roleWithPrefix);
        
        return authorities;
    }
}