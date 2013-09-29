package org.huangxiaozhe.Lianzhu;

import org.huangxiaozhe.Lianzhu.R;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity{
	private static final String OPT_UPPERHAND="upperhand";
	private static final boolean OPT_UPPERHAND_DEF=false;
	private static final String OPT_BOARDSIZE="boardsize";
	private static final String OPT_BOARDSIZE_DEF="15";
	private static final String OPT_MUSIC="music";
	private static final boolean OPT_MUSIC_DEF=true;
	private static final String OPT_DIFFICULTY="difficulty";
	private static final String OPT_DIFFICULTY_DEF="1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static boolean getMusic(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
	}
	public static int getDifficulty(Context context){
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
			.getString(OPT_DIFFICULTY, OPT_DIFFICULTY_DEF),10);
		
	}
	public static boolean getUpperhand(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(OPT_UPPERHAND, OPT_UPPERHAND_DEF);
	}
	public static int getBoardsize(Context context){
		return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
			.getString(OPT_BOARDSIZE, OPT_BOARDSIZE_DEF),10);
	}
}
