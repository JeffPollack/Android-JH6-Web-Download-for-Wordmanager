package com.example.jeff.jh5_hangman_jdpollack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity  implements OnClickListener, HangmanUpdate{
    TextView display = null;
    TextView category = null;
    String currentFileName = null;
    myDrawHangmanView myDrawHangmanView;
    ArrayList<String> myWordListArray=null;
    String lineSeparator = System.getProperty("line.separator");



    static final String wordManager_Action = "com.example.jh5_wordmanager_jdpollack.action";
    static final int wordManager_Call = 2;



    public void updateMessage(String s){
        display.setText(s);
    }
    public void gameIsDone(boolean winner){

    }
    HangmanLogic hml;


    String[]words = {"pig","cow","sheep","dog","cat"};

    int[] button_resources = {R.id.a, R.id.b, R.id.c, R.id.d, R.id.e, R.id.f,
            R.id.g, R.id.h, R.id.i, R.id.j, R.id.k, R.id.l,
            R.id.m, R.id.n, R.id.o, R.id.p, R.id.q,R.id.r,
            R.id.s, R.id.t, R.id.u, R.id.v, R.id.w,R.id.x, R.id.y, R.id.z};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hml = new HangmanLogic(this);

        for (int i = 0; i < button_resources.length; i++) {
            Button b = (Button) findViewById(button_resources[i]);
            b.setOnClickListener(this);
        }
        display = (TextView) findViewById(R.id.display);
        category = (TextView) findViewById(R.id.category);

        if (currentFileName != null)
            category.setText(currentFileName);
        else
         category.setText("Animals to start");

        myDrawHangmanView = (myDrawHangmanView)findViewById(R.id.myDrawHangmanView);

        if (myWordListArray!= null)
            hml.newGame(20, myWordListArray);
        else
            hml.newGame(20, words);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }
    //=========  Call word list back from word manager
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        currentFileName = data.getStringExtra("currentFileName");


        String lineSeparator = System.getProperty("line.separator"); // ""/n" on Linux
        if (resCode != Activity.RESULT_OK)
        {
            Log.d("Mine", "Returning without proper Result code");
            return;
        }
        switch(reqCode)
        {
            case wordManager_Call:
                myWordListArray = data.getStringArrayListExtra("myWordListArray");
                currentFileName = data.getStringExtra("currentFileName");
            default:
                Log.d("Mine","Shouldn't happen");
                break;

        }
        //================== Redraw new game ====================
        setContentView(R.layout.activity_main);
        for (int i = 0; i < button_resources.length; i++) {
            Button b = (Button) findViewById(button_resources[i]);
            b.setOnClickListener(this);
        }
        display = (TextView) findViewById(R.id.display);
        category = (TextView) findViewById(R.id.category);
        category.setText(currentFileName);


        myDrawHangmanView = (myDrawHangmanView)findViewById(R.id.myDrawHangmanView);
        hml.newGame(20, myWordListArray);
        //================= End redraw new game ==================
    }
    //=========  End call word list back from word manager

    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d("Mine", "onOptionsItemSelected: " + item.getItemId());




        switch (item.getItemId()){
            case R.id.newgame:
                for (int i = 0; i < button_resources.length; i++) {
                    Button b = (Button) findViewById(button_resources[i]);
                    b.setVisibility(View.VISIBLE);
                    myDrawHangmanView.reset();
                }
                if (myWordListArray!= null)
                    hml.newGame(20, myWordListArray);
                else
                    hml.newGame(20, words);
                return true;
            case R.id.newcategory:
                Intent intent = new Intent(wordManager_Action);
                intent.putExtra("New Catagory", words);
                startActivityForResult(intent, wordManager_Call);

                return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v)
    {
        Button b = (Button)v;
        String label = b.getText().toString();
        char c = label.charAt(0);
        hml.buttonClicked(c);

        b.setVisibility(View.INVISIBLE);

        if (hml.buttonClicked(c) != true)
            myDrawHangmanView.increment();



    }

}
