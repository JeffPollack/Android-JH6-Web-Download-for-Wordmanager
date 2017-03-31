package com.example.jeff.jh5_hangman_jdpollack;

/**
 * Created by Jeff on 10/27/2015.
 */
import java.util.ArrayList;
import android.util.Log;

class HangmanLogic
{
    private String wordToGuess;
    private boolean[] letterGuess;
    private int missesAllowed;
    private int missesSoFar;
    private boolean wordIsGuessed = false;

    private HangmanUpdate hangmanUpdate = null;

    // Constructor ... pass in the reference to the class implementing HangmanUpdate
    // It is probably your Activity, and you are probably passing "this" to it.
    public HangmanLogic(HangmanUpdate hangmanUpdate)
    {
        this.hangmanUpdate = hangmanUpdate;
    }

    private String gameStatus()
    {
        StringBuilder sb = new StringBuilder();
        wordIsGuessed = true; // optimistic
        for (int i=0; i < wordToGuess.length(); i++)
        {
            if (letterGuess[i])
                sb.append(wordToGuess.charAt(i)+" ");
            else
            {
                sb.append("_ ");
                wordIsGuessed = false;
            }
        }
        return sb.toString();
    }

    // Call either of the following 2 routines to start a new Hangman session.
    // The missesAllowed is the number of misses before the user loses.
    // The words array(or ArrayList) have the possible words that the Hangman logic can pick from.

    public void newGame(int missesAllowed, String[] words)
    {
        this.missesAllowed = missesAllowed;

        int len = words.length;
        int index = (int)(len * Math.random());
        wordToGuess = words[index];
        newGameCommon();

    }
    public void newGame(int missesAllowed, ArrayList<String> words)
    {
        this.missesAllowed = missesAllowed;

        int len = words.size();
        int index = (int)(len * Math.random());
        wordToGuess = words.get(index);
        newGameCommon();
    }
    private void newGameCommon()
    {
        missesSoFar = 0;

        Log.d("Mine", "Word to guess="+wordToGuess);

        letterGuess = new boolean[wordToGuess.length()];
        for (int i=0; i < wordToGuess.length(); i++)
            letterGuess[i]= false;

        // Activity will get called here
        hangmanUpdate.updateMessage(gameStatus());
    }

    //Call this routine everytime a letter is picked.
    // A True is returned if the letter sent in actually matched the word
    // A False is returned if the letter sent in is a Miss.
    public boolean buttonClicked(char c)
    {
        boolean letterMatched=false;
        for (int i=0; i < wordToGuess.length(); i++)
        {
            char c2 = wordToGuess.charAt(i);
            if (Character.isLetter(c2))
            {
                if (c == Character.toLowerCase(c2))
                {
                    letterGuess[i]= true;
                    letterMatched = true;
                }
            }
            else
                letterGuess[i] = true; // Non letters automatically match
        }
        String message = gameStatus();

        if (wordIsGuessed)
        {
            // Activity will get called here
            hangmanUpdate.updateMessage(message+ " and you win!!!!");
            hangmanUpdate.gameIsDone(true);
        }
        else
        {
            if (!letterMatched)
                missesSoFar += 1;
            if (missesSoFar >= missesAllowed)
            {
                // Activity will get called here
                hangmanUpdate.updateMessage(message + " and you Lose!!!" +
                        "\n"+ wordToGuess);
                hangmanUpdate.gameIsDone(false);
            }
            else
                hangmanUpdate.updateMessage(message); // Activity is called here

        }
        return letterMatched;
    }

}