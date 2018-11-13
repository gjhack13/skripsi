package id.zakkyfirdaus.kelaskoding;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import id.zakkyfirdaus.kelaskoding.config.server;

public class AuthActivity extends AppCompatActivity {

    String link, json_result, android_id;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        link = ""; json_result = "";
        android_id = Settings.Secure.getString(this.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("Log android_id", android_id);

        link = server.link + "auth.php?android_id=" + android_id;
        Log.d("Log link", link);
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
        }
        catch (IOException e) {
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
            String response = jsonResponse.getString("response");

            if ( response.equals("success") ) {
                progress_bar.setVisibility(View.GONE);

//                Toast.makeText(getApplication(), "Login berhasil", Toast.LENGTH_LONG).show();
                Log.d("Log auth", "Login berhasil");
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                finish();

            } else if ( response.equals("registered") ){
                progress_bar.setVisibility(View.GONE);

                Log.d("Log auth", "Pendaftaran pengguna berhasil");
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                finish();

            } else if ( response.equals("failed") ){
                Toast.makeText(getApplication(), "", Toast.LENGTH_LONG).show();

            }else {
                Log.d("Error ", response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
