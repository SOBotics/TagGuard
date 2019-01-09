package ch.philnet.tagguard.clients;

import ch.philnet.tagguard.services.QuestionService;
import org.sobotics.chatexchange.chat.ChatHost;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.StackExchangeClient;
import ch.philnet.tagguard.services.BotService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class TagGuard {
    public static void main(String[] args) throws IOException {
        StackExchangeClient client;
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream( "." + File.separator + "properties" + File.separator + "auth.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        client = new StackExchangeClient(prop.getProperty("email"), prop.getProperty("password"));
        Room room = client.joinRoom(ChatHost.STACK_OVERFLOW, 163468);
        new BotService().run(room, prop.getProperty("location"));
        new QuestionService().run(room);
    }
}
