package ntou.auction.spring.chat.repository;


import jakarta.validation.constraints.NotNull;
import ntou.auction.spring.chat.data.entity.ChatMessageStatus;
import ntou.auction.spring.chat.data.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, JpaSpecificationExecutor<ChatMessage> {

    List<ChatMessage> findByChatId(@NotNull Long chatId);

    List<ChatMessage> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    Long countBySenderIdAndReceiverIdAndAndChatMessageStatus(Long senderId, Long receiverId, ChatMessageStatus ChatMessageStatus);

}
