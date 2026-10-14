package com.femzyk.genericsdemo.core;

import java.util.function.Function;

/**
 * Result - a generic success/failure container.
 *
 * CS 1103-01, Unit 6 discussion demo. Instead of throwing exceptions for
 * expected situations, operations return a Result: exactly one of a value
 * or an error message. The generic parameter makes the pattern reusable
 * for every return type while the compiler still forces callers to check
 * which case they received (isSuccess before value()).
 *
 * @param <T> the type of the success value
 */
public final class Result<T> {

    private final T value;
    private final String error;

    private Result(T value, String error) {
        this.value = value;
        this.error = error;
    }

    /** Creates a successful result holding a value. */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    /** Creates a failed result holding an error message. */
    public static <T> Result<T> failure(String message) {
        return new Result<>(null, message);
    }

    /** Transforms the success value with a function, keeping failures as they are. */
    public <R> Result<R> map(Function<T, R> mapper) {
        return isSuccess() ? success(mapper.apply(value)) : failure(error);
    }

    /** Returns true when the operation succeeded. */
    public boolean isSuccess() {
        return error == null;
    }

    /** Returns the success value (check isSuccess first). */
    public T value() {
        return value;
    }

    /** Returns the error message of a failed result. */
    public String error() {
        return error;
    }
}
