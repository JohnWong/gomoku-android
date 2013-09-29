package org.huangxiaozhe.Lianzhu;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Application;

public class MyApp extends Application{
	private ObjectInputStream Reader;
	private ObjectOutputStream Writer;
	
	public void setReader(ObjectInputStream reader) {
		Reader = reader;
	}
	public ObjectInputStream getReader() {
		return Reader;
	}
	public void setWriter(ObjectOutputStream writer) {
		Writer = writer;
	}
	public ObjectOutputStream getWriter() {
		return Writer;
	}
}
