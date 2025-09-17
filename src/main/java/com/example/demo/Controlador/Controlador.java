package com.example.demo.Controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.DTO.UsuarioDTO;
import com.example.demo.Modelos.Usuario;
import com.example.demo.Servicios.UsuarioServicios;

import jakarta.validation.Valid;

@Controller
public class Controlador {
    
    @Autowired
    private UsuarioServicios userService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente");
        }
        return "login";
    }
    
    @GetMapping("/registro")
    public String showRegistrationForm(@RequestParam(value = "rol", required = false) String rol, Model model) {
        UsuarioDTO userDto = new UsuarioDTO();
        if (rol != null && !rol.isEmpty()) {
            userDto.setRole(rol);
        } 
        model.addAttribute("user", userDto);
        return "registro";
    }
    
    @PostMapping("/registro")
    public String processRegistration(@Valid @ModelAttribute("user") UsuarioDTO userDto,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "registro";
        }
        
        // Validar que las contraseñas coincidan
        if (!userDto.isPasswordMatching()) {
            result.rejectValue("confirmPassword", "error.user", "Las contraseñas no coinciden");
            return "registro";
        }
        
        try {
            // Validar si el correo ya existe
            if (userService.existsByCorreo(userDto.getCorreo())) {
                result.rejectValue("correo", "error.user", "El correo ya está registrado");
                return "registro";
            }
            
            // Validar si el teléfono ya existe
            if (userService.existsByTelefono(userDto.getTelefono())) {
                result.rejectValue("telefono", "error.user", "El teléfono ya está registrado");
                return "registro";
            }
            
            Usuario user = userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("success", 
                "Registro exitoso. Ahora puedes iniciar sesión.");
            return "redirect:/login";
            
        } catch (Exception e) {
            result.rejectValue("correo", "error.user", e.getMessage());
            return "registro";
        }
    }
}