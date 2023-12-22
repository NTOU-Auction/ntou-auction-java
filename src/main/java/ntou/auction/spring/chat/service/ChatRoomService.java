package ntou.auction.spring.chat.service;

import ntou.auction.spring.chat.entity.ChatRoom;
import ntou.auction.spring.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {
    private final ChatRoomRepository repository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.repository = chatRoomRepository;
    }

    public Optional<Long> getChatId(Long senderId, Long receiverId, boolean createIfNotExist) {

        return repository.findChatRoomBySenderIdAndReceiverId(senderId, receiverId).map(ChatRoom::getChatId).or(() -> {
            if (!createIfNotExist) {
                return Optional.empty();
            }

            String chatId = String.format("%s%s", senderId, receiverId);

            ChatRoom senderRecipient = new ChatRoom();
            senderRecipient.setChatId(Long.parseLong(chatId));
            senderRecipient.setSenderId(senderId);
            senderRecipient.setReceiverId(receiverId);

            repository.save(senderRecipient);

            ChatRoom recipientSender = new ChatRoom();
            recipientSender.setChatId(Long.parseLong(chatId));
            recipientSender.setSenderId(receiverId);
            recipientSender.setReceiverId(senderId);

            repository.save(recipientSender);

            return Optional.of(Long.parseLong(chatId));
        });
    }
}
