package com.library.strategy.allocation;

import com.library.model.BookCopy;
import java.util.List;
import java.util.Optional;

public interface IBookAllocationStrategy {
    Optional<BookCopy> allocateBook(List<BookCopy> copies);
}
