package ntou.auction.spring.security;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "帳號不可為空!")
    @Length(min = 1, max = 32, message = "帳號長度限制為1~32")
    @Column(unique = true)
    private String username;

    @NotNull(message = "暱稱不可為空!")
    @Length(min = 1, max = 32)
    private String name;

    @NotBlank(message = "密碼不可為空!")
    private String password;

    // if avatar is more than 5MB, need to modify column length
    @Lob
    @Column(length = 5242880)
    private byte[] avatarImage;

    @NotBlank(message = "電子信箱不可為空!")
    @Email(message = "電子信箱格式錯誤!")
    @Column(unique = true)
    private String email;
}
