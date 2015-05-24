package com.example.game2048;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.indywiz.game.library.*;
import com.indywiz.game.library.Game2048.Directions;
import com.example.game2048.OnSwipeTouchListener;

public class Home2048 extends Activity {
	
	private Game2048 gameLogicObject;
    int[][] prevState = null;
    int[][] currentState = null;
    
    public TextView scoreLabel;

    public TextView[][] gridLabels = null;

    public boolean goOn = false;

    @SuppressLint("UseSparseArrays")
	public Map<Integer, String> backgroundColor = new HashMap<Integer, String>();

    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home2048);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home2048, menu);
		return true;
	}

	@Override
	protected void onStart() {
		
		super.onStart();
		
		gameLogicObject = new Game2048();
		
		// Below code will set the puzzle to the centre of the screen.
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		TableLayout TableLayout1 = (TableLayout) findViewById(R.id.TableLayout1);
		TableLayout.LayoutParams tparams = new TableLayout.LayoutParams(width,width);
		TableLayout1.setLayoutParams(tparams);
		
		// Below code will recognise the swap and call appropriate function to process it
		LinearLayout LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);
		LinearLayout1.setOnTouchListener(new OnSwipeTouchListener(this){
		    public void onSwipeTop() {
		    	prevState = getStateOfGame();
		    	gameLogicObject.moveTo(Directions.UP);
		    	CheckGame();
		    }
		    public void onSwipeRight() {
		    	prevState = getStateOfGame();
		    	gameLogicObject.moveTo(Directions.RIGHT);
		    	CheckGame();
		    }
		    public void onSwipeLeft() {
		    	prevState = getStateOfGame();
		    	gameLogicObject.moveTo(Directions.LEFT);
		    	CheckGame();
		    }
		    public void onSwipeBottom() {
		    	prevState = getStateOfGame();
		    	gameLogicObject.moveTo(Directions.DOWN);
		    	CheckGame();
		    }

		    public boolean onTouch(View v, MotionEvent event) {
		    	return gestureDetector.onTouchEvent(event);
		    }
		}
		);
		
		scoreLabel = (TextView) findViewById(R.id.scoreLabel);
		
		
		gridLabels = new TextView[4][4];
		int id = 0;
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
            	id = getResources().getIdentifier("label" + String.valueOf(i) + String.valueOf(j),"id", "com.example.game2048");
            	gridLabels[i][j] = (TextView) findViewById(id);
            }
        }

        initializeColorMap();
        initializeGame();
	}
	
    

    


    

    public void CheckGame() {
    	currentState = getStateOfGame();
            boolean nextMoveDecision = Arrays.deepEquals(prevState, currentState);

            if(!gameLogicObject.checkGameOver() && !nextMoveDecision)
                generateNewBlock();

            if(gameLogicObject.gameWin && !goOn) {
            	
            	AlertDialog.Builder GameWinAlert = new AlertDialog.Builder(this);
            	GameWinAlert.setTitle("Congrats!! Your Score " + gameLogicObject.score);
            	GameWinAlert.setMessage("Cool!, now go on!");

            	GameWinAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	goOn = true;
                    }
                });
                
            	GameWinAlert.show();
            }
            refreshGrid();
        }


    private int[][] getStateOfGame() {

        int[][] state = new int[4][4];

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++)
                state[i][j] = gameLogicObject.blockStatus(i, j);
        }

        return state;

    }

    private void generateNewBlock() {
        Random randomNumber = new Random(System.nanoTime());
        int random = randomNumber.nextInt(4);
        int row = random;
        randomNumber.setSeed(System.nanoTime());
        int column = randomNumber.nextInt(4);
        int nextNumber = 2;
        double probabilityOfNextNumber = Math.random();
        if(probabilityOfNextNumber > 0.7)
            nextNumber = 4;

        System.out.println("Row : "+ row+ " Col : "+ column+ " number : "+ nextNumber);

        if(!gameLogicObject.spawnABlockAt(row, column, nextNumber))
            generateNewBlock();
    }

    private void initializeColorMap() {
        backgroundColor.put(2, "#eee4da");
        backgroundColor.put(4, "#ede0c8");
        backgroundColor.put(8, "#f2b179");
        backgroundColor.put(16, "#f59563");
        backgroundColor.put(32, "#f67c5f");
        backgroundColor.put(64, "#f65e3b");
        backgroundColor.put(128, "#edcf72");
        backgroundColor.put(256, "#edcc61");
        backgroundColor.put(512, "#edc850");
        backgroundColor.put(1024, "#edc53f");
        backgroundColor.put(2048, "#edc22e");
    }

    private void initializeGame() {
        goOn = false;
        Random randomInt = new Random(System.currentTimeMillis());
        int rowRandom = randomInt.nextInt(4);
        randomInt.setSeed(System.currentTimeMillis());
        int colRandom = randomInt.nextInt(4);
        gameLogicObject = new Game2048();
        gameLogicObject.initializeBoard(rowRandom, colRandom);
        refreshGrid();

    }

    private void refreshGrid() {
        for(int i = 0; i < 4; i++)
            for( int j = 0; j < 4; j++) {
                int gridNumber = gameLogicObject.blockStatus(i, j);

                if(gridNumber > 0) {
                    gridLabels[i][j].setText(String.valueOf(gridNumber));
                    gridLabels[i][j].setTextColor(Color.parseColor("#000000"));
                    gridLabels[i][j].setBackgroundColor(Color.parseColor((backgroundColor.get(gridNumber))));
                }
                else {
                    gridLabels[i][j].setText("0");
                    gridLabels[i][j].setTextColor(Color.parseColor("#5a534c"));
                    gridLabels[i][j].setBackgroundColor(Color.parseColor("#5a534c"));
                }
            }
        if(gameLogicObject.checkGameOver()) {
        	AlertDialog.Builder GameOverAlert = new AlertDialog.Builder(this);
        	GameOverAlert.setTitle("Game Over!! Your Score " + gameLogicObject.score);
        	GameOverAlert.setMessage("Restart Game?");

        	GameOverAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                	initializeGame();
                }
            });
        	GameOverAlert.show();
        }   
        scoreLabel.setText("Score: " + gameLogicObject.score);
    }

}
