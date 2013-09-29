package org.huangxiaozhe.Lianzhu;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class LianzhuSingle extends Activity implements OnClickListener{
    private static final String GAME_REQCONTINUE="reqcontinue";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setListeners();
    }
    
	private void setListeners() {
		// TODO Auto-generated method stub
		this.findViewById(R.id.continue_button).setOnClickListener(this);
		this.findViewById(R.id.new_button).setOnClickListener(this);
		this.findViewById(R.id.exit_button).setOnClickListener(this);
		this.findViewById(R.id.settings_button).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.exit_button:
			finish();
			break;
		case R.id.settings_button:
			startActivity(new Intent(this,Prefs.class));
			break;
		case R.id.new_button:
			startActivity(new Intent(this,GameSingle.class)
				.putExtra(GAME_REQCONTINUE, false));
			break;
		case R.id.continue_button:
			startActivity(new Intent(this,GameSingle.class)
				.putExtra(GAME_REQCONTINUE, true));
			break;
		}
	}
	

	private void openAboutDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(LianzhuSingle.this)
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		case R.id.about:
			openAboutDialog();
			return true;
		case R.id.exit:
			finish();
			return true;
		case R.id.settings:
			startActivity(new Intent(this,Prefs.class));
			return true;
		}
		return false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Music.stop(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(Prefs.getMusic(this)){
			Music.play(this, R.raw.music);
		}
	}
}