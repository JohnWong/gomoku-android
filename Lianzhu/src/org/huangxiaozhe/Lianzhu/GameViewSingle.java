package org.huangxiaozhe.Lianzhu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class GameViewSingle extends View{
	private final GameSingle game;
	
	private int boardLength;
	private float gridLength;
	private int boardX;
	private int boardY;
	private int selX;
	private int selY;
	private int chessmanSize;
	private final Rect selRect=new Rect();
	private int boardSize;
	private boolean upperHand;
	public static final int PLAY_NONE=0;
	public static final int PLAY_PLAYER=1;
	public static final int PLAY_COMPUTER=2;
	
	private int sweep=0;
	private static final int SWEEP_INC = 10;
    	
	public GameViewSingle(Context context) {
		super(context);
		this.game=(GameSingle)context;
		upperHand=game.getUpperHand();
		boardSize=game.getBoardSize();
		selX=selY=boardSize/2;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	private void getRect(int x, int y, Rect rect) {
		rect.set((int)(boardX+(x+0.5)*boardLength/(boardSize+1)), (int)(boardY+(y+0.5)*boardLength/(boardSize+1)),(int)(boardX+(x+1.5)*boardLength/(boardSize+1)),(int)(boardY+(y+1.5)*boardLength/(boardSize+1)));
	}

	private void select(int x, int y) {
		invalidate(selRect);
		selX=Math.max(0, Math.min(x,boardSize-1));
		selY=Math.max(0, Math.min(y, boardSize-1));
		getRect(selX,selY,selRect);
		invalidate(selRect);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		
		drawBackground(canvas);
		drawBoard(canvas);
		drawSelection(canvas);
		drawPoints(canvas);
	}

	private void drawPoints(Canvas canvas) {
		//draw all chess-man
		Paint chessBlack=new Paint();
		chessBlack.setColor(getResources().getColor(R.color.game_upperhand));
		chessBlack.setAntiAlias(true);
		
		Paint chessWhite=new Paint();
		chessWhite.setColor(getResources().getColor(R.color.game_nupperhand));
		chessWhite.setAntiAlias(true);
		
		for(int i=0;i<boardSize;i++){
			for(int j=0;j<boardSize;j++){
				if(upperHand){				//Player goes first with black chessman
					if(game.getTable()[i][j]==PLAY_PLAYER){
						drawChessman(canvas,chessBlack, i,j);
					}
					else if(game.getTable()[i][j]==PLAY_COMPUTER){
						drawChessman(canvas,chessWhite,i,j);
					}
				}
				else{
					if(game.getTable()[i][j]==PLAY_PLAYER){
						drawChessman(canvas,chessWhite,i,j);
					}
					else if(game.getTable()[i][j]==PLAY_COMPUTER){
						drawChessman(canvas,chessBlack, i,j);
					}
				}
				
			}
		}
	}
	
	public void drawPoint(int x,int y){
		Rect rect=new Rect();
		getRect(x,y,rect);
		invalidate(rect);
	}

	private void drawChessman(Canvas canvas, Paint paint,int x, int y) {
		Rect rect=new Rect();
		getRect(x,y,rect);
		canvas.drawCircle(rect.centerX(),rect.centerY(),chessmanSize,paint);
	}

	private void drawSelection(Canvas canvas) {
		
		//draw the selection
		Paint selected=new Paint();
		if(upperHand)
			selected.setColor(getResources().getColor(R.color.game_upperhand));
		else
			selected.setColor(getResources().getColor(R.color.game_nupperhand));
		selected.setAntiAlias(true);
		if(sweep>2*SWEEP_INC)
			sweep=0;
		if(sweep>SWEEP_INC){
			selected.setAlpha(255);
		}
		else{
			selected.setAlpha(0);
		}
			
		canvas.drawCircle(selRect.centerX(),selRect.centerY(),chessmanSize,selected);
		sweep++;
		invalidate(selRect);
	}

	private void drawBoard(Canvas canvas) {
		
		//draw the board
		//define colors for the grid lines
		Paint gridBackground=new Paint();
		gridBackground.setColor(getResources().getColor(R.color.grid_background));
		Paint gridLine=new Paint();
		gridLine.setColor(getResources().getColor(R.color.grid_line));
		Paint gridLine2=new Paint();
		gridLine2.setColor(getResources().getColor(R.color.grid_Line2));
		
		//draw the board background
		canvas.drawRect(boardX+gridLength/2, boardY+gridLength/2, boardX+boardLength-gridLength/2, boardY+boardLength-gridLength/2, gridBackground);
		
		//draw the grid shadow 
		for(int i=1,x1=(int)(boardX+boardLength/(boardSize+1)),x2=(int)(boardX+boardSize*boardLength/(boardSize+1)),y;i<=boardSize;i++){
			y=(int)(boardY+i*boardLength/(boardSize+1));
			canvas.drawLine(x1, y+1, x2, y+1, gridLine2);
		}
		for(int i=1,y1=(int)(boardY+boardLength/(boardSize+1)),y2=(int)(boardY+boardSize*boardLength/(boardSize+1)),x;i<=boardSize;i++){
			x=(int)(boardX+i*boardLength/(boardSize+1));
			canvas.drawLine(x+1, y1, x+1, y2, gridLine2);
		}
		
		//draw the grid
		for(int i=1,x1=(int)(boardX+boardLength/(boardSize+1)),x2=(int)(boardX+boardSize*boardLength/(boardSize+1)),y;i<=boardSize;i++){
			y=(int)(boardY+i*boardLength/(boardSize+1));
			canvas.drawLine(x1, y-1, x2, y-1, gridLine);
			canvas.drawLine(x1, y, x2, y, gridLine);
		}
		for(int i=1,y1=(int)(boardY+boardLength/(boardSize+1)),y2=(int)(boardY+boardSize*boardLength/(boardSize+1)),x;i<=boardSize;i++){
			x=(int)(boardX+i*boardLength/(boardSize+1));
			canvas.drawLine(x-1, y1, x-1, y2, gridLine);
			canvas.drawLine(x, y1, x, y2, gridLine);
		}		
	}

	private void drawBackground(Canvas canvas) {
		
		//draw the background
		Paint background=new Paint();
		background.setColor(getResources().getColor(R.color.game_background));
		canvas.drawRect(0,0,getWidth(),getHeight(),background);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		super.onSizeChanged(w, h, oldw, oldh);
		boardX=0;
		boardY=0;
		boardLength=Math.min(w, h);
		gridLength=boardLength/(boardSize+1);
		chessmanSize=(int)gridLength*3/8;
		getRect(selX,selY,selRect);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch(keyCode){
		 case KeyEvent.KEYCODE_DPAD_UP:
		     select(selX, selY - 1);
	         break;
	      case KeyEvent.KEYCODE_DPAD_DOWN:
	         select(selX, selY + 1);
	         break;
	      case KeyEvent.KEYCODE_DPAD_LEFT:
	         select(selX - 1, selY);
	         break;
	      case KeyEvent.KEYCODE_DPAD_RIGHT:
	         select(selX + 1, selY);
	         break;
	      case KeyEvent.KEYCODE_SPACE:
	      case KeyEvent.KEYCODE_DPAD_CENTER:
	      case KeyEvent.KEYCODE_ENTER:
	    	  game.manGo(selX,selY);
	      default:
	    	  return super.onKeyDown(keyCode, event);
	     }
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getAction()!=MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		int x=(int)((event.getX()-boardX-gridLength/2)/gridLength);
		int y=(int)((event.getY()-boardY-gridLength/2)/gridLength);
		select(x,y);
		game.manGo(x,y);
		return true;
	}
	
}
