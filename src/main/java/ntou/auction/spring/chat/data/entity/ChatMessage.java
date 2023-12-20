package ntou.auction.spring.chat.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ntou.auction.spring.data.entity.AbstractEntity;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage extends AbstractEntity {
    private Long senderId;
    private Long receiverId;
    private String senderUserName;
    private String receiverUserName;
    private Long chatId;
    private String content;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private ChatMessageStatus chatMessageStatus;
}
