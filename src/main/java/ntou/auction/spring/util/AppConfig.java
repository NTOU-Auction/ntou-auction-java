package ntou.auction.spring.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "app")
@Component
@Data
public class AppConfig {

    // default password encoder can be set by idForEncode
    // BCrypt pbkdf2 argon2
    // @Value("{security.password.encoder:argon2}")
    private String idForEncode;

    // only file size below 10MB can be uploaded
    // you can modify this value, but the limit is 2047MB
    // @Value("{upload.image.size:10}")
    private int maxImageSizeInMegaBytes;

    // max 3 files can be uploaded
    // you can modify this value
    // @Value("{upload.image.number:3}")
    private int maxImageFiles;

    // only file size below 3MB can be uploaded
    // you can modify this value, but the limit is 2047MB
    // @Value("{upload.avatar.size:3}")
    private int maxAvatarSizeInMegaBytes;

    // The default image size limit for new sign-up users
    private int newSignupImageSizeLimit;

    private String mailUsername;

    private String JWTKey;

}