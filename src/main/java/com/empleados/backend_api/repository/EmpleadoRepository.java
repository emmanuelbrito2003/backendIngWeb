package com.empleados.backend_api.repository;

import com.empleados.backend_api.model.Empleado;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends MongoRepository<Empleado, String> {

    // Buscar empleado por email (para login y validaciones)
    Optional<Empleado> findByEmail(String email);

    // Verificar si existe un empleado con un email específico
    boolean existsByEmail(String email);

    // Buscar empleados por rol
    List<Empleado> findByRol(String rol);

    // Buscar empleados por departamento
    List<Empleado> findByDepartamento(String departamento);

    // Buscar empleados por cargo
    List<Empleado> findByCargo(String cargo);

    // Buscar empleados por nombre (insensible a mayúsculas/minúsculas)
    @Query("{'nombre': {$regex: ?0, $options: 'i'}}")
    List<Empleado> findByNombreContainingIgnoreCase(String nombre);

    // Buscar empleados por salario mayor a un valor específico
    List<Empleado> findBySalarioGreaterThan(Double salario);

    // Buscar empleados por salario entre dos valores
    List<Empleado> findBySalarioBetween(Double salarioMin, Double salarioMax);

    // Contar empleados por departamento
    long countByDepartamento(String departamento);

    // Contar empleados por rol
    long countByRol(String rol);
}