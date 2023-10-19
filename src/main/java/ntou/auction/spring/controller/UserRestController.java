package ntou.auction.spring.controller;

import ntou.auction.spring.data.entity.User;
import ntou.auction.spring.data.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    Optional<User> getById() {
        return userService.get(1L);
    }

    @GetMapping("admin")
    public String helloAdmin() {
        return "Hello Admin";
    }

}
