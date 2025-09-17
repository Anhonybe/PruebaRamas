package com.example.demo.Modelos;

public enum Role {
    CLIENTE("Cliente"),
    ADMIN("Administrador"),
    EMPLEADO("Vendedor");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}