package TumblrParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class TumblrBlogParser {

    public String Url;
    public Document tumblrBlogPage;

    public TumblrBlogParser(String url) {
        Url = url;
        tumblrBlogPage = getBlogPage();
    }

    public Document getBlogPage() {
        Document doc = null;
        try {
            doc = Jsoup.connect(Url).get();
        }
        catch (IOException ex) {

        }
        return doc;
    }

    private Elements getImages() {
        return tumblrBlogPage.select("img[alt]");
    }

    private Elements getImageNotes() {
        return tumblrBlogPage.select(".craig a:nth-child(2)");
    }

    private Elements getImageBlogSources() {
        return tumblrBlogPage.select(".craig a:nth-child(1)");
    }

    public ArrayList<TumblrEntity> getTumblrData() {
        ArrayList<TumblrEntity> tumblrEntities = new ArrayList<>();
        Elements urlImages = getImages();
        Elements imageNotes = getImageNotes();
        Elements imageBlogSources = getImageBlogSources();

        for (int index = 0; index < urlImages.size(); index++) {
            TumblrEntity entity = new TumblrEntity();

            entity.UrlImage = urlImages.get(index).attr("src");
            entity.UrlImageBlogSource = imageBlogSources.get(index).attr("href");
            entity.ImageDescription = urlImages.get(index).attr("alt") != null && !urlImages.get(index).attr("alt").isEmpty()
                    ? urlImages.get(index).attr("alt").replace("\n", " ") : "Foolish Things ";
            entity.ImageNotes = imageNotes.get(index).text();

            tumblrEntities.add(entity);
        }
        
        return tumblrEntities;
    }
}

