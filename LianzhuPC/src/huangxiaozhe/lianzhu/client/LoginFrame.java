package huangxiaozhe.lianzhu.client;


import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
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
import java.util.regex.Pattern;

import org.huangxiaozhe.Common.MSG;

public class LoginFrame extends Frame{
	private Socket socket=null;
	//private static String SERVER_ADDR="192.168.0.100";
	private static String SERVER_ADDR="192.168.18.1";
	private static int PORT=8808;
	private ObjectInputStream Reader;
	private ObjectOutputStream Writer;
	public LoginFrame(){
		super("Login");
		try{
			socket=new Socket(SERVER_ADDR,PORT);
			Reader=new ObjectInputStream(socket.getInputStream());
			Writer=new ObjectOutputStream(socket.getOutputStream());
		}
		catch(IOException e){
			openDialog("Connection Error",e.toString());
		}
		//set frame layout
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize=new Dimension(300,250);
		setSize(frameSize);
		setLocation((screenSize.width-frameSize.width)/2,(screenSize.height-frameSize.height)/2);
		setBackground(Color.decode("#ccfffa"));
		
		//set layout in frame
		setLayout(null);
		Label labelTitle=new Label("Please Login");
		labelTitle.setFont(new Font("Times New Roman",Font.BOLD,30));
		labelTitle.setSize(200, 40);
		labelTitle.setLocation(60,50);
		add(labelTitle);
		Label label1=new Label("User:");
		label1.setFont(new Font("Times New Roman",Font.BOLD,16));
		label1.setSize(80,30);
		label1.setLocation(50,110);
		add(label1);
		final TextField textUser=new TextField("admin");
		textUser.setSize(110,30);
		textUser.setFont(new Font("Times New Roman",Font.BOLD,16));
		textUser.setLocation(140,110);
		add(textUser);
		Label label2=new Label("Password:");
		label2.setFont(new Font("Times New Roman",Font.BOLD,16));
		label2.setSize(80,30);
		label2.setLocation(50,145);
		add(label2);
		final TextField textPWD=new TextField("admin");
		textPWD.setSize(110,30);
		textPWD.setFont(new Font("Times New Roman",Font.BOLD,16));
		textPWD.setLocation(140,145);
		add(textPWD);
		Button btnLogin=new Button("Login");
		btnLogin.setSize(60, 30);
		btnLogin.setFont(new Font("Times New Roman",Font.BOLD,16));
		btnLogin.setLocation(180,190);
		btnLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String user=textUser.getText();
				String pwd=textPWD.getText();
				//字母开头，6-16字节，允许字母数字下划线
				Pattern pattern=Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{4,15}$");
				if(pattern.matcher(user).matches() && pattern.matcher(pwd).matches()){
					try{
						Writer.writeObject(new MSG("LOGIN",user+" "+pwd));
						MSG msg=null;
						msg=(MSG)Reader.readObject();
						String command=msg.getCommand();
						if(command.equals("LOGINTRUE")){
							MainFrame mainFrame=new MainFrame(socket,user,Reader,Writer);
							mainFrame.setVisible(true);
							dispose();
						}
						else
							openDialog("Login false","Login false");
					}
					catch(Exception arg){
						openDialog("Connection Error",arg.toString());//"Please check you network."
					}
				}
				else {
					openDialog("Input Error","Please check your input.");
				}
			}
		});
		add(btnLogin);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	        	try {
	        		if(socket!=null){
	        			socket.close();
	        			socket=null;
	        		}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            dispose();
	        }
	    });
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
