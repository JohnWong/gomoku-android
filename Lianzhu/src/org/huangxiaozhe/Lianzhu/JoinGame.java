package org.huangxiaozhe.Lianzhu;

import java.io.IOException;
import java.io.ObjectOutputStream;
import org.huangxiaozhe.Common.MSG;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class JoinGame extends Activity implements OnClickListener{

	private static final String GAMEJOIN="GAMEJOIN";
	private static final String BOARDSIZE="boardSize";
	private static final String ROOMNUMBER = "roomNumber";
	private static final String PLAYERNAME = "playerName";
	private static final String UPPERHAND="upperHand";
	private static final int QUITROOM=1;
	private static final int ROLLTRUE=2;
	private static final int ROLLFALSE=3;
	ObjectOutputStream Writer;
	TextView sizeText;
    TextView playerText;
    TextView roomText;
    private int boardSize;
	private int roomNumber;
	private String player;
	JoinGame joinGame=this;
	private Receiver receiver;	
	
	private Handler mHandler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joingame);
		findViews();
		setListener();
		MyApp app=(MyApp) getApplicationContext();
		Writer=app.getWriter();
		
		boardSize=getIntent().getIntExtra(BOARDSIZE, 15);
		roomNumber=getIntent().getIntExtra(ROOMNUMBER, 1);
		player=getIntent().getStringExtra(PLAYERNAME);
		sizeText.setText(String.format("%d¡Á%d", boardSize,boardSize));
		roomText.setText(String.format("Room %d", roomNumber));
		playerText.setText(player);	
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Log.d("Lianzhu",String.format("%d",msg.what));
				switch(msg.what){
				case ROLLTRUE:
					startActivity(new Intent(joinGame,GameMulti.class)
					.putExtra(BOARDSIZE, boardSize)
					.putExtra(UPPERHAND, true));
					break;
				case ROLLFALSE:
					startActivity(new Intent(joinGame,GameMulti.class)
					.putExtra(BOARDSIZE, boardSize)
					.putExtra(UPPERHAND, false));
					break;
				case QUITROOM:
					finish();
					break;
				}
			}
			
		};
		startService(new Intent(this,ReceiveService.class));
	}

	private void setListener() {
		// TODO Auto-generated method stub
		findViewById(R.id.ready_button).setOnClickListener(this);
		findViewById(R.id.quit_button).setOnClickListener(this);
		
	}

	private void findViews() {
		// TODO Auto-generated method stub
		sizeText=(TextView) findViewById(R.id.board_size);
		playerText=(TextView) findViewById(R.id.player_name);
		roomText=(TextView)findViewById(R.id.room_number);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.quit_button:
			try {
				Writer.writeObject(new MSG("QUIT",""));
				finish();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case R.id.ready_button:
			try {
				Writer.writeObject(new MSG("READY",""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		
	}
	
	class Receiver extends BroadcastReceiver{

		private static final String COMMAND="COMMAND";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			Log.d("Lianzhu","action"+action);
			if(action.equals(GAMEJOIN)){
				String command=intent.getStringExtra(COMMAND);
				Log.d("Lianzhu","Receive:"+command);
				if(command.equals("QUITROOM")){
					mHandler.sendEmptyMessageDelayed(QUITROOM, 0);
				}
				else if(command.equals("ROLLTRUE")){
					mHandler.sendEmptyMessageDelayed(ROLLTRUE, 0);
				}
				else if(command.equals("ROLLFALSE")){
					mHandler.sendEmptyMessageDelayed(ROLLFALSE, 0);
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
		IntentFilter filter=new IntentFilter(GAMEJOIN);
		if(receiver==null){
			receiver=new Receiver();
		}
		registerReceiver(receiver,filter);
		super.onResume();
	}
}
