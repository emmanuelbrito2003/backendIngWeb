package com.empleados.backend_api.controller;

import com.empleados.backend_api.config.JwtUtil;
import com.empleados.backend_api.dto.ApiResponse;
import com.empleados.backend_api.dto.LoginRequest;
import com.empleados.backend_api.dto.LoginResponse;
import com.empleados.backend_api.model.Empleado;
import com.empleados.backend_api.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private JwtUtil jwtUtil;

    // Login común para admin y empleado
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Buscar empleado por email
            Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorEmail(loginRequest.getEmail());

            if (empleadoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Credenciales inválidas"));
            }

            Empleado empleado = empleadoOpt.get();

            // Verificar contraseña
            if (!empleadoService.verificarPassword(loginRequest.getPassword(), empleado.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Credenciales inválidas"));
            }

            // Generar token JWT
            String token = jwtUtil.generateToken(empleado.getEmail(), empleado.getRol(), empleado.getId());

            // Crear respuesta con token y datos del usuario
            LoginResponse loginResponse = new LoginResponse(token, empleado);

            return ResponseEntity.ok(ApiResponse.success("Login exitoso", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
        }
    }

    // Verificar si el token es válido (opcional)
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);

                if (jwtUtil.validateToken(jwtToken)) {
                    String email = jwtUtil.getEmailFromToken(jwtToken);
                    String rol = jwtUtil.getRolFromToken(jwtToken);
                    String id = jwtUtil.getIdFromToken(jwtToken);

                    return ResponseEntity.ok(ApiResponse.success("Token válido",
                            new Object() {
                                public final String email = AuthController.this.jwtUtil.getEmailFromToken(jwtToken);
                                public final String rol = AuthController.this.jwtUtil.getRolFromToken(jwtToken);
                                public final String id = AuthController.this.jwtUtil.getIdFromToken(jwtToken);
                            }));
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token inválido"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token inválido: " + e.getMessage()));
        }
    }

    // Endpoint para obtener información del usuario logueado
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Empleado>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);

                if (jwtUtil.validateToken(jwtToken)) {
                    String email = jwtUtil.getEmailFromToken(jwtToken);
                    Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorEmail(email);

                    if (empleadoOpt.isPresent()) {
                        return ResponseEntity.ok(ApiResponse.success("Usuario encontrado", empleadoOpt.get()));
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("No autorizado"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
        }
    }
}