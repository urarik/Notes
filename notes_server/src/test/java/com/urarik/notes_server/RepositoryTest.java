package com.urarik.notes_server;

import com.urarik.notes_server.message.Invitation;
import com.urarik.notes_server.message.InvitationRepository;
import com.urarik.notes_server.note.*;
import com.urarik.notes_server.objects.TestObjects;
import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.project.ProjectRepository;
import com.urarik.notes_server.project.ProjectRepository.*;
import com.urarik.notes_server.note.NoteRepository.*;
import com.urarik.notes_server.note.BlockRepository.*;
import com.urarik.notes_server.note.TextRepository.*;
import com.urarik.notes_server.message.InvitationRepository.*;
import com.urarik.notes_server.security.User;
import com.urarik.notes_server.security.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
public class RepositoryTest {
    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    BlockRepository blockRepository;
    @Autowired
    TextRepository textRepository;
    @Autowired
    InvitationRepository invitationRepository;

    @BeforeAll
    void setUp() {
        userRepository.save((User) TestObjects.get("dinesh"));
        userRepository.save((User) TestObjects.get("anamika"));
        userRepository.save((User) TestObjects.get("arnav"));
        userRepository.save((User) TestObjects.get("rushika"));

        projectRepository.save((Project) TestObjects.get("p1"));
        projectRepository.save((Project) TestObjects.get("p2"));
        projectRepository.save((Project) TestObjects.get("p3"));
        projectRepository.save((Project) TestObjects.get("p4"));

        noteRepository.save((Note) TestObjects.get("n1"));
        noteRepository.save((Note) TestObjects.get("n2"));
        noteRepository.save((Note) TestObjects.get("n3"));
        noteRepository.save((Note) TestObjects.get("n4"));

        blockRepository.save((Block) TestObjects.get("b1"));
        blockRepository.save((Block) TestObjects.get("b2"));

        textRepository.save((Text) TestObjects.get("t1"));
        textRepository.save((Text) TestObjects.get("t2"));

        invitationRepository.save((Invitation) TestObjects.get("i1"));
        invitationRepository.save((Invitation) TestObjects.get("i2"));
        invitationRepository.save((Invitation) TestObjects.get("i3"));
    }

    @Nested
    class ProjectRepositoryTest {
        @Test
        public void find_by_username_for_owner() {
            List<ProjectView> lists = projectRepository.findByUsername("anamika");

            assertThat(lists, hasSize(1));
        }

        @Test
        public void find_by_username_for_member() {
            List<ProjectView> lists = projectRepository.findByUsername("dinesh");

            assertThat(lists, hasSize(4));
        }

        @Test
        public void find_by_username_for_admin() {
            List<ProjectView> lists = projectRepository.findByUsername("rushika");

            assertThat(lists, hasSize(2));
        }

        @Test
        public void find_by_pid() {
            Optional<Project> project = projectRepository.findByPid(1L);

            assertThat(project.isPresent(), is(true));
        }

        @Test
        public void find_by_invalid_pid() {
            Optional<Project> project = projectRepository.findByPid(10L);

            assertThat(project.isPresent(), is(false));
        }
    }

    @Nested
    class NoteRepositoryTest {
        @Test
        public void find_main_note() {
            Optional<NoteView> noteView = noteRepository.findMainNoteWithView(2L);

            assertThat(noteView.isPresent(), is(true));
            assertThat(noteView.get().getNid(), is(2L));
        }

        @Test
        public void find_main_note_with_project_dont_have_main_note() {
            Optional<NoteView> noteView = noteRepository.findMainNoteWithView(4L);

            assertThat(noteView.isPresent(), is(false));
        }

        @Test
        public void find_main_note_with_project_dont_have_note() {
            Optional<NoteView> noteView = noteRepository.findMainNoteWithView(3L);

            assertThat(noteView.isPresent(), is(false));
        }

        @Test
        public void find_by_pid() {
            List<NoteView> noteViewList = noteRepository.findByPid(2L);

            assertThat(noteViewList, hasSize(3));
        }

        @Test
        public void find_by_invalid_pid() {
            List<NoteView> noteViewList = noteRepository.findByPid(10L);

            assertThat(noteViewList, hasSize(0));
        }

        @Test
        public void find_by_pid_ordering_check() {
            List<NoteView> noteViewList = noteRepository.findByPid(2L);

            assertThat(noteViewList.get(0).getSequence(), is(1L));
            assertThat(noteViewList.get(1).getSequence(), is(2L));
            assertThat(noteViewList.get(2).getSequence(), is(3L));
        }
    }

    @Nested
    class BlockRepositoryTest {
        @Test
        public void find_by_nid() {
            List<BlockView> blockViewList = blockRepository.findByNid(1L);

            assertThat(blockViewList, hasSize(2));
        }

        @Test
        public void find_by_invalid_nid() {
            List<BlockView> blockViewList = blockRepository.findByNid(12L);

            assertThat(blockViewList, hasSize(0));
        }

        @Test
        public void find_by_nid_ordering_check() {
            List<BlockView> blockViewList = blockRepository.findByNid(1L);

            assertThat(blockViewList.get(0).getSequence(), is(1L));
            assertThat(blockViewList.get(1).getSequence(), is(2L));
        }
    }

    @Nested
    class TextRepositoryTest {
        @Test
        public void find_by_bid() {
            Optional<TextView> textView = textRepository.findByBid(1L);

            assertThat(textView.isPresent(), is(true));
            assertThat(textView.get().getContent(), is("text1"));
        }

        @Test
        public void find_by_invalid_bid() {
            Optional<TextView> textView = textRepository.findByBid(19L);

            assertThat(textView.isPresent(), is(false));
        }
    }

    @Nested
    class InvitationRepositoryTest {
        @Test
        public void find_by_receiver() {
            List<InvitationView> invitationViewList = invitationRepository.findInvitationByReceiver("dinesh");

            assertThat(invitationViewList, hasSize(2));
        }

        @Test
        public void find_by_invalid_receiver() {
            List<InvitationView> invitationViewList = invitationRepository.findInvitationByReceiver("kk");

            assertThat(invitationViewList, hasSize(0));
        }

        @Test
        public void find_by_iid() {
            Optional<InvitationView> invitationView = invitationRepository.findInvitationByIid(1L);

            assertThat(invitationView.isPresent(), is(true));
            assertThat(invitationView.get().getSender().getUsername(), is("rushika"));
        }

        @Test
        public void find_by_invalid_iid() {
            Optional<InvitationView> invitationView = invitationRepository.findInvitationByIid(91L);

            assertThat(invitationView.isPresent(), is(false));
        }
    }

}
