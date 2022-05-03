package nets150;
import java.util.*;
import java.net.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import se.michaelthelin.spotify.requests.data.artists.GetArtistsRelatedArtistsRequest;
import java.io.IOException;
import org.apache.hc.core5.http.ParseException;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.JsonParser;

public class ArtistDistance {

    String accessToken = "";
    String clientID = "54783b22cc5247788f41b0b1b5e03bd6";
    String secretID = "372ed25e90424066961af697a6e72b27";
    SpotifyApi spotifyApi;

    public String initialArtist;
    public String finalArtist;
    public String genre;
    public int numFrontiers = 1;

    public Map<String, ArrayList<String>> idGraph = new HashMap<String, ArrayList<String>>();
    public Map<String, ArrayList<String>> nameGraph = new HashMap<String, ArrayList<String>>();
    public Set<String> seenIDs = new HashSet<String>();

    public Stack<String> idFrontier = new Stack<String>();
    public Stack<String> nameFrontier = new Stack<String>();

    public ArtistDistance(String genre, String artist1, String artist2) {        
        this.initialArtist = artist1;
        this.finalArtist = artist2;
        this.genre = genre;
    }

    public void initializeSpotifyAPIClient() throws IOException {

        URL url = new URL("https://accounts.spotify.com/api/token");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

        String data = "grant_type=client_credentials&client_id=" + clientID + "&client_secret=" + secretID + "";

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        StringBuilder response = new StringBuilder();
        while (currentLine != null) {
            response.append(currentLine).append("\n");
            currentLine = Lines.readLine();
        }

        accessToken = String.valueOf(JsonParser.parseString(String.valueOf(response)).getAsJsonObject().get("access_token")).replace("\"", "");
        http.disconnect();

        spotifyApi = new SpotifyApi.Builder()
        .setAccessToken(accessToken)
        .build();
    }

    public void checkInitialArtist() {
        String type = ModelObjectType.ARTIST.getType();

        final SearchItemRequest searchItemRequest = spotifyApi.searchItem(initialArtist, type)
        .build();

        try {
            final SearchResult searchResult = searchItemRequest.execute();
            Artist artist = searchResult.getArtists().getItems()[0];
            List<String> genres = Arrays.asList(artist.getGenres());  

            // TODO: maybe modify later, only looking for kpop artists
            if (!genres.contains(this.genre)) {
                // reprompt user or something
            } else {
                ArrayList<String> idNodes = new ArrayList<String>();
                ArrayList<String> nameNodes = new ArrayList<String>();
                idGraph.put(artist.getId(), idNodes);
                nameGraph.put(artist.getName(), nameNodes);
                idFrontier.push(artist.getId());
                nameFrontier.push(artist.getName());
                seenIDs.add(artist.getId());
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void checkFinalArtist() {
        String type = ModelObjectType.ARTIST.getType();

        final SearchItemRequest searchItemRequest = spotifyApi.searchItem(finalArtist, type)
        .build();

        try {
            final SearchResult searchResult = searchItemRequest.execute();
            Artist artist = searchResult.getArtists().getItems()[0];
            List<String> genres = Arrays.asList(artist.getGenres());  

            // TODO: maybe modify later, only looking for kpop artists
            if (!genres.contains(this.genre)) {
                // reprompt user or something
            } 
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public boolean iterate() {
        Stack<String> nextIDFrontier = new Stack<String>();
        Stack<String> nextNameFrontier = new Stack<String>();
        while (!idFrontier.isEmpty()) {
            String id = idFrontier.pop();
            String name = nameFrontier.pop();
            final GetArtistsRelatedArtistsRequest getArtistsRelatedArtistsRequest = spotifyApi
            .getArtistsRelatedArtists(id)
            .build();

            ArrayList<String> currIdNeighbors = new ArrayList<String>();
            ArrayList<String> currNameNeighbors = new ArrayList<String>();

            try {
                final Artist[] artists = getArtistsRelatedArtistsRequest.execute();
                for (Artist artist: artists) {
                    // check for finalArtist
                    if (artist.getName().equals(finalArtist)) {
                        return true;
                    }

                    if (!seenIDs.contains(artist.getId())) {
                        currIdNeighbors.add(artist.getId());
                        currNameNeighbors.add(artist.getName());
                        nextIDFrontier.push(artist.getId());
                        nextNameFrontier.push(artist.getName());
                        seenIDs.add(artist.getId());
                    }
                }

                this.idGraph.put(id, currIdNeighbors);
                this.nameGraph.put(name, currNameNeighbors);
          
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        idFrontier = nextIDFrontier;
        nameFrontier = nextNameFrontier;
        System.out.println(idFrontier.size());
        numFrontiers++;
        return false;
    }



}