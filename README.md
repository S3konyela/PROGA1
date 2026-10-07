# QuickChat — PROG5121 POE Part 2

Prepared for Sekonyela Clive Mokoena, ST10449919.

This repository now contains the Part 1 registration/login work plus the Part 2 QuickChat messaging requirements.

## Part 2 functionality

- Login is required before QuickChat opens.
- Displays `Welcome to QuickChat.`
- Numeric menu:
  1. Send Messages
  2. Show recently sent messages
  3. Quit
- Option 2 intentionally displays `Coming Soon.`, as required by the Part 2 brief.
- User chooses how many message entries may be completed in the run.
- Each message receives a unique 10-digit message ID.
- Recipient numbers are validated using the South African international format used by the assignment examples.
- Message text is limited to 250 characters.
- Message hashes use the first two ID digits, message number, and first/last message words.
- Send, Disregard and Store actions are implemented.
- Disregard requires `0` confirmation before the message is deleted.
- Store writes the message to `stored_messages.json` using Gson.
- Only sent messages increase the sent-message total.
- The final sent total is displayed when the configured entry limit is completed and again when the user quits.

## Required Part 2 methods

The `Message` class includes:

- `checkMessageID()`
- `checkRecipientCell()`
- `createMessageHash()`
- `SentMessage()`
- `printMessages()`
- `returnTotalMessages()`
- `storeMessage()`

## Assignment test data

Message 1:

- Recipient: `+27718693002`
- Message: `Hi Mike, can you join us for dinner tonight?`
- Action: Send
- With fixed test ID `0012345678`, expected hash: `00:0:HITONIGHT`

Message 2:

- Recipient supplied by the brief: `08575975889`
- Message: `Hi Keegan, did you receive the payment?`
- Action: Disregard

The second supplied number is intentionally tested as invalid because it does not contain the international code required by the validation rule.

## Build and test

Requirements:

- JDK 17+
- Maven

Run:

```text
mvn clean verify
```

Run the console application with:

```text
mvn exec:java
```

The Maven build includes Gson for JSON storage and JUnit 5 for automated tests.

## Project structure

- `src/main/java/Login.java` — Part 1 registration and login
- `src/main/java/ProgrammingA1.java` — registration, login and QuickChat console flow
- `src/main/java/Message.java` — Part 2 message model and required methods
- `src/main/java/MessageSession.java` — unique IDs and sent-message session state
- `src/main/java/JsonMessageStore.java` — JSON persistence
- `src/test/java/LoginTest.java` — Part 1 tests
- `src/test/java/MessageTest.java` — Part 2 message tests
- `src/test/java/ProgrammingA1Test.java` — QuickChat console flow tests
- `.github/workflows/java-tests.yml` — GitHub Actions test workflow

## Important Part 2 note

Do not replace menu option 2 with a real sent-message screen for this submission. The assignment explicitly states that this feature is still in development and must display `Coming Soon.`.
