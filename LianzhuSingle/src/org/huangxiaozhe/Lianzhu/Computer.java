package org.huangxiaozhe.Lianzhu;

import java.util.Random;

import org.huangxiaozhe.Common.Point;

import android.content.Context;
import android.util.Log;

public class Computer {
	public static final int PLAY_NONE=0;
	public static final int PLAY_PLAYER=1;
	public static final int PLAY_COMPUTER=2;
	private static int boardSize;
	private static int table[][];
	public static int difficulty;
	
	Computer(Context context,int size, int diff){
		boardSize=size;
		table=new int[boardSize][boardSize];
		for(int i=0;i<boardSize;i++){
			for(int j=0;j<boardSize;j++){
				table[i][j]=PLAY_NONE;
			}
		}
		difficulty=diff;
	}
	
	private static boolean isInBoard(Point p){
		return p.x>=0 && p.x<boardSize && p.y>=0 && p.y<boardSize;
	}
	
	private static boolean isInBoard(int x,int y){
		return x>=0 && x<boardSize && y>=0 && y<boardSize;
	}
	
    private boolean randomTrue()
    {
    	Random r = new Random();
        return r.nextBoolean();
    }
    
    private static int gainAt(int statue){
	    if(statue<21){			//低于"冲二"棋型
	    	return 0;
	    }
	    else if(statue<22){		//"冲二"棋型
	    	return 1;
	    }
	    else if(statue<31){		//"活二"棋型
	    	return 3;
	    }
	    else if(statue<32){		//"冲三"棋型
	    	return 4;
	    }
	    else if(statue<41){		//"活三"棋型
	    	return 10;
	    }
	    else if(statue<42){		//"冲四"棋型
	    	return 15;
	    }
	    else if(statue<50){		//"活四"棋型
	    	return 40;
	    }else					//"连五"棋型
	    	return 100;
    }

	private static int gainIn1D(int x,int y,int direction,int player){
		Point p=new Point(x,y);
		if(table[x][y]!=PLAY_NONE)
			return 0;
		int count=10;
		int expand=1;
		int i=2;
		while(i-->0){
			for(p.moveOneStep(direction);isInBoard(p);p.moveOneStep(direction)){	
				int t=table[p.x][p.y];
				if(t==PLAY_NONE){
					count++;
					while(expand<5 && isInBoard(p) && table[p.x][p.y]==PLAY_NONE){
						expand++;
						p.moveOneStep(direction);
					}
					break;
				}
				else if(t==player){
					expand++;
					count+=10;
				}
				else {
					break;
				}
			};
			p.set(x, y);
			direction+=4;
		}		
		if(difficulty>=2 && expand<5)		//无法形成连五
			return 0;
		if(difficulty>=3)
			return gainAt(count)*10+expand;
		else 
			return gainAt(count)*10;
	}
	
	private static int gainIn4D(int x,int y,int player){
		int max1=0,max2=0,t;
		for(int direction=0;direction<4;direction++){
			t=gainIn1D(x,y,direction,player);
			if(t>max1){
				max1=t;
				max2=max1;
			}
			else if(t>max2){
				max2=t;
			}
		}
		return max1+max2;
	}
	
    public Point play(int t[][])
    {
    	table=t;
    	Point pointPlayer = new Point();
        Point pointComputer=new Point();
        int maxPlayer=-1,maxComputer=-1,temp;
        for(int i=0;i<boardSize;i++){
        	for(int j=0;j<boardSize;j++){
        		if(isInBoard(i,j) && table[i][j]==PLAY_NONE){
        			//get max gain for player
        			temp=gainIn4D(i,j,PLAY_PLAYER);
        			if(temp>maxPlayer || (temp==maxPlayer&& randomTrue())){
        				maxPlayer=temp;
        				pointPlayer.set(i, j);
        			}
        			//get max gain for computer
        			temp=gainIn4D(i,j,PLAY_COMPUTER);
        			if(temp>maxComputer || (temp==maxComputer && randomTrue())){
        				maxComputer=temp;
        				pointComputer.set(i,j);
        			}
        		}
        			
        	}
        }
        Log.d("Lianzhu",String.valueOf(maxComputer));
        if(maxComputer>=maxPlayer && difficulty >=2 || maxComputer>=400){
          	return pointComputer;
        }
        else {
        	return pointPlayer;
        }
    }
    /*
     * 		if(difficulty>=2 && expand<5)		//无法形成连五
     *			return 0;
	 *		if(difficulty>=3)
	 *			return gainAt(count)*10+expand;
	 *		else 
	 *			return gainAt(count)*10;
     * 
     * 
     * 		if(maxComputer>=maxPlayer && difficulty >=2 || maxComputer>=400){
     *     		return pointComputer;
     *   	}
     *  	else {
     *  		return pointPlayer;
     *   	}
     * 
     * 	difficulty					easy	medium	hard
     *  同等分值时在空间大的位置下棋	否		否		是
     * 	不在无空间连五的位置下棋		否		是		是
     * 	非"活四"或者"连五"时主动进攻	否		是		是
     */
}
