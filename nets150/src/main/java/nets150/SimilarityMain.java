package nets150;

import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class SimilarityMain {
    
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Are you interested in songs or artists? ");
        String string1 = reader.nextLine(); 

        switch (string1) {
        case "songs":
            System.out.println("Do you want to learn more about an individual artist's songs similarity, or similarity between two songs? Please put 'individual' or 'two'.");
            String string2 = reader.nextLine(); 

            switch (string2) {
                case "individual":
                    System.out.println("Please name an artist.");
                    String string5 = reader.nextLine(); 

                    LyricParser lparse = new LyricParser();
                    lparse.getArtistPage(string5);  
                    ArrayList<String> songFiles = lparse.getSongs();

                    // TODO: iterate through songFiles, compare each song to all of the other songs and get average similarity score

                case "two":
                    System.out.println("Please name 2 songs and their respective artists, in this format: song_1,artist_1:song_2,artist_2");
                    String string6 = reader.nextLine(); 

                    LyricParser lparse2 = new LyricParser();
                    lparse2.getSong(string6.split(":")[0].split(",")[0], string6.split(":")[0].split(",")[1]);
                    lparse2.getSong(string6.split(":")[1].split(",")[0], string6.split(":")[1].split(",")[1]);

                    // TODO: get cosine similarity between these two songs

            }

        case "artists":

            System.out.println("Please pick one of the following genres: k-pop");
            String string3 = reader.nextLine(); 

            // TODO: add more genre options
            switch (string3) {
                case "k-pop":
                    System.out.println("Please pick two artists, separated by a comma.");
                    String string4 = reader.nextLine(); 
                    String[] artists = string4.split(",");
                    
                    ArtistDistance ad = new ArtistDistance("k-pop", artists[0], artists[1]);
                    try {
                        ad.initializeSpotifyAPIClient();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ad.checkInitialArtist();
                    ad.checkFinalArtist();

                    // TODO: add more meaningful statistics while search is happening, setting time limit for runtime, etc.
        
                    while (!ad.iterate()) {
                        System.out.println("iteration passed");
                    }

            }
            
        }
        
        
        
    }
}
