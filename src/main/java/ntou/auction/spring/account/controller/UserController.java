package ntou.auction.spring.account.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.account.request.FavoriteRequest;
import ntou.auction.spring.product.entity.Product;
import ntou.auction.spring.account.entity.User;
import ntou.auction.spring.product.service.ProductService;
import ntou.auction.spring.account.response.UserIdentity;
import ntou.auction.spring.account.service.UserService;
import ntou.auction.spring.account.request.SignupRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = {"http://localhost:3000","https://ntou-auction.com","http://ntou-auction.com"})
public class UserController {
    private final UserService userService;

    private final UserIdentity userIdentity;
    private final ProductService productService;

    public UserController(UserService userService, UserIdentity userIdentity, ProductService productService) {
        this.userService = userService;
        this.userIdentity = userIdentity;
        this.productService = productService;
    }

    // for admin usage
    /*
    @GetMapping("/users/{username}")
    @ResponseBody
    User getByUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }
    */

    @GetMapping("/users")
    @ResponseBody
    User getUserProfileByJWT() {
        return userService.findByUsername(userIdentity.getUsername());
    }

    @GetMapping("/favorite")
    @ResponseBody
    List<Product> getFavorite() {
        Set<Long> favoriteProductIds = userService.getFavoriteProducts(userService.findByUsername(userIdentity.getUsername()).getId());
        List<Product> favoriteProducts = new ArrayList<>();
        for (Long favoriteProductId : favoriteProductIds) {
            favoriteProducts.add(productService.getID(favoriteProductId));
        }
        return favoriteProducts;
    }

    @PostMapping("/favorite")
    @ResponseBody
    ResponseEntity<Map<String, String>> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        Map<String, String> duplicatedProduct = Collections.singletonMap("message", "商品已在我的最愛");
        Map<String, String> productNotFound = Collections.singletonMap("message", "找不到商品");
        Map<String, String> success = Collections.singletonMap("message", "成功將商品加入我的最愛");
        if (productService.getID(request.getProductId()) == null) {
            return ResponseEntity.badRequest().body(productNotFound);
        }
        if (userService.addFavoriteProducts(userService.findByUsername(userIdentity.getUsername()).getId(), request.getProductId())) {
            return ResponseEntity.ok(success);
        } else {
            return ResponseEntity.badRequest().body(duplicatedProduct);
        }
    }

    @DeleteMapping("/favorite")
    @ResponseBody
    ResponseEntity<Map<String, String>> removeFavorite(@Valid @RequestBody FavoriteRequest request) {
        Map<String, String> failed = Collections.singletonMap("message", "因為商品不存在，無法將商品從我的最愛移除");
        Map<String, String> success = Collections.singletonMap("message", "成功將商品從我的最愛移除");
        if (userService.removeFavoriteProducts(userService.findByUsername(userIdentity.getUsername()).getId(), request.getProductId())) {
            return ResponseEntity.ok(success);
        } else {
            return ResponseEntity.badRequest().body(failed);
        }
    }

    @PatchMapping("/users")
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignupRequest request) {
        String successMessage = "成功更新";
        String usernameDuplicatedMessage = "更新失敗，輸入的帳號已被其他人使用!";
        String emailDuplicatedMessage = "更新失敗，輸入的電子信箱已被其他人使用!";
        String emailAndUsernameDuplicatedMessage = "更新失敗，輸入的帳號及電子信箱皆已被其他人使用!";
        String passwordMessage = "至少需要8位密碼，且不超過128位";
        Map<String, String> successResponse = Collections.singletonMap("message", successMessage);
        Map<String, String> usernameDuplicatedResponse = Collections.singletonMap("message", usernameDuplicatedMessage);
        Map<String, String> emailDuplicatedResponse = Collections.singletonMap("message", emailDuplicatedMessage);
        Map<String, String> emailAndUsernameDuplicatedResponse = Collections.singletonMap("message", emailAndUsernameDuplicatedMessage);
        Map<String, String> passwordResponse = Collections.singletonMap("message", passwordMessage);
        User user = userService.findByUsername(userIdentity.getUsername());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        if(request.getPassword().length() < 8 || request.getPassword().length() > 128){
            return ResponseEntity.badRequest().body(passwordResponse);
        }
        user.setHashedPassword(userService.getPasswordEncoder().encode(request.getPassword()));
        if(!userService.isUsernameNonExist(request.getUsername())){
            if(!userService.isEmailNonExist(request.getEmail())){
                return ResponseEntity.badRequest().body(emailAndUsernameDuplicatedResponse);
            }
            return ResponseEntity.badRequest().body(usernameDuplicatedResponse);
        }
        if(!userService.isEmailNonExist(request.getEmail())){
            return ResponseEntity.badRequest().body(emailDuplicatedResponse);
        }
        userService.update(user);
        return ResponseEntity.ok(successResponse);
    }

}
