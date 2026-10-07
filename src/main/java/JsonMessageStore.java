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

        Path absolute = path.toAbsolutePath();
        Path parent = absolute.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path temp = Files.createTempFile(parent, "quickchat-", ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            gson.toJson(messages, LIST_TYPE, writer);
        }

        try {
            Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
            Files.move(temp, absolute, StandardCopyOption.REPLACE_EXISTING);
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

    public Path getPath() {
        return path.toAbsolutePath();
    }

    public static class StoredMessage {
        String messageId;
        int messageNumber;
        String messageHash;
        String recipient;
        String message;
        String status;

        StoredMessage(Message source) {
            this.messageId = source.getMessageId();
            this.messageNumber = source.getMessageNumber();
            this.messageHash = source.getMessageHash();
            this.recipient = source.getRecipient();
            this.message = source.getMessageText();
            this.status = source.getStatus();
        }
    }
}
