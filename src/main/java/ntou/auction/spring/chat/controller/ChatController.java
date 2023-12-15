package ntou.auction.spring.chat.controller;


import ntou.auction.spring.chat.data.entity.ChatMessage;
import ntou.auction.spring.chat.data.entity.ChatNotification;
import ntou.auction.spring.chat.data.service.ChatMessageService;
import ntou.auction.spring.chat.data.service.ChatRoomService;
import ntou.auction.spring.data.service.UserIdentity;
import ntou.auction.spring.data.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final UserService userService;

    private final UserIdentity userIdentity;

    private final SimpMessagingTemplate messageTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    public ChatController(UserService userService, UserIdentity userIdentity, SimpMessagingTemplate messageTemplate, ChatMessageService chatMessageService, ChatRoomService chatRoomService) {
        this.userService = userService;
        this.userIdentity = userIdentity;
        this.messageTemplate = messageTemplate;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        /*
        User sender = userService.findByUsername(userIdentity.getUsername());
        ChatMessage message = new ChatMessage();
        message.setSenderId(sender.getId());
        message.setReceiverId(chatMessageRequest.getReceiverId());
        message.setContent(chatMessageRequest.getContent());
        */

        // token無效
        if (principal.getName() == null) {
            return;
        }
        // 傳給不存在的使用者
        if (userService.get(chatMessage.getReceiverId()).isEmpty()) {
            return;
        }

        Optional<Long> chatId = chatRoomService.getChatId(userService.findByUsername(principal.getName()).getId(), chatMessage.getReceiverId(), true);
        System.out.println(chatId);
        chatId.ifPresent(chatMessage::setChatId);
        chatMessage.setSenderId(userService.findByUsername(principal.getName()).getId());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setSenderUserName(principal.getName());
        chatMessage.setReceiverUserName(userService.get(chatMessage.getReceiverId()).get().getUsername());
        ChatMessage saved = chatMessageService.save(chatMessage);

        messageTemplate.convertAndSendToUser(saved.getReceiverId().toString(), "/queue/messages", new ChatNotification(
                saved.getId(),
                saved.getSenderId(),
                principal.getName()
        ));
    }

    @GetMapping("/api/v1/chat/message/{id}")
    public ResponseEntity<?> findMessage(@PathVariable Long id) {
        return ResponseEntity
                .ok(chatMessageService.findById(id));
    }

    @GetMapping("/api/v1/chat/messages/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(
            @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.countNewMessages(userService.findByUsername(userIdentity.getUsername()).getId(), Long.parseLong(recipientId)));
    }

    @GetMapping("/api/v1/chat/messages/{recipientId}")
    public ResponseEntity<?> findChatMessages(
            @PathVariable Long recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(userService.findByUsername(userIdentity.getUsername()).getId(), recipientId));
    }

    @GetMapping("/api/v1/chat/contact")
    public ResponseEntity<?> findContact() {
        System.out.println(userIdentity.getUsername());
        return ResponseEntity
                .ok(chatMessageService.getContact(userService.findByUsername(userIdentity.getUsername()).getId()));
    }
}
