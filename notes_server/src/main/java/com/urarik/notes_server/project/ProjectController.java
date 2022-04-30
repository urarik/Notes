package com.urarik.notes_server.project;

import com.urarik.notes_server.note.dto.Ids;
import com.urarik.notes_server.project.dto.Roles;
import com.urarik.notes_server.security.UserInfo;
import com.urarik.notes_server.project.ProjectRepository.ProjectView;
import com.urarik.notes_server.security.UserService;
import com.urarik.notes_server.view.UserNameView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.InvalidParameterException;
import java.util.*;

//TODO controller와 service의 분리
@RestController
public class ProjectController {
    UserInfo userInfo;
    ProjectService projectService;
    UserService userService;

    public ProjectController(UserInfo userInfo, ProjectService projectService, UserService userService) {
        this.userInfo = userInfo;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/projects")
    public ResponseEntity<Map<Object, Object>> getProjects() {
        Map<Object, Object> model = new HashMap<>();
        List<ProjectView> projects = projectService.getProjectList(userInfo.getUsername());

        model.put("projects", projects);
        return ResponseEntity.ok(model);
    }

    @PostMapping("/projects/create")
    public ResponseEntity<Object> createProject(@RequestBody Project project) {
        if(projectService.create(project, userInfo.getUsername())) {
            return ResponseEntity.created(URI.create("")).build();
        } else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/projects/update")
    public ResponseEntity<Object> updateProject(@RequestBody Project project) {
        Optional<Project> oldProject = projectService.findByPidForOnlyOwner(project.getPid(), userInfo.getUsername());

        if(oldProject.isPresent()) {
            projectService.update(project, userInfo.getUsername());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/projects/delete")
    public ResponseEntity<Object> deleteProject(@RequestBody Project project) {
        Long pid = project.getPid();
        Optional<Project> oldProject = projectService.findByPidForOnlyOwner(pid, userInfo.getUsername());

        if(oldProject.isPresent()) {
            projectService.delete(pid, userInfo.getUsername());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/projects/update/add/roles")
    public ResponseEntity<Object> addProjectRoles(@RequestBody Roles roles) {
        if(projectService.addRoles(roles, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/projects/update/delete/roles")
    public ResponseEntity<Object> deleteProjectRoles(@RequestBody Roles roles) {
        if(projectService.deleteRoles(roles, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/projects/exit")
    public ResponseEntity<Object> exitProject(@RequestBody Ids ids) {
        try {
            projectService.exit(ids.getPid(), userInfo.getUsername());
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body("owner는 프로젝트 삭제 기능을 이용해주세요.");
        }
    }

    @GetMapping("/project/invite")
    public ResponseEntity<Object> getUserList(
            @RequestParam(value = "pid") Long pid,
            @RequestParam(value = "prefix") String prefix) {
        System.out.println("!");
        try {
            Set<UserNameView> userSet =
                    projectService.getUsersExceptMembersLike(pid, prefix, userInfo.getUsername());
            return ResponseEntity.ok(userSet);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body("owner나 admin이 아니면 불가능!");
        }
    }
    
    @GetMapping("/project/grant")
    public ResponseEntity<Object> getMemberList(@RequestParam Long pid, @RequestParam String prefix) {
        try {
            Set<UserNameView> memberSet =
                    projectService.getMembersLike(pid, prefix, userInfo.getUsername());
            return ResponseEntity.ok(memberSet);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().body("owner가 아니면 불가능!");
        }
    }

}
