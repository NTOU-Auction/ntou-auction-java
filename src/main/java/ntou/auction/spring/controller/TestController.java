package ntou.auction.spring.controller;

import ntou.auction.spring.data.entity.User;
import ntou.auction.spring.data.service.UserIdentity;
import ntou.auction.spring.data.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/test", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {
    private final UserService userService;
    private final UserIdentity userIdentity;

    public TestController(UserService userService, UserIdentity userIdentity) {
        this.userService = userService;
        this.userIdentity = userIdentity;
    }

    @GetMapping("/hello")
    @ResponseBody
    @CrossOrigin(origins = "http://localhost:3000")
    public User helloAdmin() {
        return userService.findByUsername("admin");
    }
}
