package ntou.auction.spring.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ntou.auction.spring.security.AuthRequest;
import ntou.auction.spring.security.JWTService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")
public class AuthController {


    @PostMapping("/log-in")
    public ResponseEntity<Map<String, String>> issueToken(@Valid @RequestBody AuthRequest request) {
        String token = JWTService.generateJWT(request);
        Map<String, String> response = Collections.singletonMap("token", token);
        return ResponseEntity.ok(response);
    }

}
