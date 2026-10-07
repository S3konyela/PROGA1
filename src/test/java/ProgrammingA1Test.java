import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ProgrammingA1Test {
    @TempDir
    Path tempDir;

    private String runQuickChat(String input) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));

        try {
            ProgrammingA1.runQuickChat(
                    new Scanner(input),
                    new JsonMessageStore(tempDir.resolve("stored_messages.json")));
            return bytes.toString();
        } finally {
            System.setOut(oldOut);
        }
    }

    @Test
    void optionTwoStillSaysComingSoon() {
        String output = runQuickChat("2\n2\n3\n");
        assertTrue(output.contains("Coming Soon."));
    }

    @Test
    void totalIsShownOnQuit() {
        String output = runQuickChat("1\n3\n");
        assertTrue(output.contains("Total messages sent: 0"));
    }

    @Test
    void finalTotalAppearsWhenConfiguredEntriesComplete() {
        String input = "1\n1\n+27718693002\nHi there\n1\n3\n";
        String output = runQuickChat(input);

        assertTrue(output.contains("Messages entered: 1 of 1"));
        assertTrue(output.contains("Total messages sent: 1"));
    }

    @Test
    void disregardRequiresZeroConfirmation() {
        String input = "1\n1\n+27718693002\nHi there\n2\n0\n3\n";
        String output = runQuickChat(input);

        assertTrue(output.contains("Press 0 to delete the message."));
        assertTrue(output.contains("Message deleted."));
        assertTrue(output.contains("Total messages sent: 0"));
    }

    @Test
    void storeCreatesJson() throws Exception {
        String input = "1\n1\n+27718693002\nSave me\n3\n3\n";
        String output = runQuickChat(input);

        assertTrue(output.contains("Message successfully stored."));
        assertTrue(java.nio.file.Files.exists(tempDir.resolve("stored_messages.json")));
        assertEquals(1,
                new JsonMessageStore(tempDir.resolve("stored_messages.json")).readAll().size());
    }

    @Test
    void messageLimitBlocksExtraEntry() {
        String input = "1\n1\n+27718693002\nHi\n1\n1\n3\n";
        String output = runQuickChat(input);

        assertTrue(output.contains("You have reached your message limit."));
    }
}
