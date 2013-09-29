package org.huangxiaozhe.Lianzhu;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.huangxiaozhe.Common.MSG;
import org.huangxiaozhe.Common.Point;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class GameMulti extends Activity{
	private static final String GAME="GAME";
	private static final String BOARDSIZE="boardSize";
	private static final String UPPERHAND="upperHand";
	private static final int PLAY_NONE=0;
	private static final int PLAY_PLAYER=1;
	private static final int PLAY_COMPUTER=2;
	private static final String TAG = "Lianzhu";
	private static final int DELAY=1;
	private static final int GO=2;
	private static final int LOSE=3;
	private static final int DRAW=4;
	private int x;
	private int y;
	
	private static boolean turn=false;			//1 if player goes next;0 if computer goes next
	private static boolean upperHand;	//True if player goes first
	private static int boardSize;
	private static int table[][];
	private static int playCounter;
	Point point;
	private GameViewMulti gameView;
	
	Timer timer;
	Handler handler;
	TimerTask task;
	private ObjectOutputStream Writer;
	private Handler mHandler;
	private Receiver receiver;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MyApp app=(MyApp) getApplicationContext();
		app.getReader();
		Writer=app.getWriter();
		
		boardSize=getIntent().getIntExtra(BOARDSIZE, 15);
		upperHand=getIntent().getBooleanExtra(UPPERHAND, false);
		
		table=new int[boardSize][boardSize];
		for(int i=0;i<boardSize;i++)
			for(int j=0;j<boardSize;j++)
				table[i][j]=PLAY_NONE;
		playCounter = boardSize * boardSize;
		if(upperHand)
			turn=true;
		else
			turn=false;
		
		gameView=new GameViewMulti(this);
		setContentView(gameView);
		gameView.requestFocus();
		
		mHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case GO:
					table[x][y]=PLAY_COMPUTER;
					playCounter--;
					gameView.drawPoint(x,y);
					break;
				case LOSE:
					Toast.makeText(gameView.getContext(), R.string.game_lose, Toast.LENGTH_SHORT).show();
					delay();
					break;
				case DRAW:
					Toast.makeText(gameView.getContext(), R.string.game_draw, Toast.LENGTH_SHORT).show();
					delay();
					break;
				}
			}
			
		};
		
		//Timer initialize
		timer=new Timer();
		handler=new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
				case DELAY:
					finish();
					break;
				}
				super.handleMessage(msg);
			}
		};
		task=new TimerTask(){
			@Override
			public void run() {
				Message msg=new Message();
				msg.what=DELAY;
				handler.sendMessage(msg);
			}
		};
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
		if(!turn){
			Toast.makeText(gameView.getContext(), "Wait,it is not your turn!", Toast.LENGTH_SHORT).show();
			return;
		}
		if(isInBoard(new Point(x,y))&& table[x][y]==PLAY_NONE){
			turn=false;
			table[x][y]=PLAY_PLAYER;
			playCounter--;
			gameView.drawPoint(x,y);
			try {
				Writer.writeObject(new MSG("GO",String.format("%d %d", x,y)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(playCounter<=0){
				try {
					Writer.writeObject(new MSG("DRAW",""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(gameView.getContext(), R.string.game_draw, Toast.LENGTH_SHORT).show();
				delay();
				return;
			}
			if(isGameOver(x,y)){
				try {
					Writer.writeObject(new MSG("WIN",""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(gameView.getContext(), R.string.game_win, Toast.LENGTH_SHORT).show();
				delay();
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

	private void delay() {
		timer.schedule(task, 3000);
	}
	
	class Receiver extends BroadcastReceiver{

		private static final String CONTENT="CONTENT";
		private static final String COMMAND="COMMAND";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			Log.d("Lianzhu","action"+action);
			if(action.equals(GAME)){
				String command=intent.getStringExtra(COMMAND);
				Log.d("Lianzhu","Receive:"+command);
				if(command.equals("GO")){
					String str[]=intent.getStringExtra(CONTENT).split(" ");
					x=Integer.parseInt(str[0],10);
					y=Integer.parseInt(str[1],10);
					Log.d(TAG,String.format("%d %d", x,y));
					mHandler.sendEmptyMessageDelayed(GO, 0);
					turn=true;
				}
				else if(command.equals("LOSE")){
					mHandler.sendEmptyMessageDelayed(LOSE, 0);
				}
				else if(command.equals("DRAW")){
					mHandler.sendEmptyMessageDelayed(DRAW, 0);
				}
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		IntentFilter filter=new IntentFilter(GAME);
		if(receiver==null){
			receiver=new Receiver();
			registerReceiver(receiver,filter);
		}
		super.onResume();
	}
}