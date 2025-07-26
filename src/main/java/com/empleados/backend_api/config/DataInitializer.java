package com.empleados.backend_api.config;

import com.empleados.backend_api.model.Empleado;
import com.empleados.backend_api.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear admin por defecto si no existe
        if (!empleadoRepository.existsByEmail("admin@empresa.com")) {
            Empleado admin = new Empleado();
            admin.setNombre("Administrador");
            admin.setEmail("admin@empresa.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setCargo("Administrador General");
            admin.setDepartamento("Administración");
            admin.setSalario(5000.0);
            admin.setFechaIngreso(LocalDate.of(2020, 1, 1));
            admin.setTelefono("0987654321");
            admin.setRol("ADMIN");

            empleadoRepository.save(admin);
            System.out.println("✅ Admin por defecto creado: admin@empresa.com / admin123");
        }

        // Crear empleado de prueba si no existe
        if (!empleadoRepository.existsByEmail("empleado@empresa.com")) {
            Empleado empleado = new Empleado();
            empleado.setNombre("Juan Pérez");
            empleado.setEmail("empleado@empresa.com");
            empleado.setPassword(passwordEncoder.encode("empleado123"));
            empleado.setCargo("Desarrollador");
            empleado.setDepartamento("Tecnología");
            empleado.setSalario(3000.0);
            empleado.setFechaIngreso(LocalDate.of(2023, 6, 15));
            empleado.setTelefono("0987654322");
            empleado.setRol("EMPLEADO");

            empleadoRepository.save(empleado);
            System.out.println("✅ Empleado de prueba creado: empleado@empresa.com / empleado123");
        }

        System.out.println("🚀 Sistema iniciado correctamente!");
        System.out.println("📊 Total de empleados en la base de datos: " + empleadoRepository.count());
    }
}