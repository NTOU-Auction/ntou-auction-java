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
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignupRequest request, Errors errors) {
        String successMessage = "Success";
        String usernameDuplicatedMessage = "輸入的帳號已被其他人使用，請使用別的帳號註冊!";
        String emailDuplicatedMessage = "輸入的電子信箱已被其他人使用，請使用別的信箱註冊!";
        String emailAndUsernameDuplicatedMessage = "輸入的帳號及電子信箱皆已被其他人使用，請重新註冊!";
        String passwordMessage = "至少需要8位密碼，且不超過128位";
        Map<String, String> successResponse = Collections.singletonMap("message", successMessage);
        Map<String, String> usernameDuplicatedResponse = Collections.singletonMap("message", usernameDuplicatedMessage);
        Map<String, String> emailDuplicatedResponse = Collections.singletonMap("message", emailDuplicatedMessage);
        Map<String, String> emailAndUsernameDuplicatedResponse = Collections.singletonMap("message", emailAndUsernameDuplicatedMessage);
        Map<String, String> passwordResponse = Collections.singletonMap("message", passwordMessage);
        User newUser = new User();
        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setName(request.getName());
        if(request.getPassword().length() < 8 || request.getPassword().length() > 128){
            return ResponseEntity.badRequest().body(passwordResponse);
        }
        newUser.setHashedPassword(userService.getPasswordEncoder().encode(request.getPassword()));
        newUser.setRoles(roles);
        newUser.setEnabled(true);
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        if(!userService.isUsernameNonExist(request.getUsername())){
            if(!userService.isEmailNonExist(request.getEmail())){
                return ResponseEntity.badRequest().body(emailAndUsernameDuplicatedResponse);
            }
            return ResponseEntity.badRequest().body(usernameDuplicatedResponse);
        }
        if(!userService.isEmailNonExist(request.getEmail())){
            return ResponseEntity.badRequest().body(emailDuplicatedResponse);
        }
        userService.store(newUser);
        return ResponseEntity.ok(successResponse);
    }

}
