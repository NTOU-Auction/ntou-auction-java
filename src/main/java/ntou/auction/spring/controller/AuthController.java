package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.Role;
import ntou.auction.spring.data.entity.User;
import ntou.auction.spring.data.service.UserService;
import ntou.auction.spring.security.AuthRequest;
import ntou.auction.spring.security.JWTService;
import ntou.auction.spring.security.SignupRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/log-in")
    public ResponseEntity<Map<String, String>> issueToken(@Valid @RequestBody AuthRequest request) {
        String token = JWTService.generateJWT(request);
        Map<String, String> response = Collections.singletonMap("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupRequest request, Errors errors) {
        String response = "Success";
        User newUser = new User();
        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setName(request.getName());
        newUser.setHashedPassword(userService.getPasswordEncoder().encode(request.getPassword()));
        newUser.setRoles(roles);
        newUser.setEnabled(true);
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        if(!userService.isUsernameNonExist(request.getUsername())){
            return ResponseEntity.badRequest().body("Username duplicated");
        }
        if(!userService.isEmailNonExist(request.getEmail())){
            return ResponseEntity.badRequest().body("Email duplicated");
        }
        userService.store(newUser);
        return ResponseEntity.ok(response);
    }

}
