package com.urarik.notes_server.message;

import com.urarik.notes_server.message.dto.Decision;
import com.urarik.notes_server.message.dto.InvitationRequest;
import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.project.ProjectService;
import com.urarik.notes_server.security.User;
import com.urarik.notes_server.message.InvitationRepository.InvitationView;
import com.urarik.notes_server.security.UserService;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MessageService {
    private final UserService userService;
    private final InvitationRepository invitationRepository;
    private final ProjectService projectService;

    public MessageService(UserService userService, InvitationRepository invitationRepository, ProjectService projectService) {
        this.userService = userService;
        this.invitationRepository = invitationRepository;
        this.projectService = projectService;
    }

    public void invite(InvitationRequest request, String userName) {
        Project project = projectService.findByPidForOnlyOwner(request.getId(), userName).orElseThrow();

        Set<User> targets = userService.findByUserNameIn(request.getUserNames());
        if(targets.size() != request.getUserNames().size())
            throw new InvalidParameterException();

        User sender = userService.findUser(userName).orElseThrow();

        List<Invitation> invitationList = targets.stream()
                .filter(target -> !projectService.isMember(request.getId(), target.getUsername()))
                .map(target -> new Invitation(sender, target, project))
                .collect(Collectors.toList());
        invitationRepository.saveAll(invitationList);
    }

    public Map<String, List<Object>> getMessageMap(String userName) {
        HashMap<String, List<Object>> map = new HashMap<>();

        map.put("Invitation", new ArrayList<>());
        invitationRepository.findInvitationByReceiver(userName).forEach(
                invitationView -> map.get("Invitation").add(MessageFactory.of(invitationView))
        );

        return map;
    }

    public boolean processInvitation(Decision decision, String userName) {
        InvitationView invitationView = invitationRepository.findInvitationByIid(decision.getIid()).orElse(null);
        if(invitationView == null) return false;
        if(!invitationView.getReceiver().getUsername().equals(userName)) return false;

        Project project = projectService.findByPidForOnlyOwnerOrAdmin(
                invitationView.getProject().getPid(),
                invitationView.getSender().getUsername()).orElse(null);
        if(project == null) return false;

        invitationRepository.deleteById(decision.getIid());
        if(decision.getAccept()) {
            User user = userService.findUser(userName).orElse(null);
            if(user == null) return false;
            project.getMember().add(user);
            projectService.update(project, invitationView.getSender().getUsername());
        }
        return true;
    }
}
