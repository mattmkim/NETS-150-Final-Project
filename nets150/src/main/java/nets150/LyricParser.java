package nets150;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricParser {
    private String baseURL;
    private Document currDoc;

    public LyricParser() {
        
        this.baseURL = "https://www.lyricsmode.com/";
        
        try {
            this.currDoc = Jsoup.connect(this.baseURL).get();
        } catch (IOException e) {
            System.out.println("url not found");
        }
    }

    public void getSong(String artist, String song) {
        String firstLetter = artist.substring(0, 1);
        firstLetter = firstLetter.toUpperCase();

        //get to list page by first letter of first name
        Elements linksOnHome = this.currDoc.getElementsByClass("js-seo-hide-link ");
        String letterURL = "";
        for (Element link : linksOnHome) {
            if (link.text().equals(firstLetter)) {
                letterURL = link.attr("abs:data-href");
            }
        }

        try {
            this.currDoc = Jsoup.connect(letterURL).get();
        }
        catch (IOException e) {
            System.out.println("Couldn't get country page");
        }

        Elements linksOnAlphaPage = this.currDoc.select("a");
        String artistURL = "";
        for (Element link : linksOnAlphaPage) {
            if (link.attr("title").equalsIgnoreCase(artist)) {
                artistURL = link.attr("abs:href");
            }
        }

        try {
            this.currDoc = Jsoup.connect(artistURL).get();
        }
        catch (IOException e) {
            System.out.println("Couldn't get country page");
        }

        Elements linksOnArtistPage = this.currDoc.getElementsByClass("ui-song-title");
        String songURL = "";
        String lyrics = "";

        for (Element link : linksOnArtistPage) {
            String currSong = link.text();
            if (currSong.equals(song)) {
                songURL = link.attr("abs:href");
                System.out.println(songURL);
                try {
                    this.currDoc = Jsoup.connect(songURL).get();
                }
                catch (IOException e) {
                    System.out.println("Couldn't get song page");
                }
    
                lyrics = this.currDoc.getElementById("lyrics_text").text().replace("Explain Request ×", "");
                String fileName = writeToTxt(currSong, lyrics);
            }
        }
    }

    public void getArtistPage(String artist) {
        String firstLetter = artist.substring(0, 1);
        firstLetter = firstLetter.toUpperCase();

        //get to list page by first letter of first name
        Elements linksOnHome = this.currDoc.getElementsByClass("js-seo-hide-link ");
        String letterURL = "";
        for (Element link : linksOnHome) {
            if (link.text().equals(firstLetter)) {
                letterURL = link.attr("abs:data-href");
            }
        }

        try {
            this.currDoc = Jsoup.connect(letterURL).get();
        }
        catch (IOException e) {
            System.out.println("Couldn't get country page");
        }

        Elements linksOnAlphaPage = this.currDoc.select("a");
        String artistURL = "";
        for (Element link : linksOnAlphaPage) {
            if (link.attr("title").equalsIgnoreCase(artist)) {
                artistURL = link.attr("abs:href");
            }
        }

        try {
            this.currDoc = Jsoup.connect(artistURL).get();
        }
        catch (IOException e) {
            System.out.println("Couldn't get country page");
        }

    }

    public ArrayList<String> getSongs() {
        Elements linksOnArtistPage = this.currDoc.getElementsByClass("ui-song-title");
        String songURL = "";
        String lyrics = "";
        ArrayList<String> allSongs = new ArrayList<>();

        for (Element link : linksOnArtistPage) {
            String currSong = link.text();
            songURL = link.attr("abs:href");
            System.out.println(songURL);
            try {
                this.currDoc = Jsoup.connect(songURL).get();
            }
            catch (IOException e) {
                System.out.println("Couldn't get song page");
            }

            lyrics = this.currDoc.getElementById("lyrics_text").text().replace("Explain Request ×", "");
            System.out.println(lyrics);

            String fileName = writeToTxt(currSong, lyrics);
            allSongs.add(fileName);
        }

        return allSongs;
    }

    public String writeToTxt(String songName, String lyrics) {
        String fileName = songName + " Lyrics.txt";
        File lyricFile = new File(fileName);
        boolean successfulFileCreation;
        try {
            successfulFileCreation = lyricFile.createNewFile();
            if (successfulFileCreation) {
                BufferedWriter writer = null;
                try
                {
                    writer = new BufferedWriter( new FileWriter( lyricFile));
                    writer.write(lyrics);

                }
                catch ( IOException e)
                {
                }
                finally
                {
                    try
                    {
                        if ( writer != null)
                            writer.close( );
                    }
                    catch ( IOException e)
                    {
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return fileName;


    }

}
