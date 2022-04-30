package com.urarik.notes_server.message;

import com.urarik.notes_server.message.InvitationRepository.InvitationView;

import java.util.HashMap;
import java.util.Map;

public class MessageFactory {
    public static Map<String, Object> of(InvitationView invitationView) {
        String title = invitationView.getProject().getTitle() + " 초대장";
        String sender = invitationView.getSender().getUsername();
        String content = sender + "님의 " + title;
        Long id = invitationView.getIid();

        return transform(id, title, content, sender);
    }

    private static Map<String, Object> transform(Long id, String title, String content, String sender) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("content", content);
        map.put("sender", sender);

        return map;
    }
}
