package com.app.emcurauc.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AudioRecordTest {
    private MediaRecorder recorder = null;
    private String fileName = null;
    private Context context;

    public AudioRecordTest(Context context) {
        this.context = context;
        fileName = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/audiorecordtest.mp3";
    }

    boolean isRecording = false;

    public void startRecording() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // Use MPEG_4 for MP3 encoding
            recorder.setAudioEncodingBitRate(560000); // 128 kbps
            recorder.setAudioSamplingRate(44100); // 44.1 kHz
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); // Use AAC for MP3 encoding

            try {
                recorder.prepare();
                recorder.start();
                isRecording = true;
            } catch (IOException | IllegalStateException e) {
                Log.e("-- AudioRecordTest", "startRecording: ", e);
            }
        }
    }


    public void stopRecording() {
        if (isRecording) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            isRecording = false;
        }
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    private MediaPlayer mediaPlayer = null;

    public void playRecordedAudio() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(fileName);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e("AudioRecordTest", "Error playing audio: " + e.getMessage());
            }
        }
    }

    public String getFilePath() {
        return fileName;
    }
}
