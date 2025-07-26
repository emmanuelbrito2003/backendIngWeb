package com.empleados.backend_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/public")
    public Map<String, Object> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint público funcionando");
        response.put("status", "OK");
        return response;
    }

    @GetMapping("/protected")
    public Map<String, Object> protectedEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint protegido funcionando");
        response.put("status", "OK");
        return response;
    }
} 