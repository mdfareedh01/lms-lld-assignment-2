package com.library.service;

import com.library.exception.LibraryException;
import com.library.model.BookCopy;
import com.library.model.Patron;
import com.library.model.enums.BookStatus;
import com.library.model.enums.PatronType;
import com.library.strategy.allocation.IBookAllocationStrategy;
import com.library.strategy.fee.IFeeCalculationStrategy;
import com.library.strategy.fee.StandardFeeStrategy;
import com.library.strategy.fee.VIPFeeStrategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class LendingService {
    private static final Logger LOGGER = Logger.getLogger(LendingService.class.getName());
    private static final int LOAN_PERIOD_DAYS = 14;

    private final InventoryService inventory;
    private final IBookAllocationStrategy allocationStrategy;

    private final Map<String, Patron> patronRegistry = new ConcurrentHashMap<>();

    private final Map<String, String> activeLoans = new ConcurrentHashMap<>();
    private final Map<String, LocalDate> dueDates = new ConcurrentHashMap<>();

    public LendingService(InventoryService inventory, IBookAllocationStrategy strategy) {
        this.inventory = inventory;
        this.allocationStrategy = strategy;
    }

    public void registerPatron(Patron patron) {
        patronRegistry.put(patron.getId(), patron);
        LOGGER.info(() -> "Member registered: " + patron.getName());
    }

    public Optional<BookCopy> checkoutBook(String isbn, String patronId) {
        Patron patron = patronRegistry.get(patronId);
        if (patron == null) {
            throw new LibraryException("Checkout failed: Patron " + patronId + " not found.");
        }

        var availableCopies = inventory.getCopies(isbn);
        var selectedCopyOpt = allocationStrategy.allocateBook(availableCopies);

        if (selectedCopyOpt.isPresent()) {
            BookCopy copy = selectedCopyOpt.get();
            performCheckout(copy, patron);
            return Optional.of(copy);
        }

        LOGGER.warning(() -> "Checkout failed: No copies available for ISBN " + isbn);
        return Optional.empty();
    }

    private void performCheckout(BookCopy copy, Patron patron) {
        copy.setStatus(BookStatus.BORROWED);
        activeLoans.put(copy.getId(), patron.getId());
        dueDates.put(copy.getId(), LocalDate.now().plusDays(LOAN_PERIOD_DAYS));

        LOGGER.info(() -> String.format("Checkout: [%s] borrowed by %s",
                copy.getBook().getTitle(), patron.getName()));
    }

    public double returnBook(String copyId) {
        if (!activeLoans.containsKey(copyId)) {
            throw new LibraryException("Return failed: Item " + copyId + " is not currently checked out.");
        }

        String patronId = activeLoans.remove(copyId);
        LocalDate dueDate = dueDates.remove(copyId);

        BookCopy copy = inventory.findCopyById(copyId)
                .orElseThrow(() -> new LibraryException(
                        "System inconsistency: Copy " + copyId + " missing from inventory."));
        copy.setStatus(BookStatus.AVAILABLE);

        return calculateFines(patronId, dueDate);
    }

    private double calculateFines(String patronId, LocalDate dueDate) {
        long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());

        if (overdueDays <= 0)
            return 0.0;

        Patron patron = patronRegistry.get(patronId);
        IFeeCalculationStrategy strategy = resolveFeeStrategy(patron.getType());

        double fine = strategy.calculateFee((int) overdueDays);
        if (fine > 0) {
            LOGGER.info(() -> String.format("Late return by %s (%d days). Fine: $%.2f",
                    patron.getName(), overdueDays, fine));
        }
        return fine;
    }

    private IFeeCalculationStrategy resolveFeeStrategy(PatronType type) {
        return switch (type) {
            case VIP -> new VIPFeeStrategy();
            default -> new StandardFeeStrategy();
        };
    }

    public void setDueDateForSimulation(String copyId, LocalDate simulatedDate) {
        if (dueDates.containsKey(copyId)) {
            dueDates.put(copyId, simulatedDate);
        }
    }
}
