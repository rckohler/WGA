package com.example.dsix;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Guy {
	int face,maxDice,team;
	int currentDice;
	int speed, attack;
	int movesLeft; //if move then attack move = 0;
	Suit suit;
	boolean hasActed,hasMoved,isTurn,isTargeted;
	
	float tileSize;
	int x,y;
	
	public RectF bounds;   // Needed for Canvas.drawOval
	private Paint paint;    // The paint style, color used for drawing
	int color = Color.RED;
	
	
	public Guy(int f, Suit s, int d, float t)
	{
		face 		= f;
		maxDice 	= d;
		tileSize 	= t;
		suit		= s;
		
		currentDice = maxDice;
		speed  		= currentDice;
		attack 		= currentDice;
		movesLeft 	= currentDice;
		bounds		= new RectF();
	    paint  		= new Paint();
	    
	    paint.setColor(color);
	}
	
	public boolean attack(Guy g){
		boolean result = false;
		if (rollDiceMax(attack)>rollDiceMax(g.currentDice)) {
			result = true;
			g.currentDice--;
		}
		return result;
	}
	public int rollDiceMax(int d){
		Random rand = new Random();
		int r,result=0;
		for (int i=0; i < d; i++){
			r = rand.nextInt(6)+1;
			if (r>result) result = r;	
		}
		return result;
	}
	
	public void move(Tile t){
		{
			x = t.x;
			y = t.y;
		}
	}
	
	
	public void draw(Canvas canvas) {
		if (currentDice>0)
		{
		String csuit="na";
		if (suit == Suit.Fighter) csuit = "C";
		if (suit == Suit.Mage) csuit = "D";
		if (suit == Suit.Cleric) csuit = "H";
		if (suit == Suit.Archer) csuit = "S";
		String chd = Integer.toString(currentDice);
		String tag = chd+csuit;
		if (team==1) color = Color.RED;
		if (team==2) color = Color.YELLOW;
		if (isTurn)color = Color.GREEN;
		paint.setColor(color);
		bounds.set(x*tileSize, y*tileSize, x*tileSize+tileSize, y*tileSize+tileSize);
		canvas.drawOval(bounds, paint);
		paint.setColor(Color.BLACK);
		bounds.set(x*tileSize, y*tileSize, x*tileSize+tileSize-15, y*tileSize+tileSize+10);
		canvas.drawText(tag, bounds.centerX(),bounds.centerY() , paint);		
		}
		else
		{
		paint.setColor(Color.GRAY);
		bounds.set(x*tileSize, y*tileSize, x*tileSize+tileSize, y*tileSize+tileSize);
		canvas.drawOval(bounds, paint);
		}
	}
}

