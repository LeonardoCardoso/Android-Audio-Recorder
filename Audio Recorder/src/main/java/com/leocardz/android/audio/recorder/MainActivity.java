package com.leocardz.android.audio.recorder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = "AudioRecord";
    private static String root = null;
    private static String audioPlayerName = null;
    private static Long millis;

    private Button recordButton = null;
    private Button playButton = null;

    private MediaRecorder recorder = null;
    private MediaPlayer mediaPlayer = null;

    private boolean isPlaying = false;
    private boolean isRecording = false;

    public MainActivity() {
        createDirectoriesIfNeeded();
        millis = Calendar.getInstance().getTimeInMillis();
        audioPlayerName = root + "/" + millis + "audio.mp4";
    }

    private void createDirectoriesIfNeeded() {

        root = Environment.getExternalStorageDirectory().getAbsolutePath();

        File folder = new File(root, "AudioRecord");

        if (!folder.exists()) {
            folder.mkdir();
        }

        File audioFolder = new File(folder.getAbsolutePath(), "Audio");

        if (!audioFolder.exists()) {
            audioFolder.mkdir();
        }

        root = audioFolder.getAbsolutePath();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setUpIds();

        setUpListeners();

    }

    private void setUpIds() {
        recordButton = (Button) findViewById(R.id.record_button);
        playButton = (Button) findViewById(R.id.play_button);
    }

    private void setUpListeners() {
        recordButton.setOnClickListener(recordClickListener);
        playButton.setOnClickListener(playClickListener);
    }


    private View.OnClickListener recordClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            isRecording = !isRecording;

            onRecord(isRecording);

            recordButton.setText(isRecording ? R.string.stop_recording : R.string.start_recording);
            playButton.setEnabled(!isRecording);
        }
    };

    private View.OnClickListener playClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            isPlaying = !isPlaying;

            onPlay(isPlaying);

            playButton.setText(isPlaying ? R.string.stop_playing : R.string.start_playing);
            recordButton.setEnabled(!isPlaying);
        }
    };

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioPlayerName);
            mediaPlayer.setOnCompletionListener(completionListener);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            stopPlaying();
        }
    };

    private void startRecording() {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // ACC_ELD is supported only from SDK 16+.
        // You can use other encoders for lower vesions.
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(96000);
        recorder.setOutputFile(audioPlayerName);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.release();
            completionRecording();
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            completionPlaying();
        }
    }

    private void reset() {
        isRecording = false;
        isPlaying = false;
    }

    private void completionRecording() {
        reset();
        recordButton.setText(R.string.start_recording);
        playButton.setText(R.string.start_playing);
        playButton.setEnabled(true);
    }

    private void completionPlaying() {
        reset();
        playButton.setText(R.string.start_playing);
        recordButton.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        stopRecording();

        stopPlaying();

    }

    @Override
    public void onStop() {
        super.onStop();

        stopRecording();

        stopPlaying();

    }

}
