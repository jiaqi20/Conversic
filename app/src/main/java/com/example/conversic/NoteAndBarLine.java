package com.example.conversic;

import androidx.annotation.NonNull;

import org.jfugue.theory.Note;

public class NoteAndBarLine {
    private int id;
    private Note note;

    private static final int noteId = 1;
    private static final int barLineId = 0;

    /**
     * Constructor for bar line.
     */
    NoteAndBarLine() {
        this.id = 0;
    }

    /**
     * Constructor for note.
     * @param note Note.
     */
    NoteAndBarLine(Note note) {
        this.id = 1;
        this.note = note;
    }

    public Note getNote() {
        return this.note;
    }

    public int getId() {
        return this.id;
    }

    public static boolean isNote(NoteAndBarLine element) {
        return element.getId() == 1;
    }

    @NonNull
    @Override
    public String toString() {
        if(id == barLineId) {
            return "|";
        }
        return note.getOriginalString();
    }
}
