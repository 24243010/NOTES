package com.example.notes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteRequest(
        @NotBlank @Size(max = 120) String title,
        @NotNull @Size(max = 5000) String content) {
}