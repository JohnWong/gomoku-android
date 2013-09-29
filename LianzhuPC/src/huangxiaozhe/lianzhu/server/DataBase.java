package huangxiaozhe.lianzhu.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase
{
	private static String url = "jdbc:mysql://localhost:3306/lianzhu";
	private static String userName = "sa";
	private static String password = "123456";
	private static String JDBCDriver="com.mysql.jdbc.Driver";
	private static Connection conn=null;
	private static Statement sql;
	
	public void getConnection(){
		try {
			Class.forName(JDBCDriver);
			conn = DriverManager.getConnection(url,userName,password);
			sql=conn.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("DataBase connected!");
	}
    
    public void close()
    {
        try {
            if(!conn.isClosed())
            {
                conn.close();
                System.out.println("close successful");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


/*    public static void main(String[] args) {
        // TODO Auto-generated method stub
        DataBase cdb=new DataBase();
        cdb.connect();
        Statement stmt;
        ResultSet rs;
        try {
            stmt = cdb.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery("select * from Users");
            while(rs.next())
            {
                String s=rs.getString(1);
                System.out.print(s+"\t");
                s=rs.getString(2);
                System.out.print(s+"\t");
                s=rs.getString(3);
                System.out.println(s);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cdb.close();  
    }	*/
    
    public boolean login(String user,String pwd){
    	try {
    		ResultSet result = sql.executeQuery("select count(*) from users where user='"+user+"' and password='"+pwd+"'");
			result.next();
			if(result.getInt(1)>0){
				return true;
			}
			else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
}
