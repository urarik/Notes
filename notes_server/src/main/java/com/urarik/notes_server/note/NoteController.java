package com.urarik.notes_server.note;

import com.urarik.notes_server.note.dto.Content;
import com.urarik.notes_server.note.dto.FullNote;
import com.urarik.notes_server.note.dto.Ids;
import com.urarik.notes_server.project.dto.Roles;
import com.urarik.notes_server.security.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.InvalidParameterException;
import java.util.*;

//TODO repository 분리
@RestController
public class NoteController {
    private final NoteService noteService;
    private final UserInfo userInfo;

    public NoteController(NoteService noteService, UserInfo userInfo) {
        this.noteService = noteService;
        this.userInfo = userInfo;
    }

    @GetMapping("/notes/main")
    public ResponseEntity<Map<Object, Object>> getMainNote(@RequestParam("pid") Long pid) {
        try {
            Optional<FullNote> fullNote = noteService.getMainFullNote(pid, userInfo.getUsername());
            Map<Object, Object> model = new HashMap<>();
            if(fullNote.isPresent()) {
                model.put("note", fullNote.get().getNoteView());
                model.put("contents", fullNote.get().getContentList());
            }
            return ResponseEntity.ok(model);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/note")
    public ResponseEntity<Map<Object, Object>> getNote(@RequestParam("pid") Long pid,
                                                       @RequestParam("nid") Long nid) {
        try {
            Optional<FullNote> fullNote = noteService.getFullNoteByNid(nid, pid, userInfo.getUsername());
            Map<Object, Object> model = new HashMap<>();
            if(fullNote.isPresent()) {
                model.put("note", fullNote.get().getNoteView());
                model.put("contents", fullNote.get().getContentList());
            }
            return ResponseEntity.ok(model);
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/notes")
    public ResponseEntity<Map<Object, Object>> getNotes(@RequestParam("pid") Long pid) {
        try {
            List<NoteRepository.NoteView> noteList = noteService.getNoteList(pid, userInfo.getUsername());
            Map<Object, Object> model = new HashMap<>();
            model.put("notes", noteList);
            return ResponseEntity.ok(model);
        } catch(InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/note/create")
    public ResponseEntity<Object> createNote(@RequestBody Note note) {
        if (noteService.create(note, note.getPid(), userInfo.getUsername()))
            return ResponseEntity.created(URI.create("")).build();
        else return ResponseEntity.badRequest().build();
    }

    @GetMapping("/note/delete")
    public ResponseEntity<Object> deleteNote(@RequestParam("nid") Long nid) {
        if(noteService.delete(nid, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    //TODO admins, members에 동시에 속하는 경우 처리 해야 함
    @PostMapping("/note/update/add/roles")
    public ResponseEntity<Object> addNoteRoles(@RequestBody Roles roles) {
        if(noteService.addRoles(roles, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/note/update/delete/roles")
    public ResponseEntity<Object> deleteNoteRoles(@RequestBody Roles roles) {
        if(noteService.deleteRoles(roles, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/note/update")
    public ResponseEntity<Object> updateNote(@RequestBody Note note) {
        if(noteService.update(note, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @Transactional
    @PostMapping("/notes/update")
    public ResponseEntity<Object> updateNotes(@RequestBody List<Note> noteList) {
        for(Note note: noteList) {
            if(!noteService.update(note, userInfo.getUsername()))
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/notes/update/main")
    public ResponseEntity<Object> updateMainNote(@RequestBody Ids ids) {
        if(noteService.updateMainNote(ids, userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/note/block/delete")
    public ResponseEntity<Object> deleteBlock(@RequestBody Ids bid) {
        if(noteService.deleteBlock(bid.getBid(), userInfo.getUsername()))
            return ResponseEntity.ok().build();
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/note/block/add")
    public ResponseEntity<Object> addBlock(@RequestBody Content content) {
        Long bid = noteService.addBlock(content, userInfo.getUsername());
        if(bid != 0)
            return ResponseEntity.ok(bid);
        else return ResponseEntity.badRequest().build();
    }

    @PostMapping("/note/block/update")
    public ResponseEntity<Object> updateBlock(@RequestBody Content content) {
        try {
            if (noteService.updateBlock(content, userInfo.getUsername()))
                return ResponseEntity.ok().build();
            else return ResponseEntity.badRequest().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/note/block/up")
    public ResponseEntity<Boolean> upBlock(@RequestBody Ids ids) {
        try {
            if (noteService.upBlock(ids.getNid(), ids.getBid(), userInfo.getUsername()))
                return ResponseEntity.ok().build();
            else return ResponseEntity.badRequest().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/note/block/down")
    public ResponseEntity<Boolean> downBlock(@RequestBody Ids ids) {
        try {
            if (noteService.downBlock(ids.getNid(), ids.getBid(), userInfo.getUsername()))
                return ResponseEntity.ok().build();
            else return ResponseEntity.badRequest().build();
        } catch (InvalidParameterException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
