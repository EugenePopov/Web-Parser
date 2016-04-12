package paiip.webcrawler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import TumblrParser.TumblrEntity;

public class ImageListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<TumblrEntity> tumblrEntities;
    TumblrEntity tumblrEntity;

    public ImageListAdapter(Context context, ArrayList<TumblrEntity> tumblrEntities) {
        this.context = context;
        this.tumblrEntities = tumblrEntities;
    }

    @Override
    public int getCount() {
        return tumblrEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView imageDescription;
        TextView imageNotes;
        ImageView image;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.activity_image_list_row, parent, false);
        tumblrEntity = tumblrEntities.get(position);

        imageDescription = (TextView) itemView.findViewById(R.id.imageDescription);
        imageNotes = (TextView) itemView.findViewById(R.id.imageNotes);
        image = (ImageView) itemView.findViewById(R.id.imageView);

        imageDescription.setText(tumblrEntity.ImageDescription);
        imageNotes.setText(tumblrEntity.ImageNotes);
        image.setImageBitmap(tumblrEntity.Image);

        final int pos = position;
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                tumblrEntity = tumblrEntities.get(pos);
                Intent intent = new Intent(context, ImageListItemActivity.class);
                intent.putExtra("imageBlogSource", tumblrEntity.UrlImageBlogSource);
                intent.putExtra("imageDescription", tumblrEntity.ImageDescription);
                intent.putExtra("imageNotes",tumblrEntity.ImageNotes);
                intent.putExtra("imagePath", tumblrEntity.ImagePath);
                context.startActivity(intent);
            }
        });
        return itemView;
    }


}
