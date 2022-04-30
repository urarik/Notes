package com.urarik.notes_server;

import com.urarik.notes_server.message.Invitation;
import com.urarik.notes_server.message.InvitationRepository;
import com.urarik.notes_server.message.dto.InvitationRequest;
import com.urarik.notes_server.message.MessageService;
import com.urarik.notes_server.message.dto.Decision;
import com.urarik.notes_server.objects.TestObjects;
import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.project.ProjectService;
import com.urarik.notes_server.security.User;
import com.urarik.notes_server.security.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static com.urarik.notes_server.objects.TestObjects.getSet;


@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @InjectMocks
    MessageService messageService;

    @Mock
    UserService userService;
    @Mock
    ProjectService projectService;
    @Mock
    InvitationRepository invitationRepository;

    @Test
    void invite() {
        Long pid = 2L;
        List<String> users = List.of("dinesh", "arnav");
        InvitationRequest invitationRequest = new InvitationRequest(
                users,
                pid
        );
        when(projectService.findByPidForOnlyOwner(pid, "anamika"))
                .thenReturn(Optional.ofNullable((Project) TestObjects.get("p1")));
        when(userService.findByUserNameIn(users)).thenReturn(getSet(users));
        when(userService.findUser("anamika")).thenReturn(Optional.ofNullable((User) TestObjects.get("anamika")));
        when(projectService.isMember(pid, "dinesh")).thenReturn(true);
        when(projectService.isMember(pid, "arnav")).thenReturn(false);

        messageService.invite(invitationRequest, "anamika");

        ArgumentCaptor<List<Invitation>> captor = ArgumentCaptor.forClass(List.class);
        verify(invitationRepository).saveAll(captor.capture());
        List<Invitation> capturedList = captor.getValue();
        assertThat(capturedList, hasSize(1));
        assertThat(capturedList.get(0).getReceiver().getUsername(), is("arnav"));
    }

    @Test
    void process_invitation() {
        Long iid = 1L;
        Decision decision = new Decision(iid, true);
        //TODO
    }

}
