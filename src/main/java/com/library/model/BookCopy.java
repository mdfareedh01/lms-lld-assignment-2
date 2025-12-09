package com.library.model;

import com.library.model.enums.BookStatus;

public class BookCopy {
    private final String id; // Barcode
    private final Book book;
    private BookStatus status;

    public BookCopy(String id, Book book) {
        this.id = id;
        this.book = book;
        this.status = BookStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BookCopy{" +
                "id='" + id + '\'' +
                ", book=" + book.getTitle() +
                ", status=" + status +
                '}';
    }
}
