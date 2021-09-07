package com.example.ai_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private String[] itemsAll;
    private ListView mSongsList;

    private RelativeLayout parent_Relative_Layout;
    private SpeechRecognizer speechRecognizer_r;
    private Intent speechRecognizerIntent_r;
    private String Keeper="";

    private int ok;
    private ImageButton voiceSearchBtn;
    private String checker="";
    private ArrayList<File> checkme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceSearchBtn = findViewById(R.id.voice_search_btn);

        parent_Relative_Layout = findViewById(R.id.parent_Relative_Layout);
        speechRecognizer_r=SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent_r=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent_r.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent_r.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        mSongsList= findViewById(R.id.songsList);
        appExternalStoragePermission();
        checkVoiceCommandPermission();

        speechRecognizer_r.setRecognitionListener(new RecognitionListener() {
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
                ArrayList<String> matchesFound = bundle.getStringArrayList(speechRecognizer_r.RESULTS_RECOGNITION);

                if(matchesFound!=null)
                {
                    Keeper = matchesFound.get(0);
                    checker = Keeper;
                    Search_song();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please Say Something...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        voiceSearchBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionevent)
            {
                switch (motionevent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer_r.startListening(speechRecognizerIntent_r);
                        Keeper="";

                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer_r.stopListening();
                        break;
                }
                return false;

            }
        });


    }
    public void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("Package: "+getPackageName()));
                startActivity(intent);
            }
        }
    }
    public void appExternalStoragePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displayAudioSongsName();

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {


                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {

                        token.continuePermissionRequest();
                    }
            }).check();
    }

    public ArrayList<File> readOnlyAudioSongs(File file)
    {
        ArrayList<File> arrayList=new ArrayList<>();

        File[] allfiles=file.listFiles();
        for(File individualFile:allfiles)
        {
            if(individualFile.isDirectory() && !individualFile.isHidden())
            {
                arrayList.addAll(readOnlyAudioSongs(individualFile));
            }
            else
            {
                if(individualFile.getName().endsWith(".mp3"))
                {
                    arrayList.add(individualFile);
                }
            }
        }

        return arrayList;
    }

    private void displayAudioSongsName()
    {
        final ArrayList<File> audiosongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        checkme = audiosongs;

        itemsAll= new String[audiosongs.size()];
        int songcounter;
        for( songcounter=0;songcounter<audiosongs.size();songcounter++)
        {
            itemsAll[songcounter] = audiosongs.get(songcounter).getName();

        }

        ok = audiosongs.size();

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,itemsAll);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String songName=mSongsList.getItemAtPosition(position).toString();

                Intent intent = new Intent(MainActivity.this,smartPlayerActivity.class);

                intent.putExtra("song",audiosongs);
                intent.putExtra("name",songName);
                intent.putExtra("position",position);

                startActivity(intent);
            }
        });
    }


    private void Search_song()
    {
        for (int i=0;i<=ok;i++)
        {
            if (checker == checkme.get(i).toString())
            {
                String songName=checkme.get(i).toString();

                Intent intent = new Intent(MainActivity.this,smartPlayerActivity.class);

                intent.putExtra("song",checkme);
                intent.putExtra("name",songName);
                intent.putExtra("position",i);

                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Please say something...", Toast.LENGTH_LONG).show();
            }
        }
    }


}




