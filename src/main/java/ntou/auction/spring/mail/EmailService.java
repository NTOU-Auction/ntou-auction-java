package ntou.auction.spring.mail;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import ntou.auction.spring.util.AppConfig;
import ntou.auction.spring.product.entity.Product;
import ntou.auction.spring.account.entity.User;
import ntou.auction.spring.account.service.UserService;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
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

    public void sendMailBid(Long userId, Product product) {

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

}
