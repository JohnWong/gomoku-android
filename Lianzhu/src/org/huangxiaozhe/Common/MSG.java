package org.huangxiaozhe.Common;

public class MSG implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String command;
	private String content;
	
	public MSG(String str1,String str2){
		command=str1;
		content=str2;
	}
	public String getCommand(){
		return command;
	}
	
	public String getContent(){
		return content;
	}
}
