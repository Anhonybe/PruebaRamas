package com.example.demo.Servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; // ✅ IMPORT NECESARIO
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.DTO.UsuarioDTO;
import com.example.demo.Modelos.Role;
import com.example.demo.Modelos.Usuario;
import com.example.demo.Repositorios.UserRepository;



@Service
public class UsuarioServicios implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServicios(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> user = userRepository.findByCorreo(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
    }

    public Usuario registerUser(UsuarioDTO registrationDTO) {
        if (!registrationDTO.isPasswordMatching()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        if (userRepository.existsByCorreo(registrationDTO.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        if (userRepository.existsByTelefono(registrationDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        Usuario user = new Usuario();
        user.setNombre(registrationDTO.getNombre());
        user.setApellidos(registrationDTO.getApellidos());
        user.setCorreo(registrationDTO.getCorreo());
        user.setTelefono(registrationDTO.getTelefono());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        
        // Asignar el rol del DTO o CLIENTE por defecto
        if (registrationDTO.getRole() != null && !registrationDTO.getRole().isEmpty()) {
            try {
                user.setRole(Role.valueOf(registrationDTO.getRole()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.CLIENTE); // Si el rol no es válido, usar CLIENTE
            }
        } else {
            user.setRole(Role.CLIENTE); // Por defecto es cliente
        }
        
        
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return userRepository.findByCorreo(correo);
    }

    public boolean existsByCorreo(String correo) {
        return userRepository.existsByCorreo(correo);
    }

    public boolean existsByTelefono(String telefono) {
        return userRepository.existsByTelefono(telefono);
    }

    public long countClientes() {
        return userRepository.countByRole(Role.CLIENTE);
    }
    
    public long countEmpleados() {
        return userRepository.countByRole(Role.EMPLEADO);
    }
    
    public long countAdmins() {
        return userRepository.countByRole(Role.ADMIN);
    }
    
    public long countTotalUsers() {
        return userRepository.count();
    }

     public List<Usuario> getAllUsersExcludingCurrent(String currentUserEmail) {
        return userRepository.findAllExcludingCurrentUser(currentUserEmail);
    }

    public Usuario registerVendedor(UsuarioDTO registrationDTO) {
        // Validar que las contraseñas coincidan
        if (!registrationDTO.isPasswordMatching()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        
        // Validar que el correo no esté en uso
        if (userRepository.existsByCorreo(registrationDTO.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        
        // Validar que el teléfono no esté en uso
        if (userRepository.existsByTelefono(registrationDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }
        
        // Crear nuevo vendedor/empleado
        Usuario user = new Usuario();
        user.setNombre(registrationDTO.getNombre());
        user.setApellidos(registrationDTO.getApellidos());
        user.setCorreo(registrationDTO.getCorreo());
        user.setTelefono(registrationDTO.getTelefono());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(Role.EMPLEADO); // Los vendedores son empleados
        user.setEnabled(true);
        
        return userRepository.save(user);
    }

    public Usuario obtenerPorId(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<Usuario> listarEmpleados() {
    return userRepository.findByRole(Role.EMPLEADO);
}

    public void eliminarUsuario(Long id) {
        userRepository.deleteById(id);
    }

}
