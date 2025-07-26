package com.empleados.backend_api.dto;

import com.empleados.backend_api.model.Empleado;

public class LoginResponse {
    
    private String token;
    private String tipo = "Bearer";
    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String cargo;
    private String departamento;
    
    // Constructor por defecto
    public LoginResponse() {}
    
    // Constructor con token y empleado
    public LoginResponse(String token, Empleado empleado) {
        this.token = token;
        this.id = empleado.getId();
        this.nombre = empleado.getNombre();
        this.email = empleado.getEmail();
        this.rol = empleado.getRol();
        this.cargo = empleado.getCargo();
        this.departamento = empleado.getDepartamento();
    }
    
    // Getters y Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public String getCargo() {
        return cargo;
    }
    
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}