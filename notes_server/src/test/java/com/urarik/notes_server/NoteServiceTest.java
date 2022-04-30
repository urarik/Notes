package com.urarik.notes_server;

import com.urarik.notes_server.note.*;
import com.urarik.notes_server.note.dto.FullNote;
import com.urarik.notes_server.objects.TestObjects;
import com.urarik.notes_server.project.ProjectRepository;
import com.urarik.notes_server.project.ProjectService;
import com.urarik.notes_server.security.UserService;
import com.urarik.notes_server.note.NoteRepository.NoteView;
import com.urarik.notes_server.note.BlockRepository.*;
import com.urarik.notes_server.note.TextRepository.TextView;
import com.urarik.notes_server.view.UserNameView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static com.urarik.notes_server.objects.TestObjects.getSet;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {
    @InjectMocks
    NoteService noteService;

    @Mock
    NoteRepository noteRepository;
    @Mock
    ProjectService projectService;
    @Mock
    UserService userService;
    @Mock
    BlockRepository blockRepository;
    @Mock
    TextRepository textRepository;

    // TODO
    // https://stackoverflow.com/questions/47258103/mock-projection-result-spring-data-jpa
    // View Factory를 만들자
    @Test
    void get_full_note_by_nid() {
        Long nid = 2L;
        when(projectService.isMember(anyLong(), anyString())).thenReturn(true);

        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        NoteView noteView = new NoteView() {
            @Override
            public Long getNid() {
                return null;
            }

            @Override
            public Long getSequence() {
                return null;
            }

            @Override
            public Boolean getIsMain() {
                return null;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public ProjectRepository.PidView getBelongsTo() {
                return null;
            }

            @Override
            public List<UserNameView> getAdmins() {
                return null;
            }
        };
        when(noteRepository.findNoteViewByNid(nid)).thenReturn(Optional.of(noteView));
        when(blockRepository.findByNid(nid))
                .thenReturn(List.of((BlockView)TestObjects.get("b1"), (BlockView) TestObjects.get("b2")));
        when(textRepository.findByBid(anyLong()))
                .thenReturn(Optional.ofNullable((TextView) TestObjects.get("t1")),
                            Optional.ofNullable((TextView) TestObjects.get("t2")));

        Optional<FullNote> fullNote = noteService.getMainFullNote(1L, "dinesh");

        assertThat(fullNote.isPresent(), is(true));
        assertThat(fullNote.get().getContentList(), hasSize(2));


    }

}
