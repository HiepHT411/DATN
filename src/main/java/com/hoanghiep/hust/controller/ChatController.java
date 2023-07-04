package com.hoanghiep.hust.controller;

import com.hoanghiep.hust.entity.ChatMessage;
import com.hoanghiep.hust.entity.User;
import com.hoanghiep.hust.repository.ChatMessageRepository;
import com.hoanghiep.hust.serviceImpl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/livechat")
    public String getLiveChatPage(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        log.info("user {} go to live chat page", user);
        return "livechat";
    }
    /*
     * a message with destination /app/chat.sendMessage will be routed to the sendMessage() method,
     * and a message with destination /app/chat.addUser will be routed to the addUser() method.
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public List<ChatMessage> sendMessage(@Payload ChatMessage chatMessage) {
        ChatMessage message = chatMessageRepository.save(chatMessage);
        if(message.getType().equals(ChatMessage.MessageType.JOIN)) {
            log.info("User {} joined live chat", message.getSender());
        }
        return Collections.singletonList(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public List<ChatMessage> addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessageRepository.save(chatMessage);

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByOrderByIdAsc();
        return chatMessageList;
    }

}
