package org.huangxiaozhe.Lianzhu;

import java.util.Timer;
import java.util.TimerTask;

import org.huangxiaozhe.Common.EventsData;
import org.huangxiaozhe.Common.Point;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GameSingle extends Activity{
	private static final String GAME_REQCONTINUE="reqcontinue";
	private static final int PLAY_NONE=0;
	private static final int PLAY_PLAYER=1;
	private static final int PLAY_COMPUTER=2;
	private static final String PREF_TABLE="table";
	private static final String PREF_BOARDSIZE="boardsize";
	private static final String PREF_UPPERHAND="upperhand";
	private static final String PREF_DIFFICULTY="difficulty";
	private static final String PREF_COUNTER="counter";
	private static final String TAG = "Lianzhu";
	private static final String CAN_CONTINUE = "cancontinue";
	private static boolean newgame;
	SharedPreferences Pref;
	
	private static int turn=0;			//1 if player goes next;0 if computer goes next
	private static boolean upperHand;	//True if player goes first
	private static int difficulty;
	private static int boardSize;
	private static int table[][];
	private static int playCounter;
	Point point;
	
	private GameViewSingle gameView;
	private Computer computer;
	private EventsData eventsData;
	
	Timer timer;
	Handler handler;
	TimerTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Pref=getSharedPreferences("GAME", 0);
	    if(getIntent().getBooleanExtra(GAME_REQCONTINUE, false) && Pref.getBoolean(CAN_CONTINUE, true)){
	    	continueGameInit();
	     }
		else{
			newGameInit();
		}
	    
	    //classes initialize
		computer=new Computer(this,boardSize,difficulty);
		eventsData=new EventsData(this);
		eventsData.clearEvent();
		gameView=new GameViewSingle(this);
		
		//if computer gose first,computer goes to the ceter of board
		if(!upperHand){
			int t=boardSize/2;
			table[t][t]=PLAY_COMPUTER;
			playCounter--;
			try{
				eventsData.addEvent(playCounter, t, t, PLAY_COMPUTER);
			}
			catch(Exception e){
				Log.d("Lianzhu","SQL error!");
			}
			gameView.drawPoint(t, t);
		}
		turn=1;
		
		setContentView(gameView);
		gameView.requestFocus();
		
		newgame=false;
		getIntent().putExtra(GAME_REQCONTINUE, true); 
		Pref.edit().putBoolean(CAN_CONTINUE, true).commit();
		
		//Timer initialize
		timer=new Timer();
		handler=new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
				case 1:
					openFinalDialog();
					break;
				case 2:
					computerGo();
					break;
				}
				super.handleMessage(msg);
			}
		};
		task=new TimerTask(){
			@Override
			public void run() {
				Message msg=new Message();
				msg.what=1;
				handler.sendMessage(msg);
			}
		};
	}
	private void openFinalDialog() {
		//open dialog to start new game or return
		new AlertDialog.Builder(gameView.getContext())
			.setTitle(R.string.game_over_label)
			.setMessage(R.string.game_over_message)
			.setPositiveButton(R.string.again_label, 
					new DialogInterface.OnClickListener() {		
						@Override
						public void onClick(DialogInterface dialog, int which) {
							newGame();
						}
					})
			.setNegativeButton(R.string.return_label, 
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
			.show();			
	}
	private void newGameInit() {
		//start new game
		difficulty=Prefs.getDifficulty(this);
		boardSize=Prefs.getBoardsize(this);
		upperHand=Prefs.getUpperhand(this);
		table=new int[boardSize][boardSize];
		for(int i=0;i<boardSize;i++){
			for(int j=0;j<boardSize;j++){
				table[i][j]=PLAY_NONE;
			}
		}
		playCounter = boardSize * boardSize;
	}

	private void continueGameInit() {
		//continue game read preferences
		boardSize=Pref.getInt(PREF_BOARDSIZE, Prefs.getBoardsize(this));
		StringBuffer string=new StringBuffer();
		for(int i=0;i<boardSize*boardSize;i++){
			string.append(0);
		}
		tableFromString(boardSize,Pref.getString(PREF_TABLE, string.toString()));
		upperHand=Pref.getBoolean(PREF_UPPERHAND,Prefs.getUpperhand(this));
		difficulty=Pref.getInt(PREF_DIFFICULTY,Prefs.getDifficulty(this));
		playCounter=Pref.getInt(PREF_COUNTER,boardSize*boardSize);
	}

	protected int getBoardSize(){
		return boardSize;
	}
	
	protected boolean getUpperHand(){
		return upperHand;
	}
	
	protected int[][] getTable(){
		return table;
	}
	
	//get the number of connected chessman in one direction
	private static boolean findFiveIn1D(int x,int y,int direction,int player){
		Point p=new Point(x,y);
		int count=1;
		for(p.moveOneStep(direction);isInBoard(p);p.moveOneStep(direction)){	
			if(table[p.x][p.y]==player){
				count++;
			}
			else {
				break;
			}
		};
		p.set(x, y);
		direction+=4;
		for(p.moveOneStep(direction);isInBoard(p);p.moveOneStep(direction)){	
			if(table[p.x][p.y]==player){
				count++;
			}
			else {
				break;
			}
		};
		if(count>=5)
			return true;
		else
			return false;
	}
	
	private boolean isGameOver(int x,int y){
		int player=table[x][y];
		for(int i=0;i<4;i++){
			if(findFiveIn1D(x,y,i,player))
				return true;
		}
		return false;
	}
	
	private static boolean isInBoard(Point p){
		return p.x>=0 && p.x<boardSize && p.y>=0 && p.y<boardSize;
	}

	public void manGo(int x, int y) {
		if(turn!=1){
			Toast.makeText(gameView.getContext(), "Wait a minute.Computer is thinking!", Toast.LENGTH_SHORT).show();
			return;
		}
		if(isInBoard(new Point(x,y))&& table[x][y]==PLAY_NONE){
			table[x][y]=PLAY_PLAYER;
			playCounter--;
			gameView.drawPoint(x,y);
			try{
				eventsData.addEvent(playCounter, x, y, PLAY_PLAYER);
			}
			catch(Exception e){
				Log.d("Lianzhu","SQL error!");
			}
			if(playCounter<=0){
				Toast.makeText(gameView.getContext(), R.string.game_draw, Toast.LENGTH_SHORT).show();
				gameOver();
				return;
			}
			if(isGameOver(x,y)){
				Toast.makeText(gameView.getContext(), R.string.game_win, Toast.LENGTH_SHORT).show();
				gameOver();
				return;
			}
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=2;
					handler.sendMessage(msg);
				}
			}, 500);	
		}
		else{
			Toast.makeText(gameView.getContext(), "You can not go here!", Toast.LENGTH_SHORT).show();
		}
	}

	private void gameOver() {
		Pref.edit().putBoolean(CAN_CONTINUE, false).commit();
		eventsData.clearEvent();
		timer.schedule(task, 3000);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.gamemenu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch(item.getItemId()){
		case R.id.game_new:
			newGame();
			return true;
		case R.id.game_undo:
			unDo();
			return true;
		case R.id.game_about:
			openAboutDialog();
			return true;
		case R.id.game_exit:
			finish();
			return true;
		case R.id.game_settings:
			startActivity(new Intent(this,Prefs.class));
			return true;
		}
		return false;
	}
	
	private void computerGo(){
		point=computer.play(table);
		Log.d("Lianzhu",String.format("x: %d y: %d",point.x,point.y));
		table[point.x][point.y]=PLAY_COMPUTER;
		playCounter--;
		gameView.drawPoint(point.x, point.y);
		try{
			eventsData.addEvent(playCounter, point.x, point.y, PLAY_COMPUTER);
		}
		catch(Exception e){
			Log.d("Lianzhu","SQL error!");
		}
		if(playCounter<=0){
			Toast.makeText(gameView.getContext(), R.string.game_draw, Toast.LENGTH_SHORT).show();
			gameOver();
		}
		else if(isGameOver(point.x,point.y)){
			Toast.makeText(gameView.getContext(), R.string.game_lose, Toast.LENGTH_SHORT).show();
			gameOver();
		}
		else
			turn=1;
		
	}
	
	private void newGame() {
		newgame=true;
		this.finish();
	}

	private void unDo() {
		int t[]=new int[4];
		boolean b=eventsData.delete2Events(t);
		if(!b){
			Toast.makeText(gameView.getContext(),"Unable to undo!", Toast.LENGTH_SHORT).show();
			return;
		}
		else{
			table[t[0]][t[1]]=PLAY_NONE;
			gameView.drawPoint(t[0], t[1]);
			table[t[2]][t[3]]=PLAY_NONE;
			gameView.drawPoint(t[2], t[3]);
			playCounter++;
		}
	}

	private void openAboutDialog() {
		new AlertDialog.Builder(GameSingle.this)
		.setTitle(R.string.about_title)
		.setMessage(R.string.about_text)
		.setPositiveButton(R.string.return_label, 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
		.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Music.stop(this); 
		
		// Save the current game
		Pref.edit()
			.putInt(PREF_BOARDSIZE, boardSize)
	    	.putString(PREF_TABLE,tableToString())
	    	.putBoolean(PREF_UPPERHAND, upperHand)
	    	.putInt(PREF_DIFFICULTY, difficulty)
	    	.putInt(PREF_COUNTER, playCounter)
	    	.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Prefs.getMusic(this)){
			Music.play(this, R.raw.music);
		}
	}
	
	 //convert table[][] to string
	 private String tableToString() {
		StringBuilder str = new StringBuilder();
	    for(int i=0;i<boardSize;i++){
	       for(int j=0;j<boardSize;j++){
	   		  str.append(table[i][j]);
           }
	    }
	    return str.toString();
	 }

	 //convert string to table[][]
     static protected void tableFromString(int size,String str) {
	    Log.d(TAG,str);
    	table  = new int[size][size];
	    for (int i = 0; i <size; i++) {
	    	for(int j=0;j<size;j++){
	    		if(str.charAt(i*size+j)>='0')
	    			table[i][j] = str.charAt(i*size+j) - '0';
	    		else
	    			table[i][j]=0;
	    	}
	    }
	 }

	@Override
	protected void onDestroy() {
		if(newgame==true){
			startActivity(new Intent(this,GameSingle.class)
				.putExtra(GAME_REQCONTINUE, false));		
		}
		super.onDestroy();
	}
}