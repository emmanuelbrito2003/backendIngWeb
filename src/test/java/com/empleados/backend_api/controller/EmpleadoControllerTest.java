package com.empleados.backend_api.controller;

import com.empleados.backend_api.model.Empleado;
import com.empleados.backend_api.service.EmpleadoService;
import com.empleados.backend_api.config.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@SpringJUnitConfig
public class EmpleadoControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @org.junit.jupiter.api.BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    // PRUEBA 1: Login exitoso
    @Test
    public void testLoginExitoso() throws Exception {
        // Arrange
        Empleado empleado = new Empleado();
        empleado.setId("123");
        empleado.setEmail("admin@empresa.com");
        empleado.setRol("ADMIN");
        empleado.setNombre("Administrador");
        empleado.setCargo("Admin");
        empleado.setDepartamento("Administración");
        empleado.setSalario(3000.0);
        empleado.setFechaIngreso(LocalDate.now());

        when(empleadoService.obtenerEmpleadoPorEmail("admin@empresa.com"))
            .thenReturn(Optional.of(empleado));
        when(empleadoService.verificarPassword("admin123", empleado.getPassword()))
            .thenReturn(true);
        when(jwtUtil.generateToken("admin@empresa.com", "ADMIN", "123"))
            .thenReturn("fake-jwt-token");

        String loginJson = """
            {
                "email": "admin@empresa.com",
                "password": "admin123"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.data.rol").value("ADMIN"))
                .andExpect(jsonPath("$.data.email").value("admin@empresa.com"));
    }

    // PRUEBA 2: Crear empleado válido
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCrearEmpleadoValido() throws Exception {
        // Arrange
        Empleado nuevoEmpleado = new Empleado();
        nuevoEmpleado.setId("456");
        nuevoEmpleado.setNombre("Test User");
        nuevoEmpleado.setEmail("test@empresa.com");
        nuevoEmpleado.setCargo("Tester");
        nuevoEmpleado.setDepartamento("QA");
        nuevoEmpleado.setSalario(2000.0);
        nuevoEmpleado.setFechaIngreso(LocalDate.now());
        nuevoEmpleado.setRol("EMPLEADO");

        when(empleadoService.crearEmpleado(any(Empleado.class)))
            .thenReturn(nuevoEmpleado);

        String empleadoJson = """
            {
                "nombre": "Test User",
                "email": "test@empresa.com",
                "password": "test123",
                "cargo": "Tester",
                "departamento": "QA",
                "salario": 2000.0,
                "fechaIngreso": "2025-01-01",
                "rol": "EMPLEADO"
            }
            """;

        // Mock JWT para simular admin autenticado
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getEmailFromToken(anyString())).thenReturn("admin@empresa.com");
        when(jwtUtil.getRolFromToken(anyString())).thenReturn("ADMIN");
        when(jwtUtil.getIdFromToken(anyString())).thenReturn("123");

        // Act & Assert
        mockMvc.perform(post("/api/empleados")
                .header("Authorization", "Bearer fake-jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(empleadoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test User"))
                .andExpect(jsonPath("$.data.email").value("test@empresa.com"));
    }

    // PRUEBA 3: Validación de email duplicado
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEmailDuplicado() throws Exception {
        // Arrange
        when(empleadoService.crearEmpleado(any(Empleado.class)))
            .thenThrow(new RuntimeException("Ya existe un empleado con este email"));

        String empleadoJson = """
            {
                "nombre": "Usuario Duplicado",
                "email": "admin@empresa.com",
                "password": "test123",
                "cargo": "Test",
                "departamento": "Test",
                "salario": 1000.0,
                "fechaIngreso": "2025-01-01",
                "rol": "EMPLEADO"
            }
            """;

        // Mock JWT para simular admin autenticado
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getEmailFromToken(anyString())).thenReturn("admin@empresa.com");
        when(jwtUtil.getRolFromToken(anyString())).thenReturn("ADMIN");
        when(jwtUtil.getIdFromToken(anyString())).thenReturn("123");

        // Act & Assert
        mockMvc.perform(post("/api/empleados")
                .header("Authorization", "Bearer fake-jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(empleadoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Ya existe un empleado con este email"));
    }
}
