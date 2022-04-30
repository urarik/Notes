package com.urarik.notes_server.note;

import com.urarik.notes_server.note.dto.Content;
import com.urarik.notes_server.note.dto.FullNote;
import com.urarik.notes_server.note.dto.Ids;
import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.project.ProjectService;
import com.urarik.notes_server.note.NoteRepository.NoteView;
import com.urarik.notes_server.project.dto.Roles;
import com.urarik.notes_server.security.User;
import com.urarik.notes_server.security.UserService;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.BiFunction;

import com.urarik.notes_server.note.BlockRepository.*;

@Component
public class NoteService {
    private final NoteRepository noteRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final BlockRepository blockRepository;
    private final TextRepository textRepository;

    public NoteService(NoteRepository noteRepository, ProjectService projectService, UserService userService, BlockRepository blockRepository, TextRepository textRepository) {
        this.noteRepository = noteRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.blockRepository = blockRepository;
        this.textRepository = textRepository;
    }

    public List<NoteRepository.NoteView> getNoteList(Long pid, String userName) {
        if(!projectService.isMember(pid, userName))
            throw new InvalidParameterException();

        return noteRepository.findByPid(pid);
    }

    private boolean isNoteAdmin(Long nid, String userName) {
        Optional<NoteView> noteView = noteRepository.findNoteViewByNid(nid);
        if(noteView.isEmpty()) return false;

        return noteView.get().getAdmins().stream()
                                         .anyMatch(admin -> userName.equals(admin.getUsername()));
    }

    public Optional<FullNote> getFullNoteByNid(Long nid, Long pid, String userName) {
        if(!projectService.isMember(pid, userName))
            throw new InvalidParameterException();

        Optional<NoteView> noteView = noteRepository.findNoteViewByNid(nid);
        List<Content> contentList = getContentList(nid);

        return noteView.map(noteView1 -> new FullNote(noteView1, contentList));
    }

    public Optional<FullNote> getMainFullNote(Long pid, String userName) {
        if(!projectService.isMember(pid, userName))
            throw new InvalidParameterException();

        Optional<NoteView> noteView = noteRepository.findMainNoteWithView(pid);

        return noteView.map(noteView1 -> {
            List<Content> contentList = getContentList(noteView1.getNid());
            return new FullNote(noteView1, contentList);
        });
    }

    private List<Content> getContentList(Long nid) {
        List<BlockView> blockList = blockRepository.findByNid(nid);
        List<Content> contents = new ArrayList<>();
        for(BlockView block: blockList) {
            Content content;
            if("Text".equals(block.getType())) {
                String text = textRepository.findByBid(block.getBid()).map(
                        TextRepository.TextView::getContent
                ).orElse("");
                content = new Content(block.getBid(), "Text", text);
            }
            else throw new IllegalArgumentException();

            contents.add(content);
        }

        return contents;
    }

    public boolean create(Note note, Long pid, String userName) {
        Optional<User> creator = userService.findUser(userName);
        Optional<Project> project = projectService.findByPidForOnlyOwnerOrAdmin(pid, userName);

        //TODO non-null column checking
        if(creator.isPresent() && project.isPresent()) {
            note.setBelongsTo(project.get());
            if(creator.get() != project.get().getOwner())
                note.setAdmins(Set.of(creator.get(), project.get().getOwner()));
            else
                note.setAdmins(Set.of(creator.get()));
            noteRepository.save(note);

            return true;
        } else return false;
    }

    public boolean delete(Long nid, String userName) {
        if(isNoteAdmin(nid, userName)) {
            noteRepository.deleteById(nid);
            return true;
        }
        else return false;
    }

    private boolean modifyRoles(Roles roles, String userName, BiFunction<Set<User>, Set<User>, Boolean> action) {
        Set<User> newAdmins = userService.findByUserNameIn(roles.getAdmins());
        if(newAdmins.size() != roles.getAdmins().size()) return false;

        Optional<Note> note = noteRepository.findById(roles.getId());
        if(note.isEmpty()) return false;

        if(note.get().getAdmins().stream()
                .anyMatch(admin -> admin.getUsername().equals(userName))) {
            action.apply(note.get().getAdmins(), newAdmins);
            note.get().getAdmins().addAll(newAdmins);
            noteRepository.save(note.get());
            return true;
        } else return false;
    }

    public boolean addRoles(Roles roles, String userName) {
        return modifyRoles(roles, userName, Set::addAll);
    }

    public boolean deleteRoles(Roles roles, String userName) {
        return modifyRoles(roles, userName, Set::removeAll);
    }

    public boolean update(Note note, String userName) {
        Optional<Note> oldNote = noteRepository.findById(note.getNid());
        if(oldNote.isEmpty()) return false;

        if(oldNote.get().getAdmins().stream()
                .anyMatch(admin -> admin.getUsername().equals(userName))) {
            noteRepository.save(note);
            return true;
        } else return false;
    }

    public boolean updateMainNote(Ids ids, String userName) {
        if(!projectService.isOwner(ids.getPid(), userName)) return false;

        noteRepository.findMainNote(ids.getPid()).ifPresent(
                note -> {
                    note.setIsMain(false);
                    noteRepository.save(note);
                }
        );

        noteRepository.findById(ids.getNid()).ifPresent(
                note -> {
                    note.setIsMain(true);
                    noteRepository.save(note);
                }
        );
        return true;
    }

    public boolean deleteBlock(Long bid, String userName) {
        Optional<Block> block = blockRepository.findById(bid);
        if(block.isEmpty()) return false;
        if(block.get().getBelongsTo()
                .getAdmins().stream().anyMatch(admin -> admin.getUsername().equals(userName))) {

            blockRepository.deleteById(bid);
            return true;
        }
        return false;
    }

    public Long addBlock(Content content, String userName) {
        Long nid = content.getId();
        Optional<Note> note = noteRepository.findNoteByNid(nid);
        if(note.isEmpty()) return -1L;
        if(note.get()
                .getAdmins().stream().anyMatch(admin -> admin.getUsername().equals(userName))) {
            Long sequence = blockRepository.findLastSequence(nid).orElse(1L);
            Block block = new Block(note.get(), sequence + 1, content.getType());
            Long bid = blockRepository.save(block).getBid();

            if(content.getType().equals("Text")) {
                Text text = new Text(content.getContent(), block);
                textRepository.save(text);
            }
            return bid;
        }
        return -1L;
    }

    public boolean updateBlock(Content content, String userName) {
        blockRepository.findById(content.getId()).ifPresent(block -> {
            if(block.getBelongsTo().getAdmins().stream().noneMatch(admin -> admin.getUsername().equals(userName))) {
                throw new InvalidParameterException();
            }
            textRepository.findTidByBid(content.getId()).ifPresent( tid -> {
                System.out.println(content.getContent());
                Text newText = new Text(tid, content.getContent(), block);
                textRepository.save(newText);
            });
        });
        return true;
    }

}
