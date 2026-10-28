package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.label.Label;

/**
 * Jackson-friendly version of {@link Label}.
 */
class JsonAdaptedLabel {

    private final String labelName;

    /**
     * Constructs a {@code JsonAdaptedLabel} with the given {@code labelName}.
     */
    @JsonCreator
    public JsonAdaptedLabel(String labelName) {
        this.labelName = labelName;
    }

    /**
     * Converts a given {@code Label} into this class for Jackson use.
     */
    public JsonAdaptedLabel(Label source) {
        labelName = source.labelName;
    }

    @JsonValue
    public String getLabelName() {
        return labelName;
    }

    /**
     * Converts this Jackson-friendly adapted label object into the model's {@code Label} object.
     * Trims surrounding whitespace before validation.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted label.
     */
    public Label toModelType() throws IllegalValueException {
        if (labelName == null) {
            throw new IllegalValueException(Label.MESSAGE_CONSTRAINTS);
        }
        final String trimmedLabelName = labelName.trim();
        if (!Label.isValidLabelName(trimmedLabelName)) {
            throw new IllegalValueException(Label.MESSAGE_CONSTRAINTS);
        }
        return new Label(trimmedLabelName);
    }
}
