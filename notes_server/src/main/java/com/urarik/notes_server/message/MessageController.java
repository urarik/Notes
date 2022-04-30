package com.urarik.notes_server.message;


import com.urarik.notes_server.message.dto.Decision;
import com.urarik.notes_server.message.dto.InvitationRequest;
import com.urarik.notes_server.security.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class MessageController {
    private final MessageService messageService;
    private final UserInfo userInfo;

    public MessageController(MessageService messageService, UserInfo userInfo) {
        this.messageService = messageService;
        this.userInfo = userInfo;
    }

    @PostMapping("/messages/invite")
    public ResponseEntity<Object> inviteUsers(@RequestBody InvitationRequest invitationRequest) {
        try {
            messageService.invite(invitationRequest, userInfo.getUsername());
            return ResponseEntity.ok().build();
        }catch (NoSuchElementException | InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, List<Object>>> getMessages() {
        Map<String, List<Object>> model = messageService.getMessageMap(userInfo.getUsername());

        return ResponseEntity.ok(model);
    }

    @PostMapping("/messages/decide")
    public ResponseEntity<Object> decideInvitation(@RequestBody Decision decision) {
        if(messageService.processInvitation(decision, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }
}
