package id.zakkyfirdaus.kelaskoding;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class GenreActivity extends AppCompatActivity {

    MainActivity M = new MainActivity();

    private String json_result, link;
    private ListView list_genre;

    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String, String>> genres = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);

        json_result = ""; link = "";

        list_genre    = (ListView) findViewById(R.id.list_genre);

        link = server.link + "genre.php";
        Log.d("Log link", link );
        AccessService();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Genres");
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

            for (int i = 0; i < jsonMainNode.length(); i++) {

                JSONObject jsonChildNode    = jsonMainNode.getJSONObject(i);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("genre_id", jsonChildNode.optString("genre_id"));
                map.put("genre",    jsonChildNode.optString("genre"));
                genres.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        simpleAdapter = new SimpleAdapter(this, genres, R.layout.list_genre,
                new String[] { "genre_id", "genre"},
                new int[] {R.id.text_genre_id, R.id.text_genre}){

        };

        list_genre.setAdapter(simpleAdapter);
        list_genre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String genre_id = ((TextView) view.findViewById(R.id.text_genre_id)).getText().toString();
                String genre = ((TextView) view.findViewById(R.id.text_genre)).getText().toString();
                Log.d("Log genre_id", genre_id);

                M.genre_selected    = genre;
                M.select_genres     = true;
                finish();
            }
        });
    }
}
