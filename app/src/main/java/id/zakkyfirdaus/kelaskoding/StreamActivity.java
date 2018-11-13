package id.zakkyfirdaus.kelaskoding;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import id.zakkyfirdaus.kelaskoding.config.server;
import id.zakkyfirdaus.kelaskoding.config.view;

public class StreamActivity extends AppCompatActivity {

    MainActivity M = new MainActivity();

    VideoView video_view;
    MediaController mediaControls;
    ProgressBar progress_bar;
    RelativeLayout progress_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        String path = server.video + M.streaming;
        Log.d("Log path", path);
        Uri uri     = Uri.parse(path);

        progress_bar    = (ProgressBar) findViewById(R.id.progress_bar);
        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);

        video_view  = (VideoView) findViewById(R.id.video_view);
        if (mediaControls == null) {
            // create an object of media controller class
            mediaControls = new MediaController(StreamActivity.this);
            mediaControls.setAnchorView(video_view);
        }

        video_view.start(); // start the video
        video_view.requestFocus();

        video_view.setMediaController(mediaControls);
        video_view.setVideoURI(uri);
        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                progress_layout.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);

                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                progress_bar.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                progress_bar.setVisibility(View.GONE);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // when an video is completed, code here
                finish();
            }
        });

        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // when an error is occured while playing an video, code here
                return false;
            }
        });

        new view(StreamActivity.this).execute(
                M.episode_id
        );
    }
}
