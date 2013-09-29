package org.huangxiaozhe.Lianzhu;

import java.util.Random;

import android.content.Context;

public class CopyOfComputer {
	public static final int PLAY_NONE=0;
	public static final int PLAY_PLAYER=1;
	public static final int PLAY_COMPUTER=2;
	private static int boardSize;
	private static int table[][];
	public int playerCounter[];
    
	
	private Point tryPoint;
	
	CopyOfComputer(Context context,int size){
		tryPoint = new Point(-1, -1);
		boardSize=size;
		table=new int[boardSize][boardSize];
		for(int i=0;i<boardSize;i++){
			for(int j=0;j<boardSize;j++){
				table[i][j]=PLAY_NONE;
			}
		}
		
	}
	
	//get the number of connected chessman in one direction
	private int connectedIn1D(int player,int x,int y,int direction){
		int count=0;
		Point p=new Point(x,y);
		while(true){
			p.moveOneStep(direction);
			if(isInBoard(p) && table[p.x][p.y]==player)
				count++;
			else
				return count;
		}
	}
	
	private int[] expandedIn8D(int player,int x,int y){
		int count[]=new int[8];
		for(int direction=0;direction<8;direction++)
			count[direction]=expandedIn1D(player,x,y,direction);
		return count;
	}
	
	private int expandedIn1D(int player,int x,int y,int direction){
		int count=0;
		int nonecount=0;
		Point p=new Point(x,y);
		while(nonecount<4){
			p.moveOneStep(direction);
			if(!isInBoard(p))
				break;
			int t=table[x][y];
			if(t==0)
				nonecount++;
			if(t!=player && t!=PLAY_NONE){
				break;
			}
			count++;				
		}
		return count;
	}
	
	private int[] connectedIn8D(int player,int x,int y){
		int count[]=new int[8];
		for(int direction=0;direction<8;direction++){
			count[direction]=connectedIn1D(player,x,y,direction);
		}
		return count;
	}
	
	private boolean isInBoard(Point p){
		return p.x>=0 && p.x<boardSize && p.y>=0 && p.y<boardSize;
	}
	
	private int to5LAt(int player, int x, int y)
    {
        int lines = 0;
        int otherGain = 0;
        if(table[x][y] == 0)
        {
            int cd[] = connectedIn8D(player, x, y);
            int ed[] = expandedIn8D(player, x, y);
            for(int i = 0; i < 4; i++)
                if(ed[i] + ed[i + 4] + 1 >= 5)
                {
                    int l = cd[i] + cd[i + 4] + 1;
                    if(l >= 5)
                        lines++;
                    else
                        otherGain += 2 ^ l;
                }

        }
        return lines > 0 ? lines * 32 + otherGain : 0;
    }
	

    private Point to5L(int player)
    {
        if(playerCounter[player] < 4)
            return null;
        int maxGain = 0;
        Point point = null;
        for(int i = 0; i < boardSize; i++)
        {
            for(int j = 0; j < boardSize; j++)
            {
                int gain = to5LAt(player, i, j);
                if(gain > maxGain || gain > 0 && gain == maxGain && randomTrue())
                {
                    maxGain = gain;
                    point = new Point(i, j);
                }
            }

        }
        return point;
    }
    
    private boolean randomTrue()
    {
    	Random r = new Random();
        return r.nextBoolean();
    }
    
    private Point to4B(int player)
    {
        if(playerCounter[player] < 3)
            return null;
        Point point = null;
        int maxGain = 0;
        for(int i = 1; i < boardSize - 1; i++)
        {
            for(int j = 1; j < boardSize - 1; j++)
                if(table[i][j] == 0)
                {
                    int cd[] = connectedIn8D(player, i, j);
                    int ed[] = expandedIn8D(player, i, j);
                    for(int k = 0; k < 4; k++)
                        if(ed[k] > cd[k] && ed[k + 4] > cd[k + 4] && cd[k] + cd[k + 4] + 1 >= 4)
                        {
                            int gain = gainAt(i, j);
                            if(gain > maxGain || gain > 0 && gain == maxGain && randomTrue())
                            {
                                maxGain = gain;
                                point = new Point(i, j);
                            }
                        }

                }

        }

        return point;
    }

    private Point toSingle4S_3B_2N1B(int player)
    {
        if(playerCounter[player] < 2)
            return null;
        Point point = null;
        for(int r = 0; r < boardSize; r++)
        {
            for(int c = 0; c < boardSize; c++)
            {
                if(table[r][c] != 0 || find4S_3B_2N1BAt(r, c, player, -1) == -1)
                    continue;
                point = new Point(r, c);
                break;
            }

            if(point != null)
                break;
        }

        return point;
    }

    private Point toDouble4S_3B_2N1B(int player, boolean only4S)
    {
        if(playerCounter[player] < 4)
            return null;
        Point point = null;
        for(int rTest = 0; rTest < boardSize; rTest++)
        {
            for(int cTest = 0; cTest < boardSize; cTest++)
            {
                if(table[rTest][cTest] != 0)
                    continue;
                int cd[] = connectedIn8D(player, rTest, cTest);
                if(cd[0] + cd[1] + cd[2] + cd[3] + cd[4] + cd[5] + cd[6] + cd[7] <= 0)
                    continue;
                tryPoint.set(rTest, cTest);
                table[rTest][cTest] = player;
                boolean found = false;
                int dFirst = find4S_3B_2N1B(player, -1, rTest, cTest, only4S);
                if(dFirst != -1 && find4S_3B_2N1B(player, dFirst, rTest, cTest, false) != -1)
                    found = true;
                table[rTest][cTest] = 0;
                tryPoint.set(-1, -1);
                if(!found)
                    continue;
                point = new Point(rTest, cTest);
                break;
            }

            if(point != null)
                break;
        }

        return point;
    }

    private int find4SAt(int x, int y, int player, int exceptDirection)
    {
        int dFond = -1;
        int cd[] = connectedIn8D(player, x, y);
        int ed[] = expandedIn8D(player, x, y);
        for(int d = 0; d < 4; d++)
        {
            if(d == exceptDirection || table[x][y] != player)
                continue;
            int nConnect = cd[d] + cd[d + 4] + 1;
            int nFree1 = ed[d] - cd[d];
            int nFree2 = ed[d + 4] - cd[d + 4];
            boolean b4S = nConnect >= 4 && (nFree1 >= 1 || nFree2 >= 1);
            if(!b4S)
                continue;
            dFond = d;
            break;
        }

        return dFond;
    }

    private int find4S_3B_2N1BAt(int x, int y, int player, int exceptDirection)
    {
        int dFond = -1;
        int cd[] = connectedIn8D(player, x, y);
        int ed[] = expandedIn8D(player, x, y);
        for(int d = 0; d < 4; d++)
        {
            if(d == exceptDirection)
                continue;
            if(table[x][y] == player)
            {
                int nConnect = cd[d] + cd[d + 4] + 1;
                int nFree1 = ed[d] - cd[d];
                int nFree2 = ed[d + 4] - cd[d + 4];
                boolean b4S = nConnect >= 4 && (nFree1 >= 1 || nFree2 >= 1);
                boolean b3B = nConnect >= 3 && nFree1 >= 1 && nFree2 >= 1;
                if(b4S || b3B)
                {
                    dFond = d;
                    break;
                }
            }
            if(table[x][y] != 0)
                continue;
            int nFree1 = ed[d] - cd[d];
            int nFree2 = ed[d + 4] - cd[d + 4];
            boolean b2N1 = cd[d] >= 2 && cd[d + 4] >= 1 || cd[d] >= 1 && cd[d + 4] >= 2;
            boolean bSFree = nFree1 >= 1 && nFree2 >= 1;
            if(!b2N1 || !bSFree)
                continue;
            dFond = d;
            break;
        }

        return dFond;
    }

    private int find4S_3B_2N1B(int player, int exceptDirection, int rTest, int cTest, boolean only4S)
    {
        int dFond = -1;
        int rMin = rTest - 3;
        if(rMin < 0)
            rMin = 0;
        int rMax = rTest + 3;
        if(rMax > boardSize)
            rMax = boardSize;
        int cMin = cTest - 3;
        if(cMin < 0)
            cMin = 0;
        int cMax = cTest + 3;
        if(cMax > boardSize)
            cMax = boardSize;
        for(int r = rMin; r < rMax; r++)
        {
            for(int c = cMin; c < cMax; c++)
            {
                if(table[r][c] != player && table[r][c] != 0)
                    continue;
                if(only4S)
                    dFond = find4SAt(r, c, player, exceptDirection);
                else
                    dFond = find4S_3B_2N1BAt(r, c, player, exceptDirection);
                if(dFond != -1)
                    break;
            }

            if(dFond != -1)
               break;
        }

        return dFond;
    }
    
    private int gainAt(int x, int y)
    {
        if(table[x][y] == 0)
        {
            int gain = 0;
            for(int d = 0; d < 8; d++)
            {
                int gd = gainAtDirection(x, y, d);
                if(gd == 0)
                    gain >>= 2;
                else
                    gain += gd;
            }

            if(gain < 1)
                gain = 1;
            return gain;
        } else
        {
            return 0;
        }
    }
    
    private int gainAtDirection(int x, int y, int direction)
    {
        int gain = 0;
        Point p = new Point(x, y);
        int step = 0;
        do
        {
            p.moveOneStep(direction);
            step++;
            if(!isInBoard(new Point(p.x,p.y)))
                break;
            int player = table[p.x][p.y];
            if(player == 2)
                break;
            int gainByStone = player == 1 ? 5 : 1;
            gain += gainByStep(step) * gainByStone;
        } while(true);
        return gain;
    }

    private int gainByStep(int step)
    {
        int gain = (boardSize - step) / 2;
        if(gain < 1)
            gain = 1;
        return gain;
    }
    
    private Point maxGainedPoint()
    {
        Point pointWithMaxGain = null;
        int maxGain = 0;
        for(int i = 0; i < boardSize; i++)
        {
            for(int j = 0; j < boardSize; j++)
            {
                int gain = gainAt(i, j);
                if(gain > maxGain || gain > 0 && gain == maxGain && randomTrue())
                {
                    maxGain = gain;
                    pointWithMaxGain = new Point(i, j);
                }
            }

        }

        return pointWithMaxGain;
    }
    
    public Point play(int t[][],int c[])
    {
    	table=t;
    	playerCounter=c;
        Point dc = null;
        if((dc = to5L(1)) == null && (dc = to5L(2)) == null && (dc = to4B(1)) == null && (dc = to4B(2)) == null && (dc = toDouble4S_3B_2N1B(1, true)) == null && (dc = toDouble4S_3B_2N1B(2, true)) == null && (dc = toDouble4S_3B_2N1B(1, false)) == null && (dc = toDouble4S_3B_2N1B(2, false)) == null && (dc = toSingle4S_3B_2N1B(1)) == null)
            dc = toSingle4S_3B_2N1B(2);
        if(dc == null)
            dc = maxGainedPoint();
        return dc;
    }
}
