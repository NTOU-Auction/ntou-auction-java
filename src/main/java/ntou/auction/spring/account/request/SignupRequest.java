package ntou.auction.spring.account.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Length(min = 1, max = 128, message = "帳號長度限制為1~32位!")
    @Column(unique = true)
    private String username;

    @Length(min = 1, max = 128, message = "暱稱長度限制為1~32位!")
    private String name;

    @Length(min = 8, max = 512, message = "密碼長度限制為8~128位!")
    private String password;

    // if avatar is more than 5MB, need to modify column length
    @Lob
    @Column(length = 5243000)
    private byte[] avatarImage;

    @NotBlank(message = "電子信箱不可為空!")
    @Email(message = "電子信箱格式錯誤!")
    @Column(unique = true)
    private String email;
}
