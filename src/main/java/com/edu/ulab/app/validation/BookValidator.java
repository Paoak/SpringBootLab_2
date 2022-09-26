package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.BookDto;

public class BookValidator {
    public static boolean isValidBook(BookDto bookDto){
        boolean validTitle = false;
        boolean validAuthor = false;
        boolean validPageCount = false;

        if (bookDto.getTitle() != null) {
            validTitle = !bookDto.getTitle().isBlank();
        }

        if (bookDto.getAuthor() != null) {
            validAuthor = !bookDto.getAuthor().isBlank();
        }

        if (bookDto.getPageCount() > 0) {
            validPageCount = true;
        }

        return validTitle && validAuthor && validPageCount;
    }
}
