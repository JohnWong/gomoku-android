package org.huangxiaozhe.Lianzhu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.huangxiaozhe.Common.MSG;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class GameList extends ListActivity{

	ObjectInputStream Reader;
	ObjectOutputStream Writer;
	protected static final int MENU_HOST = Menu.FIRST;
	protected static final int MENU_JOIN = Menu.FIRST+1;
    protected static final int MENU_QUIT = Menu.FIRST+2;
    protected static final int MENU_REFRESH=Menu.FIRST+3;
    public String[] array=new String[]{noGame};
    private static final String noGame="No Games!";
    private static final String BOARDSIZE="boardSize";
	private static final String ROOMNUMBER = "roomNumber";
	private static final String PLAYERNAME = "playerName";
    private int hostSize;
    GameList gameList=this;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApp app=(MyApp)getApplicationContext();
		Reader=app.getReader();
		Writer=app.getWriter();
		registerForContextMenu(getListView());
		setAdater();
		refreshGame();
	}

	public void setAdater() {
		// TODO Auto-generated method stub
		ListAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,MENU_REFRESH,0,"Refresh");
		menu.add(0,MENU_HOST,0,"Host Game");
		menu.add(0,MENU_QUIT,0,"Quit Game");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case MENU_REFRESH:
			refreshGame();
			return true;
		case MENU_HOST:
			hostGame();
			return true;
		case MENU_QUIT:
			try {
				Writer.writeObject(new MSG("QUT",""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.setHeaderTitle("Operation");
		menu.add(0,MENU_REFRESH,0,"Refresh");
		menu.add(0,MENU_HOST,0,"Host Game");
		menu.add(0,MENU_JOIN,0,"Join Game");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	public void refreshGame(){
		try {
			Writer.writeObject(new MSG("REQLIST",""));
			MSG msg=null;
			while((msg=(MSG)Reader.readObject())!=null){
				Log.d("Lianzhu",msg.getCommand()+msg.getContent());
				if(msg.getCommand().equals("ROOMLIST")){
					array=new String[]{
						noGame	
					};
					if(msg.getContent().equals("")){
						array=new String[]{noGame};
					}
					else{
						String []str=msg.getContent().split(" ");
						array=new String[str.length];
						for(int i=0;i<str.length;i++){
							array[i]=str[i];
						}
					}
					setAdater();
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case MENU_REFRESH:
			refreshGame();
			return true;
		case MENU_HOST:
			hostGame();
			return true;
		case MENU_JOIN:
			AdapterView.AdapterContextMenuInfo info;
			info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			joinGame(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void joinGame(int position) {
		// TODO Auto-generated method stub
		
		String select=array[position];
		if(select.equals(noGame) || select.equals(""))
			return;
		String []str=select.split(":");
		try {
			int roomNumber=Integer.parseInt(str[0],10);
			Writer.writeObject(new MSG("JOIN",str[0]));
			MSG msg=null;
			while((msg=(MSG)Reader.readObject())!=null){
				String command=msg.getCommand();
				if(command.equals("JOINTRUE")){
					str=msg.getContent().split(" ");
					String player=str[0];
					int boardSize=Integer.parseInt(str[1],10);
					startActivity(new Intent(gameList,JoinGame.class)
					   .putExtra(PLAYERNAME, player)
					   .putExtra(BOARDSIZE, boardSize)
					   .putExtra(ROOMNUMBER,roomNumber));
					break;
				}
				else if(command.equals("JOINFALSE")){
					refreshGame();
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void hostGame() {
		// TODO Auto-generated method stub
		hostSize=10;
		new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_title_boardsize)
        .setSingleChoiceItems(R.array.entries_boardsize, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	hostSize=10+whichButton;
                    }
                })
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	try {
							Writer.writeObject(new MSG("HOST",String.format("%d", hostSize)));
							MSG msg=null;
							while((msg=(MSG) Reader.readObject())!=null){
								String command=msg.getCommand();
								if(command.equals("HOSTTRUE")){
									int room=Integer.parseInt(msg.getContent(),10);
									startActivity(new Intent(gameList,HostGame.class)
										.putExtra(BOARDSIZE, hostSize)
										.putExtra(ROOMNUMBER,room));
									break;
								}
								else if(command.equals("HOSTFALSE")){
									break;
								}
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	
                    	
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	
                    }
                })
               .create().show();
	}
}
