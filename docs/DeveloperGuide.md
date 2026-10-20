---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TeachAssist Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows the components interacting for `delete i/A0123456B`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The [add](#adding-student-information) and [delete](#deleting-student-information) sequence diagrams below
show how parsed commands change a temporary model before the live model is updated.


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* uses `studentId` as the domain identifier for `Person` duplicate checks. The JavaFX `id` label in `PersonListCard.fxml` is only the displayed list index, not the student's identifier.
* stores group labels as `Label` objects inside each `Person`.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user's preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of labels in `AddressBook`, and each `Person` references values from that list. This lets `AddressBook` maintain one value object per unique label instead of each `Person` holding its own objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-F12-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* expects each stored `Person` JSON object to contain a valid `studentId`. There is currently no migration or default-value strategy for old data files missing this field.
* stores group labels through `JsonAdaptedLabel`. Missing or null `labels` fields are treated as empty label sets for compatibility with older data files.
* ignores legacy `tags` fields when loading older data files and omits them when saving.
* reports loading failures as `DataLoadingException`, distinguishing unreadable files, invalid data, and duplicate Student IDs. See [Loading student data at startup](#loading-student-data-at-startup).
* writes student data atomically and reports a data folder that cannot be created as `DataStorageLocationException`. See [Saving before publishing changes](#saving-before-publishing-changes).
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Group labels

An optional group label is assigned when creating a student with `add ... l/LABEL_NAME`.
`Label` validates the value, and `ParserUtil#parseLabel` trims command input before constructing it.
`JsonAdaptedLabel` and `JsonAdaptedPerson` persist labels under each person's `labels` JSON field.
`EditCommand` preserves existing labels, and `PersonCard` renders them in its `FlowPane`.
Existing saved labels remain compatible, including records containing multiple labels.

### Edit command

Format: `edit i/STUDENT_ID [n/NAME] [e/EMAIL] [r/REMARK] [l/LABEL_NAME]... [dl/LABEL_NAME]...`

`EditCommandParser` checks command structure and parses the Student ID and requested field changes.
It collects repeated `l/` and `dl/` values into the `EditPersonDescriptor`. An empty `l/` requests
clearing all labels and is rejected if combined with another label change. An empty `r/` becomes
an empty remark.

`EditCommand` finds the student by Student ID in the complete address book, so a filtered view
does not change the target. It builds a replacement `Person`, keeping fields that were not supplied.
For labels, it copies the student's set, removes each `dl/` label, then adds each `l/` label.
Matching is case-insensitive. Removing a missing label or adding one already present throws a
`CommandException`, leaving the record unchanged. An empty `l/` produces an empty label set.
The command then replaces the student through `Model#setPerson`; `LogicManager` saves the pending
model before publishing it to the live model.

### Adding student information

Command format: `add n/NAME i/STUDENT_ID e/EMAIL [r/REMARK] [l/LABEL_NAME]`.
One optional group label can be assigned during add; `Label` defines its validation rules.
The accepted values and exact error messages are documented in the [User Guide](UserGuide.md#adding-a-student-add).

#### Validation and identity

* `ArgumentTokenizer` recognises supported prefixes at the start of arguments or after whitespace, including tabs.
* `AddCommandParser` checks line breaks, unknown alphabetic prefixes, preamble text, repeated prefixes,
  missing mandatory fields, then empty mandatory fields. Errors are reported before parsing field values.
  Repeated/missing/empty checks follow `n/`, `i/`, `e/` order; repeated `r/` and `l/` are checked after them.
* `ParserUtil` trims values and converts invalid fields into `ParseException`s. Model constructors also validate,
  so records created by storage or other callers follow the same rules.
* `Name` accepts 1–100 Unicode code points after trimming and collapsing repeated spaces, requires a letter,
  and permits Unicode letters/combining marks, spaces, apostrophes, hyphens, and periods. Case is preserved.
* `StudentId` accepts nine ASCII alphanumeric characters and stores uppercase using `Locale.ROOT`.
  `Person#isSamePerson` compares this normalised ID, not the name or email.
* `Email` enforces the supported ASCII format, local-part limit of 64, overall limit of 254, domain-label limits
  of 63, and a final label of 2–63 letters. Only the domain is lowercased using `Locale.ROOT`.
* `Remark` accepts at most 4,000 Unicode code points; command parsing trims only surrounding whitespace.
  Empty or omitted remarks become empty strings. `r/` can appear anywhere and does not consume later parameters.
* `Label` accepts 1–60 Unicode code points and requires at least one letter or digit. It permits letters,
  combining marks, digits, spaces, hyphens, underscores, apostrophes, parentheses, and periods. Empty labels
  are rejected, omitted labels become an empty label set, and `l/` can appear anywhere without consuming later parameters.

For example, `add n/  Alex   Tan i/a0123456b e/Alex@EXAMPLE.COM l/Tutorial 1 r/Quiz: 8/10`
creates a student with name `Alex Tan`, ID `A0123456B`, email `Alex@example.com`, label `Tutorial 1`,
and remark `Quiz: 8/10`.
An alphabetic token such as `x/value` is rejected even inside a remark; `8/10` and URLs remain literal text.
A duplicate ID rejects the complete command without merging any details or remarks.

#### Saving before publishing changes

The execution path is:

```text
AddressBookParser → AddCommandParser → AddCommand
LogicManager → temporary ModelManager → JsonAddressBookStorage → live ModelManager
```

1. `Command#isModifyingData` identifies commands that alter student records. `add`, `edit`, `delete`, and `clear`
   return true. Future modifying commands must override this method too.
2. `LogicManager` copies the live records and current filter into a temporary `ModelManager`, then executes the command
   against that copy. Copying the filter preserves the active view if saving fails; `edit` identifies its target by
   Student ID from the full record list rather than by displayed index.
3. `JsonAddressBookStorage` writes the proposed records to a temporary file in the destination directory.
   `Files.move` with `ATOMIC_MOVE` and `REPLACE_EXISTING` replaces the old file only after writing completes.
   There is no fallback overwrite when atomic replacement is unsupported. Temporary files are removed after failure.
4. After saving succeeds, `LogicManager` publishes the proposed records and filter to the live model, then returns
   the success feedback. If saving fails, it returns `Unable to save student data.`; live records and the active filter
   are unchanged, and the previous data file remains intact.

The sequence diagram shows an add with an optional remark and label. It also shows that a failed save leaves the
live model unchanged. Invalid parameters or a duplicate Student ID stop execution before saving.

![Add a student and save before updating the live model](images/AddSequenceDiagram.svg)

[PlantUML source](diagrams/AddSequenceDiagram.puml)

For example, adding a student while viewing a filtered list restores the complete list only after saving succeeds.
If replacement fails, the filtered list remains as it was and the new student is not added.
Read-only commands such as `find`, `list`, `help`, and `exit` execute on the live model without saving student data.

#### Startup and feedback

`MainApp` starts empty when the data file is missing. If loading fails, it creates an empty model and configures
`LogicManager` to block modifying commands until the file is fixed and the application is restarted.
The error is `Student data is unavailable because loading failed. Fix the data file and restart.`
This prevents an empty model from overwriting unreadable or invalid records. Read-only commands remain available.

Successful adds display `Added student ID: NAME.`, followed by ` Remark: TEXT` when the remark is non-empty,
and the total student count on the next line. The count uses the complete record list.
`PersonCard` displays labels and non-empty remarks with wrapping, and hides the remark field for
empty remarks.
JSON storage preserves remarks and defaults missing/null remark fields to empty strings.

#### Verification

Tests cover field boundaries and normalisation, label parsing, parameter errors and ordering, duplicate IDs with changed details,
feedback with/without remarks, JSON round trips, filtered index handling, startup loading failures, and save failures.
Replacement-failure tests simulate an error after the temporary file is written and check that the original file,
records, and active filter remain unchanged. They also check temporary-file cleanup and failed first saves.

### Find command

Command format: `find n/KEYWORD [MORE_KEYWORDS]...` or `find i/KEYWORD [MORE_KEYWORDS]...`
or `find e/KEYWORD [MORE_KEYWORDS]...` or `find l/LABEL_NAME`.
Exactly one supported search field must be supplied: name, student ID, email, or label.
The accepted format and user-facing examples are documented in the
[User Guide](UserGuide.md#locating-students-by-name-student-id-email-or-label-find).

#### Validation and matching

* `ArgumentTokenizer` recognises the supported prefixes `n/`, `i/`, `e/`, and lowercase `l/`.
* `FindCommandParser` validates malformed input in this order: words before the first parameter prefix, unknown
  parameter prefixes, repeated search prefixes, missing search prefixes, more than one search prefix type, and empty
  values. Label values then pass through `ParserUtil#parseLabel` and the shared `Label` validation rules.
* Name, student ID, and email search keywords are split on whitespace. Multiple spaces, tabs, and line breaks between
  keywords are collapsed.
* `FindCommandParser` creates a field-specific predicate: `NameContainsKeywordsPredicate`,
  `StudentIdContainsKeywordsPredicate`, `EmailContainsKeywordsPredicate`, or `LabelMatchesPredicate`.
* Keyword predicates check whether the selected field contains any keyword using `StringUtil#containsIgnoreCase`.
  Matching is case-insensitive and supports partial matches.
* `LabelMatchesPredicate` checks whether any assigned label's complete name matches using `String#equalsIgnoreCase`.
  Label input is not split into keywords. Leading and trailing whitespace is trimmed; internal spacing is preserved.
  Invalid labels, including names containing slashes or internal control characters, are rejected before execution.

For example, `find n/alex david` creates a `NameContainsKeywordsPredicate` with keywords `alex` and `david`.
Any student whose name contains either keyword is shown.
`find i/a012` searches student IDs, while `find e/example.com` searches email addresses.
`find l/discrete math tutorial` matches the complete label `Discrete Math Tutorial`, while `find l/Math` does not.

#### Execution and displayed list

The execution path is:

```text
AddressBookParser → FindCommandParser → FindCommand
FindCommand → Model#updateFilteredPersonList
```

`FindCommand` stores only the predicate created by the parser. During execution, it calls
`Model#updateFilteredPersonList` with that predicate, replacing the current displayed list with the matching students.
The underlying student records are not modified.

The command returns the standard listed-students feedback containing the number of matching records.
When no records match, the filtered list becomes empty and the feedback reports zero listed students.
The previous filter is replaced by the new search predicate.

The sequence diagram below shows how a successful `find l/Discrete Math Tutorial` command is parsed and executed.
<puml src="diagrams/FindCommandSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `find` Command" />

The activity diagram shows validation and execution for all four search fields:

<puml src="diagrams/FindCommandActivityDiagram.puml" alt="Find command validation and execution" />

#### Saving and startup behaviour

`FindCommand` does not override `Command#isModifyingData`, so it remains a read-only command.
`LogicManager` executes it on the live model without creating a temporary model and without writing the JSON data file.
If startup loading failed, the command is still available because it does not risk overwriting invalid persisted data.

#### Verification

Tests cover words before the first prefix, unknown prefixes, validation precedence, missing prefixes, multiple prefixes,
duplicate prefixes, empty keyword values, name searches with multiple keywords and whitespace, student ID searches,
email searches, complete-label matching with spaces and case differences, invalid labels, zero-result execution,
multi-result execution, replacement of previous search results, and equality of commands with equivalent predicates.
Integration tests cover label searches after reloading saved data, read-only execution without saving, and rejection
of the removed `filter` command without changing records, the displayed list, or the saved file.

### Deleting student information

Command format: `delete i/STUDENT_ID`. The display index is not used to identify students for deletion.

`DeleteCommandParser` accepts exactly one non-empty `i/`. It rejects line breaks, unknown prefixes, preamble text,
repeated IDs, missing IDs, and empty IDs before calling `ParserUtil#parseStudentId`.
`ArgumentTokenizer#findUnknownPrefix` shares the same case-sensitive alphabetic-prefix detection with `add`.
An invalid Student ID retains the field constraint message instead of being replaced with a usage error.

`DeleteCommand` stores the validated `StudentId` and searches `model.getAddressBook().getPersonList()` for an exact
normalised ID match. It retrieves the existing `Person` and passes that object to `model.deletePerson`.
This removes the entire record, including remarks and labels, without separately deleting its fields.
The complete record list is searched so a student hidden by a filter can still be deleted.

For example, after `find n/Benson`, `delete i/a1234567b` removes Alice's `A1234567B` record while the displayed Benson
results remain unchanged. If Alice is already absent, the command reports
`Student with ID A1234567B is not in the records.` and changes no data.
Successful feedback is `Deleted student A1234567B: Alice Pauline.`

Deletion preserves the current filter and returns true from `Command#isModifyingData`.
`LogicManager` therefore performs it on the temporary model, saves the proposed records, and only then publishes
the change. If saving fails, the live record, filter, and previous saved file remain unchanged.
If startup loading failed, deletion is blocked by the existing loading guard.

The sequence diagram shows deletion by Student ID, including the save-failure path.

![Delete by Student ID and save before updating the live model](images/DeleteSequenceDiagram.svg)

[PlantUML source](diagrams/DeleteSequenceDiagram.puml)

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of the diagram.
</box>

Tests cover normalised IDs, visible/hidden students, an empty displayed list, nonexistent IDs, malformed parameters,
whole-record removal from persisted JSON, filter preservation, and save failures.

### Loading student data at startup

The startup messages and the commands available after a failed load are documented in the
[User Guide](UserGuide.md#loading-the-data).

#### Loading and error reporting

The loading path is:

```text
MainApp#initModelManager → StorageManager → JsonAddressBookStorage → JsonSerializableAddressBook
MainApp#initLogic → LogicManager
```

1. `JsonAddressBookStorage#readAddressBook` returns `Optional.empty()` if the data file does not exist.
2. If the file exists, `JsonUtil#readJsonFile` reads it. Failures are reported as `DataLoadingException` with one of
   two messages, chosen by the cause:
   * an `IOException` other than a JSON error, such as missing read permission, gives `Unable to load student data.`
   * a `JsonProcessingException`, such as malformed JSON, gives
     `Unable to load student data. The data file may be corrupted or invalid.`
3. `JsonSerializableAddressBook#toModelType` converts records in file order. A missing or invalid field throws
   `IllegalValueException`, which is reported with the corrupted or invalid message. A repeated Student ID throws
   `DuplicateStudentIdException`, a subclass of `IllegalValueException`. `JsonAddressBookStorage` catches it first so
   that its specific message, `Unable to load student data: duplicate Student ID A0123456B detected.`, is kept.
   Student IDs are compared after normalisation to uppercase.
4. `MainApp` records the outcome as the startup message: `N students loaded.` (`1 student loaded.` for one record),
   the no-existing-data message, or the `DataLoadingException` message.
5. `MainWindow#fillInnerParts` shows `Logic#getStartupMessage` in the result display when the window opens.

The sequence diagram below shows the loading path. The `alt` block shows the three outcomes of reading the data file:
a successful load, a missing file, and a `DataLoadingException`. Model and logic creation continue the same way after
each outcome.

<puml src="diagrams/LoadSequenceDiagram.puml" alt="Loading student data at startup" />

#### Blocking modifications after a failed load

When loading fails, `MainApp` starts with an empty `AddressBook` and passes `isStudentDataAvailable = false` to
`LogicManager`. `LogicManager#execute` parses each command first, so malformed commands still receive their usual
error messages. It then rejects any command whose `Command#isModifyingData` returns true with
`Student data is unavailable because loading failed. Fix the data file and restart.`

This prevents the empty in-memory list from being saved over the user's data file, which may still be recoverable.
Read-only commands, such as `list` and `find`, still run against the empty list. Restarting is the only way
to retry loading.

Saving is described in [Saving before publishing changes](#saving-before-publishing-changes).

#### Design considerations

**Aspect: What to do when the data file cannot be loaded**

* **Alternative 1 (current choice):** Start with an empty list, keep the file, and block modifying commands.
  * Pros: The user's data file is never overwritten, and the user sees why loading failed.
  * Cons: The app cannot be used to change records until the file is fixed.
* **Alternative 2:** Start with an empty list and allow modifying commands, as in AddressBook-Level3.
  * Pros: The app remains fully usable.
  * Cons: The first save overwrites the original file, losing data that might have been fixable.

#### Verification

`MainAppTest` covers a missing file, files with zero, one, and several students, a corrupted file, and duplicate Student
IDs. It checks that modifying commands are blocked and the file is unchanged after a failed load.
`JsonAddressBookStorageTest` covers unreadable files, invalid JSON, invalid values, and duplicate Student IDs.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete i/A0123456B` to delete that student's record. In this proposed undo/redo design,
the `delete` command calls `Model#commitAddressBook()` after deletion, saving the modified state in the
`addressBookStateList` and advancing `currentStatePointer` to the new state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David ...` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite - it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David ...` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

TeachAssist is for teaching assistants (TAs) who support a fixed group of undergraduate students through weekly tutorials and consultations throughout a semester. They need to retrieve and update student information quickly while handling teaching, assessment, and follow-up tasks.

**Value proposition**: Help teaching assistants organise student information and learning progress so they can remember individual students' needs and provide consistent, targeted support throughout the semester.

TeachAssist supports the management of student information, learning progress, interactions, and follow-up needs. It does not aim to replace a learning management system (LMS) for distributing teaching materials, conducting assessments, calculating official grades, or communicating directly with students.

### User stories
Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`.

High-priority stories cover the agreed MVP, including finding students by name. Medium-priority stories capture other requirements considered by the team; they are not commitments for the MVP or final product. No stories are currently assigned low priority. These are product requirements, not a list of implemented features.

TA refers to a teaching assistant. Story IDs are retained from the project notes, including the gap between US29 and US34.

#### Student information

| ID | Priority | As a/an ... | I want to ... | So that ... |
|----|----------|--------|-------------|----------------|
| US01 | `* * *` | TA | add a student under my care | I can keep information about them throughout the semester |
| US02 | `* * *` | TA | view a student's information | I can quickly recall who the student is |
| US03 | `* * *` | TA | update a student's information | my records remain accurate when circumstances change |
| US04 | `* * *` | TA | remove a student who is no longer under my care | my records remain relevant |
| US05 | `* * *` | TA handling multiple tutorial groups | associate students with their tutorial groups | I can distinguish students from different classes |
| US06 | `* * *` | TA | view students belonging to a particular tutorial group | I can prepare for that group's tutorial |
| US07 | `* *` | TA who remembers only partial information about a student | search for students using information I remember | I can retrieve their records quickly |

#### Student notes and interactions

| ID | Priority | As a/an ... | I want to ... | So that ... |
|----|----------|--------|-------------|----------------|
| US08 | `* * *` | TA | record notes about a student | I can remember important information about them later |
| US09 | `* * *` | TA preparing to meet a student | view my previous notes about the student | I can provide support consistent with our earlier interactions |
| US10 | `* *` | TA | record a consultation with a student | I can remember what we discussed |
| US11 | `* *` | TA | view a student's past interactions chronologically | I can understand how their situation has developed over the semester |
| US12 | `* * *` | TA who made an incorrect note | edit or remove the note | misleading information does not remain in the student's record |
| US13 | `* *` | busy TA | quickly record a short observation about a student during or immediately after class | I do not forget it before recording it later |

#### Learning progress

| ID | Priority | As a/an ... | I want to ... | So that ... |
|----|----------|--------|-------------|----------------|
| US14 | `* *` | TA | record topics that a student is struggling with | I know where the student may need additional support |
| US15 | `* *` | TA | record topics that a student has improved in | I can track their learning progress over time |
| US16 | `* *` | TA preparing for a consultation | view a student's learning progress | I can tailor the consultation to their needs |
| US17 | `* *` | TA preparing a tutorial | see which topics students in my class are commonly struggling with | I can spend more time addressing those areas |
| US18 | `* *` | TA | update a student's progress for a topic | the student's record reflects their current level of understanding |

#### Follow-up tasks with students

| ID | Priority | As a/an ... | I want to ... | So that ... |
|----|----------|--------|-------------|----------------|
| US19 | `* *` | TA | record a follow-up action associated with a student | I do not forget things I need to do for them |
| US20 | `* *` | TA | view my outstanding follow-ups | I know which students still require my attention |
| US21 | `* *` | TA | mark a follow-up as completed | I can distinguish finished tasks from those still requiring action |
| US22 | `* *` | TA handling many students | see which students currently require follow-up | nobody accidentally gets overlooked |
| US23 | `* *` | TA | associate a follow-up with a deadline | I know which matters should be handled first |
| US24 | `* *` | TA | view overdue follow-ups | I can address things I have failed to complete on time |

#### Attendance and participation

| ID | Priority | As a/an ... | I want to ... | So that ... |
|----|----------|--------|-------------|----------------|
| US25 | `* *` | TA | record a student's tutorial attendance | I can keep track of whether they have been attending classes |
| US26 | `* *` | TA | record a student's participation in tutorials | I can remember how actively they have been engaging in class |
| US27 | `* *` | TA | view a student's attendance history | I can notice repeated absences |
| US28 | `* *` | TA | identify students whom I have had little interaction with | I can make an effort to engage them |
| US29 | `* *` | TA preparing for a tutorial | see students who have recently missed tutorials | I am aware that they may need additional support |

#### Finding and organising information

| ID | Priority | As a/an ... | I want to ... | So that ... |
|----|----------|--------|-------------|----------------|
| US34 | `* * *` | TA in a hurry | quickly find a student by name | I can access their information without interrupting my workflow |
| US35 | `* *` | TA with many students | filter students based on information relevant to my current task | I only see the students I need to focus on |
| US36 | `* *` | TA | view students who need my attention | I can prioritize whom to follow up with |
| US37 | `* *` | TA | organize students using meaningful categories | I can retrieve groups of related students easily |
| US38 | `* *` | experienced user | perform common operations efficiently | managing student information does not distract me from teaching |

### Use cases

(For all use cases below, the **System** is the `TeachAssist` and the **Actor** is the `Teaching Assistant`, unless specified otherwise)

Use case: UC01 - Add a Student
MSS:
1. TA enters command to add a student with name, student ID, email, and optional remarks and group label.
2. TeachAssist validates the student details and checks for duplicate student IDs.
3. TeachAssist saves the student record to the data file.
4. TeachAssist displays a confirmation message and updates the student list.
   Use case ends.

**Extensions**
* 1a. The command format is invalid or required parameters are missing or empty.
* 1a1. TeachAssist shows an error message naming the missing/empty parameter or explaining the invalid format.
* Use case ends.
* 1b. An unknown or repeated parameter is supplied.
* 1b1. TeachAssist shows an error message naming the offending prefix. No student data changes.
* Use case ends.
* 1c. The student data file failed to load at startup.
* 1c1. TeachAssist blocks the add command and asks the TA to fix the data file and restart.
* Use case ends.
* 2a. One or more field values are invalid (e.g., malformed student ID, invalid email format, or invalid name characters).
* 2a1. TeachAssist shows an error message indicating the invalid field value.
* Use case ends.
* 2b. A student with the given student ID already exists in TeachAssist.
* 2b1. TeachAssist shows an error message indicating that the student ID already exists.
* Use case ends.
* 3a. Saving data to the file fails.
* 3a1. TeachAssist shows an error message indicating that it is unable to save student data.
* 3a2. TeachAssist leaves existing student records, the displayed filter, and the previous saved file unchanged.
* Use case ends.

---

Use case: UC02 - Find a Student
MSS:
1. TA enters a `find` command specifying a name, student ID, email, or complete group label query.
2. TeachAssist searches existing records for matches.
3. TeachAssist displays the list of matching student records and the count of results.
   Use case ends.

**Extensions**
* 1a. The command contains no search parameters, multiple search parameters, or empty query fields.
* 1a1. TeachAssist shows an error message explaining the correct find format.
* Use case ends.
* 1b. The search query contains invalid characters.
* 1b1. TeachAssist shows an error message indicating invalid characters in the query.
* Use case ends.
* 2a. No student records match the search query.
* 2a1. TeachAssist reports zero listed results.
* 2a2. TeachAssist clears the displayed student list.
* Use case ends.

---

Use case: UC04 - Filter Students by Group Label
MSS:
1. TA enters `find l/LABEL_NAME` to search for a specific complete group label.
2. TeachAssist scans stored records for matching group labels.
3. TeachAssist displays only the students with that label, ignoring case, and reports the result count.
   Use case ends.

**Extensions**
* 1a. The group label parameter is missing or empty.
* 1a1. TeachAssist shows an error message indicating the missing or empty parameter.
* Use case ends.
* 1b. Multiple label parameters are provided.
* 1b1. TeachAssist shows an error message indicating that the parameter must be specified only once.
* Use case ends.
* 2a. No students have the specified group label.
* 2a1. TeachAssist reports zero listed results.
* 2a2. TeachAssist clears the displayed student list.
* Use case ends.

---

Use case: UC05 - Delete a Student
MSS:
1. TA enters command to delete a student using their student ID.
2. TeachAssist verifies that the student exists in the records.
3. TeachAssist removes the student record and saves the changes to the data file.
4. TeachAssist displays a confirmation message and updates the student list.
   Use case ends.

**Extensions**
* 1a. The student ID parameter is missing, empty, or improperly formatted.
* 1a1. TeachAssist shows an error message specifying the invalid parameter or usage format.
* Use case ends.
* 1b. The command contains a repeated Student ID or an unknown parameter.
* 1b1. TeachAssist shows the error message naming the offending prefix without changing student records.
* Use case ends.
* 1c. The data file failed to load at startup.
* 1c1. TeachAssist blocks deletion and asks the TA to fix the data file and restart.
* Use case ends.
* 2a. No student with the specified student ID exists in the records.
* 2a1. TeachAssist shows an error message stating that the student is not in the records.
* Use case ends.
* 3a. Saving data to the file fails.
* 3a1. TeachAssist shows an error message indicating that it is unable to save student data.
* 3a2. TeachAssist retains the entire student record, current filter, and previous saved file.
* Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 persons without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

