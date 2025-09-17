package com.example.demo.Repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.Modelos.Role;
import com.example.demo.Modelos.Usuario;



@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByCorreo(String correo);
    
    boolean existsByCorreo(String correo);
    
    boolean existsByTelefono(String telefono);

    // Contar usuarios por rol
    long countByRole(Role role);
    
    // Método alternativo con Query personalizada
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.role = ?1")
    long countUsersByRole(Role role);

    // Obtener todos los usuarios excepto el usuario actual
    @Query("SELECT u FROM Usuario u WHERE u.correo != ?1 ORDER BY u.id ASC")
    List<Usuario> findAllExcludingCurrentUser(String correo);

    List<Usuario> findByRole(Role role);

}
