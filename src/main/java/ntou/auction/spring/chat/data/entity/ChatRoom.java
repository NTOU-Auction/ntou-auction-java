package ntou.auction.spring.chat.data.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntou.auction.spring.data.entity.AbstractEntity;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends AbstractEntity {
    private Long chatId;
    private Long senderId;
    private Long receiverId;
}
