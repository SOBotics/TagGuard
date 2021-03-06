package ch.philnet.tagguard.services;

import ch.philnet.tagguard.TagListReader;
import ch.philnet.tagguard.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;
import com.neovisionaries.ws.client.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Room;

import java.io.IOException;
import java.util.*;

/**
 * Monitor the site for new questions and analyze tags
 */
public class QuestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);

    public void run(Room chatRoom) {
        try {

            LOGGER.info("Connecting to Websocket...");
            new WebSocketFactory()
                    .createSocket("wss://qa.sockets.stackexchange.com/")
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                            LOGGER.info("Successfully connected to Websocket, listening for new posts.");
                        }
                    })
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                            LOGGER.warn("Disconnected from the socket");
                        }
                    })
                    .addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            //Parse JSON string to object
                            JSONObject question = new JSONObject(new JSONObject(text).get("data").toString());

                            //Check if it's a question from Stack Overflow and not some other site
                            //We need to do this since the single-site websocket doesn't work for SO
                            if(question.getString("siteBaseHostAddress").equals("stackoverflow.com")) {
                                JSONArray tags = question.getJSONArray("tags");
                                if(hasMissingTags(Utils.jsonArrayToStringList(tags))) {
                                    LOGGER.info("Detected missing tags in post " + question.getString("url"));
                                    chatRoom.send("[ [TagGuard](https://sobotics.org) ] [tag:" + String.join("] [tag:", Utils.jsonArrayToStringList(tags)) + "] Only narrow language tag on this post: [" + question.getString("titleEncodedFancy") + "](" + question.getString("url") + ")");
                                }
                            }
                        }
                    })
                    .connect()
                    .sendText("155-questions-active");
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    private boolean hasMissingTags(List<String> questionTags) {
        HashMap<String, String> watchedTags = new TagListReader().readTagList();

        for(HashMap.Entry<String, String> watchedTag : watchedTags.entrySet()) {
            if(questionTags.contains(watchedTag.getKey())) {
                if(!questionTags.contains(watchedTag.getValue()))
                    return true;
            }
        }
        return false;
    }

}
