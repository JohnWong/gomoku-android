package org.huangxiaozhe.Common;

public class Point {
	public int x;
	public int y;
	
	public Point(int i,int j){
		x=i;
		y=j;
	}
	
	public Point() {
		// TODO Auto-generated constructor stub
		x=-1;
		y=-1;
	}

	public void moveOneStep(int direction){
		switch(direction){
		case 0:
			x++;
			break;
		case 1:
			x++;
			y--;
			break;
		case 2:
			y--;
			break;
		case 3:
			x--;
			y--;
			break;
		case 4:
			x--;
			break;
		case 5:
			x--;
			y++;
			break;
		case 6:
			y++;
			break;
		case 7:
			x++;
			y++;
			break;
		}
	}

	public void set(int i, int j) {
		// TODO Auto-generated method stub
		x=i;
		y=j;
	}
}
