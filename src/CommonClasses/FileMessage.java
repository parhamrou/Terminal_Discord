package CommonClasses;

import java.time.format.*;
import java.util.*;

public class FileMessage extends Message{

    private byte[] content;
    private String fileName;

    public FileMessage(User user, String fileName, byte[] content) {
        super(user);
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    public String getText() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatDateTime = getDate().format(formatter);
        return String.format("______________" +
                        "\n%s\t%s\n%s\n%s", getUser().getUsername(), formatDateTime, fileName,
                "______________");
    }
}
