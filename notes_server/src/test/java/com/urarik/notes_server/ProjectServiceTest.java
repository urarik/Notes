package com.urarik.notes_server;

import com.urarik.notes_server.objects.TestObjects;
import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.project.ProjectRepository;
import com.urarik.notes_server.project.ProjectService;
import com.urarik.notes_server.project.dto.Roles;
import com.urarik.notes_server.security.User;
import com.urarik.notes_server.security.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.security.InvalidParameterException;
import java.util.*;

import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static com.urarik.notes_server.objects.TestObjects.getSet;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @InjectMocks
    ProjectService projectService;

    @Mock
    UserService userService;
    @Mock
    ProjectRepository projectRepository;

    @Test
    void create() {
        when(userService.findUser("dinesh"))
                .thenReturn(Optional.ofNullable((User)TestObjects.get("dinesh")));
        Project project = new Project(
                null,
                null,
                null,
                "text"
        );

        projectService.create(project, "dinesh");

        verify(projectRepository).save(project);
        assertThat(project.getOwner().getUsername(), is("dinesh"));
    }

    @Test
    void create_with_null_field() {
        //TODO
    }

    @Test
    void update() {
        when(userService.findUser("dinesh"))
                .thenReturn(Optional.ofNullable((User)TestObjects.get("dinesh")));
        Project project = (Project) TestObjects.get("p3"); // dinesh is an admin

        projectService.update(project, "dinesh");
        verify(projectRepository).save(project);
    }

    @Test
    void update_with_member() {
        when(userService.findUser("dinesh"))
                .thenReturn(Optional.ofNullable((User)TestObjects.get("dinesh")));
        Project project = (Project) TestObjects.get("p2"); // dinesh is a member

        projectService.update(project, "dinesh");
        verify(projectRepository, never()).save(project);
    }

    @Test
    void find_by_pid_only_owner_with_admin() {
        Long pid = 1L;
        when(projectRepository.findByPid(pid)).thenReturn(
                Optional.ofNullable((Project) TestObjects.get("p3"))
        );

        Optional<Project> project = projectService.findByPidForOnlyOwner(pid, "dinesh");

        assertThat(project.isPresent(), is(false));
    }

    @Test
    void find_by_pid_only_owner_with_owner() {
        Long pid = 1L;
        when(projectRepository.findByPid(pid)).thenReturn(
                Optional.ofNullable((Project) TestObjects.get("p1"))
        );

        Optional<Project> project = projectService.findByPidForOnlyOwner(pid, "dinesh");

        assertThat(project.isPresent(), is(true));
    }

    @Test
    void find_by_pid_only_owner_with_member() {
        Long pid = 3L;
        when(projectRepository.findByPid(pid)).thenReturn(
                Optional.ofNullable((Project) TestObjects.get("p2"))
        );

        Optional<Project> project = projectService.findByPidForOnlyOwner(pid, "dinesh");

        assertThat(project.isPresent(), is(false));
    }

    @Test
    void find_by_pid_only_owner_or_admin_with_admin() {
        Long pid = 1L;
        when(projectRepository.findByPid(pid)).thenReturn(
                Optional.ofNullable((Project) TestObjects.get("p3"))
        );

        Optional<Project> project = projectService.findByPidForOnlyOwnerOrAdmin(pid, "dinesh");

        assertThat(project.isPresent(), is(true));
    }

    @Test
    void find_by_pid_only_owner_or_admin_with_owner() {
        Long pid = 1L;
        when(projectRepository.findByPid(pid)).thenReturn(
                Optional.ofNullable((Project) TestObjects.get("p1"))
        );

        Optional<Project> project = projectService.findByPidForOnlyOwnerOrAdmin(pid, "dinesh");

        assertThat(project.isPresent(), is(true));
    }

    @Test
    void find_by_pid_only_owner_or_admin_with_member() {
        Long pid = 3L;
        when(projectRepository.findByPid(pid)).thenReturn(
                Optional.ofNullable((Project) TestObjects.get("p2"))
        );

        Optional<Project> project = projectService.findByPidForOnlyOwnerOrAdmin(pid, "dinesh");

        assertThat(project.isPresent(), is(false));
    }

    @Test
    void add_roles() {
        List<String> members = List.of("dinesh", "rushika");
        List<String> admins = List.of("rushika", "arnav");
        Long pid = 2L;
        Roles roles = new Roles(
                members,
                admins,
                pid
        );
        when(userService.findByUserNameIn(members)).thenReturn(getSet(members));
        when(userService.findByUserNameIn(admins)).thenReturn(getSet(admins));
        when(userService.findUser("arnav")).thenReturn(Optional.ofNullable((User) TestObjects.get("arnav")));
        Project project = new Project(
                (User)TestObjects.get("arnav"),
                null,
                getSet(List.of("dinesh", "anamika")),
                "title"
        );
        when(projectRepository.findByPid(pid)).thenReturn(Optional.of(project));

        boolean result = projectService.addRoles(roles, "arnav");

        assertThat(result, is(true));
        final ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project capturedProject = captor.getValue();
        assertThat(capturedProject.getMember(),
                is(getSet(List.of("dinesh", "anamika"))));
        assertThat(capturedProject.getAdmin(),
                is(getSet(List.of("rushika"))));
    }

    @Test
    void delete_roles() {
        List<String> members = List.of("dinesh", "rushika");
        List<String> admins = List.of("arnav");
        Long pid = 2L;
        Roles roles = new Roles(
                members,
                admins,
                pid
        );
        when(userService.findByUserNameIn(members)).thenReturn(getSet(members));
        when(userService.findByUserNameIn(admins)).thenReturn(getSet(admins));
        when(userService.findUser("arnav")).thenReturn(Optional.ofNullable((User) TestObjects.get("arnav")));
        Project project = new Project(
                (User)TestObjects.get("arnav"),
                null,
                getSet(List.of("dinesh", "anamika")),
                "title"
        );
        when(projectRepository.findByPid(pid)).thenReturn(Optional.of(project));

        boolean result = projectService.deleteRoles(roles, "arnav");

        assertThat(result, is(true));
        final ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project capturedProject = captor.getValue();
        assertThat(capturedProject.getMember(),
                is(getSet(List.of("anamika"))));
        assertNull(capturedProject.getAdmin());
    }

    @Test
    void exit_with_owner() {
        Long pid = 1L;
        Project project = new Project(
                (User)TestObjects.get("arnav"),
                null,
                getSet(List.of("dinesh", "anamika")),
                "title"
        );
        when(userService.findUser("arnav")).thenReturn(Optional.ofNullable((User) TestObjects.get("arnav")));
        when(projectRepository.findByPid(pid)).thenReturn(Optional.of(project));

        InvalidParameterException exception = assertThrows(
                InvalidParameterException.class,
                () -> projectService.exit(pid, "arnav"),
                "Expected exit() to throw, but it didn't"
        );
    }

    @Test
    void exit_with_member() {
        Long pid = 1L;
        Project project = new Project(
                (User)TestObjects.get("arnav"),
                null,
                getSet(List.of("dinesh", "anamika")),
                "title"
        );
        when(userService.findUser("dinesh")).thenReturn(Optional.ofNullable(((User) TestObjects.get("dinesh"))));
        when(projectRepository.findByPid(pid)).thenReturn(Optional.of(project));

        projectService.exit(pid, "dinesh");

        final ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project capturedProject = captor.getValue();
        assertThat(capturedProject.getMember(), hasSize(1));
    }
}
