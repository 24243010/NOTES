package com.example.notes;

import java.time.Instant;

public record Note(long id, String title, String content, Instant createdAt, Instant updatedAt) {
}