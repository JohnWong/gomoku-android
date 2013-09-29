package huangxiaozhe.lianzhu.client;


import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.huangxiaozhe.Common.MSG;

@SuppressWarnings("serial")
public class MainFrame extends Frame{
	private String owner;
	private Socket socket;
	private Dimension screenSize;
	private Dimension frameSize;
	private static ObjectInputStream Reader;
	private static ObjectOutputStream Writer;
	private TextArea textArea;
	private List userList;
	private boolean isGame=false;
	private ControlPanel controlPanel;
	private MainFrame frame;
	private boolean runFlag=true;
	
	public MainFrame(final Socket socket,String user,ObjectInputStream reader,ObjectOutputStream writer){
		super("Main");
		
		frame=this;
		this.socket=socket;
		owner=user;
		Reader=reader;
		Writer=writer;
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize=new Dimension(300,510);
		setSize(frameSize);
		setLocation(screenSize.width/2-frameSize.width,(screenSize.height-frameSize.height)/2);
		setBackground(Color.decode("#ccfffa"));
		setResizable(false);
		setLayout(null);
		
		//add Label "Online User List:"
		Label label1=new Label("Online User List:");
		label1.setSize(200,20);
		label1.setLocation(20, 40);
		add(label1);
		//add user list
		userList=new List(10,false);
		userList.setSize(260,95);
		userList.setLocation(20,60);
		add(userList);
		//add label "chat content:"
		Label label2=new Label("Chat Content:");
		label2.setSize(200,20);
		label2.setLocation(20, 160);
		add(label2);
		//add chat content
		textArea=new TextArea("",18,30,TextArea.SCROLLBARS_VERTICAL_ONLY);
		textArea.setBackground(Color.white);
		textArea.setEditable(false);
		textArea.setSize(260,220);
		textArea.setLocation(20,180);
		add(textArea);
		//add label "Send Message:"
		Label label3=new Label("Send Message:");
		label3.setSize(200,20);
		label3.setLocation(20, 405);
		add(label3);
		//add input textfield
		final TextField inputArea=new TextField();
		inputArea.setSize(205,25);
		inputArea.setLocation(20,425);
		add(inputArea);
		
		//add send button
		Button send=new Button("Send");
		send.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				String text=inputArea.getText();
				try {
					Writer.writeObject(new MSG("MSG",text));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				textArea.setText(textArea.getText().concat("["+owner+"]:"+text+"\n"));
				inputArea.setText("");
			}
		});
		send.setSize(50,25);
		send.setLocation(230,425);
		add(send);
		
		//add button "Start Game"
		final Button btnGame=new Button("Start Game");
		btnGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(!isGame){
					setSize(460,510);
					controlPanel=new ControlPanel(socket,Reader,Writer,frame,owner);
					controlPanel.setLocation(300,60);
					add(controlPanel);
					isGame=true;
					try {
						Writer.writeObject(new MSG("REQLIST",""));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					btnGame.setLabel("End Game");
				}
				else{
					try {
						Writer.writeObject(new MSG("QUIT",""));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setSize(300,510);
					remove(controlPanel);
					isGame=false;
					btnGame.setLabel("Start Game");
				}
			}
		});
		btnGame.setSize(90,25);
		btnGame.setLocation(65,465);
		add(btnGame);
		
		//add button quit
		Button btnQuit=new Button("Exit");
		btnQuit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(isGame){
					if(!controlPanel.canQuit)
						return;
					try {
						Writer.writeObject(new MSG("QUIT",""));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					controlPanel.isHost=false;
					controlPanel.panelHost=null;
					controlPanel.panelHost=null;
					controlPanel.initPanelStart();
					controlPanel.panelStart.setSize(135,420);
					controlPanel.panelStart.setLocation(0,0);
					controlPanel.add(controlPanel.panelStart);
				}
				runFlag=false;
				if(socket!=null){
					try {
						Writer.writeObject(new MSG("QUT",""));
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
				System.exit(0);
			}
		});
		btnQuit.setSize(55,25);
		btnQuit.setLocation(175,465);
		add(btnQuit);
		
		new ReceiveThread(Reader).start();
		
	}
	
	class ReceiveThread extends Thread{
		private ObjectInputStream Reader;
		
		public ReceiveThread(ObjectInputStream Reader){
			this.Reader=Reader;
		}
		
		public void run(){
			try {
				MSG msg=null;
				while(runFlag && (msg=(MSG)Reader.readObject())!=null){
					String command=msg.getCommand();
					System.out.println("["+owner+"]:"+command);
					if(command.equals("MSG")){
						String[] str=msg.getContent().split(" ");
						if(str.length==2)
							textArea.setText(textArea.getText()+"["+str[0]+"]:"+str[1]+"\n");
					}
					else if(command.equals("USERS")){
						if(userList.getItemCount()>0)
							userList.removeAll();
						String []users=msg.getContent().split(" ");
						for(String str:users){
							userList.add(str);
						}
					}
					else if(command.equals("LOGINOUT")){
						openDialog("Logout",owner+" login in other places");
						LoginFrame loginFrame=new LoginFrame();
						loginFrame.setVisible(true);
						dispose();
					}
					if(!isGame)
						continue;
					if(command.equals("ROOMLIST")){
						controlPanel.gameList.removeAll();
						String[]str=msg.getContent().split(" ");
						for(int i=0;i<str.length;i++){
							controlPanel.gameList.add(str[i]);
						}
					}
					else if(command.equals("HOSTTRUE")){
						controlPanel.isHost=true;
						controlPanel.boardSize=controlPanel.hostSize;
						controlPanel.remove(controlPanel.panelStart);
						controlPanel.ready.setEnabled(false);
						controlPanel.start.setEnabled(false);
						controlPanel.roomNumber=Integer.parseInt(msg.getContent(),10);
						controlPanel.initPanelHost();
						controlPanel.panelHost.setSize(135,420);
						controlPanel.panelHost.setLocation(0,0);
						controlPanel.add(controlPanel.panelHost);
					}
					else if(command.equals("HOSTFALSE")){
					}
					else if(command.equals("JOINTRUE")){
						String str[]=msg.getContent().split(" ");	//get player name
						System.out.println(str[1]);
						controlPanel.competitor=str[0];
						controlPanel.boardSize=Integer.parseInt(str[1],10);
						controlPanel.remove(controlPanel.panelStart);
						controlPanel.initPanelClient();
						controlPanel.panelClient.setSize(135,420);
						controlPanel.panelClient.setLocation(0,0);
						controlPanel.add(controlPanel.panelClient);
						controlPanel.ready.setEnabled(true);
						controlPanel.start.setEnabled(false);
					}
					else if(command.equals("JOIN")){
						String str[]=msg.getContent().split(" ");	//get player name
						controlPanel.competitor=str[0];
						controlPanel.competitorInf.setText("Player:"+controlPanel.competitor);
						Writer.writeObject(new MSG("JOINSYN",""));
						controlPanel.ready.setEnabled(false);
						controlPanel.start.setEnabled(false);
					}
					else if(command.equals("READY")){
						Writer.writeObject(new MSG("READYSYN",""));
						controlPanel.ready.setEnabled(false);
						controlPanel.start.setEnabled(true);
					}
					else if(command.equals("READYFALSE")){
						controlPanel.competitor=null;
						controlPanel.competitorInf.setText("Player:"+controlPanel.competitor);
						controlPanel.start.setEnabled(false);
						controlPanel.ready.setEnabled(false);
					}
					else if(command.equals("QUITROOM")){
						controlPanel.competitor=null;
						controlPanel.isHost=false;
						controlPanel.start.setEnabled(false);
						controlPanel.ready.setEnabled(false);
						controlPanel.remove(controlPanel.panelClient);
						controlPanel.initPanelStart();
						controlPanel.panelStart.setSize(135,420);
						controlPanel.panelStart.setLocation(0,0);
						controlPanel.add(controlPanel.panelStart);
					}
					else if(command.equals("ROLLTRUE")){
						controlPanel.canQuit=false;
						controlPanel.text.setText("You go first");
						controlPanel.upperHand=true;
						controlPanel.turn=true;
						controlPanel.startGame();
					}
					else if(command.equals("ROLLFALSE")){
						controlPanel.canQuit=false;
						controlPanel.upperHand=false;
						controlPanel.turn=false;
						controlPanel.text.setText("You go second");
						controlPanel.startGame();
					}
					else if(command.equals("GO")){
						String[] str=msg.getContent().split(" ");
						controlPanel.gamePanel.computerGo(Integer.parseInt(str[0],10), Integer.parseInt(str[1],10));
						controlPanel.turn=true;
					}
					else if(command.equals("LOSE")){
						controlPanel.gameOver(2);
					}
					else if(command.equals("DRAW")){
						controlPanel.gameOver(3);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void openDialog(String title,String string) {
		// TODO Auto-generated method stub
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize=new Dimension(200, 120);
		final Dialog dialog=new Dialog((Frame) getParent(),title);
		dialog.setSize(dialogSize);
		dialog.setLayout(null);
		dialog.setResizable(true);
		
		Label label=new Label(string);
		label.setSize(400, 50);
		label.setLocation(20,30);
		dialog.add(label,"Center");
		
		Button enter=new Button("Return");
		enter.setSize(50,25);
		enter.setLocation(75,80);
		enter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dialog.dispose();
			}
		});
		dialog.add(enter);
		
		dialog.setLocation((screenSize.width-dialogSize.width+50)/2,(screenSize.height-dialogSize.height+40)/2);
		dialog.setModal(true);
		dialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				dialog.dispose();
			}
		});
		dialog.setVisible(true);
	}
}
