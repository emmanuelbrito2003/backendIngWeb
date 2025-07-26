package com.empleados.backend_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class EmpleadoUpdateDTO {
    
    private String nombre;
    
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    private String cargo;
    
    private String departamento;
    
    @DecimalMin(value = "0.0", message = "El salario debe ser mayor a 0")
    private Double salario;
    
    private LocalDate fechaIngreso;
    
    private String telefono;
    
    private String rol; // "ADMIN" o "EMPLEADO"
    
    // Constructor por defecto
    public EmpleadoUpdateDTO() {}
    
    // Getters y Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public Double getSalario() {
        return salario;
    }
    
    public void setSalario(Double salario) {
        this.salario = salario;
    }
    
    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }
    
    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
}