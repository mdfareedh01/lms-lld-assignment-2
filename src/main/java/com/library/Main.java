package com.library;

import com.library.factory.BookFactory;
import com.library.model.BookCopy;
import com.library.model.Patron;
import com.library.model.enums.PatronType;
import com.library.service.InventoryService;
import com.library.service.LendingService;
import com.library.strategy.allocation.FirstAvailableStrategy;

import java.time.LocalDate;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    static {
        Logger rootLogger = Logger.getLogger("");
        for (var handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.INFO);
        rootLogger.addHandler(handler);
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the Library Management System\n");

        InventoryService inventory = new InventoryService();
        LendingService lending = new LendingService(inventory, new FirstAvailableStrategy());

        seedLibraryData(inventory);
        seedPatrons(lending);

        runCheckoutScenario(lending);
        runReturnScenario(lending);

        System.out.println("\nEnd of the LMS!");
    }

    private static void seedLibraryData(InventoryService inventory) {
        var cleanCoder = BookFactory.createBook("978-0137081073", "The Clean Coder", "Robert C. Martin", 2011);
        var designPatterns = BookFactory.createBook("978-0201633610", "Design Patterns", "Erich Gamma", 1994);

        inventory.addBook(cleanCoder);
        inventory.addBookCopy(cleanCoder.getIsbn(), new BookCopy("ITEM-101", cleanCoder));
        inventory.addBookCopy(cleanCoder.getIsbn(), new BookCopy("ITEM-102", cleanCoder));

        inventory.addBook(designPatterns);
        inventory.addBookCopy(designPatterns.getIsbn(), new BookCopy("ITEM-201", designPatterns));
    }

    private static void seedPatrons(LendingService lending) {
        lending.registerPatron(new Patron("P-100", "Jordan Hayes", PatronType.STANDARD, "jordan.h@example.com"));
        lending.registerPatron(new Patron("P-200", "Maria Rodriguez", PatronType.VIP, "m.rodriguez@library.org"));
    }

    private static void runCheckoutScenario(LendingService lending) {
        System.out.println("\n Processing Checkout Request");

        lending.checkoutBook("978-0137081073", "P-100")
                .ifPresentOrElse(
                        copy -> System.out.println("Checkout successful! Jordan now has " + copy.getBook().getTitle()),
                        () -> System.err.println("Checkout failed: Book is currently unavailable."));
    }

    private static void runReturnScenario(LendingService lending) {
        System.out.println("\n Processing Return Request");

        String copyId = "ITEM-101";
        LocalDate simulatedDueDate = LocalDate.now().minusDays(5);

        lending.setDueDateForSimulation(copyId, simulatedDueDate);

        double fee = lending.returnBook(copyId);
        if (fee > 0) {
            System.out.printf("Late return processed. Fee applied: $%.2f%n", fee);
        } else {
            System.out.println("Return successful, No late fees applied.");
        }
    }
}
