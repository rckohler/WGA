package com.example.dsix;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Tile {
boolean isOccupied,isSelected,isBlock,isPath;
int x,y;
float tileSize;

public RectF bounds;   // Needed for Canvas.drawOval
private Paint paint;    // The paint style, color used for drawing
int color = Color.RED;
int velocityX =0, velocityY =0;
public Tile()
	{
	bounds = new RectF();
    paint  = new Paint();
    paint.setColor(color);
	}
void changeColor(){
	float z = x+y;
	if (z==((int)z/2)*2)  	color = Color.WHITE;
	else 					color = Color.BLUE;
}
public void draw(Canvas canvas) {
	//changeColor();
	paint.setColor(color);	
	bounds.set(x*tileSize, y*tileSize, x*tileSize+tileSize, y*tileSize+tileSize);
	//xPos,yPos, xPos+tileSize, yPos+tileSize
	canvas.drawRect(bounds, paint);
	if (isBlock){
		color = Color.BLACK;
		paint.setColor(color);
		canvas.drawOval(bounds, paint);	
	}
	if (isPath){
		color = Color.RED;
		paint.setColor(color);
		canvas.drawRect(bounds, paint);
	}
	
	}
public void move(){
	x += velocityX;
	y += velocityY;
}
}