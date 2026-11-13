import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageSession {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Set<String> issuedIds = new HashSet<>();
    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIds = new ArrayList<>();
    private String sender = "Developer";

    public String generateUniqueMessageId() {
        String id;
        do {
            id = String.format("%010d", RANDOM.nextLong(10_000_000_000L));
        } while (!issuedIds.add(id));
        return id;
    }

    public void setSender(String sender) {
        if (sender != null && !sender.isBlank()) this.sender = sender.trim();
    }

    public String getSender() { return sender; }

    private void registerMetadata(Message message) {
        if (!messageIds.contains(message.getMessageId())) {
            messageIds.add(message.getMessageId());
            messageHashes.add(message.getMessageHash());
        }
    }

    public void registerSentMessage(Message message) {
        if (!sentMessages.contains(message)) {
            sentMessages.add(message);
            registerMetadata(message);
        }
    }

    public void registerDisregardedMessage(Message message) {
        if (!disregardedMessages.contains(message)) {
            disregardedMessages.add(message);
            registerMetadata(message);
        }
    }

    public void registerStoredMessage(Message message) {
        if (!storedMessages.contains(message)) {
            storedMessages.add(message);
            registerMetadata(message);
        }
    }

    public boolean removeStoredMessageByHash(String hash) {
        Message found = storedMessages.stream()
                .filter(message -> message.getMessageHash().equals(hash))
                .findFirst().orElse(null);
        if (found == null) return false;
        storedMessages.remove(found);
        return true;
    }

    public List<Message> getSentMessages() { return List.copyOf(sentMessages); }
    public List<Message> getDisregardedMessages() { return List.copyOf(disregardedMessages); }
    public List<Message> getStoredMessages() { return List.copyOf(storedMessages); }

    public List<String> getSentMessageArray() {
        return sentMessages.stream().map(Message::getMessageText).collect(Collectors.toUnmodifiableList());
    }
    public List<String> getDisregardedMessageArray() {
        return disregardedMessages.stream().map(Message::getMessageText).collect(Collectors.toUnmodifiableList());
    }
    public List<String> getStoredMessageArray() {
        return storedMessages.stream().map(Message::getMessageText).collect(Collectors.toUnmodifiableList());
    }
    public List<String> getMessageHashArray() { return List.copyOf(messageHashes); }
    public List<String> getMessageIdArray() { return List.copyOf(messageIds); }

    public Message findMessageById(String id) {
        return allSessionMessages().stream()
                .filter(message -> message.getMessageId().equals(id))
                .findFirst().orElse(null);
    }

    public List<Message> findMessagesByRecipient(String recipient) {
        return allSessionMessages().stream()
                .filter(message -> message.getRecipient().equals(recipient))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Message> allSessionMessages() {
        List<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(disregardedMessages);
        all.addAll(storedMessages);
        return List.copyOf(all);
    }

    public int getTotalSentMessages() { return sentMessages.size(); }
}
