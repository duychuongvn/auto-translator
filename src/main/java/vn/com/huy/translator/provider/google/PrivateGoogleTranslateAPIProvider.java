package vn.com.huy.translator.provider.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import vn.com.huy.translator.provider.ExternalTranslationProvider;

/**
 * The Translator provider that use the private translate API of Google Translate (this API is designed for Chrome plugin)
 * and parse the content of the returned json to get the translated text.
 */
@Profile({"default", "google"})
@Component
public class PrivateGoogleTranslateAPIProvider extends ExternalTranslationProvider {

    public String translate(String text, String langIn, String langOut) {
        // TODO to use RestTemplate
        try {
            String urlStr = "https://translate.googleapis.com/translate_a/single?" +
                    "client=gtx&" +
                    "sl=" + langIn +
                    "&tl=" + langOut +
                    "&dt=t&q=" + URLEncoder.encode(text, "UTF-8");

//            URL url = new URL(urlStr);
//            URLConnection con = url.openConnection();
//            con.setRequestProperty("User-Agent", "Mozilla/5.0");
//            con.setConnectTimeout(20000);
//            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8) )) {
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//
                return text;
//                return parseResult(response.toString());
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseResult(String inputJson) {
        JSONArray jsonArray = new JSONArray(inputJson);
        JSONArray translationsList = (JSONArray) jsonArray.get(0);
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < translationsList.length(); i++) {
            buffer.append(((JSONArray) translationsList.get(i)).get(0));
        }
        return buffer.toString();
    }

}
