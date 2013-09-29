package org.huangxiaozhe.Lianzhu;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity{
	private static final String OPT_UPPERHAND="upperhand";
	private static final boolean OPT_UPPERHAND_DEF=true;
	private static final String OPT_BOARDSIZE="boardsize";
	private static final int OPT_BOARDSIZE_DEF=15;
	private static final String OPT_MUSIC="music";
	private static final boolean OPT_MUSIC_DEF=true;
	private static final String OPT_DIFFICULTY="difficulty";
	private static final int OPT_DIFFICULTY_DEF=1;
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
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getInt(OPT_DIFFICULTY, OPT_DIFFICULTY_DEF);
	}
	public static boolean getUpperhand(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(OPT_UPPERHAND, OPT_UPPERHAND_DEF);
	}
	public static int getBoardsize(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getInt(OPT_BOARDSIZE, OPT_BOARDSIZE_DEF);
	}
}
