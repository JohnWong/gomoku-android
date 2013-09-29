package org.huangxiaozhe.Lianzhu;

import java.io.ObjectInputStream;

import org.huangxiaozhe.Common.MSG;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ReceiveService extends Service{
	ReceiveThread receive;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("Lianzhu","service started");
		MyApp app=(MyApp) getApplicationContext();
		receive=new ReceiveThread(app.getReader());
		receive.start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public class ReceiveThread extends Thread{

		private static final String GAME="GAME";
		private static final String GAMEHOST="GAMEHOST";
		private static final String GAMEJOIN="GAMEJOIN";
		private static final String CONTENT="CONTENT";
		private static final String COMMAND="COMMAND";
		private boolean runFlag=true;
		private ObjectInputStream Reader;

		public ReceiveThread(ObjectInputStream reader){
			Reader=reader;
		}
		
		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			runFlag=false;
			super.destroy();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			MSG msg=null;
			while(runFlag){
				try {
					if((msg=(MSG)Reader.readObject())!=null){
						String command=msg.getCommand();
						Log.d("Lianzhu","Broadcast:"+msg.getCommand()+msg.getContent());
						if(command.equals("JOIN" )|| command.equals("READY") || 
							command.equals("QUIT") || command.equals("READYFALSE") || 
							command.equals("ROLLTRUE")||command.equals("ROLLFALSE")){
							Intent intent=new Intent(GAMEHOST);
							intent.putExtra(COMMAND, command);
							intent.putExtra(CONTENT, msg.getContent());
							sendBroadcast(intent);
						}
						if(command.equals("GO")||command.equals("LOSE")|| command.equals("DRAW")){
							Intent intent=new Intent(GAME);
							intent.putExtra(COMMAND, command);
							intent.putExtra(CONTENT, msg.getContent());
							sendBroadcast(intent);
						}
						if(command.equals("QUITROOM")||command.equals("ROLLTRUE")||command.equals("ROLLFALSE")){
							Intent intent=new Intent(GAMEJOIN);
							intent.putExtra(COMMAND, command);
							intent.putExtra(CONTENT, msg.getContent());
							sendBroadcast(intent);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
