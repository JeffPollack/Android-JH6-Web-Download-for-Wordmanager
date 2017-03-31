package com.example.jeff.jh5_wordmanager_jdpollack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

enum WordOrder {Sorted, FrontInsert, Append};

public class MainActivity extends Activity
        implements AdapterView.OnItemClickListener, FileOperations{

    ArrayList<String> myWordListArray = new ArrayList<String>();
    ArrayAdapter<String> arrayListAdapter;
    ListView myListView;
    WordOrder wordOrder = WordOrder.Sorted;
    WordFileManager wordFileManager = new WordFileManager(this);
    private String currentFileName;
    TextView fileNameDisplay;
    Button backButton;

    static final String webDownloader_Action = "com.example.jh6_webdownloader_jdpollack.action";
    static final int webDownloader_Call = 1;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myListView = (ListView)findViewById(R.id.myListView);
        Intent receivedIntent = getIntent();
        String words = receivedIntent.getStringExtra("words");
        backButton = (Button)findViewById(R.id.backButton);



        arrayListAdapter=
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myWordListArray);

        myListView.setAdapter(arrayListAdapter);

        myListView.setOnItemClickListener(this);

        updateFromResource(R.array.countries_array);

        MyEditText myEditText = new MyEditText();
        registerForContextMenu(myListView);
        fileNameDisplay = (TextView)findViewById(R.id.file_name); //filenameDisplay
        String lastFileName = wordFileManager.getMyLastFileName();

        open(lastFileName);

        // ======================= Back to Hangman
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent outData = new Intent();
                outData.putExtra("currentFileName",currentFileName);
                outData.putStringArrayListExtra("myWordListArray", myWordListArray);
                setResult(Activity.RESULT_OK, outData);

                finish();
            }
        });
        // ======================== Eng back to Hangman
    }



    // It is Important that we never change the address of our ArrayList.
    private void updateList()
    {
        if (wordOrder == WordOrder.Sorted)
            Collections.sort(myWordListArray);
        arrayListAdapter.notifyDataSetChanged();
    }
    // This is called when on the first program call to initialize words from an array resource
    public void updateFromResource(int resource)
    {
        String[] words = getResources().getStringArray(resource);
        myWordListArray.clear();
        for (int i=0; i < words.length; i++)
            myWordListArray.add(words[i]);

        updateList();
    }

    // This is called when when just read in a new category to work with
    public void updateFromArrayList(ArrayList<String> arr)
    {
        myWordListArray.clear();
        for (int i=0; i < arr.size(); i++)
            myWordListArray.add(arr.get(i));

        updateList();
    }


    // This is called when the user hits the Enter key from the EditText box
    public void addWord(String word)
    {
        if(wordOrder == WordOrder.FrontInsert)
            myWordListArray.add(0, word);
        else
            myWordListArray.add(word);
        updateList();
    }

    // This is called when the user does a Context Menu (Long Press) on a word and selectes "Remove"
    public void removeWord(int index)
    {
        myWordListArray.remove(index);
        updateList();
    }

    // This is called when the user changes a word (Edit, CAPITALIZE, lower case, etc.)
    public void replaceWord(int index, String value)
    {
        myWordListArray.set(index, value);
        updateList();
    }



    // Standard Options Menu Creation from Resource
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_options_menu, menu);

        return true;
    }

    // User has selected an item from the Options Menu
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            //enum WordOrder {Sorted, FrontInsert, Append};
            case R.id.insert_at_front:
                wordOrder = WordOrder.FrontInsert;
                return true;
            case R.id.insert_at_end:
                wordOrder = WordOrder.Append;
                return true;
            case R.id.sort_words:
                wordOrder = WordOrder.Sorted;
                updateList();
                return true;

            case R.id.save:
                Log.d("Mine", "save option menu");
                saveList();
                return true;
            case R.id.open:
                Log.d("Mine", "open option menu");
                OpenDeleteFileHandler openDelete = new OpenDeleteFileHandler(this, wordFileManager,
                        "Select Category to Open, or hit Back button",
                        this, DIALOG_TYPE.Open);
                return true;
            case R.id.new_word_file:
                Log.d("Mine", "new option menu");
                NewFileDialogHandler nfdh = new NewFileDialogHandler(this, this);
                nfdh.show();
                return true;
            case R.id.delete:
                Log.d("Mine", "delete option menu");
                OpenDeleteFileHandler openDelete2 = new OpenDeleteFileHandler(this, wordFileManager,
                        "Select Category to Delete, or hit Back button",
                        this, DIALOG_TYPE.Delete);
                return true;
            case R.id.download_files:
                Log.d("Mine", "download files");
                Intent intent = new Intent(webDownloader_Action);
                intent.putExtra("Download Files", myWordListArray);
                startActivityForResult(intent, webDownloader_Call);

                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    ///*********************** Options items ****************
    public void onPause()
    {
        super.onPause();
        saveList();
    }
    private void saveList()
    {
        wordFileManager.saveSerializable( myWordListArray , currentFileName);
        wordFileManager.storeMyFileName(currentFileName);
    }

    private void changeFileName(String fileName)
    {
        currentFileName = fileName;
        if (currentFileName == null)
            currentFileName = "default_drawing";
        fileNameDisplay.setText(currentFileName);
        wordFileManager.storeMyFileName(currentFileName);
    }

    @Override
    public void newFile(String fileName) {
        ArrayList<String> temp = new ArrayList<>();
        updateFromArrayList(temp);
        changeFileName(fileName);
    }


    @Override
    public void open(String fileName) {
        changeFileName(fileName);
        ArrayList<String> arr;
        Serializable obj = wordFileManager.getSerializable(currentFileName);
        if (obj == null)
            arr = new ArrayList<String>(); // Empty list
        else
            arr = (ArrayList<String>)obj;
        updateFromArrayList(arr);
        wordFileManager.storeMyFileName(currentFileName);
    }


    @Override
    public void delete(String fileName)
    {
        wordFileManager.deleteFile(fileName);
    }

    //****************** End options items ************************

    // Create Context Menu for the ListView

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(R.string.word_context_menu_prompt);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.word_list_menu, menu);
    }

    //***************** Start call back from web downloader *******************
    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        super.onActivityResult(reqCode, resCode, data);


        if (resCode != Activity.RESULT_OK)
        {
            Log.d("Mine", "Returning without proper Result code");
            return;
        }
        switch(reqCode)
        {
            case webDownloader_Call:
                ArrayList<String>arr = data.getStringArrayListExtra("myWordListArray");
                updateFromArrayList(arr);
                break;
            default:
                Log.d("Mine","Shouldn't happen");
                break;

        }


    }

    //***************** End call back from web downloader *******************

    // User has done a long press on a particular word in ListView
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        int index = menuInfo.position;
        TextView text = (TextView) menuInfo.targetView;
        String value =text.getText().toString();


        switch (item.getItemId())
        {
            case R.id.remove_word:
                removeWord(index);
                return true;

            case R.id.capitalize_word:
                replaceWord(index, value.toUpperCase());
                return true;

            case R.id.lower_case_word:
                replaceWord(index, value.toLowerCase());
                return true;

            case R.id.capitalize_first_letter:
                char c = value.charAt(0);
                value = Character.toUpperCase(c) + value.substring(1).toLowerCase();
                replaceWord(index, value);
                return true;

            case R.id.edit_word:
                EditWordDialogHandler ewdh = new EditWordDialogHandler(this, index,
                        value, this);
                ewdh.show();
                return true;

        }

        return false;
    }

    // Not doing anything with the normal Item click ... just logging for now
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // GridView grid = (GridView) parent;
        TextView text = (TextView) view;
        String str =text.getText() + " pos="+position + " id=" +id;
        Log.d("Mine", "onItemClick: " + str);
    }

    // Processing for the EditText widget for entering in new words
    //*****************  Inner class for EditText   **************************
    class MyEditText implements View.OnKeyListener
    {
        EditText myEdit;

        MyEditText()
        {
            myEdit = (EditText)findViewById(R.id.myEditText);
            myEdit.setOnKeyListener(MyEditText.this);
        }
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    addWord(myEdit.getText().toString());
                    myEdit.setText("");
                    return true;
                }
            return false;
        }

    }
    // *****************  End of Inner class for EditText   *******************

}
