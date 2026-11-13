import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonMessageStore {
    private static final Type LIST_TYPE = new TypeToken<List<StoredMessage>>() {}.getType();

    private final Path path;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonMessageStore(Path path) {
        this.path = path;
    }

    public void append(Message message) throws IOException {
        List<StoredMessage> messages = readAll();
        messages.add(new StoredMessage(message));
        writeAll(messages);
    }

    private void writeAll(List<StoredMessage> messages) throws IOException {
        Path absolute = path.toAbsolutePath();
        Path parent = absolute.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temp = Files.createTempFile(parent, "quickchat-", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                gson.toJson(messages, LIST_TYPE, writer);
            }
            try {
                Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
                Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    public List<StoredMessage> readAll() throws IOException {
        Path absolute = path.toAbsolutePath();
        if (!Files.exists(absolute) || Files.size(absolute) == 0) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(absolute, StandardCharsets.UTF_8)) {
            List<StoredMessage> values = gson.fromJson(reader, LIST_TYPE);
            return values == null ? new ArrayList<>() : new ArrayList<>(values);
        }
    }

    public Optional<StoredMessage> findByMessageId(String messageId) throws IOException {
        return readAll().stream()
                .filter(message -> message.getMessageId().equals(messageId))
                .findFirst();
    }

    public List<StoredMessage> findByRecipient(String recipient) throws IOException {
        return readAll().stream()
                .filter(message -> message.getRecipient().equals(recipient))
                .collect(Collectors.toUnmodifiableList());
    }

    public Optional<StoredMessage> findLongestMessage() throws IOException {
        return readAll().stream()
                .max((first, second) -> Integer.compare(first.getMessage().length(), second.getMessage().length()));
    }

    public boolean deleteByHash(String messageHash) throws IOException {
        List<StoredMessage> messages = readAll();
        boolean removed = messages.removeIf(message -> message.getMessageHash().equals(messageHash));
        if (removed) {
            writeAll(messages);
        }
        return removed;
    }

    public Path getPath() {
        return path.toAbsolutePath();
    }

    public static class StoredMessage {
        String sender;
        String messageId;
        int messageNumber;
        String messageHash;
        String recipient;
        String message;
        String status;

        StoredMessage(Message source) {
            this.sender = source.getSender();
            this.messageId = source.getMessageId();
            this.messageNumber = source.getMessageNumber();
            this.messageHash = source.getMessageHash();
            this.recipient = source.getRecipient();
            this.message = source.getMessageText();
            this.status = source.getStatus();
        }

        public String getSender() { return sender == null || sender.isBlank() ? "Unknown" : sender; }
        public String getMessageId() { return messageId == null ? "" : messageId; }
        public String getMessageHash() { return messageHash == null ? "" : messageHash; }
        public String getRecipient() { return recipient == null ? "" : recipient; }
        public String getMessage() { return message == null ? "" : message; }
        public String getStatus() { return status == null ? "" : status; }
    }
}
