package ntou.auction.spring.order.service;

import ntou.auction.spring.account.entity.User;
import ntou.auction.spring.account.service.UserService;
import ntou.auction.spring.mail.EmailService;
import ntou.auction.spring.order.entity.Order;
import ntou.auction.spring.order.response.OrderWithProductDetail;
import ntou.auction.spring.order.repository.OrderRepository;
import ntou.auction.spring.product.entity.Product;
import ntou.auction.spring.shoppingcart.response.ProductAddAmount;
import ntou.auction.spring.product.service.ProductService;
import ntou.auction.spring.shoppingcart.service.ShoppingcartService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository repository;

    private final ProductService productService;

    private final UserService userService;
    private final EmailService emailService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderService(OrderRepository repository, ProductService productService, UserService userService, EmailService emailService) {
        this.repository = repository;
        this.productService = productService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public Order findOrderById(Long Id) {
        return repository.findById(Id).orElse(null);
    }

    public List<Order> findAllByBuyerId(Long buyerId) {
        return repository.findAllByBuyerid(buyerId);
    }

    public List<Order> findRejectByBuyerId(Long buyerId) {
        return repository.findRejectByBuyerid(buyerId);
    }

    public List<Order> findWaitingByBuyerId(Long buyerId) {
        return repository.findWaitingByBuyerid(buyerId);
    }

    public List<Order> findSubmittedByBuyerId(Long buyerId) {return repository.findSubmittedByBuyerid(buyerId);}

    public List<Order> findDoneByBuyerId(Long buyerId) {
        return repository.findDoneByBuyerid(buyerId);
    }

    public List<Order> findAllBySellerId(Long sellerId) { return repository.findAllBySellerid(sellerId);}

    public List<Order> findRejectBySellerId(Long sellerId) { return repository.findRejectBySellerid(sellerId); }

    public List<Order> findWaitingBySellerId(Long sellerId) { return repository.findWaitingBySellerid(sellerId);}

    public List<Order> findSubmittedBySellerId(Long sellerId) { return repository.findSubmittedBySellerid(sellerId);}

    public List<Order> findDoneBySellerId(Long sellerId) { return repository.findDoneBySellerid(sellerId);}

    public Long submitOrder(Long orderId, Long userId) {
        // for status -> 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if (getorder == null) return 0L;
        if (!getorder.getStatus().equals(1L)) return 1L;
        if (!Objects.equals(findOrderById(orderId).getSellerid(), userId)) return 2L;
        getorder.setStatus(2L);
        repository.save(getorder);
        emailService.sendMailOrderUpdate(getorder.getBuyerid(),getorder);
        return 3L;
    }

    public Long rejectOrder(Long orderId, Long userId) {
        // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if (getorder == null) return 0L;
        if (!getorder.getStatus().equals(1L)) return 1L;
        if (!Objects.equals(findOrderById(orderId).getSellerid(), userId)) return 2L;
        getorder.setStatus(0L);
        repository.save(getorder);
        emailService.sendMailOrderUpdate(getorder.getBuyerid(),getorder);
        return 3L;
    }

    public Long cancelOrder(Long orderId, Long userId) {
        // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if (getorder == null) return 0L;
        if (getorder.getStatus().equals(3L) || getorder.getStatus().equals(0L)) return 1L;
        if (!Objects.equals(findOrderById(orderId).getBuyerid(), userId)) return 2L;
        if (Duration.between(getorder.getUpdateTime(), LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter)).toSeconds() > (86400 * 7L))
            return -1L;
        getorder.setStatus(0L);
        repository.save(getorder);
        return 3L;
    }

    // make order be done
    public Long doneOrder(Long orderId, Long userId) {
        // 0: reject, 1: waiting for submit, 2: submitted but not paid, 3: order done
        // for return -> 0: orderNotFound, 1: statusError, 2: idError, 3: success, -1: expired
        Order getorder = repository.findById(orderId).orElse(null);
        if (getorder == null) return 0L;
        if (!getorder.getStatus().equals(2L)) return 1L;
        if (!Objects.equals(findOrderById(orderId).getSellerid(), userId)) return 2L;
        getorder.setStatus(3L);
        repository.save(getorder);
        return 3L;
    }

    public List<Long> checkIncome(Long userId) {
        List<Order> doneOrder = findDoneBySellerId(userId);
        List<Long> re = new ArrayList<>();
        for (int i = 0; i < 8; i++) re.add(0L);
        for (Order order : doneOrder) {
            Temporal nowTime = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
            long duringDay = Duration.between(order.getUpdateTime(), nowTime).toDays();
            if (duringDay < 8L) {
                Long total = 0L;
                for(List<Long> product: order.getProductAddAmountList()) {
                    Product tempProduct = productService.getID(product.get(0));
                    total += tempProduct.getCurrentPrice()*product.get(1);
                }
                re.set((int)duringDay, re.get((int)duringDay)+total);
            }
        }
        return re;
    }

    public void addOrder(Order order) {
        repository.save(order);
    }

    public boolean checkIsSameSeller(List<List<Long>> list) {
        Set<Long> check = new HashSet<>();
        for(List<Long> productAddAmount: list) {
            check.add(productService.getID(productAddAmount.getFirst()).getSellerID());
        }
        return check.size() == 1;
    }

    public List<OrderWithProductDetail> orderToOrderWithProductDetail(List<Order> getOrder) {
        List<OrderWithProductDetail> result = new ArrayList<>();
        for (Order order : getOrder) {
            OrderWithProductDetail addOrder = new OrderWithProductDetail();
            addOrder.setSellerid(order.getSellerid());
            addOrder.setBuyerid(order.getBuyerid());
            addOrder.setUpdateTime(order.getUpdateTime());
            addOrder.setStatus(order.getStatus());
            addOrder.setOrderid(order.getId());
            User buyer = userService.get(order.getBuyerid()).orElse(null);
            User seller = userService.get(order.getSellerid()).orElse(null);
            if(buyer!=null) addOrder.setBuyername(buyer.getUsername());
            if(seller!=null) addOrder.setSellername(seller.getUsername());
            List<ProductAddAmount> temp = new ArrayList<>();
            for (List<Long> product : order.getProductAddAmountList()) {
                temp.add(new ProductAddAmount(productService.getID(product.get(0)), product.get(1)));
            }
            addOrder.setProductAddAmountList(temp);
            result.add(addOrder);
        }
        return result;
    }

    public boolean addAmountToProduct(Order order) {
        // (order == null) this may not be happened
        if (order == null) return false;
        // add product amount with amount
        for (List<Long> eachProduct : order.getProductAddAmountList()) {
            productService.productAmountIncrease(eachProduct.get(0), eachProduct.get(1));
        }
        return true;
    }

}
