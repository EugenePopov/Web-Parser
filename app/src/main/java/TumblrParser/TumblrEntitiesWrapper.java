package TumblrParser;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class TumblrEntitiesWrapper {

    @ElementList
    public ArrayList<TumblrEntity> TumblrEntities;

    public TumblrEntitiesWrapper() {

    }

    public TumblrEntitiesWrapper(ArrayList<TumblrEntity> tumblrEntities) {
        TumblrEntities = tumblrEntities;
    }

    public ArrayList<TumblrEntity> getTumblrEntities() {
        return TumblrEntities;
    }
}
