---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AB-3 User Guide

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. With no saved data file, the student list starts empty.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe i/A0123456B e/johnd@example.com r/Needs help with recursion` : Adds a student named `John Doe`.

   * `delete i/A0123456B` : Deletes the student whose Student ID is `A0123456B`.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, `add e/john@example.com i/A0123456B n/John Doe` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student: `add`

Adds a student to TeachAssist with identifying information and optional free-text remarks.
Use remarks for observations such as test results, weak topics, and consultation timings.

Format: `add n/NAME i/STUDENT_ID e/EMAIL [r/REMARK] [l/LABEL_NAME] [t/TAG]...`

#### Parameter rules

* `n/`, `i/`, and `e/` are compulsory and cannot be empty. Each can appear only once.
* Parameters can appear in any order, including `r/` and `l/`. Prefixes are lowercase and case-sensitive.
* Enter one command line. Leading and trailing whitespace around the command and each value is ignored.
* A supported prefix at the start of a whitespace-separated token starts a parameter. Spaces or tabs can separate parameters.
* Unknown alphabetic prefix tokens, such as `x/` or `p/`, are rejected, including inside remarks.
* A prefix joined to other text does not start a parameter. For example, `https://example.com/n/Alex` remains remark text.

| Field | Accepted values and storage |
|-------|-----------------------------|
| Name | 1–100 Unicode code points after normalisation, with at least one letter. Allows Unicode letters, combining marks, spaces, apostrophes (`'` and `’`), hyphens, and periods. Digits and other symbols are rejected. Repeated internal spaces become one space; capitalisation is preserved. |
| Student ID | Exactly nine ASCII letters or digits, without internal spaces or punctuation. Stored in uppercase; `a0123456b` and `A0123456B` identify the same student. |
| Email | At most 254 characters with exactly one `@`. The local part has 1–64 ASCII letters, digits, periods, underscores, plus signs, or hyphens. It cannot start/end with a period or contain consecutive periods. The domain has at least two dot-separated labels of 1–63 ASCII letters, digits, or hyphens, without boundary hyphens. The final label has 2–63 letters. Only the domain is lowercased. Spaces are rejected, and mailbox existence is not checked. |
| Remark | Optional, at most 4,000 Unicode code points after trimming. Internal spacing and capitalisation are preserved. Omitting `r/` or supplying an empty `r/` stores an empty remark. At most one `r/` is allowed. Dates, grades, and the meaning of the text are not validated. Long remarks wrap on the student card. |
| Label | Optional. Follows the same label name rules as the `label` command. At most one `l/` is allowed. Omitting `l/` stores no label. |
| Tags | Optional and repeatable. Existing tag rules apply: non-empty alphanumeric values, with identical tags stored once. |

Unlike numeric text such as `8/10`, a recognised prefix after `r/` starts another parameter.
For example, `r/Quiz: 8/10 t/friends` stores `Quiz: 8/10` as the remark and `friends` as a tag.

Examples:

* `add n/Samuel i/A0456832Y e/samuel123@example.com r/Consultation time: 3rd Oct 12pm at Science`
* `add n/James Ho i/A0123456B e/jamesho@example.com l/Discrete Math Tutorial r/Needs help with recursion`
* `add n/Jamie Lim i/A0234567C e/jamie@example.com r/Struggles with recursion. Quiz 1: 8/10.`
* `add r/Needs help with recursion e/Alex@EXAMPLE.COM i/a0123456b n/Alex Tan`

#### Successful add

After saving succeeds, the student appears in the complete student list. The result shows the normalised Student ID,
name, optional remark, and total number of students. For example, when this creates the eighth student:

```text
Added student A0123456B: Alex Tan. Remark: Needs help with recursion
8 students listed.
```

When the remark is empty, the `Remark:` part is omitted. Empty remarks are also hidden on student cards.
Names do not determine duplicates: different students can share a name.
If the normalised Student ID already exists, the entire command is rejected; existing details and remarks are not merged.

#### Errors

| Problem | Message |
|---------|---------|
| Missing compulsory parameter | `Missing required parameter: i/.` (the message names the missing prefix) |
| Empty compulsory parameter | `Parameter n/ cannot be empty.` |
| Repeated parameter | `Parameter n/ must be specified only once.` |
| Unknown parameter | `Unknown parameter: p/.` |
| Duplicate Student ID | `Student ID A0123456B already exists.` |
| Invalid name | `Name must contain 1-100 characters, include a letter, and use only letters, spaces, apostrophes, hyphens, or periods.` |
| Invalid Student ID | `Student ID must contain exactly 9 letters or digits, with no spaces.` |
| Invalid email | `Email must have the form name@example.com and meet the supported email format.` |
| Empty label | `Parameter l/ cannot be empty.` |
| Invalid label | `Label names should be 1 to 60 characters long, contain at least one letter or digit, and only contain letters, digits, spaces, hyphens, underscores, apostrophes, parentheses, and periods.` |
| Oversized remark | `Remark must not exceed 4000 characters.` |
| Invalid command structure | `Invalid command format. Usage: add n/NAME i/STUDENT_ID e/EMAIL [r/REMARK] [l/LABEL_NAME] [t/TAG]...` |
| Saving fails | `Unable to save student data.` |

Invalid structure, such as text before the first parameter or an embedded line break, displays the add usage.
If several errors exist, the parser checks line breaks, unknown prefixes, preamble text, repeated parameters,
missing parameters, then empty compulsory values before validating field values.
Within repeated/missing/empty checks, `n/`, `i/`, and `e/` are checked in that order; repeated `r/` and `l/`
are checked after them.
Failed commands do not change existing student records or the saved file.

### Listing all students: `list`

Shows all students in TeachAssist. If the displayed list was previously filtered, this command restores the complete student list.

Format: `list`

The `list` command does not accept additional arguments. For example, `list 3` is rejected as an invalid command.

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [e/EMAIL] [t/TAG]...`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* Student ID cannot be edited with this command.
* Remarks are preserved when editing other fields. Name and email values follow the rules listed under `add`.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
* `edit 1 e/johndoe@example.com` Changes the first person's email address to `johndoe@example.com`.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Labelling a student by group: `label`

Adds a group label to an existing student.

Format: `label l/LABEL_NAME i/STUDENT_ID`

* `LABEL_NAME` must not be empty after trimming leading and trailing whitespace.
* `LABEL_NAME` can contain 1 to 60 characters.
* `LABEL_NAME` must contain at least one letter or digit.
* `LABEL_NAME` can contain letters, digits, spaces, hyphens, underscores, apostrophes, parentheses, and periods.
* `LABEL_NAME` cannot contain `/` or ASCII control characters.
* Internal spacing and capitalization are preserved for display.
* `STUDENT_ID` must identify an existing student.
* A student cannot have the same label more than once. Duplicate checks are case-insensitive.

Examples:
* `label l/Discrete Math Tutorial i/A0101010A`
* `label i/A0101010A l/Tutorial (Monday)`

Note: Currently, labels and tags are indistinguishable in the UI. Dedicated styling will be implemented soon.

Expected successful output:
`Added label "Discrete Math Tutorial" to Samuel Tan (A0101010A).`

### Locating persons by name, student ID, or email: `find`

Finds persons whose name, student ID, or email matches any of the given keywords.

Format: `find n/KEYWORD [MORE_KEYWORDS]` or `find i/KEYWORD [MORE_KEYWORDS]` or `find e/KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* The search accepts exactly one prefix: `n/`, `i/`, or `e/`.
* Partial words match; for example, `Han` matches `Hans`.

Examples:
* `find n/John` returns `john` and `John Doe`
* `find n/alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a student: `delete`

Deletes the student with the specified Student ID, including their name, email, remark, and tags.

Format: `delete i/STUDENT_ID`

* Exactly one non-empty `i/` parameter is required. No other parameter prefixes are accepted.
* Student IDs follow the same nine-character ASCII alphanumeric rules as `add`.
* Matching is case-insensitive: `delete i/a0123456b` and `delete i/A0123456B` identify the same student.
* The student is located in the complete record list, even when hidden by a `find` filter.
* The current filter is preserved after deletion. If the student was hidden, the visible list can stay unchanged.
* Leading/trailing whitespace is ignored, and a tab can separate `delete` from `i/`. Prefixes remain lowercase.
* The command must occupy one line. Numeric index commands such as `delete 1` are no longer accepted.
* Deletion becomes visible only after saving succeeds. A save failure retains the entire record and previous data file.

Examples:

* `delete i/A0123456B` deletes the student with that ID.
* After `find Betsy`, `delete i/A0123456B` still deletes that ID, whether or not the student appears in the results.

Successful output, when the student is Alex Yeoh:

```text
Deleted student A0123456B: Alex Yeoh.
```

| Problem | Message |
|---------|---------|
| Missing Student ID | `Missing required parameter: i/.` |
| Empty Student ID | `Parameter i/ cannot be empty.` |
| Repeated Student ID | `Parameter i/ must be specified only once.` |
| Unknown parameter | `Unknown parameter: x/.` (the message names the supplied prefix) |
| Invalid Student ID | `Student ID must contain exactly 9 letters or digits, with no spaces.` |
| Student does not exist | `Student with ID A0123456B is not in the records.` |
| Invalid command structure | `Invalid command format. Usage: delete i/STUDENT_ID` |
| Saving fails | `Unable to save student data.` |

Malformed commands and unknown IDs leave records, the current filter, and the saved file unchanged.
If loading failed at startup, deletion is blocked until the data file is fixed and TeachAssist is restarted.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

TeachAssist automatically saves after commands that change student records (`add`, `edit`, `delete`, and `clear`).
You do not need to save manually. Commands such as `find`, `list`, `help`, and `exit` do not write student data.
If saving fails, the proposed change is not applied, and existing records, the displayed filter, and the previous
saved file remain unchanged. The application displays `Unable to save student data.`
Storage must support atomic file replacement; unsupported storage produces the same save error.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, TeachAssist starts with an empty student list and preserves the invalid file.
Commands that modify records are blocked with `Student data is unavailable because loading failed. Fix the data file and restart.`
Commands such as `list` remain available and do not overwrite the invalid file. Back up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.

Each person record must include a valid `studentId`. Older data files that do not include `studentId` for every person may fail to load until the missing values are added manually.
Group labels are saved under each person record as `labels`. Older data files without `labels` still load; those persons simply start with no labels.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME i/STUDENT_ID e/EMAIL [r/REMARK] [l/LABEL_NAME] [t/TAG]...` <br> e.g., `add n/James Ho i/A0123456B e/jamesho@example.com r/Needs help with recursion l/Discrete Math Tutorial t/friend`
**Clear**  | `clear`
**Delete** | `delete i/STUDENT_ID`<br> e.g., `delete i/A0123456B`
**Edit**   | `edit INDEX [n/NAME] [e/EMAIL] [t/TAG]...`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find n/KEYWORD [MORE_KEYWORDS]` or `find i/KEYWORD [MORE_KEYWORDS]` or `find e/KEYWORD [MORE_KEYWORDS]`<br> e.g., `find n/James Jake`
**Label**  | `label l/LABEL_NAME i/STUDENT_ID`<br> e.g., `label l/Discrete Math Tutorial i/A0101010A`
**List**   | `list`
**Help**   | `help`
