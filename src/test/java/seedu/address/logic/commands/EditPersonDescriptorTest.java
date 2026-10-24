package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void equals() {
        // EP: same field and label-change values
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // EP: same object
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // EP: null
        assertFalse(DESC_AMY.equals(null));

        // EP: different type
        assertFalse(DESC_AMY.equals(5));

        // EP: different values
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // EP: different name
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // EP: different email
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // EP: different remark
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withRemark(VALID_REMARK_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // EP: different label additions, removals, or clearing flag
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withLabelsToAdd("Tutorial A").build();
        assertFalse(DESC_AMY.equals(editedAmy));
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withLabelsToRemove("Tutorial A").build();
        assertFalse(DESC_AMY.equals(editedAmy));
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withClearedLabels().build();
        assertFalse(DESC_AMY.equals(editedAmy));

    }

    @Test
    public void toStringMethod() {
        // EP: valid object representation
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", remark="
                + editPersonDescriptor.getRemark().orElse(null) + ", labelsToAdd=[]"
                + ", labelsToRemove=[], isClearingLabels=false}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}

