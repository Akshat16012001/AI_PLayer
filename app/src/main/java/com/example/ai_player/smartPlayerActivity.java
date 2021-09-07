package com.example.ai_player;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class smartPlayerActivity extends AppCompatActivity
    {

        private RelativeLayout parentRelativeLayout;
        private SpeechRecognizer speechRecognizer;
        private Intent speechRecognizerIntent;
        private String Keeper0="";

        private ImageView pausePlayBtn, nextBtn, previousBtn;
        private TextView songNametxt;

        private ImageView imageView;
        private RelativeLayout lowerRelativeLayout;
        private Button voiceEnabledButton;
        private String mode="ON";

        private MediaPlayer myMediaPlayer;
        private int position;
        private ArrayList<File> mysongs;
        private String mSongName;

        private final Random myRandom = new Random();
        private int om;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.smartplayer);

            checkVoiceCommandPermission();




            pausePlayBtn = findViewById(R.id.play_pause_btn);
            nextBtn = findViewById(R.id.next_btn);
            previousBtn = findViewById(R.id.previous_btn);
            imageView = findViewById(R.id.logo);

            lowerRelativeLayout = findViewById(R.id.lower);
            voiceEnabledButton = findViewById(R.id.voice_enabled_btn);
            songNametxt = findViewById(R.id.songName);



            parentRelativeLayout=findViewById(R.id.parentRelativeLayout);
            speechRecognizer=SpeechRecognizer.createSpeechRecognizer(smartPlayerActivity.this);
            speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


            validateReceivedValuesAndStartPlaying();

            imageView.setBackgroundResource(R.drawable.logo);

            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle bundle)
                {
                    ArrayList<String> matchesFound = bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);

                    if(matchesFound!=null)
                    {
                        if(mode.equals("ON"))
                        {
                            Keeper0 = matchesFound.get(0);

                            if(Keeper0.equals("pause the song" )||Keeper0.equals("pause"))
                            {
                                playPauseSong();
                                Toast.makeText(smartPlayerActivity.this,"Command: "+Keeper0,Toast.LENGTH_LONG).show();
                            }
                            else if(Keeper0.equals("play the song")||Keeper0.equals("play"))
                            {
                                playPauseSong();
                                Toast.makeText(smartPlayerActivity.this,"Command: "+Keeper0,Toast.LENGTH_LONG).show();
                            }
                            else if(Keeper0.equals("play next song")||Keeper0.equals("next"))
                            {
                                playNextSong();
                                Toast.makeText(smartPlayerActivity.this,"Command: "+Keeper0,Toast.LENGTH_LONG).show();
                            }
                            else if(Keeper0.equals("play previous song")||Keeper0.equals("previous"))
                            {
                                playPreviousSong();
                                Toast.makeText(smartPlayerActivity.this,"Command: "+Keeper0,Toast.LENGTH_LONG).show();
                            }
                            else if (Keeper0.equals("stop the song")||Keeper0.equals("stop"))
                            {
                                stopSong();
                                Toast.makeText(smartPlayerActivity.this, "Command :"+Keeper0, Toast.LENGTH_LONG).show();
                            }
                            else if (Keeper0.equals("randomize the song")||Keeper0.equals("randomize")||Keeper0.equals("random"))
                            {
                                Random_ize();
                                Toast.makeText(smartPlayerActivity.this, "Command :"+Keeper0, Toast.LENGTH_LONG).show();
                            }
                            else if (Keeper0.equals("repeat the song")||Keeper0.equals("repeat")||Keeper0.equals("once more"))
                            {
                                REpeat();
                                Toast.makeText(smartPlayerActivity.this, "Command :"+Keeper0, Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });



            voiceEnabledButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(mode.equals("ON"))
                    {
                        mode="OFF";
                        voiceEnabledButton.setText("Voice Enabled Mode = OFF");
                        lowerRelativeLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mode="ON";
                        voiceEnabledButton.setText("Voice Enabled Mode = ON");
                        lowerRelativeLayout.setVisibility(View.GONE);
                    }
                }
            });


            pausePlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPauseSong();
                }
            });


            previousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myMediaPlayer.getCurrentPosition() > 0)
                    {
                        playPreviousSong();
                    }
                }
            });

            nextBtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myMediaPlayer.getCurrentPosition() > 0)
                    {
                        playNextSong();
                    }
                }
            });
        }


        private void validateReceivedValuesAndStartPlaying()
        {
            if(myMediaPlayer!=null)
            {
                myMediaPlayer.stop();
                myMediaPlayer.release();
            }
            Intent intent = getIntent();
            Bundle bundle=intent.getExtras();

            mysongs = (ArrayList)bundle.getParcelableArrayList("song");
            mSongName = mysongs.get(position).getName();
            String songName=intent.getStringExtra("name");

            songNametxt.setText(songName);
            songNametxt.setSelected(true);

            om = mysongs.size();

            position=bundle.getInt("position",0);
            Uri uri=Uri.parse(mysongs.get(position).toString());

            myMediaPlayer=MediaPlayer.create(smartPlayerActivity.this,uri);
            myMediaPlayer.start();


        }

        private void playPauseSong()
        {
            imageView.setBackgroundResource(R.drawable.four);

            if(myMediaPlayer.isPlaying())
            {
                pausePlayBtn.setImageResource(R.drawable.play);
                myMediaPlayer.pause();
            }
            else
            {
                pausePlayBtn.setImageResource(R.drawable.pause);
                myMediaPlayer.start();

                imageView.setImageResource(R.drawable.five);
            }
        }

        private void stopSong()
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        private void REpeat()
        {
            Uri uri=Uri.parse(mysongs.get(position).toString());

            myMediaPlayer= MediaPlayer.create(smartPlayerActivity.this,uri);

            mSongName=mysongs.get(position).toString();

            songNametxt.setText(mSongName);
            myMediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.three);

            if(myMediaPlayer.isPlaying())
            {
                pausePlayBtn.setImageResource(R.drawable.pause);
            }
            else
            {
                pausePlayBtn.setImageResource(R.drawable.play);

                imageView.setImageResource(R.drawable.five);
            }
        }

        private void Random_ize()
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();

            for (int i=0; i<=om; i++)
            {
                position = myRandom.nextInt(om);
            }
            Uri uri=Uri.parse(mysongs.get(position).toString());

            myMediaPlayer= MediaPlayer.create(smartPlayerActivity.this,uri);

            mSongName=mysongs.get(position).toString();

            songNametxt.setText(mSongName);
            myMediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.four);

            if(myMediaPlayer.isPlaying())
            {
                pausePlayBtn.setImageResource(R.drawable.pause);
            }
            else
            {
                pausePlayBtn.setImageResource(R.drawable.play);

                imageView.setImageResource(R.drawable.five);
            }

        }

        private void playNextSong()
        {
            myMediaPlayer.pause();
            myMediaPlayer.stop();
            myMediaPlayer.release();

            position=((position+1)%mysongs.size());

            Uri uri=Uri.parse(mysongs.get(position).toString());

            myMediaPlayer= MediaPlayer.create(smartPlayerActivity.this,uri);

            mSongName=mysongs.get(position).toString();

            songNametxt.setText(mSongName);
            myMediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.three);

            if(myMediaPlayer.isPlaying())
            {
                pausePlayBtn.setImageResource(R.drawable.pause);
            }
            else
            {
                pausePlayBtn.setImageResource(R.drawable.play);

                imageView.setImageResource(R.drawable.five);
            }
        }
        private void playPreviousSong()
        {
            myMediaPlayer.pause();
            myMediaPlayer.stop();
            myMediaPlayer.release();

            position=((position-1)<0 ? (mysongs.size()-1):(position-1));



            Uri uri=Uri.parse(mysongs.get(position).toString());
            myMediaPlayer= MediaPlayer.create(smartPlayerActivity.this,uri);

            mSongName=mysongs.get(position).toString();

            songNametxt.setText(mSongName);
            myMediaPlayer.start();

            imageView.setBackgroundResource(R.drawable.two);

            if(myMediaPlayer.isPlaying())
            {
                pausePlayBtn.setImageResource(R.drawable.pause);
            }
            else
            {
                pausePlayBtn.setImageResource(R.drawable.play);

                imageView.setImageResource(R.drawable.five);
            }
        }

        private void checkVoiceCommandPermission()
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                if(!(ContextCompat.checkSelfPermission(smartPlayerActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
                {
                 Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("Package: "+getPackageName()));
                 startActivity(intent);
                }
            }
        }
    }


