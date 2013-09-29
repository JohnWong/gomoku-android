package huangxiaozhe.lianzhu.server;

import java.io.IOException;

public class GameManager {
	private final static int GAME_THREAD=10;
	ServerThread[] gameThread;
	
	public GameManager(){
		gameThread=new ServerThread[10];
		for(int i=0;i<GAME_THREAD;i++)
			gameThread[i]=null;
		System.out.println("Game Manager started!");
	}
	
	public int createRoom(ServerThread Thread){
		for(int i=0;i<GAME_THREAD;i++){
			if(gameThread[i]==null){
				gameThread[i]=Thread;
				return i;
			}
		}
		return -1;
	}
	
	public void deleteRoom(ServerThread Thread){
		for(int i=0;i<GAME_THREAD;i++){
			if(gameThread[i]==Thread){
				gameThread[i]=null;
				break;
			}
		}
	}
	
	public ServerThread joinRoom(ServerThread thread, int i) throws IOException{
		if(gameThread[i]!=null && gameThread[i].getStates()=='B'){
			gameThread[i].addPlayer(thread);
			return gameThread[i];
		}
		else
			return null;
	}

	public String getRoomList() {
		// TODO Auto-generated method stub
		StringBuilder list=new StringBuilder();
		for(int i=0;i<GAME_THREAD;i++){
			if(gameThread[i]!=null && gameThread[i].getStates()=='B'){
				list.append((i+1)+":"+gameThread[i].getOwner()+" ");
			}
		}
		return list.toString().trim();
	}

}
