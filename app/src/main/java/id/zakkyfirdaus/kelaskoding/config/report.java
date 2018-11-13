package id.zakkyfirdaus.kelaskoding.config;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class report extends AsyncTask<String, Void, String> {
    private Context context;
    private String link = server.link;
    public report (Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {
        String android_id   = arg0[0];
        String email        = arg0[1];
        String report       = arg0[2];

        String data;
        BufferedReader bufferedReader;
        String result;
        try {
            data = "?android_id=" + URLEncoder.encode(android_id, "UTF-8");
            data += "&email=" + URLEncoder.encode(email, "UTF-8");
            data += "&report=" + URLEncoder.encode(report, "UTF-8");

            link = link + "report.php" + data;

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
                    Toast.makeText(context, "Report sent", Toast.LENGTH_SHORT).show();
                    ((Activity)(context)).finish();
                } else if (response.equals("failed")) {
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
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