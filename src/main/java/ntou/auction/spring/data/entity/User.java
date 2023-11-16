package ntou.auction.spring.data.entity;

import jakarta.validation.constraints.NotBlank;
import ntou.auction.spring.data.Role;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @NotBlank(message = "密碼不可為空!")
    private String hashedPassword;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    // if avatar is more than 5MB, need to modify column length
    @Lob
    @Column(length = 5242880)
    private byte[] avatarImage;

    private String avatarImageName;

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
