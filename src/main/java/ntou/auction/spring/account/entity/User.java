package ntou.auction.spring.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntou.auction.spring.util.AbstractEntity;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends AbstractEntity implements UserDetails {

    @Length(min = 1, max = 128, message = "帳號長度限制為1~32位!")
    @Column(unique = true)
    private String username;

    @Length(min = 1, max = 128, message = "暱稱長度限制為1~32位!")
    private String name;

    @JsonIgnore
    @NotBlank(message = "密碼不可為空!")
    private String hashedPassword;

    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    // if avatar is more than 5MB, need to modify column length
    @Lob
    @Column(length = 5242880)
    private byte[] avatarImage;

    private String avatarImageName;

    @ElementCollection
    @CollectionTable(name = "favorite_products")
    private Set<Long> favoriteProducts;

    @NotBlank(message = "電子信箱不可為空!")
    @Email(message = "電子信箱格式錯誤!")
    @Column(unique = true)
    private String email;

    private boolean enabled;

    private boolean isAccountNonExpired;

    private boolean isAccountNonLocked;

    private boolean isCredentialsNonExpired;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.hashedPassword;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ADMIN);
    }

    public void setAdmin(boolean bool) {
        if (!bool && isAdmin()) {
            roles.remove(Role.ADMIN);
        } else if (bool && !isAdmin()) {
            roles.add(Role.ADMIN);
        }
    }

    public @NotNull String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }
}
