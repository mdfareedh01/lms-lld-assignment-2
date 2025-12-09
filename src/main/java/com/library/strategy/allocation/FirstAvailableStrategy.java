package com.library.strategy.allocation;

import com.library.model.BookCopy;
import com.library.model.enums.BookStatus;
import java.util.List;
import java.util.Optional;

public class FirstAvailableStrategy implements IBookAllocationStrategy {
    @Override
    public Optional<BookCopy> allocateBook(List<BookCopy> copies) {
        return copies.stream()
                .filter(copy -> copy.getStatus() == BookStatus.AVAILABLE)
                .findFirst();
    }
}
