package com.example.simpleanimation;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class AnimationView extends View {
	Vector<Bitmap> bitmaps = new Vector<Bitmap>();
	Vector<AnimatedObject> animatedObjects = new Vector<AnimatedObject>();
	AnimatedObject player;
	
	public AnimationView(Context context) {
		super(context);
		setUp();
	}
	public boolean onTouchEvent(MotionEvent event) {

		int eventAction = event.getAction();   

		switch (eventAction) {
		case MotionEvent.ACTION_DOWN:         
			player.setDestination((int)event.getX(), (int)event.getY());
			//if(newDestination)
		}
		return false;
	}

	private void setUp(){
		animatedObjects.add(new AnimatedObject(MoveDirection.east,AnimationModel.captain,AnimationType.directional,1));
		player = animatedObjects.elementAt(0);

		for (int i=0; i< animatedObjects.size(); i++)
		{
			Bitmap b;
			b = BitmapFactory.decodeResource(getResources(), animatedObjects.get(i).a.bitmapName);
			bitmaps.add(b);
		}
	}
	private void update(){
		for (int i=0; i< animatedObjects.size(); i++){
			animatedObjects.elementAt(i).move();
		}
	}
	private void drawGuys(Canvas canvas){
		for (int i=0; i < animatedObjects.size(); i++){	//draw each guy with animation

			Rect frame;
			RectF size;
			int width = bitmaps.get(i).getWidth();
			int height= bitmaps.get(i).getHeight();
			int currentRow=0;
			AnimatedObject animatedObject = animatedObjects.elementAt(i);
			Animation animation = animatedObject.a;

			int numberOfFrames = animation.frames;
			int frameWidth = width/animation.columns;	
			int frameHeight = height/animation.rows;

			if (animation.animationCount>=numberOfFrames)animation.animationCount = 0; 
			currentRow = animation.animationCount/animation.columns;
			size   = animatedObject.size;
			frame  = animatedObject.frame;

			if(animation.animationType==AnimationType.single)
				frame.set(
						animation.animationCount*frameWidth-width*currentRow,//left
						currentRow*frameHeight,//upper
						frameWidth+animation.animationCount*frameWidth-currentRow*width,//right
						currentRow*frameHeight+frameHeight//bottom
						); //(left,top,right,bottom)
			if (animation.animationType==AnimationType.directionalLR){
				int left=0, right=0, top=0, bottom=0;
				switch(animatedObject.direction){
				case east:
					top= animation.east*frameHeight;
					break;
				case north:
					top = animation.north*frameHeight;
					break;
				case northEast:
					top =animation.northeast*frameHeight;
					break;
				case northWest:
					top =animation.northwest*frameHeight;
					break;
				case south:
					top = animation.south*frameHeight;
					break;
				case southEast:
					top = animation.southeast*frameHeight;
					break;
				case southWest: 
					top = animation.southwest*frameHeight;
					break;
				case west:
					top = animation.west*frameHeight;
					break;
				default:
					break;	
				}			
				left = animation.animationCount*frameWidth;
				right = left + frameWidth;
				bottom = top + frameHeight;
				frame.set(left,top,right,bottom);
			}
			if (animation.animationType==AnimationType.directional){
				int left=0, right=0, top=0, bottom=0;
				switch(animatedObject.direction){
				case east:
					left= animation.east*frameWidth;
					break;
				case north:
					left = animation.north*frameWidth;
					break;
				case northEast:
					left =animation.northeast*frameWidth;
					break;
				case northWest:
					left =animation.northwest*frameWidth;
					break;
				case south:
					left = animation.south*frameWidth;
					break;
				case southEast:
					left = animation.southeast*frameWidth;
					break;
				case southWest: 
					left = animation.southwest*frameWidth;
					break;
				case west:
					left = animation.west*frameWidth;
					break;
				default:
					break;	
				}			
				top = animation.animationCount*frameHeight;
				right = left + frameWidth;
				bottom = top + frameHeight;
				frame.set(left,top,right,bottom);
			}
			canvas.drawBitmap(bitmaps.get(i),frame,size, null);
			animation.animationCount++;

		}	

	}
	protected void onDraw(Canvas canvas)
	{	
		update();
		drawGuys(canvas);
				try {  
			Thread.sleep(300);   
		} catch (InterruptedException e) { }      
		invalidate(); 	}
}