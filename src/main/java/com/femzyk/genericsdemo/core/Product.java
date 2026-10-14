package com.femzyk.genericsdemo.core;

/**
 * Product - small entity used to demonstrate the generic repository.
 *
 * CS 1103-01, Unit 6 discussion demo.
 *
 * @param id       the product's primary key
 * @param name     display name
 * @param quantity stock on hand
 */
public record Product(int id, String name, int quantity) {

    /** Higher bound demo target: products compare naturally by quantity. */
    public int quantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("[P%d] %-22s qty %d", id, name, quantity);
    }
}
