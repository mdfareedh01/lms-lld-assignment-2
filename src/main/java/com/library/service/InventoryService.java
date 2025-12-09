package com.library.service;

import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.BookCopy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class InventoryService {
    private static final Logger LOGGER = Logger.getLogger(InventoryService.class.getName());

    private final Map<String, Book> bookCatalog = new ConcurrentHashMap<>();
    private final Map<String, List<BookCopy>> inventory = new ConcurrentHashMap<>();

    public void addBook(Book book) {
        if (bookCatalog.putIfAbsent(book.getIsbn(), book) != null) {
            LOGGER.warning(() -> String.format("Attempted to add duplicate book ISBN: %s", book.getIsbn()));
            return;
        }
        inventory.put(book.getIsbn(), new ArrayList<>());
        LOGGER.info(() -> String.format("Cataloged new book: %s", book.getTitle()));
    }

    public void addBookCopy(String isbn, BookCopy copy) {
        if (!bookCatalog.containsKey(isbn)) {
            throw new LibraryException("Cannot add copy. ISBN " + isbn + " not found in catalog.");
        }

        List<BookCopy> copies = inventory.get(isbn);
        synchronized (copies) {
            copies.add(copy);
        }
        LOGGER.fine(() -> "Added copy " + copy.getId());
    }

    public List<BookCopy> getCopies(String isbn) {
        return inventory.getOrDefault(isbn, Collections.emptyList());
    }

    public Optional<BookCopy> findCopyById(String copyId) {
        return inventory.values().stream()
                .flatMap(List::stream)
                .filter(c -> c.getId().equals(copyId))
                .findFirst();
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(bookCatalog.get(isbn));
    }

    public List<Book> findBooksByTitle(String titleFragment) {
        if (titleFragment == null || titleFragment.isBlank())
            return Collections.emptyList();

        String query = titleFragment.toLowerCase();
        return bookCatalog.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }
}
