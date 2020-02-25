
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
import java.util.Date;

public class DiscordApi {

    public static void main(String[] args) throws IOException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://develop.ms.codingtree.pl/api/meetings/sorted"))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        JSONArray jsonArray = new JSONArray(response.body());

        ArrayList<String> listOfDates = new ArrayList<>();
        ArrayList<String> listOfTopics = new ArrayList<>();
        ArrayList<String> listOfLeaders = new ArrayList<>();

        for (int i = 0 ; i < jsonArray.length(); i++){
            listOfDates.add(jsonArray.getJSONObject(i).get("date").toString());
            listOfTopics.add(jsonArray.getJSONObject(i).get("topic").toString());
            listOfLeaders.add(jsonArray.getJSONObject(i).get("leader").toString());
        }

        long firstDate = Long.parseLong(listOfDates.get(0));
        long secondDate = Long.parseLong(listOfDates.get(1));

        Timestamp firstDateTimestamp = new Timestamp(firstDate);
        Date firstDateToDateTime = new Date(firstDateTimestamp.getTime());

        Timestamp secondDateTimestamp = new Timestamp(secondDate);
        Date secondDateToDateTime = new Date(secondDateTimestamp.getTime());

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String nearestDate = sdfDate.format(firstDateToDateTime);
        String nextDate = sdfDate.format(secondDateToDateTime);

// kanałCT     DiscordWebhook webhook = new DiscordWebhook("https://discordapp.com/api/webhooks/665191792676831234/n1yHAY0oCU1C7vjtkQDDzugcQvW7DdkmnKca8_kdz3L0xo-c8OID75mfiROhj5dRv0wp");

        DiscordWebhook webhook = new DiscordWebhook("https://discordapp.com/api/webhooks/681766338250801163/4ayouBjghURBqi4d0dIgMdaJIo3EpPxuXliyZ779zN_2gupzLWOwoJXM--a4i-nB--ke");
        webhook.setContent("@everyone");
        webhook.setAvatarUrl("https://cdn3.vectorstock.com/i/1000x1000/99/37/white-tree-icon-in-green-round-vector-1869937.jpg");
        webhook.setUsername("Przypominajka");
        webhook.setTts(true);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(":exclamation: Najbliższe spotkanie już " + nearestDate + " :exclamation:")
                .setColor(Color.green)
//                .addField("Data", nearestDate, false)
                .addField(":mega: Temat", listOfTopics.get(0), false)
                .addField(":speaking_head: Prowadzący", listOfLeaders.get(0), false)
                .addField(":arrow_right: Link do aplikacji - linkiem może być tytuł postu, wtedy bedzie na niebiesko", "jakiś url", true)
                .setThumbnail("https://www.kindpng.com/picc/m/73-737324_abstract-submission-nutrition-congress-circle-meeting-icon-png.png")
                .setImage("https://cdn3.iconfinder.com/data/icons/roles-computer-it/128/programmer-2-512.png"));
//                .setAuthor("Coding Tree", "https://kryptongta.com", "https://kryptongta.com/images/kryptonlogowide.png")
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.lightGray)
                .setAuthor("Kolejne spotkanie " + nextDate, " ", " ")
                .addField(":mega: Temat ", listOfTopics.get(1), true)
                .addField(":speaking_head: Prowadzący ", listOfLeaders.get(1), true));
        webhook.execute();
    }
}
