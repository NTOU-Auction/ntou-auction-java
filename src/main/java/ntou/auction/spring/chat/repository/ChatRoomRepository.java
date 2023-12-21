package ntou.auction.spring.chat.repository;

import ntou.auction.spring.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>,
        JpaSpecificationExecutor<ChatRoom> {

    Optional<ChatRoom> findChatRoomBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
