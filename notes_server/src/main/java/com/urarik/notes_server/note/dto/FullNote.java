package com.urarik.notes_server.note.dto;

import com.urarik.notes_server.note.NoteRepository.NoteView;

import java.io.Serializable;
import java.util.List;

public class FullNote implements Serializable {
    private NoteView noteView;
    private List<Content> contentList;

    public FullNote(NoteView noteView, List<Content> contentList) {
        this.noteView = noteView;
        this.contentList = contentList;
    }

    public NoteView getNoteView() {
        return noteView;
    }

    public void setNoteView(NoteView noteView) {
        this.noteView = noteView;
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }
}
