package com.example.conversic;

import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParser;

import java.util.ArrayList;
import java.util.List;

public class GetElement extends ParserListenerAdapter {
    private List<NoteAndBarLine> notes; //list of note and bar line objects

    public GetElement() {
        notes = new ArrayList<>();
    }

    @Override
    public void onNoteParsed(Note note) {
        notes.add(new NoteAndBarLine(note));
    }

    @Override
    public void onBarLineParsed(long id) {
        notes.add(new NoteAndBarLine());
    }

    /**
     * Get a list of notes and bar lines only from a pattern that contains some unimportant data fpr conversion.
     * @param pattern Pattern generated when parsing musicxml file using jfugue.
     * @return List of notes and bar lines.
     */
    public List<NoteAndBarLine> getNotesUsed(Pattern pattern) {
        notes.clear();

        StaccatoParser parser = new StaccatoParser();
        parser.addParserListener(this);
        parser.parse(pattern);
        return notes;
    }

}
