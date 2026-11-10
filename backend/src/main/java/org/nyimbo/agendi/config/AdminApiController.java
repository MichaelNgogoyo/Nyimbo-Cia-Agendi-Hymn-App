package org.nyimbo.agendi.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AdminApiController {
    @GetMapping("/api/v1/admin/csrf") public Map<String,String> csrf(CsrfToken token) { return Map.of("token", token.getToken()); }
    @GetMapping("/api/v1/health") public Map<String,String> health() { return Map.of("status", "ok"); }
}
