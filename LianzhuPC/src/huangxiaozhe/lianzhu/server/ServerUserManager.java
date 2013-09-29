package huangxiaozhe.lianzhu.server;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.huangxiaozhe.Common.MSG;

public class ServerUserManager {
	GameManager gameManager;
	private Map<String,ServerThread> onlineUser = new HashMap<String,ServerThread>();   // ”√ªß”≥…‰

	public ServerUserManager(GameManager gameManager){
		this.gameManager=gameManager;
		System.out.println("User Manager started!");
	}
	
	public void addOnlineUser(String user,ServerThread gameThread){
		if(onlineUser.containsKey(user)){
			try {
				onlineUser.get(user).send(new MSG("LOGINOUT",""));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			onlineUser.get(user).end();
		}
		onlineUser.put(user,gameThread);
	}
	
	public void delOnlineUser(String user){
		if(onlineUser.containsKey(user))
			onlineUser.remove(user);
	}
	
	public void sendOnlineUser() throws IOException{
		StringBuilder users=new StringBuilder();
		for(String str:onlineUser.keySet()){
			users.append(str+" ");
		}
		for(String str:onlineUser.keySet()){
			onlineUser.get(str).send(new MSG("USERS",users.toString().trim()));
		}
	}
	
	public void sendMessage(String user,String msg){
		for(String str:onlineUser.keySet()){
			if(!str.endsWith(user))
				try {
					onlineUser.get(str).send(new MSG("MSG",user+" "+msg));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public boolean isOnline(String user){
		return onlineUser.containsKey(user);
	}
	public Map<String, ServerThread>getOnlineUser(){
		return onlineUser;
	}
}
