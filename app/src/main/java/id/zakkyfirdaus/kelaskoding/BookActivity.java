package id.zakkyfirdaus.kelaskoding;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

import id.zakkyfirdaus.kelaskoding.config.server;
import id.zakkyfirdaus.kelaskoding.config.unbook;

public class BookActivity extends AppCompatActivity {

    final Context context = BookActivity.this;

    MainActivity M = new MainActivity();

    private String json_result, link ;
    private ListView list_book;
    private TextView text_notif;
    private ProgressBar progress_bar;

    public static String book_id;

    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String, String>> books = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        book_id = "";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bookmark");

        list_book       = (ListView) findViewById(R.id.list_book);
        progress_bar    = (ProgressBar) findViewById(R.id.progress_bar);
        text_notif      = (TextView) findViewById(R.id.text_notif);

        Load();
    }

    private void Load(){
        link = server.link + "bookmark.php?android_id=" + M.android_id;
        Log.d("Log link", link );
        AccessService();
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
            ListDrawer();
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

    public void ListDrawer() {

        try {
            JSONObject jsonResponse = new JSONObject(json_result);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("result");

            int i;
            for (i = 0; i < jsonMainNode.length(); i++) {
                progress_bar.setVisibility(View.GONE);

                JSONObject jsonChildNode    = jsonMainNode.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("bookmark_id",     jsonChildNode.optString("bookmark_id"));
                map.put("episode_id",     jsonChildNode.optString("episode_id"));
                map.put("title",     jsonChildNode.optString("title"));
                map.put("created",    jsonChildNode.optString("created"));
                map.put("link",    jsonChildNode.optString("link"));
                map.put("view",    "");
                books.add(map);
            }

            if (i == 0) {
                progress_bar.setVisibility(View.GONE);
                text_notif.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        simpleAdapter = new SimpleAdapter(this, books, R.layout.list_episode,
                new String[] { "bookmark_id", "episode_id", "title", "created", "link", "view"},
                new int[] {R.id.text_book_id, R.id.text_episode_id, R.id.text_title, R.id.text_created,
                        R.id.text_link, R.id.text_views}){

        };

        list_book.setAdapter(simpleAdapter);
        list_book.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                M.episode_id    = ((TextView) view.findViewById(R.id.text_episode_id)).getText().toString();
                book_id         = ((TextView) view.findViewById(R.id.text_book_id)).getText().toString();
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

        text_book.setText("Remove from list");

        text_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, StreamActivity.class));
                alertDialog.dismiss();
            }
        });

        text_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new unbook(context).execute( M.android_id, book_id);
                list_book.setAdapter(null); books.clear();
                Load();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
