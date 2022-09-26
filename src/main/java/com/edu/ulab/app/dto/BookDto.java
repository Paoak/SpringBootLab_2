package com.edu.ulab.app.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private Long userId;
    private String title;
    private String author;
    private long pageCount;

    public BookDto() {
    }

    public BookDto(Long id, Long userId, String title, String author, long pageCount) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
    }
}
