# PROG5121 POE — Programming 1A

Prepared for Sekonyela Clive Mokoena, ST10449919.

This repository contains the work completed for **Part 1** and **Part 2** of the PROG5121 POE. Part 1 covers registration and login validation. Part 2 extends the same application with the QuickChat messaging feature.

## Part 1 — Registration and Login

Part 1 implements the required account registration and login functionality.

### Functionality

- First name and last name capture
- Username validation
  - must contain an underscore
  - must be no more than five characters long
- Password complexity validation
  - at least eight characters
  - at least one capital letter
  - at least one number
  - at least one special character
- South African cellphone number validation using an international code
- Registration status messages
- Login using the registered username and password
- Welcome message after a successful login
- Error message for an unsuccessful login

### Part 1 test examples for NetBeans presentation

Use these exact examples when demonstrating Part 1.

| Test | Input | Expected result |
| --- | --- | --- |
| Valid username | `kyl_1` | `Username successfully captured.` |
| Invalid username | `kyle!!!!!!!` | Username formatting error |
| Valid password | `Ch&&sec@ke99!` | `Password successfully captured.` |
| Invalid password | `password` | Password complexity error |
| Valid cellphone | `+27838968976` | `Cell number successfully captured.` |
| Invalid cellphone | `08966553` | Cellphone formatting error |
| Successful registration | `kyl_1`, `Ch&&sec@ke99!`, `+27838968976` | `User has been registered successfully.` |
| Successful login | Username `kyl_1`, password `Ch&&sec@ke99!` | `Welcome Kyle, Smith it is great to see you again.` |
| Failed login | Username `kyl_1`, password `password` | `Username or password incorrect, please try again.` |

#### Full successful Part 1 run

Enter:

```text
First name: Kyle
Last name: Smith
Username: kyl_1
Password: Ch&&sec@ke99!
Cell number: +27838968976
```

Expected validation and registration messages:

```text
Username successfully captured.
Password successfully captured.
Cell number successfully captured.
User has been registered successfully.
```

Then log in with:

```text
Username: kyl_1
Password: Ch&&sec@ke99!
```

Expected result:

```text
Welcome Kyle, Smith it is great to see you again.
```

#### Invalid Part 1 example

Restart the application and enter:

```text
First name: Kyle
Last name: Smith
Username: kyle!!!!!!!
Password: password
Cell number: 08966553
```

Expected errors:

```text
Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.
Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.
Cell phone number is incorrectly formatted or does not contain an international code; please correct the number and try again.
```

Because registration is invalid, the program displays:

```text
Registration failed. Please restart the application and try again.
```

### Part 1 class

`src/main/java/Login.java`

The class contains the Part 1 validation, registration and login logic.

### Part 1 tests

`src/test/java/LoginTest.java`

The original Part 1 test suite is retained and checks both valid and invalid username, password, cellphone, registration and login cases.

---

## Part 2 — QuickChat: Sending Messages

Part 2 extends the Part 1 application. A user must register and log in successfully before QuickChat becomes available.

### QuickChat menu

After login, the application displays:

```text
Welcome to QuickChat.

1. Send Messages
2. Show recently sent messages
3. Quit
```

Option 2 intentionally returns:

```text
Coming Soon.
```

This is required by the Part 2 brief because the recently-sent-messages feature is still in development at this stage.

### Message functionality

- The user chooses how many message entries may be completed during the run.
- Each message receives a randomly generated unique 10-digit Message ID.
- The recipient number is validated before the message can continue.
- Message text must not exceed 250 characters.
- Each message receives an automatically generated Message Hash.
- The hash contains:
  - the first two digits of the Message ID
  - the message number
  - the first and last words of the message in uppercase
- The user can:
  1. Send Message
  2. Disregard Message
  3. Store Message
- A disregarded message requires `0` confirmation before deletion.
- A stored message is written to `stored_messages.json` using Gson.
- Only messages actually sent increase the sent-message total.
- Sent message details are shown in the required order:
  1. Message ID
  2. Message Hash
  3. Recipient
  4. Message
- The total number of sent messages is displayed when the configured message-entry limit is completed and when the user quits.

### Required Part 2 methods

The `Message` class includes:

- `checkMessageID()`
- `checkRecipientCell()`
- `createMessageHash()`
- `SentMessage()`
- `printMessages()`
- `returnTotalMessages()`
- `storeMessage()`

### Assignment test data

#### Message 1

- Recipient: `+27718693002`
- Message: `Hi Mike, can you join us for dinner tonight?`
- Action: Send
- With fixed test ID `0012345678`, expected hash: `00:0:HITONIGHT`

#### Message 2

- Recipient supplied by the brief: `08575975889`
- Message: `Hi Keegan, did you receive the payment?`
- Action: Disregard

The supplied second recipient does not contain the required international code, so recipient validation rejects it in the interactive application. The message action is also tested independently in the unit tests.

---

## Project structure

- `src/main/java/Login.java` — Part 1 validation, registration and login
- `src/main/java/ProgrammingA1.java` — complete console flow for Part 1 and Part 2
- `src/main/java/Message.java` — message validation, hash generation and required Part 2 methods
- `src/main/java/MessageSession.java` — unique Message IDs and sent-message session state
- `src/main/java/JsonMessageStore.java` — JSON storage
- `src/test/java/LoginTest.java` — Part 1 tests
- `src/test/java/MessageTest.java` — Part 2 message tests
- `src/test/java/ProgrammingA1Test.java` — QuickChat menu and console-flow tests
- `.github/workflows/java-tests.yml` — automated Maven/JUnit testing on GitHub Actions

## Build and run in NetBeans

Requirements:

- JDK 17 or newer
- Maven support

Open the folder containing `pom.xml` as a Maven project in NetBeans.

To build and run all tests:

```text
mvn clean verify
```

To run the application:

```text
mvn exec:java
```

The Maven project uses JUnit 5 for unit testing and Gson for JSON message storage.

## Current submission stage

The repository currently covers **POE Part 1 and Part 2**.

Part 1 remains included and functional. Part 2 builds on Part 1 rather than replacing it.
