package id.zakkyfirdaus.kelaskoding;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import id.zakkyfirdaus.kelaskoding.config.book;
import id.zakkyfirdaus.kelaskoding.config.server;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

public class DetailActivity extends AppCompatActivity {

    final Context context = DetailActivity.this;

    MainActivity M = new MainActivity();

    private String json_result, link ;
    private ListView list_episode;
    private ProgressBar progress_bar;
    private ImageView img_view;
    private TextView text_notif, text_summary, text_eps, text_show;

    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

    //AdView adView;
    //AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(M.title);

        list_episode    = (ListView) findViewById(R.id.list_episode);
        progress_bar    = (ProgressBar) findViewById(R.id.progress_bar);
        img_view        = (ImageView) findViewById(R.id.imageviewplaceholder);
        text_notif      = (TextView) findViewById(R.id.text_notif);
        text_summary    = (TextView) findViewById(R.id.text_summary);
        text_eps        = (TextView) findViewById(R.id.text_eps);
        text_show       = (TextView) findViewById(R.id.text_show);

        text_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text_show.getText().toString().equals("More")){
                    text_show.setText("Less");
                    text_summary.setSingleLine(false);
                } else {
                    text_show.setText("More");
                    text_summary.setSingleLine(true);
                }
            }
        });

        text_summary.setText(M.summary); text_eps.setText(M.eps);

        Picasso.with(context).
                load(server.image + M.cover)
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_preview)
                .into(img_view);
        Log.d("Log cover", server.image + M.cover);

        link = server.link + "episode.php?video_id=" + M.video_id;
        Log.d("Log link", link );
        AccessService();

        //adView = (AdView) findViewById(R.id.adView);
        //adRequest = new AdRequest.Builder().build();
        //adView.loadAd(adRequest);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]);
            try {
                HttpResponse response = httpclient.execute(httppost);
                json_result = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            JsonResponses();
        }
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public void AccessService() {
        JsonReadTask task = new JsonReadTask();
        task.execute(new String[]{link});
    }

    public void JsonResponses() {

        try {
            JSONObject jsonResponse = new JSONObject(json_result);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");

            int i;
            for (i = 0; i < jsonMainNode.length(); i++) {


                JSONObject jsonChildNode    = jsonMainNode.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("anime_id",     jsonChildNode.optString("anime_id"));
                map.put("episode_id",     jsonChildNode.optString("episode_id"));
                map.put("title",     jsonChildNode.optString("title"));
                map.put("category",     "kategori, ");
                map.put("created",    jsonChildNode.optString("created"));
                map.put("link",    jsonChildNode.optString("link"));
                map.put("view",    jsonChildNode.optString("view") + " views");
                list.add(map);
            }

            if (i > 0) {
                text_notif.setVisibility(View.GONE);
                list_episode.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
            } else {
                text_notif.setVisibility(View.VISIBLE);
                list_episode.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        simpleAdapter = new SimpleAdapter(this, list, R.layout.list_episode,
                new String[] { "episode_id", "title", "category", "created", "link", "view"},
                new int[] {R.id.text_episode_id, R.id.text_title, R.id.text_category, R.id.text_created,
                        R.id.text_link, R.id.text_views}){

        };

        list_episode.setAdapter(simpleAdapter);
        list_episode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                M.episode_id = ((TextView) view.findViewById(R.id.text_episode_id)).getText().toString();
                M.streaming = ((TextView) view.findViewById(R.id.text_link)).getText().toString();
                Log.d("Log streaming", M.streaming);
                ListMenu();
            }
        });
    }

    private void ListMenu(){

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.list_menu, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final TextView text_play = (TextView) promptsView
                .findViewById(R.id.text_play);
        final TextView text_book = (TextView) promptsView
                .findViewById(R.id.text_book);


        text_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, StreamActivity.class));
                alertDialog.dismiss();
            }
        });

        text_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new book(context).execute(
                        M.android_id, M.episode_id
                );
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
