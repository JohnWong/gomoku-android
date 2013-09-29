package org.huangxiaozhe.Lianzhu;

import org.huangxiaozhe.Common.MSG;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class Login  extends Activity implements OnClickListener{
	
	private static int PORT=8808;
	//private static String SERVER_ADDR="192.168.0.100";
	private static String SERVER_ADDR="192.168.18.1";
	private Socket socket;
	public ObjectInputStream Reader;
	public ObjectOutputStream Writer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setTitle("Login");
		setListeners();
		MyApp app=((MyApp)getApplicationContext());
		try {
			socket=new Socket(SERVER_ADDR,PORT);
			Writer=new ObjectOutputStream(socket.getOutputStream());
			Reader=new ObjectInputStream(socket.getInputStream());
			app.setWriter(Writer);
			app.setReader(Reader);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		}
	}

	
	private void setListeners() {
		// TODO Auto-generated method stub
		findViewById(R.id.exit_button).setOnClickListener(this);
		findViewById(R.id.login_button).setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.exit_button:
			finish();
			break;
		case R.id.login_button:
			String user=((EditText)findViewById(R.id.user_edit)).getText().toString();
			String password=((EditText)findViewById(R.id.password_edit)).getText().toString();
			//字母开头，6-16字节，允许字母数字下划线
			Pattern pattern=Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{4,15}$");
			if(pattern.matcher(user).matches() && pattern.matcher(password).matches()){
				try{
					Writer.writeObject(new MSG("LOGIN",user+" "+password));
					MSG msg=null;
					msg=(MSG)Reader.readObject();
					String command=msg.getCommand();
					if(command.equals("LOGINTRUE")){
						Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
						
						startActivity(new Intent(this,GameList.class));
						this.finish();
					}
					else
						Toast.makeText(this, "Login False", Toast.LENGTH_SHORT).show();
				}
				catch(Exception e){
					Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
				}
			}
			else {
				Toast.makeText(this, "Please check your input.", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

}
