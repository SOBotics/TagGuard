package ch.philnet.tagguard;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TagListReader {
    public HashMap<String, String> readTagList() {
        HashMap<String, String> tags = new HashMap<>();
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader("." + File.separator + "data" + File.separator + "tags.csv"));
            while((line = br.readLine()) != null) {
                String[] lineTags = line.split(",");
                tags.put(lineTags[0], lineTags[1]);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tags;
    }
}
