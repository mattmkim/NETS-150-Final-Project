package nets150;


import java.util.ArrayList;

public class LyricParserMain {

    public static void main(String[] args) {
        LyricParser lparse = new LyricParser();
        lparse.getArtistPage("Justin Bieber");
        ArrayList<String> songFiles = lparse.getSongs();

        // for (String f : songFiles) {
        //     System.out.println(f);
        // }
    }

    public void getSimilarity(ArrayList<String> songFiles) {
        ArrayList<Document> songDocs = new ArrayList<>();
        for (String songFile : songFiles) {
            Document d = new Document(songFile);
            songDocs.add(d);
        }

        Corpus songCorp = new Corpus(songDocs);
        VectorSpaceModel songVSModel = new VectorSpaceModel(songCorp);

        //nested loop to get all similarities to average
        for(int i = 0; i < songDocs.size(); i++) {
            for (int j = 0; j < songDocs.size(); j++) {
                if (i != j) {
                    Document doc1 = songDocs.get(i);
                    Document doc2 = songDocs.get(j);
                    double cosSim = songVSModel.cosineSimilarity(doc1, doc2);
                }
            }

        }
    }

}
