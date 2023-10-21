package ntou.auction.spring.controller;

import ntou.auction.spring.data.entity.User;
import ntou.auction.spring.data.service.UserIdentity;
import ntou.auction.spring.data.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;

    private final UserIdentity userIdentity;

    public UserController(UserService userService, UserIdentity userIdentity) {
        this.userService = userService;
        this.userIdentity = userIdentity;
    }

    // for admin usage
    @GetMapping("/users/{username}")
    @ResponseBody
    User getByUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/users")
    @ResponseBody
    User getUserProfileByJWT() {
        return userService.findByUsername(userIdentity.getUsername());
    }

    @GetMapping("admin")
    public String helloAdmin() {
        return "Hello Admin";
    }
}
