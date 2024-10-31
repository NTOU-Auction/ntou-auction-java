package ntou.auction.spring.mail;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import ntou.auction.spring.account.entity.User;
import ntou.auction.spring.account.service.UserService;
import ntou.auction.spring.order.entity.Order;
import ntou.auction.spring.product.entity.Product;
import ntou.auction.spring.util.AppConfig;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final AppConfig appConfig;

    public EmailService(JavaMailSender mailSender, UserService userService, AppConfig appConfig) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.appConfig = appConfig;
    }

    @Async
    public void sendMailBidSuccess(Long userId, Product product) {

        if (userService.get(userId).isEmpty()) {
            System.err.println("找不到ID為 " + userId + " 的使用者，無法寄出得標成功通知");
            return;
        }
        User customer = userService.get(userId).get();

        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setSubject("[NTOU Auction] 得標通知", "UTF-8");
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(customer.getEmail()));
            mimeMessage.setFrom(new InternetAddress(appConfig.getMailUsername()));
            mimeMessage.setText("親愛的 " + customer.getName()
                    + " (@" + customer.getUsername() + ") 您好:" + "\n"
                    + "您已成功標得 " + product.getProductName() + " 商品，"
                    + "目前商品已加入購物車，為了能夠盡早取得您心儀的商品，麻煩您盡早結帳。" + "\n\n"
                    + "感謝您使用 NTOU Auction，祝您購物愉快！" + "\n\n"
                    + "此為系統自動發送之郵件，請勿回覆!", "UTF-8"

            );
            mimeMessage.setSentDate(new Date());
        };

        try {
            this.mailSender.send(preparator);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /*
    public void sendMailBidFailed(Long userId, Product product) {

        if (userService.get(userId).isEmpty()) {
            System.err.println("找不到ID為 " + userId + " 的使用者，無法寄出商品下架通知");
            return;
        }
        User customer = userService.get(userId).get();

        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setSubject("[NTOU Auction] 商品下架通知", "UTF-8");
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(customer.getEmail()));
            mimeMessage.setFrom(new InternetAddress(appConfig.getMailUsername()));
            mimeMessage.setText("親愛的 " + customer.getName()
                    + " (@" + customer.getUsername() + ") 您好:" + "\n"
                    + "您之前參加競標的 " + product.getProductName() + " 商品"
                    + "目前已由賣家下架，造成您的不便還請見諒。" + "\n\n"
                    + "感謝您使用 NTOU Auction，歡迎選購其他商品！" + "\n\n"
                    + "此為系統自動發送之郵件，請勿回覆!", "UTF-8"

            );
            mimeMessage.setSentDate(new Date());
        };

        try {
            this.mailSender.send(preparator);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }
    */
    @Async
    public void sendMailOrderEstablished(Long userId, Order order) {

        if (userService.get(userId).isEmpty() || userService.get(order.getSellerid()).isEmpty()) {
            System.err.println("找不到ID為 " + userId + " 的使用者，或查無賣家，無法寄出訂單成立通知");
            return;
        }
        User customer = userService.get(userId).get();
        User seller = userService.get(order.getSellerid()).get();

        MimeMessagePreparator buyerPreparator = mimeMessage -> {
            mimeMessage.setSubject("[NTOU Auction] 訂單成立通知", "UTF-8");
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(customer.getEmail()));
            mimeMessage.setFrom(new InternetAddress(appConfig.getMailUsername()));
            mimeMessage.setText("親愛的 " + customer.getName()
                    + " (@" + customer.getUsername() + ") 您好:" + "\n"
                    + "您已成功購買賣家為 @" + seller.getUsername() + " 的商品，"
                    + "您這次購買了 " + order.getProductAddAmountList().size() + " 個品項的商品" + "\n"
                    + "目前訂單狀態為等待賣家確認，訂單詳細資訊請上NTOU Auction確認。" + "\n\n"
                    + "感謝您使用 NTOU Auction，祝您購物愉快！" + "\n\n"
                    + "此為系統自動發送之郵件，請勿回覆!", "UTF-8"

            );
            mimeMessage.setSentDate(new Date());
        };

        MimeMessagePreparator sellerPreparator = mimeMessage -> {
            mimeMessage.setSubject("[NTOU Auction] 訂單成立通知", "UTF-8");
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(seller.getEmail()));
            mimeMessage.setFrom(new InternetAddress(appConfig.getMailUsername()));
            mimeMessage.setText("親愛的 " + seller.getName()
                    + " (@" + seller.getUsername() + ") 您好:" + "\n"
                    + "買家 @" + customer.getUsername() + " 已下訂您的商品，"
                    + "目前訂單狀態為等待確認，請您盡快上NTOU Auction更新訂單狀態。" + "\n\n"
                    + "感謝您使用 NTOU Auction，祝您交易愉快！" + "\n\n"
                    + "此為系統自動發送之郵件，請勿回覆!", "UTF-8"

            );
            mimeMessage.setSentDate(new Date());
        };

        try {
            this.mailSender.send(buyerPreparator);
            this.mailSender.send(sellerPreparator);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Async
    public void sendMailOrderUpdate(Long userId, Order order) {

        if (userService.get(userId).isEmpty() || userService.get(order.getSellerid()).isEmpty()) {
            System.err.println("找不到ID為 " + userId + " 的使用者，或查無賣家，無法寄出訂單狀態更新通知");
            return;
        }
        User customer = userService.get(userId).get();
        User seller = userService.get(order.getSellerid()).get();
        String status;
        if (order.getStatus() == 0L) {
            status = "賣家拒絕您的訂單";
        } else if (order.getStatus() == 2L) {
            status = "賣家同意您的訂單";
        } else {
            status = "未知";
        }

        MimeMessagePreparator buyerPreparator = mimeMessage -> {
            mimeMessage.setSubject("[NTOU Auction] 訂單狀態更新通知", "UTF-8");
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(customer.getEmail()));
            mimeMessage.setFrom(new InternetAddress(appConfig.getMailUsername()));
            mimeMessage.setText("親愛的 " + customer.getName()
                    + " (@" + customer.getUsername() + ") 您好:" + "\n"
                    + "您之前購買賣家為 @" + seller.getUsername() + " 的商品，"
                    + "目前訂單狀態為 " + status + " ，訂單詳細資訊請上NTOU Auction確認。" + "\n\n"
                    + "感謝您使用 NTOU Auction，祝您購物愉快！" + "\n\n"
                    + "此為系統自動發送之郵件，請勿回覆!", "UTF-8"

            );
            mimeMessage.setSentDate(new Date());
        };

        try {
            this.mailSender.send(buyerPreparator);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
