import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoredMessagesPart3Test {
    @TempDir
    Path tempDir;

    private Message message(MessageSession session, String id, int number, String recipient, String text) {
        return new Message(session, id, number, recipient, text);
    }

    private Message sent(MessageSession session, String id, int number, String recipient, String text) {
        Message value = message(session, id, number, recipient, text);
        value.setAction(Message.Action.SEND);
        value.SentMessage();
        return value;
    }

    private Message stored(MessageSession session, JsonMessageStore store, String id, int number,
                           String recipient, String text) throws Exception {
        Message value = message(session, id, number, recipient, text);
        value.storeMessage(store);
        return value;
    }

    @Test
    void sentMessagesArrayIsCorrectlyPopulatedWithMessagesOneAndFour() {
        MessageSession session = new MessageSession();
        sent(session, "0000000001", 0, "+27834557896", "Did you get the cake?");
        sent(session, "0838884567", 3, "+27834557896", "It is dinner time !");

        assertEquals(List.of("Did you get the cake?", "It is dinner time !"),
                session.getSentMessageArray());
    }

    @Test
    void longestStoredMessageReturnsMessageTwo() throws Exception {
        MessageSession session = new MessageSession();
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("stored_messages.json"));
        stored(session, store, "0000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        stored(session, store, "0000000005", 4, "+27838884567",
                "Ok, I am leaving without you.");

        assertEquals("Where are you? You are late! I have asked you to be on time.",
                store.findLongestMessage().orElseThrow().getMessage());
    }

    @Test
    void searchByMessageIdReturnsMessageFourText() {
        MessageSession session = new MessageSession();
        sent(session, "0838884567", 3, "+27834557896", "It is dinner time !");

        Message found = session.findMessageById("0838884567");

        assertNotNull(found);
        assertEquals("It is dinner time !", found.getMessageText());
    }

    @Test
    void searchByRecipientReturnsMessagesTwoAndFive() throws Exception {
        MessageSession session = new MessageSession();
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("stored_messages.json"));
        stored(session, store, "0000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        stored(session, store, "0000000005", 4, "+27838884567",
                "Ok, I am leaving without you.");

        assertEquals(List.of(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."),
                session.findMessagesByRecipient("+27838884567").stream()
                        .map(Message::getMessageText).toList());
    }

    @Test
    void messageTwoCanBeDeletedUsingItsMessageHash() throws Exception {
        MessageSession session = new MessageSession();
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("stored_messages.json"));
        Message messageTwo = stored(session, store, "0000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");

        assertTrue(store.deleteByHash(messageTwo.getMessageHash()));
        assertTrue(store.readAll().isEmpty());
    }

    @Test
    void reportContainsMessageHashRecipientAndMessageForStoredMessages() throws Exception {
        MessageSession session = new MessageSession();
        JsonMessageStore store = new JsonMessageStore(tempDir.resolve("stored_messages.json"));
        Message messageTwo = stored(session, store, "0000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");

        String report = store.createReport();

        assertTrue(report.contains("Message Hash: " + messageTwo.getMessageHash()));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message: Where are you? You are late! I have asked you to be on time."));
    }
}
