package com.empleados.backend_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String email = null;
        String jwtToken = null;
        
        // El token JWT está en el formato "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                email = jwtUtil.getEmailFromToken(jwtToken);
            } catch (Exception e) {
                logger.warn("No se pudo obtener el email del token JWT: " + e.getMessage());
            }
        }
        
        // Validar token y configurar autenticación
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            if (jwtUtil.validateToken(jwtToken)) {
                String rol = jwtUtil.getRolFromToken(jwtToken);
                String id = jwtUtil.getIdFromToken(jwtToken);
                
                // Crear autoridad basada en el rol
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);
                
                // Crear token de autenticación
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        email, 
                        null, 
                        Collections.singletonList(authority)
                    );
                
                // Agregar detalles adicionales al token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Agregar información personalizada
                request.setAttribute("empleadoId", id);
                request.setAttribute("empleadoRol", rol);
                request.setAttribute("empleadoEmail", email);
                
                // Establecer autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // No filtrar rutas públicas
        return path.startsWith("/api/auth/") || 
               path.equals("/api/") || 
               path.startsWith("/api/public/") ||
               path.equals("/error") ||
               "OPTIONS".equals(method);
    }
}