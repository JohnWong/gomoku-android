package huangxiaozhe.lianzhu.server;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import org.huangxiaozhe.Common.MSG;

public class ServerThread extends Thread{
	private int roomNumber;
	private Socket socket=null;
	private ObjectInputStream Reader=null;
	private ObjectOutputStream Writer=null;
	private DataBase database;
	private ServerUserManager userManager;
	private GameManager gameThreadManager;
	private ServerThread clientThread;
	private ServerThread hostThread;
	private String owner;
	private String boardSize;
	private char state;	
	private boolean runFlag=true;
	
	public ServerThread(Socket sock,GameManager gameManager, DataBase database, ServerUserManager usermanager){
		state='0';
		socket=sock;
		this.database=database;
		this.userManager=usermanager;
		gameThreadManager=gameManager;
		try{
			Writer=new ObjectOutputStream(socket.getOutputStream());
			Reader=new ObjectInputStream(socket.getInputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void end(){
		runFlag=false;
	}
	public void run(){
		MSG msg=null;
		try {
			while(runFlag && (msg=(MSG)Reader.readObject())!=null){
				String command=msg.getCommand();
				System.out.println(command);
				if(command.equals("REQLIST")){
					Writer.writeObject(new MSG("ROOMLIST",gameThreadManager.getRoomList()));
					continue;
				}
				else if(command.equals("MSG")){
					userManager.sendMessage(owner, msg.getContent());
					System.out.println("["+owner+"]:"+msg.getContent());
					continue;
				}
				else if(command.equals("QUT")){
					System.out.println("["+owner+"]:QUIT");
					userManager.delOnlineUser(owner);
					userManager.sendOnlineUser();
					gameThreadManager.deleteRoom(this);
					break;
				}
				switch (state){
				case '0':
					if(command.equals("LOGIN")){
						String[] str=msg.getContent().split(" ");
						System.out.println("["+str[0]+"]:login password:"+str[1]);
						if(database.login(str[0], str[1])){
							System.out.println("["+str[0]+"]:login true");
							owner=str[0];
							Writer.writeObject(new MSG("LOGINTRUE",""));
							userManager.addOnlineUser(owner, this);
							userManager.sendOnlineUser();
							state='A';
							break;
						}
						else{
							System.out.println("["+str[0]+"]:login false");
							Writer.writeObject(new MSG("LOGINFALSE",""));
						}
						
					}
					break;
				case 'A':
					if(command.equals("HOST")){
						if((roomNumber=gameThreadManager.createRoom(this))<0){	//unable to create a room
							System.out.println("["+owner+"]:host a game in room "+String.format("%d", roomNumber));
							Writer.writeObject(new MSG("HOSTFALSE",""));
							continue;
						}
						boardSize=msg.getContent();
						Writer.writeObject(new MSG("HOSTTRUE",String.format("%d", roomNumber+1)));
						System.out.println(String.format("boardSize:"+boardSize));
						state='B';
					}
					else if(command.equals("JOIN")){
						if((hostThread=gameThreadManager.joinRoom(this,Integer.parseInt(msg.getContent(),10)-1))!=null){
							System.out.println("["+owner+"]:join room of "+hostThread.owner);
							hostThread.send(new MSG("JOIN",owner));
							Writer.writeObject(new MSG("JOINTRUE",hostThread.owner+" "+hostThread.boardSize));
							System.out.println(hostThread.boardSize);
							state='M';
						}
						else{
							Writer.writeObject(new MSG("JOINFALSE",""));
							Writer.writeObject(new MSG("ROOMLIST",gameThreadManager.getRoomList()));
						}
					}
					break;
				case 'B':
					if(command.equals("JOINSYN")){
						state='C';
					}
					else if(command.equals("QUIT")){
						System.out.println("["+owner+"]:delete room");
						gameThreadManager.deleteRoom(this);
						state='A';
						Writer.writeObject(new MSG("ROOMLIST",gameThreadManager.getRoomList()));
					}
					break;
				case 'C':
					if(command.equals("READYSYN")){
						state='D';
					}
					else if(command.equals("QUIT")){
						clientThread.quitRoom();
						System.out.println("["+owner+"]:delete room");
						gameThreadManager.deleteRoom(this);
						Writer.writeObject(new MSG("ROOMLIST",gameThreadManager.getRoomList()));
						state='A';
					}
					break;
				case 'D':
					if(command.equals("START")){
						System.out.println("["+owner+"]:start game");
						Random random=new Random();
						if(random.nextBoolean()){	//host goes first
							Writer.writeObject(new MSG("ROLLTRUE",""));
							state='E';
							clientThread.send(new MSG("ROLLFALSE",""));
							clientThread.state='O';	
						}
						else{						//client goes first
							Writer.writeObject(new MSG("ROLLFALSE",""));
							state='F';
							clientThread.send(new MSG("ROLLTRUE",""));
							clientThread.state='N';
						}
					}
					else if(command.equals("QUIT")){
						clientThread.quitRoom();
						gameThreadManager.deleteRoom(this);
						Writer.writeObject(new MSG("ROOMLIST",gameThreadManager.getRoomList()));
						state='A';
					}
					break;
				case 'E':
					if(command.equals("GO")){
						clientThread.send(msg);
						state='F';
						clientThread.state='N';
					}
					else if(command.equals("WIN")){
						clientThread.send(new MSG("LOSE",""));
						clientThread.state='M';
						state='C';
					}
					else if(command.equals("DRAW")){
						clientThread.send(msg);
						clientThread.state='M';
						state='C';
					}
				case 'F':
					if(command.equals("WIN")){
						clientThread.send(new MSG("LOSE",""));
						clientThread.state='M';
						state='C';
					}
					else if(command.equals("DRAW")){
						clientThread.send(msg);
						clientThread.state='M';
						state='C';
					}
				case 'M':
					if(command.equals("READY")){
						hostThread.send(new MSG("READY",""));
						state='N';
					}
					else if(command.equals("QUIT")){
						hostThread.send(new MSG("READYFALSE",""));
						hostThread.state='B';
						hostThread.clientThread=null;
						state='A';
						Writer.writeObject(new MSG("ROOMLIST",gameThreadManager.getRoomList()));
					}
					break;
				case 'N':
					if(command.equals("GO")){
						hostThread.send(msg);
						state='O';
						hostThread.state='E';
					}
					else if(command.equals("WIN")){
						hostThread.send(new MSG("LOSE",""));
						state='M';
						hostThread.state='C';
					}
					else if(command.equals("DRAW")){
						hostThread.send(msg);
						state='M';
					}
				case 'O':
					if(command.equals("WIN")){
						hostThread.send(new MSG("LOSE",""));
						state='M';
						hostThread.state='C';
					}
					else if(command.equals("DRAW")){
						hostThread.send(msg);
						state='M';
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				gameThreadManager.deleteRoom(this);
				userManager.delOnlineUser(owner);
				runFlag=false;
				e.printStackTrace();
				System.out.println("["+owner+"]:exit game");
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	void send(MSG msg) throws IOException {
		Writer.writeObject(msg);
	}

	private void quitRoom() throws IOException {
		state='A';
		Writer.writeObject(new MSG("QUITROOM",""));
		Writer.writeObject(new MSG("ROOMLIST",gameThreadManager.getRoomList()));
	}

	public void addPlayer(ServerThread thread) throws IOException {
		clientThread=thread;
	}
	
	public char getStates(){
		return this.state;
	}

	public String getOwner() {
		return owner;
	}
}
