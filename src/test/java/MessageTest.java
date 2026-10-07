import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    @TempDir
    Path tempDir;

    private Message fixed(String text, String recipient, int number) {
        return new Message(new MessageSession(), "0012345678", number, recipient, text);
    }

    @Test
    void validMessageLengthReturnsReady() {
        assertEquals("Message ready to send.",
                fixed("Hi Mike, can you join us for dinner tonight?", "+27718693002", 0).validateMessageLength());
    }

    @Test
    void overLengthMessageReportsExactExcess() {
        String text = "x".repeat(255);
        assertEquals("Message exceeds 250 characters by 5; please reduce the size.",
                fixed(text, "+27718693002", 0).validateMessageLength());
    }

    @Test
    void validRecipientReturnsTrue() {
        assertTrue(fixed("Hi", "+27718693002", 0).checkRecipientCell());
    }

    @Test
    void invalidRecipientReturnsFalse() {
        assertFalse(fixed("Hi", "08575975889", 0).checkRecipientCell());
    }

    @Test
    void suppliedHashIsCorrect() {
        assertEquals("00:0:HITONIGHT",
                fixed("Hi Mike, can you join us for dinner tonight?", "+27718693002", 0).createMessageHash());
    }

    @Test
    void secondHashUsesMessageNumber() {
        assertEquals("00:1:HIPAYMENT",
                fixed("Hi Keegan, did you receive the payment?", "+27718693002", 1).createMessageHash());
    }

    @Test
    void generatedMessageIdHasTenDigits() {
        Message message = new Message(new MessageSession(), 0, "+27718693002", "Hi");
        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageId().length());
    }

    @Test
    void sendActionReturnsRequiredTextAndCountsMessage() {
        Message message = fixed("Hi", "+27718693002", 0);
        message.setAction(Message.Action.SEND);

        assertEquals("Message successfully sent.", message.SentMessage());
        assertEquals(1, message.returnTotalMessages());
    }

    @Test
    void discardActionReturnsRequiredPromptAndDoesNotCount() {
        Message message = fixed("Hi", "08575975889", 1);
        message.setAction(Message.Action.DISCARD);

        assertEquals("Press 0 to delete the message.", message.SentMessage());
        assertEquals(0, message.returnTotalMessages());
    }

    @Test
    void storeWritesJsonAndDoesNotCountAsSent() throws Exception {
        Message message = fixed("Stored \"message\" ✓", "+27718693002", 0);
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("stored_messages.json"));

        assertEquals("Message successfully stored.", message.storeMessage(store));

        String json = java.nio.file.Files.readString(store.getPath());
        assertTrue(json.contains("Stored \\\"message\\\" ✓"));
        assertTrue(json.contains("\"status\": \"Stored\""));
        assertEquals(0, message.returnTotalMessages());
    }

    @Test
    void storeAppendsInsteadOfReplacing() throws Exception {
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("stored_messages.json"));

        fixed("First", "+27718693002", 0).storeMessage(store);
        fixed("Second", "+27718693002", 1).storeMessage(store);

        assertEquals(2, store.readAll().size());
    }

    @Test
    void printMessagesUsesRequiredOrder() {
        MessageSession session = new MessageSession();
        Message message = new Message(session, "0012345678", 0, "+27718693002", "Hi Mike");
        message.setAction(Message.Action.SEND);
        message.SentMessage();

        String output = message.printMessages();

        assertTrue(output.indexOf("Message ID:") < output.indexOf("Message Hash:"));
        assertTrue(output.indexOf("Message Hash:") < output.indexOf("Recipient:"));
        assertTrue(output.indexOf("Recipient:") < output.indexOf("Message:"));
    }
}
