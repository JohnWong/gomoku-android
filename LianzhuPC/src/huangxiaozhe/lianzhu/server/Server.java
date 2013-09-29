package huangxiaozhe.lianzhu.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private static int PORT=8808;
	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private final int POOL_SIZE=10;
	private final static DataBase dataBase=new DataBase();
	private final static GameManager gameManager=new GameManager();
	private static ServerUserManager userManager=new  ServerUserManager(gameManager);
	
	public Server() throws IOException{
		serverSocket=new ServerSocket(PORT);
		executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
		System.out.println("Server socket started! port:"+String.format("%d", PORT));
		dataBase.getConnection();
		System.out.println("================================");
	}
	
	public void service(){
		while(true){
			Socket socket=null;
			try{
				socket=serverSocket.accept();
				System.out.println("New socket accepted");
				executorService.execute(new ServerThread(socket,gameManager, dataBase,userManager));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)throws IOException{
		new Server().service();
	}
	

}
