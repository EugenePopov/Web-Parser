package TumblrParser;

import android.graphics.Bitmap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class TumblrEntity {

    @Element
    public String UrlImage;

    @Element
    public String ImageDescription;

    @Element
    public String ImageNotes;

    @Element
    public String UrlImageBlogSource;

    @Element
    public String ImagePath;

    public Bitmap Image;

    public TumblrEntity() {
        UrlImage = "";
        ImageDescription = "";
        ImageNotes = "";
        UrlImageBlogSource = "";
        ImagePath = "";
    }

   // public TumblrEntity(String urlImage, String imageDescription, String imageNotes,
    //                    String urlImageBlogSource, String imagePath) {
    //    UrlImage = urlImage;
    //    ImageDescription = imageDescription;
    //    ImageNotes = imageNotes;
    //    UrlImageBlogSource = urlImageBlogSource;
    //    ImagePath = imagePath;
   // }
}


