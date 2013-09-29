package org.huangxiaozhe.Lianzhu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Lianzhu extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
	    setListeners();
	}
	
	private void setListeners() {
		// TODO Auto-generated method stub
		this.findViewById(R.id.single_button).setOnClickListener(this);
		this.findViewById(R.id.multi_button).setOnClickListener(this);
		this.findViewById(R.id.about_button).setOnClickListener(this);
		this.findViewById(R.id.exit_button).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.single_button:
			startActivity(new Intent(this,LianzhuSingle.class));
			break;
		case R.id.multi_button:
			startActivity(new Intent(this,Login.class));
			break;
		case R.id.about_button:
			openAboutDialog();
			break;
		case R.id.exit_button:
			finish();
			break;
		}	
	}
	
	private void openAboutDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(Lianzhu.this)
		.setTitle(R.string.about_title)
		.setMessage(R.string.about_text)
		.setPositiveButton(R.string.return_label, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				})
		.show();
	}
}
