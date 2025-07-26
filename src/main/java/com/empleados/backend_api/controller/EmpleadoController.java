package com.empleados.backend_api.controller;

import com.empleados.backend_api.dto.ApiResponse;
import com.empleados.backend_api.dto.EmpleadoUpdateDTO;
import com.empleados.backend_api.model.Empleado;
import com.empleados.backend_api.service.EmpleadoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "http://localhost:4200")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    // GET /empleados - Solo ADMIN puede ver todos los empleados
    @GetMapping
    public ResponseEntity<ApiResponse<List<Empleado>>> obtenerTodosLosEmpleados() {
        try {
            List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();
            return ResponseEntity.ok(ApiResponse.success("Empleados obtenidos exitosamente", empleados));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener empleados: " + e.getMessage()));
        }
    }

    // GET /empleados/{id} - ADMIN puede ver cualquier empleado, EMPLEADO solo su
    // propio perfil
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Empleado>> obtenerEmpleadoPorId(@PathVariable String id,
            HttpServletRequest request) {
        try {
            String empleadoLogueadoId = (String) request.getAttribute("empleadoId");
            String rolLogueado = (String) request.getAttribute("empleadoRol");

            // Si es EMPLEADO, solo puede ver su propio perfil
            if ("EMPLEADO".equals(rolLogueado) && !id.equals(empleadoLogueadoId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permisos para acceder a este perfil"));
            }

            Optional<Empleado> empleado = empleadoService.obtenerEmpleadoPorId(id);

            if (empleado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Empleado no encontrado"));
            }

            return ResponseEntity.ok(ApiResponse.success("Empleado encontrado", empleado.get()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener empleado: " + e.getMessage()));
        }
    }

    // POST /empleados - Solo ADMIN puede crear empleados
    @PostMapping
    public ResponseEntity<ApiResponse<Empleado>> crearEmpleado(@Valid @RequestBody Empleado empleado) {
        try {
            Empleado nuevoEmpleado = empleadoService.crearEmpleado(empleado);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Empleado creado exitosamente", nuevoEmpleado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al crear empleado: " + e.getMessage()));
        }
    }

    // PUT /empleados/{id} - ADMIN puede editar cualquier empleado, EMPLEADO solo su propio perfil
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Empleado>> actualizarEmpleado(
            @PathVariable String id, 
            @Valid @RequestBody EmpleadoUpdateDTO empleadoActualizado,
            HttpServletRequest request) {
        try {
            String empleadoLogueadoId = (String) request.getAttribute("empleadoId");
            String rolLogueado = (String) request.getAttribute("empleadoRol");
            
            // Convertir DTO a Empleado
            Empleado empleadoParaActualizar = convertirDTOAEmpleado(empleadoActualizado);
            
            Empleado empleadoResultado;
            
            if ("EMPLEADO".equals(rolLogueado)) {
                // EMPLEADO solo puede editar su propio perfil y campos limitados
                if (!id.equals(empleadoLogueadoId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No tienes permisos para editar este perfil"));
                }
                
                empleadoResultado = empleadoService.actualizarPerfilPropio(id, empleadoParaActualizar);
            } else {
                // ADMIN puede editar cualquier empleado completamente
                empleadoResultado = empleadoService.actualizarEmpleado(id, empleadoParaActualizar);
            }
            
            return ResponseEntity.ok(ApiResponse.success("Empleado actualizado exitosamente", empleadoResultado));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error al actualizar empleado: " + e.getMessage()));
        }
    }

    // DELETE /empleados/{id} - Solo ADMIN puede eliminar empleados
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarEmpleado(@PathVariable String id) {
        try {
            empleadoService.eliminarEmpleado(id);
            return ResponseEntity.ok(ApiResponse.success("Empleado eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar empleado: " + e.getMessage()));
        }
    }

    // GET /empleados/buscar/departamento/{departamento} - Solo ADMIN
    @GetMapping("/buscar/departamento/{departamento}")
    public ResponseEntity<ApiResponse<List<Empleado>>> buscarPorDepartamento(@PathVariable String departamento) {
        try {
            List<Empleado> empleados = empleadoService.buscarPorDepartamento(departamento);
            return ResponseEntity.ok(ApiResponse.success("Empleados encontrados", empleados));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al buscar empleados: " + e.getMessage()));
        }
    }

    // GET /empleados/buscar/cargo/{cargo} - Solo ADMIN
    @GetMapping("/buscar/cargo/{cargo}")
    public ResponseEntity<ApiResponse<List<Empleado>>> buscarPorCargo(@PathVariable String cargo) {
        try {
            List<Empleado> empleados = empleadoService.buscarPorCargo(cargo);
            return ResponseEntity.ok(ApiResponse.success("Empleados encontrados", empleados));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al buscar empleados: " + e.getMessage()));
        }
    }

    // GET /empleados/buscar/nombre/{nombre} - Solo ADMIN
    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<ApiResponse<List<Empleado>>> buscarPorNombre(@PathVariable String nombre) {
        try {
            List<Empleado> empleados = empleadoService.buscarPorNombre(nombre);
            return ResponseEntity.ok(ApiResponse.success("Empleados encontrados", empleados));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al buscar empleados: " + e.getMessage()));
        }
    }

    // GET /empleados/estadisticas - Solo ADMIN
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<Object>> obtenerEstadisticas() {
        try {
            long totalEmpleados = empleadoService.contarEmpleados();
            long totalAdmins = empleadoService.contarEmpleadosPorRol("ADMIN");
            long totalEmpleadosRegulares = empleadoService.contarEmpleadosPorRol("EMPLEADO");

            Object estadisticas = new Object() {
                public final long totalEmpleados = empleadoService.contarEmpleados();
                public final long totalAdmins = empleadoService.contarEmpleadosPorRol("ADMIN");
                public final long totalEmpleadosRegulares = empleadoService.contarEmpleadosPorRol("EMPLEADO");
            };

            return ResponseEntity.ok(ApiResponse.success("Estadísticas obtenidas", estadisticas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    // Método auxiliar para convertir DTO a Empleado
    private Empleado convertirDTOAEmpleado(EmpleadoUpdateDTO dto) {
        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setEmail(dto.getEmail());
        empleado.setPassword(dto.getPassword());
        empleado.setCargo(dto.getCargo());
        empleado.setDepartamento(dto.getDepartamento());
        empleado.setSalario(dto.getSalario());
        empleado.setFechaIngreso(dto.getFechaIngreso());
        empleado.setTelefono(dto.getTelefono());
        empleado.setRol(dto.getRol());
        return empleado;
    }
}