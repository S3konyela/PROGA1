import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageSession {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Set<String> issuedIds = new HashSet<>();
    private final List<Message> sentMessages = new ArrayList<>();

    public String generateUniqueMessageId() {
        String id;
        do {
            long number = RANDOM.nextLong(10_000_000_000L);
            id = String.format("%010d", number);
        } while (!issuedIds.add(id));
        return id;
    }

    public void registerSentMessage(Message message) {
        if (!sentMessages.contains(message)) {
            sentMessages.add(message);
        }
    }

    public List<Message> getSentMessages() {
        return List.copyOf(sentMessages);
    }

    public int getTotalSentMessages() {
        return sentMessages.size();
    }
}
