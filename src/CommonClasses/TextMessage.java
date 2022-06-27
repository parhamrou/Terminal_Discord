package CommonClasses;

import java.time.format.*;

public class TextMessage extends Message {

    private final String text;

    public TextMessage(User user, String text) {
        super(user);
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatDateTime = getDate().format(formatter);
        return String.format("______________" +
                        "\n%s\t%s\n%s\n%s", getUser().getUsername(), formatDateTime, text,
                "______________");
    }
}
