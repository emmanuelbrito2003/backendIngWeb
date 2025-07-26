package com.empleados.backend_api.service;

import com.empleados.backend_api.model.Empleado;
import com.empleados.backend_api.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener todos los empleados (solo ADMIN)
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoRepository.findAll();
    }

    // Obtener empleado por ID
    public Optional<Empleado> obtenerEmpleadoPorId(String id) {
        return empleadoRepository.findById(id);
    }

    // Obtener empleado por email
    public Optional<Empleado> obtenerEmpleadoPorEmail(String email) {
        return empleadoRepository.findByEmail(email);
    }

    // Crear nuevo empleado (solo ADMIN)
    public Empleado crearEmpleado(Empleado empleado) {
        // Verificar si el email ya existe
        if (empleadoRepository.existsByEmail(empleado.getEmail())) {
            throw new RuntimeException("Ya existe un empleado con este email");
        }

        // Encriptar contraseña
        empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));

        // Establecer fechas
        empleado.setFechaCreacion(LocalDateTime.now());
        empleado.setFechaActualizacion(LocalDateTime.now());

        return empleadoRepository.save(empleado);
    }

    // Actualizar empleado
    public Empleado actualizarEmpleado(String id, Empleado empleadoActualizado) {
        Optional<Empleado> empleadoExistente = empleadoRepository.findById(id);
        
        if (empleadoExistente.isEmpty()) {
            throw new RuntimeException("Empleado no encontrado");
        }
        
        Empleado empleado = empleadoExistente.get();
        
        // Verificar si el email ya existe en otro empleado (solo si se está cambiando)
        if (empleadoActualizado.getEmail() != null && 
            !empleado.getEmail().equals(empleadoActualizado.getEmail()) && 
            empleadoRepository.existsByEmail(empleadoActualizado.getEmail())) {
            throw new RuntimeException("Ya existe un empleado con este email");
        }
        
        // Actualizar campos solo si se proporcionan (no son null)
        if (empleadoActualizado.getNombre() != null) {
            empleado.setNombre(empleadoActualizado.getNombre());
        }
        if (empleadoActualizado.getEmail() != null) {
            empleado.setEmail(empleadoActualizado.getEmail());
        }
        if (empleadoActualizado.getCargo() != null) {
            empleado.setCargo(empleadoActualizado.getCargo());
        }
        if (empleadoActualizado.getDepartamento() != null) {
            empleado.setDepartamento(empleadoActualizado.getDepartamento());
        }
        if (empleadoActualizado.getSalario() != null) {
            empleado.setSalario(empleadoActualizado.getSalario());
        }
        if (empleadoActualizado.getFechaIngreso() != null) {
            empleado.setFechaIngreso(empleadoActualizado.getFechaIngreso());
        }
        if (empleadoActualizado.getTelefono() != null) {
            empleado.setTelefono(empleadoActualizado.getTelefono());
        }
        if (empleadoActualizado.getRol() != null) {
            empleado.setRol(empleadoActualizado.getRol());
        }
        
        // Si se proporciona una nueva contraseña, encriptarla
        if (empleadoActualizado.getPassword() != null && 
            !empleadoActualizado.getPassword().trim().isEmpty()) {
            empleado.setPassword(passwordEncoder.encode(empleadoActualizado.getPassword()));
        }
        
        empleado.setFechaActualizacion(LocalDateTime.now());
        
        return empleadoRepository.save(empleado);
    }

    // Actualizar perfil propio (EMPLEADO)
    public Empleado actualizarPerfilPropio(String id, Empleado datosActualizados) {
        Optional<Empleado> empleadoExistente = empleadoRepository.findById(id);
        
        if (empleadoExistente.isEmpty()) {
            throw new RuntimeException("Empleado no encontrado");
        }
        
        Empleado empleado = empleadoExistente.get();
        
        // Empleado solo puede actualizar ciertos campos
        if (datosActualizados.getNombre() != null) {
            empleado.setNombre(datosActualizados.getNombre());
        }
        if (datosActualizados.getTelefono() != null) {
            empleado.setTelefono(datosActualizados.getTelefono());
        }
        
        // Si se proporciona una nueva contraseña, encriptarla
        if (datosActualizados.getPassword() != null && 
            !datosActualizados.getPassword().trim().isEmpty()) {
            empleado.setPassword(passwordEncoder.encode(datosActualizados.getPassword()));
        }
        
        empleado.setFechaActualizacion(LocalDateTime.now());
        
        return empleadoRepository.save(empleado);
    }

    // Eliminar empleado (solo ADMIN)
    public void eliminarEmpleado(String id) {
        if (!empleadoRepository.existsById(id)) {
            throw new RuntimeException("Empleado no encontrado");
        }
        empleadoRepository.deleteById(id);
    }

    // Verificar contraseña
    public boolean verificarPassword(String passwordSinEncriptar, String passwordEncriptada) {
        return passwordEncoder.matches(passwordSinEncriptar, passwordEncriptada);
    }

    // Buscar empleados por departamento
    public List<Empleado> buscarPorDepartamento(String departamento) {
        return empleadoRepository.findByDepartamento(departamento);
    }

    // Buscar empleados por cargo
    public List<Empleado> buscarPorCargo(String cargo) {
        return empleadoRepository.findByCargo(cargo);
    }

    // Buscar empleados por nombre
    public List<Empleado> buscarPorNombre(String nombre) {
        return empleadoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Obtener estadísticas básicas
    public long contarEmpleados() {
        return empleadoRepository.count();
    }

    public long contarEmpleadosPorDepartamento(String departamento) {
        return empleadoRepository.countByDepartamento(departamento);
    }

    public long contarEmpleadosPorRol(String rol) {
        return empleadoRepository.countByRol(rol);
    }
}