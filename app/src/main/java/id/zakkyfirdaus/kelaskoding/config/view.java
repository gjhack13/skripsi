package id.zakkyfirdaus.kelaskoding.config;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class view extends AsyncTask<String, Void, String> {
    private Context context;
    private String link = server.link;
    public view (Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {
        String episode_id   = arg0[0];

        String data;
        BufferedReader bufferedReader;
        String result;
        try {
            data = "?episode_id=" + URLEncoder.encode(episode_id, "UTF-8");

            link = link + "view.php" + data;

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            return result;
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        String jsonStr = result;
        Log.e("Url", link);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String response = jsonObj.getString("response");
                if (response.equals("success")) {
                    Log.d("Log response", response);
                } else if (response.equals("failed")) {
                    Log.d("Log response", response);
                } else {
                    Log.d("Log error", "Couldn't connect to remote database.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Log error", "Couldn't get any JSON data.");
        }
    }
}
