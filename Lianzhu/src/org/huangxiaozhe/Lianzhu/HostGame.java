package org.huangxiaozhe.Lianzhu;

import java.io.IOException;
import java.io.ObjectInputStream;
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
import android.widget.Button;
import android.widget.TextView;

public class HostGame extends Activity implements OnClickListener{

	private static final String GAMEHOST="GAMEHOST";
	private int boardSize;
	public static final String BOARDSIZE="boardSize";
	private static final String UPPERHAND="upperHand";
	protected static final String ROOMNUMBER = "roomNumber";
	private final static int JOIN=1;
	private final static int READY=2;
	private final static int READYFALSE=3;
	private static final int ROLLTRUE=4;
	private static final int ROLLFALSE=5;
	private final static int QUIT=6;
	public String player;
	TextView sizeText;
    TextView playerText;
    TextView roomText;
    ObjectInputStream Reader;
	ObjectOutputStream Writer;
	private int roomNumber;
	private Button start;
	HostGame hostGame=this;
	private Handler mHandler;
	private Receiver receiver;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hostgame);
		findViews();
		MyApp app=(MyApp) getApplicationContext();
		Reader=app.getReader();
		Writer=app.getWriter();
		boardSize=getIntent().getIntExtra(BOARDSIZE, 15);
		sizeText.setText(String.format("%d¡Á%d", boardSize,boardSize));
		roomNumber=getIntent().getIntExtra(ROOMNUMBER, 1);
		playerText.setText(R.string.no_player);
		roomText.setText(String.format("Room %d", roomNumber));
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case JOIN:
					playerText.setText(player);
					try {
						Writer.writeObject(new MSG("JOINSYN",""));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case READY:
					start.setEnabled(true);
					try {
						Writer.writeObject(new MSG("READYSYN",""));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case READYFALSE:
					start.setEnabled(false);
					playerText.setText(R.string.no_player);
				case QUIT:
					start.setEnabled(false);
					playerText.setText(R.string.no_player);
					break;
				case ROLLTRUE:
					start.setEnabled(false);
					startActivity(new Intent(hostGame,GameMulti.class)
						.putExtra(BOARDSIZE, boardSize)
						.putExtra(UPPERHAND, true));
					break;
				case ROLLFALSE:
					start.setEnabled(false);
					startActivity(new Intent(hostGame,GameMulti.class)
						.putExtra(BOARDSIZE, boardSize)
						.putExtra(UPPERHAND, false));
					break;
				default:
					break;
				}
			}
			
		};
		setListener();
		startService(new Intent(this,ReceiveService.class));
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		findViewById(R.id.quit_button).setOnClickListener(this);
		start.setOnClickListener(this);
		start.setEnabled(false);
	}

	private void findViews() {
		// TODO Auto-generated method stub
		sizeText=(TextView) findViewById(R.id.board_size);
		playerText=(TextView) findViewById(R.id.player_name);
		roomText=(TextView)findViewById(R.id.room_number);
		start=(Button) findViewById(R.id.start_button);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.quit_button:
			try {
				Writer.writeObject(new MSG("QUIT",""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			break;
		case R.id.start_button:
			try {
				Writer.writeObject(new MSG("START",""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
	
	class Receiver extends BroadcastReceiver{

		private static final String GAMEHOST="GAMEHOST";
		private static final String CONTENT="CONTENT";
		private static final String COMMAND="COMMAND";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			Log.d("Lianzhu","action"+action);
			if(action.equals(GAMEHOST)){
				String command=intent.getStringExtra(COMMAND);
				Log.d("Lianzhu","Receive:"+command);
				if(command.equals("JOIN")){
					player=intent.getStringExtra(CONTENT);
					mHandler.sendEmptyMessageDelayed(JOIN, 0);
				}
				else if(command.equals("READY")){
					mHandler.sendEmptyMessageDelayed(READY, 0);
				}
				else if(command.equals("QUIT")){
					mHandler.sendEmptyMessageDelayed(QUIT, 0);
				}
				else if(command.equals("READYFALSE")){
					mHandler.sendEmptyMessageDelayed(READYFALSE, 0);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopService(new Intent(this,ReceiveService.class));
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
		IntentFilter filter=new IntentFilter(GAMEHOST);
		if(receiver==null)
			receiver=new Receiver();
		registerReceiver(receiver,filter);
		super.onResume();
	}
	
}
