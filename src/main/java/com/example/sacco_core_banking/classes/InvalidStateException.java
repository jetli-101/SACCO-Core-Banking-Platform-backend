package com.example.sacco_core_banking.classes;

/** Thrown when an action is attempted against an entity in a status that doesn't allow it
 *  (e.g. approving a Member that isn't PENDING). */
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
