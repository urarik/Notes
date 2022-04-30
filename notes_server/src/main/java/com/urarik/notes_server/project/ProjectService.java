package com.urarik.notes_server.project;

import com.urarik.notes_server.project.dto.Roles;
import com.urarik.notes_server.security.User;
import com.urarik.notes_server.project.ProjectRepository.ProjectView;
import com.urarik.notes_server.security.UserService;
import com.urarik.notes_server.view.UserNameView;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProjectService {
    ProjectRepository projectRepository;
    UserService userService;

    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    List<ProjectView> getProjectList(String userName) {
        return projectRepository.findByUsername(userName);
    }

    public boolean create(Project project, String userName) {
        Optional<User> owner =  userService.findUser(userName);
        if(owner.isPresent()) {
            project.setOwner(owner.get());
            // non-null column 검사 필요
            projectRepository.save(project);
            return true;
        } else return false;
    }

    boolean isOwnerOrAdmin(Project project, String userName) {
        Optional<User> userOpt = userService.findUser(userName);
        if(userOpt.isEmpty()) return false;

        return project.getOwner().getUsername().equals(userOpt.get().getUsername()) ||
                project.getAdmin().contains(userOpt.get());
    }

    public boolean isMember(Long pid, String userName) {
        Set<Object> pidSet = projectRepository.findPidByUsername(userName);;
        return pidSet.contains(pid);
    }

    public boolean isOwner(Long pid, String userName) {
        Optional<Project> project = projectRepository.findByPid(pid);
        if(project.isEmpty()) return false;

        return userName.equals(project.get().getOwner().getUsername());
    }

    public boolean update(Project project, String userName) {
        if(isOwnerOrAdmin(project, userName)) {
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    public Optional<Project> findByPidForOnlyOwner(Long pid, String userName) {
        Optional<Project> project = projectRepository.findByPid(pid);
        return project.filter(p -> userName.equals(p.getOwner().getUsername()));
    }

    public Optional<Project> findByPidForOnlyOwnerOrAdmin(Long pid, String userName) {
        Optional<Project> project = projectRepository.findByPid(pid);

        return project.filter(p -> userName.equals(p.getOwner().getUsername()) ||
                p.getAdmin().stream().anyMatch(user -> userName.equals(user.getUsername())));
    }

    public void delete(Long pid, String userName) {
        if(isOwner(pid, userName))
            projectRepository.deleteById(pid);
    }

    private boolean modifyRoles(Roles roles, String userName, Modifier<Set<User>, Project> action) {
        Set<User> admins = userService.findByUserNameIn(roles.getAdmins());
        Set<User> members = userService.findByUserNameIn(roles.getMembers());

        Optional<Project> oldProject = findByPidForOnlyOwner(roles.getId(), userName);
        System.out.println(roles.getId());
        if(oldProject.isEmpty())
            return false;
        System.out.println(1);
        return action.accept(admins, members, oldProject.get());
    }

    public boolean addRoles(Roles roles, String userName) {
        Modifier<Set<User>, Project> action = (admins, members, oldProject) -> {
            if (oldProject.getAdmin() != null) admins.addAll(oldProject.getAdmin());
            if (oldProject.getMember() != null) members.addAll(oldProject.getMember());

            Set<User> intersection = new HashSet<>(admins);
            intersection.retainAll(members);
            if(!intersection.isEmpty()) {
                intersection.forEach(members::remove);
            }

            admins.remove(oldProject.getOwner());
            members.remove(oldProject.getOwner());

            Project newProject = new Project(
                    oldProject.getPid(),
                    oldProject.getOwner(),
                    admins,
                    members,
                    oldProject.getTitle()
            );

            return update(newProject, userName);
        };

        return modifyRoles(roles, userName, action);
    }

    public boolean deleteRoles(Roles roles, String userName) {
        Modifier<Set<User>, Project> action = (admins, members, oldProject) -> {
            Set<User> newAdmins = oldProject.getAdmin();
            Set<User> newMembers = oldProject.getMember();
            if(newAdmins != null && admins != null) newAdmins.removeAll(admins);
            if(newMembers != null && members != null) newMembers.removeAll(members);

            Project newProject = new Project(
                    oldProject.getPid(),
                    oldProject.getOwner(),
                    newAdmins,
                    newMembers,
                    oldProject.getTitle()
            );

            return update(newProject, userName);
        };

        return modifyRoles(roles, userName, action);
    }

    public void exit(Long pid, String userName) {
        Project project = projectRepository.findByPid(pid).orElseThrow();
        User user = userService.findUser(userName).orElseThrow();

        if(project.getOwner().equals(user)) throw new InvalidParameterException();

        if(project.getMember() != null) project.getMember().remove(user);
        if(project.getAdmin() != null) project.getAdmin().remove(user);

        projectRepository.save(project);
    }

    public Set<UserNameView> getUsersExceptMembersLike(Long pid, String prefix, String userName) {
        Project project = projectRepository.findByPid(pid).orElseThrow();
        if(!isOwnerOrAdmin(project, userName)) throw new InvalidParameterException();

        Set<UserNameView> userSet = userService.findByUserNameLike(prefix);
        Set<UserNameView> memberSet = new HashSet<>(toUserNameViewSet(project.getMember()));
        memberSet.addAll(toUserNameViewSet(project.getAdmin()));
        memberSet.addAll(toUserNameViewSet(Set.of(project.getOwner())));

        userSet.removeAll(memberSet);
        return userSet;
    }

    public Set<UserNameView> getMembersLike(Long pid, String prefix, String userName) {
        Project project = projectRepository.findByPid(pid).orElseThrow();
        if(!isOwner(pid, userName)) throw new InvalidParameterException();

        return toUserNameViewSet(project.getMember().stream().filter(
                user -> user.getUsername().startsWith(prefix)
        ).collect(Collectors.toSet()));
    }

    private Set<UserNameView> toUserNameViewSet(Set<User> userSet) {
        return userSet.stream().map(user -> new UserNameView() {
            @Override
            public String getUsername() {
                return user.getUsername();
            }
        }).collect(Collectors.toSet());
    }


    @FunctionalInterface
    public interface Modifier<T, U> {
        boolean accept(T a, T b, U c);
    }
}
