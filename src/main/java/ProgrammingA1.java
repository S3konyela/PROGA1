import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class ProgrammingA1 {
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

        if (!loginSuccessful) {
            return;
        }

        runQuickChat(scanner, store);
    }

    static void runQuickChat(Scanner scanner, JsonMessageStore store) {
        System.out.println();
        System.out.println("Welcome to QuickChat.");

        int messageLimit = readPositiveInt(scanner, "How many messages would you like to enter? ");
        MessageSession session = new MessageSession();
        int completedMessages = 0;

        while (true) {
            System.out.println();
            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Quit");

            int menuOption = readMenuInt(scanner, "Choose an option: ", 1, 3);

            if (menuOption == 2) {
                System.out.println("Coming Soon.");
                continue;
            }

            if (menuOption == 3) {
                System.out.println("Total messages sent: " + session.getTotalSentMessages());
                return;
            }

            if (completedMessages >= messageLimit) {
                System.out.println("You have reached your message limit.");
                continue;
            }

            Message message = captureMessage(scanner, session, completedMessages);

            if (completeMessageAction(scanner, message, store)) {
                completedMessages++;
                System.out.println("Messages entered: " + completedMessages + " of " + messageLimit);

                if (completedMessages == messageLimit) {
                    System.out.println("Total messages sent: " + session.getTotalSentMessages());
                }
            }
        }
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

            if (Message.MESSAGE_READY.equals(validation)) {
                break;
            }
        }

        System.out.println("Message ID generated: " + message.getMessageId());
        System.out.println("Message Hash: " + message.getMessageHash());

        return message;
    }

    private static boolean completeMessageAction(Scanner scanner, Message message, JsonMessageStore store) {
        while (true) {
            System.out.println("1. Send Message");
            System.out.println("2. Disregard Message");
            System.out.println("3. Store Message");

            int action = readMenuInt(scanner, "Choose a message action: ", 1, 3);

            if (action == 1) {
                message.setAction(Message.Action.SEND);
                System.out.println(message.SentMessage());
                System.out.println(message.formatDetails());
                return true;
            }

            if (action == 2) {
                message.setAction(Message.Action.DISCARD);
                System.out.println(message.SentMessage());

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
            String value = prompt(scanner, prompt);

            try {
                int parsed = Integer.parseInt(value.trim());
                if (parsed > 0) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    private static int readMenuInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            String value = prompt(scanner, prompt);

            try {
                int parsed = Integer.parseInt(value.trim());
                if (parsed >= min && parsed <= max) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }

    private static String prompt(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
