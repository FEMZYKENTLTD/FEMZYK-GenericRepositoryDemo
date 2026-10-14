package com.femzyk.genericsdemo.console;

import java.util.List;

import com.femzyk.genericsdemo.core.Customer;
import com.femzyk.genericsdemo.core.InMemoryRepository;
import com.femzyk.genericsdemo.core.Product;
import com.femzyk.genericsdemo.core.Repository;
import com.femzyk.genericsdemo.core.Result;

/**
 * ConsoleApp - console front end of the FEMZYK Generics Demo
 * (CS 1103-01, Unit 6 discussion).
 *
 * Four demonstrations, all from the course readings:
 *
 *   1. ONE generic repository class serving TWO entity types
 *      (products keyed by Integer, customers keyed by String).
 *   2. Compile-time type safety: what the compiler rejects before the
 *      program can ever run (shown as comments).
 *   3. Bounded type parameters: one generic method shared by every
 *      comparable type.
 *   4. The generic Result type: handling expected failures without
 *      exceptions, with map() chaining.
 */
public class ConsoleApp {

    /**
     * Runs the demonstrations in order.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      FEMZYK GENERICS DEMO (Unit 6)               ");
        System.out.println("==================================================");

        demoOneRepositoryManyTypes();
        demoCompileTimeSafety();
        demoBoundedMethod();
        demoResultType();

        System.out.println("\nDone: one repository class, two entity types, zero casts.");
    }

    /** Demo 1: the same generic class stores two completely different types. */
    private static void demoOneRepositoryManyTypes() {
        System.out.println("\n--- 1. One generic repository, two entity types ---");

        Repository<Product, Integer> products = new InMemoryRepository<>(Product::id);
        products.save(new Product(1, "Wireless Mouse", 40));
        products.save(new Product(2, "Mechanical Keyboard", 15));

        Repository<Customer, String> customers = new InMemoryRepository<>(Customer::customerId);
        customers.save(new Customer("C001", "Alice Johnson"));
        customers.save(new Customer("C002", "Ben Adams"));

        System.out.println("Products  (Repository<Product, Integer>):");
        for (Product p : products.findAll()) {
            System.out.println("  " + p);
        }
        System.out.println("Customers (Repository<Customer, String>):");
        for (Customer c : customers.findAll()) {
            System.out.println("  " + c);
        }
        System.out.println("findById with each key type: "
                + products.findById(2) + " / " + customers.findById("C001"));
        System.out.println("No casts anywhere - the compiler knows every type exactly.");
    }

    /** Demo 2: shows (as comments) what the compiler rejects before runtime. */
    private static void demoCompileTimeSafety() {
        System.out.println("\n--- 2. Compile-time type safety ---");
        Repository<Product, Integer> products = new InMemoryRepository<>(Product::id);
        products.save(new Product(1, "Wireless Mouse", 40));
        Product p = products.findById(1);
        System.out.println("Every insert and lookup is checked at compile time.");
        System.out.println("The pre-generics mistakes below are now IMPOSSIBLE to compile:");
        System.out.println("   products.save(\"a plain String\");   // compile error");
        System.out.println("   Customer c = (Customer) products.findById(1);  // cast not needed");
        System.out.println("Recovered object: " + p);
    }

    /** Demo 3: one bounded generic method works for every comparable type. */
    private static void demoBoundedMethod() {
        System.out.println("\n--- 3. Bounded type parameters (<T extends Comparable<T>>) ---");
        List<Integer> quantities = List.of(40, 15, 30, 22);
        List<String> names = List.of("keyboard", "mouse", "hub", "stand");
        System.out.println("Largest quantity: " + maxOf(quantities));
        System.out.println("Largest name:     " + maxOf(names));
        System.out.println("One method, any Comparable type - written once.");
    }

    /**
     * A bounded generic method: T may be any type that can compare to
     * itself, which is exactly what the method body needs.
     */
    private static <T extends Comparable<T>> T maxOf(List<T> values) {
        T largest = values.get(0);
        for (T value : values) {
            if (value.compareTo(largest) > 0) {
                largest = value;
            }
        }
        return largest;
    }

    /** Demo 4: the generic Result type with map() chaining. */
    private static void demoResultType() {
        System.out.println("\n--- 4. Generic Result<T> (success/failure without exceptions) ---");

        Result<Integer> parsed = parseQuantity("42");
        Result<String> message = parsed
                .map(q -> "stock after restock: " + (q + 10))
                .map(String::toUpperCase);
        System.out.println("Valid input  -> " + (parsed.isSuccess()
                ? "success(" + parsed.value() + ")" : parsed.error()));
        System.out.println("Chained      -> " + (message.isSuccess()
                ? "success(\"" + message.value() + "\")" : "failure(\"" + message.error() + "\")"));

        Result<Integer> invalid = parseQuantity("forty-two");
        System.out.println("Invalid input -> " + (invalid.isSuccess()
                ? "unexpected success" : "failure(\"" + invalid.error() + "\")"));
    }

    /** Parses a quantity string into a Result instead of throwing. */
    private static Result<Integer> parseQuantity(String input) {
        try {
            return Result.success(Integer.parseInt(input.trim()));
        } catch (NumberFormatException error) {
            return Result.failure("\"" + input + "\" is not a whole number");
        }
    }
}
