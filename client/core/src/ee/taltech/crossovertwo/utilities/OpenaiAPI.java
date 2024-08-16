package ee.taltech.crossovertwo.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenaiAPI {
    // THIS CLASS WAS TAKEN FROM https://rollbar.com/blog/how-to-use-chatgpt-api-with-java/

    public static String prompt = "Create a message for Wandering Trader appearing. Keep it one sentence long.";

    /**
     * Chat with GPT-3.5
     * @param prompt The prompt to chat with
     * @param key The OpenAI key
     * @return The response from ChatGPT
     */
    public static String chatGPT(String prompt, String key) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = key;
        String model = "gpt-3.5-turbo-0125";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // calls the method to extract the message.
            System.out.println(response.toString());
            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            return "A trader has appeared!";
        }
    }

    /**
     * Extract the message from the JSON response
     * @param response The JSON response
     * @return The message
     */
    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content")+ 11;

        int end = response.indexOf("\"      }", start);

        return response.substring(start, end);

    }

}