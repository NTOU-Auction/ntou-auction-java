package ntou.auction.spring.controller;

import jakarta.validation.Valid;
import ntou.auction.spring.data.entity.*;
import ntou.auction.spring.data.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/order", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    private final OrderService orderService;
    private final ProductService productService;

    private final ShoppingcartService shoppingcartService;
    private final UserService userService;

    private final UserIdentity userIdentity;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, String> successMessage = Collections.singletonMap("message", "成功");
    private static final Map<String, String> failMessage = Collections.singletonMap("message", "操作失敗");

    private static final Map<String, String> tooManySellerMessage = Collections.singletonMap("message", "訂單的賣家只能來自同一位");

    private static final Map<String, String> orderNotFound = Collections.singletonMap("message", "訂單不存在");

    private static final Map<String, String> statusError = Collections.singletonMap("message", "無法對目前訂單進行操作");

    private static final Map<String, String> identityError = Collections.singletonMap("message", "該狀身分下無法進行操作");

    private static final Map<String, String> formatError = Collections.singletonMap("message", "格式錯誤");

    private static final Map<String, String> notFoundInShoppingCartError = Collections.singletonMap("message", "商品不在購物車中或購買數量過多");

    private static final Map<String, String> selfBuyingError = Collections.singletonMap("message", "不可以購買自己的商品");

    public OrderController(OrderService orderService, ProductService productService, ShoppingcartService shoppingcartService, UserService userService, UserIdentity userIdentity) {
        this.orderService = orderService;
        this.productService = productService;
        this.shoppingcartService = shoppingcartService;
        this.userService = userService;
        this.userIdentity = userIdentity;
    }

    @GetMapping("/order/all")
    List<OrderWithProductDetail> getAllByBuyer() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        List<Order> getOrder = orderService.findAllByBuyerId(userId);
        return orderService.orderToOrderWithProductDetail(getOrder);
    }

    @GetMapping("/order/reject")
    List<OrderWithProductDetail> getRejectByBuyer() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findRejectByBuyerId(userId));
    }

    @GetMapping("/order/waiting")
    List<OrderWithProductDetail> getWaitingByBuyer() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findWaitingByBuyerId(userId));
    }

    @GetMapping("/order/submitted")
    List<OrderWithProductDetail> getSubmitByBuyer() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findSubmittedByBuyerId(userId));
    }

    @GetMapping("/order/done")
    List<OrderWithProductDetail> getDoneByBuyer() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findDoneByBuyerId(userId));
    }

    @GetMapping("/check/all")
    List<OrderWithProductDetail> getAllBySeller() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findAllBySellerId(userId));
    }

    @GetMapping("/check/reject")
    List<OrderWithProductDetail> getRejectBySeller() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findRejectBySellerId(userId));
    }

    @GetMapping("/check/waiting")
    List<OrderWithProductDetail> getWaitingBySeller() {
        // filter Waited order with seller
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findWaitingBySellerId(userId));
    }

    @GetMapping("/check/submitted")
    List<OrderWithProductDetail> getSubmittedBySeller() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findSubmittedBySellerId(userId));
    }

    @GetMapping("/check/done")
    List<OrderWithProductDetail> getDoneBySeller() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.orderToOrderWithProductDetail(orderService.findDoneBySellerId(userId));
    }
    @GetMapping("/check/income")
    List<Long> getIncomeBySeller() {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        return orderService.checkIncome(userId);
    }

    @PostMapping("/create")
    ResponseEntity<Map<String, String>> addOrder(@Valid @RequestBody AddOrderRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        List<List<Long>> getrequest = request.getProductList();

        for (List<Long> eachProductAddAmount : getrequest) {
            Long productId = eachProductAddAmount.get(0);
            Product getProduct = productService.getID(productId);
            // Id error
            if (getProduct == null) {
                Map<String, String> ErrorIdMessage = Collections.singletonMap("message", "商品(ID:" + productId + ")不存在");
                return ResponseEntity.badRequest().body(ErrorIdMessage);
            }
        }

        // checkInShoppingCart -> -1: format error, 0: false, 1: true
        Long checkInShoppingCart = shoppingcartService.checkIsProductAllInShoppingCart(getrequest, userId);
        if(checkInShoppingCart.equals(-1L)) return ResponseEntity.badRequest().body(formatError);
        if(checkInShoppingCart.equals(0L)) return ResponseEntity.badRequest().body(notFoundInShoppingCartError);

        for (List<Long> eachProductAddAmount : getrequest) {
            Long productId = eachProductAddAmount.get(0);
            Long amount = eachProductAddAmount.get(1);
            Product getProduct = productService.getID(productId);
            // amount exceed
            if (amount > getProduct.getProductAmount()) {
                Map<String, String> amountExceedReturn = Collections.singletonMap("message", "商品數量(" + getProduct.getProductName() + ")過多");
                return ResponseEntity.badRequest().body(amountExceedReturn);
            }
        }

        // Same seller
        boolean checkSameSeller = orderService.checkIsSameSeller(getrequest);
        if(!checkSameSeller) return ResponseEntity.badRequest().body(tooManySellerMessage);
    /*
        // Self buying
        boolean checkSelfBuying = shoppingcartService.checkIsViolateSelfBuying(getrequest, userId);
        if(checkSelfBuying) return ResponseEntity.badRequest().body(selfBuyingError);
*/
        // order status -> 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        Order order = new Order();
        order.setBuyerid(userId);
        order.setUpdateTime(LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter));
        order.setStatus(1L);

        for (List<Long> eachProductAddAmount : getrequest) {
            Long productId = eachProductAddAmount.get(0);
            Long amount = eachProductAddAmount.get(1);
            Product getProduct = productService.getID(productId);
            order.setSellerid(getProduct.getSellerID());
            List<Long> input = new ArrayList<>();
            input.add(productId);
            input.add(amount);
            order.addProductAddAmount(input);
        }
        for (List<Long> eachProductAddAmount : getrequest) {
            Long productId = eachProductAddAmount.get(0);
            Long amount = eachProductAddAmount.get(1);

            // decrease product's amount by amount
            productService.productAmountDecrease(productId, amount);

            // delete Product amount in Shopping cart
            shoppingcartService.decreaseProductByUserId(userId, productId, amount);
        }
        orderService.addOrder(order);
        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/makesubmit")
    ResponseEntity<Map<String, String>> makeSubmit(@Valid @RequestBody OperateOrderRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long orderId = request.getOrderId();
        if (orderId == null) return ResponseEntity.badRequest().body(failMessage);
        // result -> 0: orderNotFound, 1: statusError, 2: idError, 3: success
        Long result = orderService.submitOrder(orderId, userId);
        if (result.equals(0L)) return ResponseEntity.badRequest().body(orderNotFound);
        if (result.equals(1L)) return ResponseEntity.badRequest().body(statusError);
        if (result.equals(2L)) return ResponseEntity.badRequest().body(identityError);
        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/makedone")
    ResponseEntity<Map<String, String>> makeDone(@Valid @RequestBody OperateOrderRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long orderId = request.getOrderId();
        if (orderId == null) return ResponseEntity.badRequest().body(failMessage);
        // result -> 0: orderNotFound, 1: statusError, 2: idError, 3: success
        Long result = orderService.doneOrder(orderId, userId);
        if (result.equals(0L)) return ResponseEntity.badRequest().body(orderNotFound);
        if (result.equals(1L)) return ResponseEntity.badRequest().body(statusError);
        if (result.equals(2L)) return ResponseEntity.badRequest().body(identityError);
        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/makereject")
    ResponseEntity<Map<String, String>> makeReject(@Valid @RequestBody OperateOrderRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long orderId = request.getOrderId();
        if (orderId == null) return ResponseEntity.badRequest().body(failMessage);
        // 0: orderNotFound, 1: statusError, 2: idError, 3: success
        Long result = orderService.rejectOrder(orderId, userId);
        if (result.equals(0L)) return ResponseEntity.badRequest().body(orderNotFound);
        if (result.equals(1L)) return ResponseEntity.badRequest().body(statusError);
        if (result.equals(2L)) return ResponseEntity.badRequest().body(identityError);
        boolean check = orderService.addAmountToProduct(orderService.findOrderById(orderId));
        if (!check) return ResponseEntity.badRequest().body(orderNotFound); //this may not be happened
        return ResponseEntity.ok(successMessage);
    }

    @PostMapping("/makecancel")
    ResponseEntity<Map<String, String>> makeCancel(@Valid @RequestBody OperateOrderRequest request) {
        Long userId = userService.findByUsername(userIdentity.getUsername()).getId();
        Long orderId = request.getOrderId();
        if (orderId == null) return ResponseEntity.badRequest().body(failMessage);
        // 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Long result = orderService.cancelOrder(orderId, userId);
        if (result.equals(0L)) return ResponseEntity.badRequest().body(orderNotFound);
        if (result.equals(1L)) return ResponseEntity.badRequest().body(statusError);
        if (result.equals(2L)) return ResponseEntity.badRequest().body(identityError);
        Map<String, String> expiredError = Collections.singletonMap("message", "超過7天無法取消訂單");
        if (result.equals(-1L)) return ResponseEntity.badRequest().body(expiredError);
        Order thisOrder = orderService.findOrderById(orderId);
        boolean check = orderService.addAmountToProduct(thisOrder);
        if(!check) return ResponseEntity.badRequest().body(orderNotFound); // this may not be happened
        return ResponseEntity.ok(successMessage);
    }
}
