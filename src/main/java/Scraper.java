import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.Objects;

/**
 * BY: Yasasvi Hari
 *
 * This class is used to download the tutorial information from Adobe Tutorials online.
 */
class Scraper {
    // parameters
    private String url;
    private String outputPath;

    Scraper(String url, String outputPath) {
        this.url = Objects.requireNonNull(url);
        this.outputPath = Objects.requireNonNull(outputPath);
    }

    void writeFile() {
        try {
            org.jsoup.nodes.Document document = Jsoup.connect(url).get();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            writer.write(document.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setUrl(String url) {
        this.url = url;
    }

    void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
