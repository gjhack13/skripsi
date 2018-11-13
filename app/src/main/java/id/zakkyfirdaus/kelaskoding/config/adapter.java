package id.zakkyfirdaus.kelaskoding.config;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import id.zakkyfirdaus.kelaskoding.R;


public class adapter extends SimpleAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public adapter(Context context, ArrayList<HashMap<String, String>> data, int resource,
                   String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrayList = data;
        inflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ImageView image     = (ImageView) view.findViewById(R.id.img_image);
        Picasso.with(context).
                load(server.image + arrayList.get(position).get("image"))
                .placeholder(R.drawable.no_image)
                .fit().centerCrop()
                .error(R.drawable.no_preview)
                .into(image);

        return view;
    }
}
