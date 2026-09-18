package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Remark;

public class RemarkCommandTest {
    private Model model = new ModelManager();
    private AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_remark_throwsCommandException() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes coffee"));
        assertCommandFailure(command, model, "Index: 1, Remark: Likes coffee");
    }

    @Test
    public void parseCommand_remark() throws Exception {
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes coffee"));
        assertEquals(expectedCommand, parser.parseCommand("remark 1 r/Likes coffee"));
    }

    @Test
    public void parseCommand_emptyRemark() throws Exception {
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertEquals(expectedCommand, parser.parseCommand("remark 1 r/"));
    }
}
