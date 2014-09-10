package com.example.dsix;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Button {

public RectF bounds;   // Needed for Canvas.drawOval
private Paint paint;    // The paint style, color used for drawing
int color = Color.BLACK;
float rx,ry;
int width=50, height=50;
String name;

public Button(String s, float x, float y)
	{
	name = s;
	rx = x;
	ry = y;
	bounds = new RectF();
    paint  = new Paint();
    paint.setColor(color);
	}

public void draw(Canvas canvas) {
	color = Color.BLACK;
	paint.setColor(color);
	bounds.set(rx, ry, rx+width,ry+height);
//	bounds.set(0,0,20,20);
//	canvas.drawRect(bounds, paint);
	canvas.drawText(name, bounds.centerX(),bounds.centerY()-10 , paint);	
	}
}