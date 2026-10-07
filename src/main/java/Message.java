public class Message {
    public static final String MESSAGE_READY = "Message ready to send.";
    public static final String RECIPIENT_SUCCESS = "Cell phone number successfully captured.";
    public static final String RECIPIENT_ERROR = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    public static final String SEND_SUCCESS = "Message successfully sent.";
    public static final String DISCARD_PROMPT = "Press 0 to delete the message.";
    public static final String STORE_SUCCESS = "Message successfully stored.";

    public enum Action { SEND, DISCARD, STORE }

    private final MessageSession session;
    private final String messageId;
    private final int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String status;
    private Action action;

    public Message(MessageSession session, int messageNumber, String recipient, String messageText) {
        this(session, session.generateUniqueMessageId(), messageNumber, recipient, messageText);
    }

    Message(MessageSession session, String messageId, int messageNumber, String recipient, String messageText) {
        this.session = session;
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient == null ? "" : recipient.trim();
        this.messageText = messageText == null ? "" : messageText;
        this.messageHash = createMessageHash();
        this.status = "Pending";
    }

    public boolean checkMessageID() {
        return messageId != null && messageId.matches("\\d{10}");
    }

    public boolean checkRecipientCell() {
        return checkRecipientCell(recipient);
    }

    public boolean checkRecipientCell(String value) {
        String candidate = value == null ? "" : value.trim();
        return candidate.matches("^\\+27\\d{9}$");
    }

    public String validateMessageLength() {
        int length = messageText.length();
        if (length == 0) {
            return "Please enter a message of less than 250 characters.";
        }
        if (length <= 250) {
            return MESSAGE_READY;
        }
        return "Message exceeds 250 characters by " + (length - 250) + "; please reduce the size.";
    }

    public String createMessageHash() {
        String trimmed = messageText == null ? "" : messageText.trim();
        if (trimmed.isEmpty()) {
            return messageId.substring(0, 2) + ":" + messageNumber + ":";
        }

        String[] words = trimmed.split("\\s+");
        String first = stripBoundaryPunctuation(words[0]);
        String last = stripBoundaryPunctuation(words[words.length - 1]);
        return (messageId.substring(0, 2) + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    private String stripBoundaryPunctuation(String word) {
        return word.replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String SentMessage() {
        if (action == null) {
            throw new IllegalStateException("Select a message action first.");
        }

        return switch (action) {
            case SEND -> {
                status = "Sent";
                session.registerSentMessage(this);
                yield SEND_SUCCESS;
            }
            case DISCARD -> DISCARD_PROMPT;
            case STORE -> STORE_SUCCESS;
        };
    }

    public String printMessages() {
        StringBuilder output = new StringBuilder();
        for (Message sent : session.getSentMessages()) {
            if (output.length() > 0) {
                output.append(System.lineSeparator()).append(System.lineSeparator());
            }
            output.append(sent.formatDetails());
        }
        return output.toString();
    }

    public String formatDetails() {
        return "Message ID: " + messageId + System.lineSeparator()
                + "Message Hash: " + messageHash + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + messageText;
    }

    public int returnTotalMessages() {
        return session.getTotalSentMessages();
    }

    public String storeMessage(JsonMessageStore store) throws java.io.IOException {
        status = "Stored";
        store.append(this);
        return STORE_SUCCESS;
    }

    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }

    public void markDiscarded() {
        status = "Discarded";
    }
}
