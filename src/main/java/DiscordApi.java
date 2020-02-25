import org.json.JSONArray;
import org.json.JSONException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DiscordApi {

    private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void main(String[] args) throws IOException, InterruptedException, JSONException {
        String uriToManager = args[0];
        String urlDiscord = args[1];

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uriToManager))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONArray jsonArray = new JSONArray(response.body());

        ArrayList<String> listOfDates = new ArrayList<>();
        ArrayList<String> listOfTopics = new ArrayList<>();
        ArrayList<String> listOfLeaders = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            listOfDates.add(jsonArray.getJSONObject(i).get("date").toString());
            listOfTopics.add(jsonArray.getJSONObject(i).get("topic").toString());
            listOfLeaders.add(jsonArray.getJSONObject(i).get("leader").toString());
        }

        DiscordWebhook.EmbedObject nearestTopicMessage = createNearestTopicMessage(formatDate(listOfDates.get(0)), listOfTopics.get(0), listOfLeaders.get(0));
        DiscordWebhook.EmbedObject nextTopicMessage = createNextTopicMessage(formatDate(listOfDates.get(1)), listOfTopics.get(1), listOfLeaders.get(1));

        sendWebhook(urlDiscord, Arrays.asList(nearestTopicMessage, nextTopicMessage));
    }

    private static void sendWebhook(String urlDiscord, List<DiscordWebhook.EmbedObject> messages) throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(urlDiscord);
        webhook.setContent("@everyone");
        webhook.setAvatarUrl("https://cdn3.vectorstock.com/i/1000x1000/99/37/white-tree-icon-in-green-round-vector-1869937.jpg");
        webhook.setUsername("Przypominajka");
        webhook.setTts(true);

        for (DiscordWebhook.EmbedObject eo : messages) {
            webhook.addEmbed(eo);
        }

        webhook.execute();
    }

    private static DiscordWebhook.EmbedObject createNearestTopicMessage(String nearestDate, String topic, String leader) {
        return new DiscordWebhook.EmbedObject()
                .setTitle(":exclamation: Najbliższe spotkanie już " + nearestDate + " :exclamation:")
                .setColor(Color.green)
                .addField(":mega: Temat", topic, false)
                .addField(":speaking_head: Prowadzący", leader, false)
                .addField(":arrow_right: Link do aplikacji - linkiem może być tytuł postu, wtedy bedzie na niebiesko", "jakiś url", true)
                .setThumbnail("https://www.kindpng.com/picc/m/73-737324_abstract-submission-nutrition-congress-circle-meeting-icon-png.png")
                .setImage("https://cdn3.iconfinder.com/data/icons/roles-computer-it/128/programmer-2-512.png");
    }

    private static DiscordWebhook.EmbedObject createNextTopicMessage(String nextDate, String topic, String leader) {
        return new DiscordWebhook.EmbedObject()
                .setColor(Color.lightGray)
                .setAuthor("Kolejne spotkanie " + nextDate, " ", " ")
                .addField(":mega: Temat ", topic, true)
                .addField(":speaking_head: Prowadzący ", leader, true);
    }

    private static String formatDate(String date) {
        long firstDate = Long.parseLong(date);
        Timestamp firstDateTimestamp = new Timestamp(firstDate);
        Date firstDateToDateTime = new Date(firstDateTimestamp.getTime());
        return sdfDate.format(firstDateToDateTime);
    }
}
