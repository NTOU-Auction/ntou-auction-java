package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.security.AuthRequest;
import ntou.auction.spring.security.JWTService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {


    @PostMapping("/log-in")
    public ResponseEntity<Map<String, String>> issueToken(@Valid @RequestBody AuthRequest request) {
        String token = JWTService.generateJWT(request);
        Map<String, String> response = Collections.singletonMap("token", token);

        return ResponseEntity.ok(response);
    }

}
