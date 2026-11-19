import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Runs the QuickChat registration, login, messaging, and stored-message menus. */
public class ProgrammingA1 {
    /** Application entry point. @param args command-line arguments (not used) */
    public static void main(String[] args) {
        run(new Scanner(System.in), new JsonMessageStore(Path.of("stored_messages.json")));
    }

    static void run(Scanner scanner, JsonMessageStore store) {
        System.out.println("Part 1 - Registration and Login Feature");
        System.out.println("---------------------------------------");

        String firstName = prompt(scanner, "Enter first name: ");
        String lastName = prompt(scanner, "Enter last name: ");
        String username = prompt(scanner, "Create username: ");
        String password = prompt(scanner, "Create password: ");
        String cellPhoneNumber = prompt(scanner, "Enter South African cell number with international code: ");

        Login login = new Login(firstName, lastName, username, password, cellPhoneNumber);
        System.out.println();
        System.out.println(login.getUserNameMessage());
        System.out.println(login.getPasswordMessage());
        System.out.println(login.getCellPhoneNumberMessage());

        String registrationStatus = login.registerUser();
        System.out.println(registrationStatus);
        if (!Login.REGISTRATION_SUCCESS_MESSAGE.equals(registrationStatus)) {
            System.out.println("Registration failed. Please restart the application and try again.");
            return;
        }

        System.out.println();
        System.out.println("Login to the account using the same username and password.");
        String loginUsername = prompt(scanner, "Enter username: ");
        String loginPassword = prompt(scanner, "Enter password: ");
        boolean loginSuccessful = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loginSuccessful));
        if (!loginSuccessful) return;

        runQuickChat(scanner, store);
    }

    /** Coordinates the main menu and message-entry actions. */
    static void runQuickChat(Scanner scanner, JsonMessageStore store) {
        System.out.println();
        System.out.println("Welcome to QuickChat.");

        int messageLimit = readPositiveInt(scanner, "How many messages would you like to enter? ");
        MessageSession session = new MessageSession();
        loadStoredMessages(store, session);

        int completedMessages = 0;
        while (true) {
            displayQuickChatMenu();
            int menuOption = readMenuInt(scanner, "Choose an option: ", 1, 4);
            if (handleQuickChatMenuOption(menuOption, completedMessages, messageLimit, scanner, store, session)) {
                return;
            }
            if (menuOption == 1 && completedMessages < messageLimit) {
                completedMessages = enterNextMessage(scanner, store, session, completedMessages, messageLimit);
            }
        }
    }

    private static void loadStoredMessages(JsonMessageStore store, MessageSession session) {
        try {
            store.loadInto(session);
        } catch (IOException ex) {
            System.out.println("Stored messages could not be loaded: " + ex.getMessage());
        }
    }

    private static void displayQuickChatMenu() {
        System.out.println();
        System.out.println("1. Send Messages");
        System.out.println("2. Show recently sent messages");
        System.out.println("3. Quit");
        System.out.println("4. Stored Messages");
    }

    private static boolean handleQuickChatMenuOption(int option, int completedMessages, int messageLimit,
                                                     Scanner scanner, JsonMessageStore store,
                                                     MessageSession session) {
        switch (option) {
            case 2 -> System.out.println("Coming Soon.");
            case 3 -> {
                System.out.println("Total messages sent: " + session.getTotalSentMessages());
                return true;
            }
            case 4 -> storedMessagesMenu(scanner, store, session);
            case 1 -> {
                if (completedMessages >= messageLimit) {
                    System.out.println("You have reached your message limit.");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported menu option: " + option);
        }
        return false;
    }

    private static int enterNextMessage(Scanner scanner, JsonMessageStore store, MessageSession session,
                                        int completedMessages, int messageLimit) {
        Message message = captureMessage(scanner, session, completedMessages);
        if (!completeMessageAction(scanner, message, store)) {
            return completedMessages;
        }

        int updatedCount = completedMessages + 1;
        System.out.println("Messages entered: " + updatedCount + " of " + messageLimit);
        if (updatedCount == messageLimit) {
            System.out.println("Total messages sent: " + session.getTotalSentMessages());
        }
        return updatedCount;
    }

    /** Handles the six Part 3 operations for stored messages. */
    static void storedMessagesMenu(Scanner scanner, JsonMessageStore store, MessageSession session) {
        while (true) {
            System.out.println();
            System.out.println("--- Stored Messages ---");
            System.out.println("a. Display senders and recipients");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search by message ID");
            System.out.println("d. Search messages by recipient");
            System.out.println("e. Delete a message using its hash");
            System.out.println("f. Display stored-message report");
            System.out.println("g. Return to main menu");

            String option = prompt(scanner, "Choose an option: ").trim().toLowerCase();
            try {
                switch (option) {
                    case "a" -> displaySendersAndRecipients(store);
                    case "b" -> displayLongestStoredMessage(store);
                    case "c" -> searchStoredMessageById(scanner, store, session);
                    case "d" -> searchStoredMessagesByRecipient(scanner, store, session);
                    case "e" -> deleteStoredMessageByHash(scanner, store, session);
                    case "f" -> displayStoredMessageReport(store);
                    case "g" -> { return; }
                    default -> System.out.println("Please choose an option from a to g.");
                }
            } catch (IOException ex) {
                System.out.println("Stored-message operation failed: " + ex.getMessage());
            }
        }
    }

    private static void displaySendersAndRecipients(JsonMessageStore store) throws IOException {
        List<JsonMessageStore.StoredMessage> messages = store.readAll();
        if (messages.isEmpty()) {
            System.out.println("There are no stored messages.");
            return;
        }
        for (JsonMessageStore.StoredMessage message : messages) {
            System.out.println("Sender: " + message.getSender());
            System.out.println("Recipient: " + message.getRecipient());
            System.out.println();
        }
    }

    private static void displayLongestStoredMessage(JsonMessageStore store) throws IOException {
        var longest = store.findLongestMessage();
        if (longest.isEmpty()) {
            System.out.println("There are no stored messages.");
        } else {
            System.out.println("Longest stored message: " + longest.get().getMessage());
        }
    }

    private static void searchStoredMessageById(Scanner scanner, JsonMessageStore store,
                                                 MessageSession session) throws IOException {
        String id = prompt(scanner, "Enter message ID: ").trim();
        Message sessionMessage = session.findMessageById(id);
        if (sessionMessage != null) {
            System.out.println("Recipient: " + sessionMessage.getRecipient());
            System.out.println("Message: " + sessionMessage.getMessageText());
            return;
        }
        var result = store.findByMessageId(id);
        if (result.isEmpty()) {
            System.out.println("No message was found for message ID " + id + ".");
        } else {
            System.out.println("Recipient: " + result.get().getRecipient());
            System.out.println("Message: " + result.get().getMessage());
        }
    }

    private static void searchStoredMessagesByRecipient(Scanner scanner, JsonMessageStore store,
                                                         MessageSession session) throws IOException {
        String recipient = prompt(scanner, "Enter recipient number: ").trim();
        List<Message> sessionResults = session.findMessagesByRecipient(recipient);
        if (!sessionResults.isEmpty()) {
            for (Message message : sessionResults) {
                System.out.println("Message: " + message.getMessageText());
            }
            return;
        }
        List<JsonMessageStore.StoredMessage> results = store.findByRecipient(recipient);
        if (results.isEmpty()) {
            System.out.println("No messages were found for recipient " + recipient + ".");
            return;
        }
        for (JsonMessageStore.StoredMessage message : results) {
            System.out.println("Message: " + message.getMessage());
        }
    }

    private static void deleteStoredMessageByHash(Scanner scanner, JsonMessageStore store,
                                                   MessageSession session) throws IOException {
        String hash = prompt(scanner, "Enter message hash: ").trim();
        if (store.deleteByHash(hash)) {
            session.removeStoredMessageByHash(hash);
            System.out.println("Message successfully deleted.");
        } else {
            System.out.println("No stored message was found with that hash.");
        }
    }

    private static void displayStoredMessageReport(JsonMessageStore store) throws IOException {
        String report = store.createReport();
        if (report.isBlank()) System.out.println("There are no stored messages.");
        else System.out.println(report);
    }

    private static Message captureMessage(Scanner scanner, MessageSession session, int messageNumber) {
        String recipient;
        while (true) {
            recipient = prompt(scanner, "Enter recipient cell number (e.g. +27718693002): ");
            Message candidate = new Message(session, messageNumber, recipient, "temporary");
            if (candidate.checkRecipientCell()) {
                System.out.println(Message.RECIPIENT_SUCCESS);
                break;
            }
            System.out.println(Message.RECIPIENT_ERROR);
        }

        Message message;
        while (true) {
            String text = prompt(scanner, "Enter message (maximum 250 characters): ");
            message = new Message(session, messageNumber, recipient, text);
            String validation = message.validateMessageLength();
            System.out.println(validation);
            if (Message.MESSAGE_READY.equals(validation)) break;
        }
        System.out.println("Message ID generated: " + message.getMessageId());
        System.out.println("Message Hash: " + message.getMessageHash());
        return message;
    }

    /** Applies the user's send, disregard, or store choice to a message. */
    private static boolean completeMessageAction(Scanner scanner, Message message, JsonMessageStore store) {
        while (true) {
            System.out.println("1. Send Message");
            System.out.println("2. Disregard Message");
            System.out.println("3. Store Message");
            int action = readMenuInt(scanner, "Choose a message action: ", 1, 3);
            if (action == 1) {
                message.setAction(Message.Action.SEND);
                System.out.println(message.processMessageAction());
                System.out.println(message.formatDetails());
                return true;
            }
            if (action == 2) {
                message.setAction(Message.Action.DISCARD);
                System.out.println(message.processMessageAction());
                String confirmation = prompt(scanner, "Enter 0 to confirm: ");
                if ("0".equals(confirmation.trim())) {
                    message.markDiscarded();
                    System.out.println("Message deleted.");
                    return true;
                }
                System.out.println("Message was not deleted. Choose an action again.");
                continue;
            }
            try {
                message.setAction(Message.Action.STORE);
                System.out.println(message.storeMessage(store));
                System.out.println("Stored in: " + store.getPath());
                return true;
            } catch (IOException ex) {
                System.out.println("Message could not be stored: " + ex.getMessage());
                System.out.println("Please choose an action again.");
            }
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            Integer parsed = readInteger(scanner, prompt);
            if (parsed != null && parsed > 0) return parsed;
            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    private static int readMenuInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            Integer parsed = readInteger(scanner, prompt);
            if (parsed != null && parsed >= min && parsed <= max) return parsed;
            System.out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    private static Integer readInteger(Scanner scanner, String prompt) {
        String value = prompt(scanner, prompt);
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String prompt(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
