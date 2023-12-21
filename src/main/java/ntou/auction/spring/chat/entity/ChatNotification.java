package ntou.auction.spring.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntou.auction.spring.util.AbstractEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatNotification extends AbstractEntity {
    private Long id;
    private Long senderId;
    private String senderName;
}
