package seedu.address.storage;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Signals that the saved student data contains two students with the same Student ID.
 */
class DuplicateStudentIdException extends IllegalValueException {
    /**
     * @param message The user-facing description naming the duplicate Student ID.
     */
    DuplicateStudentIdException(String message) {
        super(message);
    }
}
