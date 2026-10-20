---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# TeachAssist User Guide

TeachAssist is a **desktop application for managing student records, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, TeachAssist can help you manage student information faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [the TeachAssist releases page](https://github.com/AY2627S1-CS2103T-F12-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for TeachAssist.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. With no saved data file, the student list starts empty.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all students.

   * `add n/John Doe i/A0123456B e/johnd@example.com r/Needs help with recursion` : Adds a student named `John Doe`.

   * `delete i/A0123456B` : Deletes the student whose Student ID is `A0123456B`.

   * `clear` : Deletes all students.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [l/LABEL_NAME]` can be used as `n/John Doe l/Tutorial` or as `n/John Doe`.

* Parameters can be in any order.<br>
  For example, `add e/john@example.com i/A0123456B n/John Doe` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows the Help window with the address of [TeachAssist’s User Guide](https://ay2627s1-cs2103t-f12-1.github.io/tp/UserGuide.html).
Click **Copy URL** to copy this address, then paste it into your browser to open the guide.

Format: `help`


### Adding a student: `add`

Adds a student to TeachAssist with identifying information and optional free-text remarks.
Use remarks for observations such as test results, weak topics, and consultation timings.

Format: `add n/NAME i/STUDENT_ID e/EMAIL [r/REMARK] [l/LABEL_NAME]`

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
| Label | Optional. Contains 1–60 Unicode code points, including at least one letter or digit. Allows letters, combining marks, digits, spaces, hyphens, underscores, apostrophes, parentheses, and periods. Surrounding whitespace is trimmed; internal spacing and capitalisation are preserved. At most one `l/` is allowed. Omitting `l/` stores no label. |

Student cards wrap long names and email addresses onto multiple lines so you can read the complete values.
Resizing the window adjusts the wrapping; use the student list's vertical scrollbar to read taller cards.
Wrapping only changes the display and does not add spaces or line breaks to stored values.

Unlike numeric text such as `8/10`, a recognised prefix after `r/` starts another parameter.
For example, `r/Quiz: 8/10 l/Tutorial` stores `Quiz: 8/10` as the remark and `Tutorial` as a label.

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

The rows below follow the order in which errors are checked. If a command has multiple errors, only the first
applicable message is shown. Prefixes and Student IDs in messages reflect the supplied command.

| Problem | Message |
|---------|---------|
| Command spans multiple lines | `Invalid command format!`<br>`help: Shows program usage instructions.`<br>`Example: help` |
| Incorrect command word or capitalisation, e.g., `Add` | `Unknown command.` |
| Unknown parameter, e.g., `p/123` | `Unknown parameter: p/.` |
| Text before the first parameter | `Invalid command format. Usage: add n/NAME i/STUDENT_ID e/EMAIL [r/REMARK] [l/LABEL_NAME]` |
| Repeated parameter, e.g., two `n/` values | `Parameter n/ must be specified only once.` |
| Missing compulsory parameter | `Missing required parameter: i/.` |
| Empty compulsory parameter | `Parameter n/ cannot be empty.` |
| Empty label | `Parameter l/ cannot be empty.` |
| Invalid name | `Name must contain 1-100 characters, include a letter, and use only letters, spaces, apostrophes, hyphens, or periods.` |
| Invalid Student ID | `Student ID must contain exactly 9 letters or digits, with no spaces.` |
| Invalid email | `Email must have the form name@example.com and meet the supported email format.` |
| Invalid label | `Label names should be 1 to 60 characters long, contain at least one letter or digit, and only contain letters, digits, spaces, hyphens, underscores, apostrophes, parentheses, and periods.` |
| Oversized remark | `Remark must not exceed 4000 characters.` |
| Student data failed to load at startup | `Student data is unavailable because loading failed. Fix the data file and restart.` |
| Duplicate Student ID | `Student ID A0123456B already exists.` |
| Data storage location cannot be created | `Unable to create data storage location.` |
| Saving fails | `Unable to save student data.` |

For example, `add n/ i/123 e/bad p/123` reports `Unknown parameter: p/.` before checking the empty name,
invalid Student ID, or invalid email. When multiple parameters are repeated or missing, they are checked in
`n/`, `i/`, `e/`, `r/`, `l/` order where applicable. Field values are validated in name, Student ID, email,
label, then remark order. A multiline command is rejected by the general command parser before the add parser
can display add-specific usage. The startup-loading check occurs after parsing; duplicate and saving checks
occur only after a valid command passes that check.
Failed commands do not change existing student records or the saved file.

### Listing all students: `list`

Displays every student stored in TeachAssist. If the displayed list was previously restricted by `find`, this command restores the complete student list.

Format: `list`

Example:

```text
list
```

If at least one student exists, the expected successful output is:

```text
Listed all students.
```

If there are no stored students, the displayed list remains empty and the command reports:

```text
No students found.
```

The command does not add, edit, delete, reorder, or save student records. Students remain in the existing master-list
order.

The `list` command does not accept additional arguments. Leading and trailing whitespace is ignored, but command words
are case-sensitive.

| Problem | Example | Message |
|---------|---------|---------|
| Additional argument | `list 3` | `Invalid command format!` followed by `Usage: list` on the next line |
| Parameter-like argument | `list n/Samuel` or `list n/` | `Invalid command format!` followed by `Usage: list` on the next line |
| Misspelled command | `lisst` | `Unknown command.` |
| Incorrect capitalisation | `List` or `LIST` | `Unknown command.` |

### Editing a student: `edit`

Edits a student identified by Student ID, including a student hidden by the current filter.

Format: `edit i/STUDENT_ID [n/NAME] [e/EMAIL] [r/REMARK] [l/LABEL_NAME]... [dl/LABEL_NAME]...`

* `i/STUDENT_ID` is required and must identify an existing student. Matching is case-insensitive.
* At least one field to change must be provided. Student ID itself cannot be edited.
* `n/` and `e/` replace the existing name and email. Their values cannot be empty and follow the rules under `add`.
* `r/` replaces the existing remark. An empty `r/` clears it.
* Each `l/LABEL_NAME` adds a label; each `dl/LABEL_NAME` removes one. Both may be repeated, and label matching is case-insensitive. Label names follow the `l/LABEL_NAME` rules in the Add section.
* An empty `l/` clears **all** labels. It cannot be combined with another `l/` or any `dl/` in the same command.
* Fields not supplied keep their existing values. A successful edit displays the complete student list.

Examples:

* `edit i/A0123456B e/johndoe@example.com` changes that student's email address.
* `edit i/A0123456B l/Tutorial 1` adds a group label.
* `edit i/A0123456B dl/Tutorial 1 l/Tutorial 2` replaces one group label while keeping the others.
* `edit i/A0123456B l/ r/` removes every label and clears the remark.

If a `dl/` label is absent, an `l/` label already exists, or an empty `l/` is combined with another label change, the edit is rejected and the student remains unchanged.

### Locating students by name, student ID, email, or label: `find`

Finds students whose name, student ID, or email contains any of the given keywords, or who have the specified label.

Format: `find n/KEYWORD [MORE_KEYWORDS]` or `find i/KEYWORD [MORE_KEYWORDS]` or `find e/KEYWORD [MORE_KEYWORDS]` or `find l/LABEL_NAME`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* The search accepts exactly one prefix: `n/`, `i/`, `e/`, or lowercase `l/`. Search fields cannot be combined.
* Name, student ID, and email searches support partial matches; for example, `Han` matches `Hans`.
* Label searches match the complete label name. Spaces are part of the label: `find l/Discrete Math Tutorial` matches `Discrete Math Tutorial`, while `find l/Math` does not.
* Label names follow the Label row in the [parameter rules](#parameter-rules). Leading and trailing spaces are ignored; internal spacing is preserved.
* Each search replaces the previous results. If no students match, the displayed list is cleared and the result count is zero. Student records and the saved file are unchanged.

Examples:

* `find l/discrete math tutorial` returns students labelled `Discrete Math Tutorial`.
* `find i/a03 a02` returns students whose student ids start with `a03` or `a02`
* `find n/John` returns `john` and `John Doe`
* `find n/tri sam` returns `tristan`, `samuel`<br>
  ![result for 'find n/tri sam'](images/findIncompleteResult.png)

#### Errors

| Problem | Message |
|---------|---------|
| Extra words before the search parameter | `Invalid command format!` followed by the correct `find` format |
| Unknown search parameter | `Unknown parameter: p/.` |
| Repeated same search parameter | `Parameter n/ must be specified only once.` |
| Missing search parameter | `Missing required parameter: n/, i/, e/, or l/.` |
| More than one search parameter type | `Parameter n/, i/, e/, or l/ must be specified only once.` |
| Empty search parameter | `Parameter n/ cannot be empty.` |
| Empty label parameter | `Parameter l/ cannot be empty.` |
| Repeated label parameter | `Parameter l/ must be specified only once.` |
| Invalid label name | `Label names should be 1 to 60 characters long, contain at least one letter or digit, and only contain letters, digits, spaces, hyphens, underscores, apostrophes, parentheses, and periods.` |

The parser checks for errors in the order of extra words before the first search parameter, unknown parameters,
repeated parameters, missing parameters, multiple search parameter types, then empty values and label validity before searching.
Failed commands do not change the displayed student records or the saved file.

### Deleting a student: `delete`

Deletes the student with the specified Student ID, including their name, email, remark, and labels.

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
* After `find n/Betsy`, `delete i/A0123456B` still deletes that ID, whether or not the student appears in the results.

Successful output, when the student is Alex Yeoh:

```text
Deleted student A0123456B: Alex Yeoh.
```

The rows below follow error-checking order. If several errors are present, only the first applicable message
is shown. Prefixes and Student IDs in messages reflect the supplied command.

| Problem | Message |
|---------|---------|
| Command spans multiple lines | `Invalid command format!`<br>`help: Shows program usage instructions.`<br>`Example: help` |
| Incorrect command word or capitalisation, e.g., `Delete` | `Unknown command.` |
| Unknown parameter, e.g., `x/123` | `Unknown parameter: x/.` |
| Text before the `i/` parameter | `Invalid command format. Usage: delete i/STUDENT_ID` |
| Repeated Student ID | `Parameter i/ must be specified only once.` |
| Missing Student ID | `Missing required parameter: i/.` |
| Empty Student ID | `Parameter i/ cannot be empty.` |
| Invalid Student ID | `Student ID must contain exactly 9 letters or digits, with no spaces.` |
| Student data failed to load at startup | `Student data is unavailable because loading failed. Fix the data file and restart.` |
| Student does not exist | `Student with ID A0123456B is not in the records.` |
| Data storage location cannot be created | `Unable to create data storage location.` |
| Saving fails | `Unable to save student data.` |

For example, `delete i/123 x/1` reports `Unknown parameter: x/.` before checking the invalid Student ID.
The startup-loading check occurs after parsing. Finding the student and saving happen only after a valid command
passes that check. Failed commands leave records, the current filter, and the saved file unchanged.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Loading the data

When TeachAssist starts, it loads student data from `[JAR file location]/data/addressbook.json` and shows the outcome
in the result display.

The rows below follow the order in which loading is checked. Records are checked in the order they appear in the file,
and only the first problem found is shown.

| Situation | Message |
|-----------|---------|
| No data file exists yet | `No existing student data found. Starting with an empty student list.` |
| The data file cannot be read, e.g., file permissions deny access | `Unable to load student data.` |
| The data file is not valid JSON, or a record has a missing or invalid value | `Unable to load student data. The data file may be corrupted or invalid.` |
| Two records share a Student ID, ignoring case | `Unable to load student data: duplicate Student ID A0123456B detected.` |
| Loading succeeds | `3 students loaded.` (or `1 student loaded.`) |

If loading fails, TeachAssist starts with an empty student list and leaves the data file untouched:

* Commands that change student records (`add`, `edit`, `delete`, and `clear`) are blocked with
  `Student data is unavailable because loading failed. Fix the data file and restart.`
* Commands that do not change student records, such as `list`, `find`, `help`, and `exit`, remain available.

To recover, close TeachAssist, fix or restore the data file, and restart. Deleting the data file makes TeachAssist start
with an empty student list.

### Saving the data

TeachAssist automatically saves after commands that change student records (`add`, `edit`, `delete`, and `clear`).
You do not need to save manually. Commands such as `find`, `list`, `help`, and `exit` do not write student data.
If saving fails, the proposed change is not applied, and existing records, the displayed filter, and the previous
saved file remain unchanged. The application displays `Unable to save student data.`
Storage must support atomic file replacement; unsupported storage produces the same save error.
If the `data` folder does not exist and cannot be created, e.g., because a file named `data` is in its place or the
JAR file location is read-only, the application displays `Unable to create data storage location.` instead.

### Editing the data file

TeachAssist data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, TeachAssist starts with an empty student list and preserves the invalid file.
See [Loading the data](#loading-the-data) for the message shown and the commands that remain available.
Back up the file before editing it.<br>
Furthermore, certain edits can cause TeachAssist to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.

Each person record must include a valid `studentId`. Older data files that do not include `studentId` for every person may fail to load until the missing values are added manually.
Group labels are saved under each person record as `labels`. Older data files without `labels` still load; those persons simply start with no labels.
Legacy `tags` fields are ignored when loading and are not saved again.
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
**Add**    | `add n/NAME i/STUDENT_ID e/EMAIL [r/REMARK] [l/LABEL_NAME]` <br> e.g., `add n/James Ho i/A0123456B e/jamesho@example.com r/Needs help with recursion l/Discrete Math Tutorial`
**Clear**  | `clear`
**Delete** | `delete i/STUDENT_ID`<br> e.g., `delete i/A0123456B`
**Edit**   | `edit i/STUDENT_ID [n/NAME] [e/EMAIL] [r/REMARK] [l/LABEL_NAME]... [dl/LABEL_NAME]...`<br> e.g., `edit i/A0123456B dl/Tutorial 1 l/Tutorial 2`
**Find**   | `find n/KEYWORD [MORE_KEYWORDS]` or `find i/KEYWORD [MORE_KEYWORDS]` or `find e/KEYWORD [MORE_KEYWORDS]` or `find l/LABEL_NAME`<br> e.g., `find n/James Jake` or `find l/Discrete Math Tutorial`
**List**   | `list`
**Help**   | `help`
