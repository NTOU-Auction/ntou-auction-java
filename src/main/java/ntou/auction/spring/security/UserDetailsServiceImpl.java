package ntou.auction.spring.security;

import ntou.auction.spring.data.entity.User;
import ntou.auction.spring.data.service.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(role ->
                        new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User
                    (user.getUsername(), user.getHashedPassword(),
                            user.isEnabled(),
                            user.isAccountNonExpired(),
                            user.isCredentialsNonExpired(),
                            user.isAccountNonLocked()
                            , getAuthorities(user));
        } else {
            throw new UsernameNotFoundException("No user present with " +
                    "username: " + username);
        }
    }

}
