package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.LabelCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.label.Label;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentId;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_addWithTabs_success() throws Exception {
        Person person = new PersonBuilder().withRemark("Quiz: 8/10.").build();
        String command = "add\tn/" + person.getName() + "\ti/" + person.getStudentId()
                + "\te/" + person.getEmail() + "\tr/" + person.getRemark();
        Person expected = new PersonBuilder(person).withTags().build();
        assertEquals(new AddCommand(expected), parser.parseCommand(command));
    }

    @Test
    public void parseCommand_addInvalidParameters_throwsSpecificErrors() {
        assertThrows(ParseException.class, "Missing required parameter: i/.", ()
            -> parser.parseCommand("add n/Samuel e/samuel@example.com"));
        assertThrows(ParseException.class, "Unknown parameter: p/.", ()
            -> parser.parseCommand("add n/Samuel i/A0123456B e/samuel@example.com p/123"));
    }

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                "delete\ti/a0123456b");
        assertEquals(new DeleteCommand(new StudentId("A0123456B")), command);
    }

    @Test
    public void parseCommand_deleteInvalidParameters_reportsSpecificErrors() {
        assertThrows(ParseException.class, "Missing required parameter: i/.", () -> parser.parseCommand("delete"));
        assertThrows(ParseException.class, DeleteCommand.MESSAGE_INVALID_FORMAT, () -> parser.parseCommand("delete 1"));
        assertThrows(ParseException.class, StudentId.MESSAGE_CONSTRAINTS, () -> parser.parseCommand("delete i/123"));
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_filter() throws Exception {
        FilterCommand command = (FilterCommand) parser.parseCommand(
                FilterCommand.COMMAND_WORD + " l/Discrete Math Tutorial");
        assertEquals(new FilterCommand(new Label("Discrete Math Tutorial")), command);
    }

    @Test
    public void parseCommand_filterInvalidArgs_throwsParseException() {
        assertThrows(ParseException.class, FilterCommandParser.MESSAGE_MISSING_LABEL, ()
            -> parser.parseCommand(FilterCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " n/" + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        String invalidCommand = ListCommand.COMMAND_WORD + " 3";
        Executable parseInvalidCommand = () -> parser.parseCommand(invalidCommand);
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE),
                parseInvalidCommand);
    }

    @Test
    public void parseCommand_label() throws Exception {
        LabelCommand command = (LabelCommand) parser.parseCommand(
                LabelCommand.COMMAND_WORD + " l/Discrete Math Tutorial i/A0101010A");
        assertEquals(new LabelCommand(new Label("Discrete Math Tutorial"), new StudentId("A0101010A")), command);
    }

    @Test
    public void parseCommand_labelInvalidArgs_throwsParseException() {
        assertThrows(ParseException.class, LabelCommandParser.MESSAGE_MISSING_STUDENT_ID, ()
            -> parser.parseCommand(LabelCommand.COMMAND_WORD + " l/Discrete Math Tutorial"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}

