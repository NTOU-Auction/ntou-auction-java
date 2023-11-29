package ntou.auction.spring.chat.config;

import io.micrometer.common.util.StringUtils;
import ntou.auction.spring.security.JWTService;
import ntou.auction.spring.security.UserDetailsServiceImpl;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthChannelInterceptor(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // 第一次連線
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 驗證token
            List<String> header = accessor.getNativeHeader("Authorization");
            // header裡面有沒有token
            if (header != null && !header.isEmpty()) {
                String token = header.get(0);
                if (StringUtils.isNotBlank(token)) {
                    String username = JWTService.validateTokenAndGetUsername(token);
                    System.out.println("username:" + username);
                    // token有效
                    if (username != null) {
                        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                        Authentication authentication =
                                new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
                        accessor.setUser(authentication);
                        return message;
                    }else{
                        System.out.println("WebSocket連線驗證失敗");
                        throw new AccessDeniedException("WebSocket連線驗證失敗");
                    }
                }
            }else{
                System.out.println("WebSocket連線驗證失敗");
                throw new AccessDeniedException("WebSocket連線驗證失敗");
            }
        }
        System.out.println("message:" + message);
        // 非第一次連線，不用驗證
        return message;
    }
}
