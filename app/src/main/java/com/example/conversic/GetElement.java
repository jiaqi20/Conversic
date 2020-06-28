package com.example.conversic;

import org.jfugue.parser.ParserListener;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Note;
import org.staccato.StaccatoParser;

import java.util.ArrayList;
import java.util.List;

public class GetElement extends ParserListenerAdapter {
    private List<NoteAndBarLine> notes;
    private byte key = 0;
    private byte scale = 1;

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

    @Override
    public void onKeySignatureParsed(byte key, byte scale) {
        this.key = key; //number of accidentals
        this.scale = scale; //-1 is minor, 1 is major
    }

    public byte getKey() {
        return this.key;
    }

    public byte getScale() {
        return this.scale;
    }

    public List<NoteAndBarLine> getNotesUsed(Pattern pattern) {
        notes.clear();

        StaccatoParser parser = new StaccatoParser();
        parser.addParserListener(this);
        parser.parse(pattern);
        return notes;
    }

}
