package com.femzyk.genericsdemo.core;

/**
 * Customer - a second entity type served by the SAME generic repository.
 *
 * CS 1103-01, Unit 6 discussion demo. Note that the id is a String here
 * while Product uses an int - exactly the flexibility that the two type
 * parameters of Repository provide.
 *
 * @param customerId the customer's key (String, e.g. "C001")
 * @param name       display name
 */
public record Customer(String customerId, String name) {

    @Override
    public String toString() {
        return String.format("[%-4s] %s", customerId, name);
    }
}
