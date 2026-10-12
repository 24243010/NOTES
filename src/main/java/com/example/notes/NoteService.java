package com.example.notes;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NoteService {
    private final ConcurrentHashMap<Long, Note> notes = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Note> findAll() {
        return notes.values().stream()
                .sorted(Comparator.comparingLong(Note::id))
                .toList();
    }

    public Note findById(long id) {
        Note note = notes.get(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
        return note;
    }

    public Note create(NoteRequest request) {
        Instant now = Instant.now();
        Note note = new Note(nextId.getAndIncrement(), request.title(), request.content(), now, now);
        notes.put(note.id(), note);
        return note;
    }

    public Note update(long id, NoteRequest request) {
        Note current = findById(id);
        Note updated = new Note(id, request.title(), request.content(), current.createdAt(), Instant.now());
        notes.put(id, updated);
        return updated;
    }

    public void delete(long id) {
        if (notes.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }
    }
}