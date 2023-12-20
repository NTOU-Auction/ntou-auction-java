package ntou.auction.spring.chat.data.service;

import ntou.auction.spring.chat.data.entity.ChatMessage;
import ntou.auction.spring.chat.data.entity.ChatMessageStatus;
import ntou.auction.spring.chat.exception.MessageNotFound;
import ntou.auction.spring.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;


    public ChatMessageService(ChatMessageRepository repository, ChatRoomService chatRoomService) {
        this.repository = repository;
        this.chatRoomService = chatRoomService;
    }

    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setChatMessageStatus(ChatMessageStatus.RECEIVED);
        repository.save(chatMessage);
        return chatMessage;
    }

    public Long countNewMessages(Long senderId, Long receiverId) {
        return repository.countBySenderIdAndReceiverIdAndAndChatMessageStatus(
                senderId, receiverId, ChatMessageStatus.RECEIVED);
    }

    public List<ChatMessage> findChatMessages(Long senderId, Long receiverId) {
        Optional<Long> chatId = chatRoomService.getChatId(senderId, receiverId, false);

        if(chatId.isEmpty()){
            return new ArrayList<>();
        }

        List<ChatMessage> messages =
                chatId.map(repository::findByChatId).orElse(new ArrayList<>());

        if(!messages.isEmpty()) {
            updateStatuses(senderId, receiverId, ChatMessageStatus.DELIVERED);
        }

        return messages;
    }

    public ChatMessage findById(Long id) {
        return repository
                .findById(id)
                .map(chatMessage -> {
                    chatMessage.setChatMessageStatus(ChatMessageStatus.DELIVERED);
                    return repository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new MessageNotFound("無法找到 ID為 " + id + " 的聊天紀錄"));
    }

    public void updateStatuses(Long senderId, Long receiverId, ChatMessageStatus status) {
        List<ChatMessage> chatMessages = repository.findBySenderIdAndReceiverId(senderId,receiverId);
        for(ChatMessage chatMessage:chatMessages){
            chatMessage.setChatMessageStatus(status);
            repository.save(chatMessage);
        }
    }

    public Set<Long> getContact(Long userId){
        List<ChatMessage> sendByUser = repository.findAllBySenderId(userId);
        List<ChatMessage> receiveByUser = repository.findAllByReceiverId(userId);
        Set<Long> contact = new HashSet<>();
       for(ChatMessage message: sendByUser){
           contact.add(message.getReceiverId());
       }
       for(ChatMessage message: receiveByUser){
            contact.add(message.getSenderId());
       }
       return contact;
    }
}
