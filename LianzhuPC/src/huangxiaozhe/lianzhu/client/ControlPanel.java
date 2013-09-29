package huangxiaozhe.lianzhu.client;


import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import org.huangxiaozhe.Common.MSG;

class ControlPanel extends Panel{
	private Socket socket;
	private ObjectInputStream Reader;
	private ObjectOutputStream Writer;
	public final List gameList=new List();
	int hostSize=15;
	public int boardSize=15;
	Panel panelStart;
	Panel panelHost;
	Panel panelClient;
	int roomNumber;
	String competitor=null;
	Label competitorInf;
	Label text;
	Button start=new Button("Start");
	Button ready=new Button("Ready");
	private Frame frame;
	boolean canQuit=true;
	boolean isHost=false;
	
	//for draw game
	private int boardLength;
	private float gridLength;
	private int chessmanSize;
	boolean upperHand;
	private int [][] table;
	private int selY;
	private int selX;
	private int sweep=0;
	private static final int SWEEP_COUNT=300;	//—°«¯…¡À∏º‰∏Ù
	private static final int PLAY_NONE=0;
	private static final int PLAY_PLAYER=1;
	private static final int PLAY_COMPUTER=2;
	private int playCount;
	private static final int GAMEPANEL_SIZE=420;
	GamePanel gamePanel;
	boolean turn;
	private String owner;
	
	public ControlPanel(Socket socket,ObjectInputStream reader, ObjectOutputStream writer, Frame frame, String owner){
		this.frame=frame;
		this.socket=socket;
		this.owner=owner;
		Reader=reader;
		Writer=writer;
		
		setBackground(Color.white);
		setSize(135,420);
		setLayout(null);
		
		initPanelStart();
		panelStart.setSize(135,420);
		panelStart.setLocation(0,0);
		add(panelStart);
			
	}
	
	public void startGame(){
		frame.setSize(905,510);
		gamePanel=new ControlPanel.GamePanel();
		gamePanel.setSize(GAMEPANEL_SIZE,GAMEPANEL_SIZE);
		gamePanel.setLocation(455,60);
		frame.add(gamePanel);

	}
	public void initPanelHost() {
		panelHost=new Panel();
		panelHost.setLayout(null);
		Label label1=new Label("New Game");
		label1.setSize(100,20);
		label1.setLocation(10,10);
		panelHost.add(label1);
		Label label3=new Label("Room Number:"+roomNumber);
		label3.setSize(100,20);
		label3.setLocation(10,40);
		panelHost.add(label3);
		Label label2=new Label("Board Size:"+String.valueOf(boardSize));
		label2.setSize(100,20);
		label2.setLocation(10,60);
		panelHost.add(label2);
		competitorInf=new Label("Player:"+competitor);
		competitorInf.setSize(120,20);
		competitorInf.setLocation(10,80);
		panelHost.add(competitorInf);
		text=new Label();
		text.setSize(100,20);
		text.setLocation(10,100);
		panelHost.add(text);
		start.setSize(90,25);
		start.setLocation(20,350);
		start.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				try {
					Writer.writeObject(new MSG("START",""));
					start.setEnabled(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		start.setEnabled(false);
		panelHost.add(start);
		
		Button quit=new Button("Quit");
		quit.setSize(90,25);
		quit.setLocation(20,380);
		quit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if(!canQuit)
					return;
				try {
					Writer.writeObject(new MSG("QUIT",""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isHost=false;
				remove(panelHost);
				initPanelStart();
				panelStart.setSize(135,420);
				panelStart.setLocation(0,0);
				add(panelStart);
			
			}
		});
		panelHost.add(quit);
	}
	
	public void initPanelClient() {
		// TODO Auto-generated method stub
		panelClient=new Panel();
		panelClient.setLayout(null);
		Label label1=new Label("Join Game");
		label1.setSize(100,20);
		label1.setLocation(10,10);
		panelClient.add(label1);
		Label label3=new Label("Room Number:"+roomNumber);
		label3.setSize(100,20);
		label3.setLocation(10,40);
		panelClient.add(label3);
		Label label2=new Label("Board Size:"+String.valueOf(boardSize));
		label2.setSize(100,20);
		label2.setLocation(10,60);
		panelClient.add(label2);
		competitorInf=new Label("Competitor:"+competitor);
		competitorInf.setSize(100,20);
		competitorInf.setLocation(10,80);
		panelClient.add(competitorInf);
		text=new Label();
		text.setSize(100,20);
		text.setLocation(10,100);
		panelClient.add(text);
		
		ready.setSize(90,25);
		ready.setLocation(20,350);
		ready.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				try {
					Writer.writeObject(new MSG("READY",""));
					ready.setEnabled(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		ready.setEnabled(false);
		panelClient.add(ready);
		
		Button quit=new Button("Quit");
		quit.setSize(90,25);
		quit.setLocation(20,380);
		quit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if(!canQuit)
					return;
				try {
					Writer.writeObject(new MSG("QUIT",""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isHost=false;
				remove(panelClient);
				initPanelStart();
				panelStart.setSize(135,420);
				panelStart.setLocation(0,0);
				add(panelStart);
			
			}
		});
		panelClient.add(quit);
	}
	
	public void initPanelStart() {
		// TODO Auto-generated method stub
		panelStart=new Panel();
		panelStart.setLayout(null);
		Label label1=new Label("Room List:");
		label1.setSize(100,20);
		label1.setLocation(10,10);
		panelStart.add(label1);
		
		gameList.setSize(115,255);
		gameList.setLocation(10,35);
		panelStart.add(gameList);
		
		Button refresh=new Button("Refresh");
		refresh.setSize(90,25);
		refresh.setLocation(20,300);
		refresh.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				try {
					Writer.writeObject(new MSG("REQLIST",""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		panelStart.add(refresh);
		Button joinBtn=new Button("Join game");
		joinBtn.setSize(90,25);
		joinBtn.setLocation(20,330);
		joinBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String str=gameList.getSelectedItem();
				if(str==null)
					return;
				roomNumber=Integer.parseInt(str.split(":")[0],10);
				try {
					roomNumber=Integer.parseInt(gameList.getSelectedItem().split(":")[0],10);
					Writer.writeObject(new MSG("JOIN",String.format("%d", roomNumber)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isHost=false;
				System.out.println("["+owner+"]:join room "+String.format("%s", roomNumber));
			}
		});
		panelStart.add(joinBtn);
	
		Button hostBtn=new Button("Host game");
		hostBtn.setSize(90,25);
		hostBtn.setLocation(20,360);
		hostBtn.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				try {
					String content=String.format("%d",hostSize);
					Writer.writeObject(new MSG("HOST",content));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		panelStart.add(hostBtn);
		
		final Choice choice=new Choice();
		choice.setSize(90,25);
		String []list=new String[]{"10°¡10","11°¡11","12°¡12","13°¡13","14°¡14","15°¡15","16°¡16","17°¡17","18°¡18","19°¡19","20°¡20"};
		for(int i=0;i<list.length;i++)
			choice.addItem(list[i]);
		choice.select(5);
		choice.addItemListener(new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				hostSize=10+choice.getSelectedIndex();
			}
		
		});
		choice.setLocation(20,390);
		panelStart.add(choice);
	}

	
	public void gameOver(int b){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(b==1)
			text.setText("You Win");
		else if(b==2)
			text.setText("You Lose");
		else if(b==3)
			text.setText("Game Draw");
		frame.remove(gamePanel);
		frame.setSize(460,510);
		canQuit=true;
		start.setEnabled(false);
		if(isHost)
			ready.setEnabled(false);
		else
			ready.setEnabled(true);
	}
	
	class GamePanel extends Panel{
		
		private Image offScreenImage=null;
		
		public GamePanel(){
			table=new int[boardSize][boardSize];
			for(int i=0;i<boardSize;i++)
				for(int j=0;j<boardSize;j++)
					table[i][j]=PLAY_NONE;
			selX=boardSize/2;
			selY=boardSize/2;
			playCount=boardSize*boardSize;
			
			TimerTask task=new TimerTask(){
				@Override
				public void run() {
					sweep++;
					if(sweep>5)
						sweep=0;
					repaint();
				}
			};
			Timer timer=new Timer();
			timer.schedule(task,0,SWEEP_COUNT);

			setFocusable(true);
			addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					switch(e.getKeyCode()){
					case KeyEvent.VK_UP:
						select(selX,selY-1);
						break;
					case KeyEvent.VK_DOWN:
						select(selX,selY+1);
						break;
					case KeyEvent.VK_LEFT:
						select(selX-1,selY);
						break;
					case KeyEvent.VK_RIGHT:
						select(selX+1,selY);
						break;
					case KeyEvent.VK_SPACE:
					case KeyEvent.VK_ENTER:
						manGo(selX,selY);
						break;
					default:
						super.keyTyped(e);
					}
				}
			});
			addMouseListener(new MouseAdapter(){
				
				public void mousePressed(MouseEvent e){
					int x=(int)((e.getX()-gridLength/2)/gridLength);
					int y=(int)((e.getY()-gridLength/2)/gridLength);
					select(x,y);
					manGo(x,y);
				}
			});
		}
		
		private boolean isGameOver(int x,int y){
			int player=table[x][y];
			for(int i=0;i<4;i++){
				if(findFiveIn1D(x,y,i,player))
					return true;
			}
			return false;
		}

		private boolean findFiveIn1D(int x,int y,int direction,int player){
			Point p=new Point(x,y);
			int count=1;
			for(p.moveOneStep(direction);isInBoard(p);p.moveOneStep(direction)){	
				if(table[p.x][p.y]==player){
					count++;
				}
				else {
					break;
				}
			};
			p.set(x, y);
			direction+=4;
			for(p.moveOneStep(direction);isInBoard(p);p.moveOneStep(direction)){	
				if(table[p.x][p.y]==player){
					count++;
				}
				else {
					break;
				}
			};
			if(count>=5)
				return true;
			else
				return false;
		}
		
		private boolean isInBoard(Point p){
			return p.x>=0 && p.x<boardSize && p.y>=0 && p.y<boardSize;
		}
		
		public void computerGo(int x,int y){
			table[x][y]=PLAY_COMPUTER;
			playCount--;
			repaint();
		}
		
		public void manGo(int x,int y){
			if(!turn)
				return;
			if(!isInBoard(new Point(x,y)))
				return;
			if(table[x][y]!=PLAY_NONE)
				return;
			table[x][y]=PLAY_PLAYER;
			playCount--;
			repaint();
			try {
				Writer.writeObject(new MSG("GO",String.format("%d %d", x,y)));
				turn=false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(isGameOver(x,y)){
				try {
					Writer.writeObject(new MSG("WIN",""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gameOver(1);
			}
			else if(playCount<=0){
				try {
					Writer.writeObject(new MSG("DRAW",""));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gameOver(3);
			}
		}
		
		private void select(int x, int y) {
			repaint();
			selX=Math.max(0, Math.min(x,boardSize-1));
			selY=Math.max(0, Math.min(y, boardSize-1));
			sweep=0;
			repaint();
		}
		
		@Override
		public void paint(Graphics g) {
			//super.paint(g);
			boardLength=Math.min(this.getSize().width,this.getSize().height);
			gridLength=boardLength/(boardSize+1);
			chessmanSize=(int)gridLength*4/9;
			
			drawBoard(g);
			drawPoints(g);
			drawSelection(g);
		}
		
		private void drawSelection(Graphics g) {
			g.setColor(Color.gray);
			g.fillOval((selX+1)*boardLength/(boardSize+1)-chessmanSize-1,(selY+1)*boardLength/(boardSize+1)-chessmanSize-1,2*chessmanSize+2,2*chessmanSize+2);
			if(sweep<4){
				if(upperHand)
					g.setColor(Color.black);
				else
					g.setColor(Color.white);
				g.fillOval((selX+1)*boardLength/(boardSize+1)-chessmanSize,(selY+1)*boardLength/(boardSize+1)-chessmanSize,2*chessmanSize,2*chessmanSize);
			}
		}

		private void drawPoints(Graphics g) {
			//draw all chess-man
			for(int i=0;i<boardSize;i++){
				for(int j=0;j<boardSize;j++){
					if(upperHand){				//Player goes first with black chessman
						if(table[i][j]==PLAY_PLAYER){
							drawPoint(g,Color.black, i,j);
						}
						else if(table[i][j]==PLAY_COMPUTER){
							drawPoint(g,Color.white,i,j);
						}
					}
					else{
						if(table[i][j]==PLAY_PLAYER){
							drawPoint(g,Color.white,i,j);
						}
						else if(table[i][j]==PLAY_COMPUTER){
							drawPoint(g,Color.black, i,j);
						}
					}
					
				}
			}
		}

		private void drawPoint(Graphics g, Color color, int i, int j) {
			g.setColor(Color.gray);
			g.fillOval((i+1)*boardLength/(boardSize+1)-chessmanSize-1,(j+1)*boardLength/(boardSize+1)-chessmanSize-1,2*chessmanSize+2,2*chessmanSize+2);
			g.setColor(color);
			g.fillOval((i+1)*boardLength/(boardSize+1)-chessmanSize,(j+1)*boardLength/(boardSize+1)-chessmanSize,2*chessmanSize,2*chessmanSize);
		}

		private void drawBoard(Graphics g){
			//get colors
			Color lightyellow=Color.decode("#FFFF80");
			Color gray=Color.decode("#333333");
			
			//draw the board
			//draw the board background
			g.setColor(lightyellow);
			g.fill3DRect(0,0,boardLength,boardLength,true);
			
			//draw the board
			//draw the grid shadows
			for(int i=1,x1=(int)(boardLength/(boardSize+1)),x2=(int)(boardSize*boardLength/(boardSize+1)),y;i<=boardSize;i++){
				y=(int)(i*boardLength/(boardSize+1));
				g.setColor(Color.lightGray);
				g.drawLine(x1, y+1, x2, y+1);
			}
			for(int i=1,y1=(int)(boardLength/(boardSize+1)),y2=(int)(boardSize*boardLength/(boardSize+1)),x;i<=boardSize;i++){
				x=(int)(i*boardLength/(boardSize+1));
				g.setColor(Color.lightGray);
				g.drawLine(x+1, y1, x+1, y2);
			}
			//draw the grid lines
			g.setColor(gray);
			for(int i=1,x1=(int)(boardLength/(boardSize+1)),x2=(int)(boardSize*boardLength/(boardSize+1)),y;i<=boardSize;i++){
				y=(int)(i*boardLength/(boardSize+1));
				g.drawLine(x1, y-1, x2, y-1);
				g.drawLine(x1, y, x2, y);
			}
			for(int i=1,y1=(int)(boardLength/(boardSize+1)),y2=(int)(boardSize*boardLength/(boardSize+1)),x;i<=boardSize;i++){
				x=(int)(i*boardLength/(boardSize+1));
				g.drawLine(x-1, y1, x-1, y2);
				g.drawLine(x, y1, x, y2);
			}
		}

		@Override
		public void update(Graphics g) {
			if(offScreenImage==null){
				offScreenImage=this.createImage(boardLength,boardLength);	
			}
			Graphics goffScreen=offScreenImage.getGraphics();
			goffScreen.fillRect(0, 0, boardLength, boardLength);
			paint(goffScreen);
			g.drawImage(offScreenImage, 0, 0, null);
			super.update(g);
		}
	}
}
