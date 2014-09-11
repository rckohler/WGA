package com.example.simpleanimation;
import android.graphics.Rect;
import android.graphics.RectF;
enum MoveDirection{north, northEast, east, southEast, south, southWest, west, northWest};

public class AnimatedObject {
	int bitmapName; //name of bitmap stored in res/drawable
	MoveDirection direction = MoveDirection.north;
	float radius=50; // radius of object
	float rx=50,ry=50; //position of object on x and y axis
	RectF size = new RectF(); //used for drawing object
	Rect frame = new Rect(); // used for drawing object
	Animation a; // animation used by this animated object (another class)
	int speed =5; // speed of object not speed of animation
	int destinationX, destinationY;

	public AnimatedObject(MoveDirection startDirection, int location){
		rx = location*50 + 50;
		ry = location*50 + 50;
		destinationX = (int) rx;
		destinationY = (int) ry;
		direction = startDirection;
		size.set(rx-radius, ry-radius, rx+radius, ry+radius);
		a = new Animation(AnimationModel.captain,AnimationType.directional); // animations are created bu telling them 
	}
	public void setDestination(int newDestinationX, int newDestinationY){
		destinationX = newDestinationX;
		destinationY = newDestinationY;
		int dx = (int) (newDestinationX-rx);
		int dy = (int) (newDestinationY-ry);
		double d = Math.pow(dx*dx+dy*dy, .5);
		int ax,ay;
		ax = Math.abs(dx);
		ay = Math.abs(dy);
		double px = ax/(d);
		double py = ay/(d);
		if (dx>0 && dy >0)
		{
			direction = MoveDirection.southEast;
			if (px>.7)direction = MoveDirection.east;
			if (px<.3)direction = MoveDirection.south;
		}
		if (dx<0 && dy <0){
			direction = MoveDirection.northWest;
			if (px>.7)direction = MoveDirection.west;
			if (px<.3)direction = MoveDirection.north;
		}

		if (dx>0 && dy <0){
			direction = MoveDirection.northEast;
			if (px>.7)direction = MoveDirection.east;
			if (px<.3)direction = MoveDirection.north;
		}
		if(dx <0 && dy >0){
			direction = MoveDirection.southWest;
			if (px>.7)direction = MoveDirection.west;
			if (px<.3)direction = MoveDirection.south;
		}
		System.out.println("dx,dy,px =" + dx + " , "+ dy + " , " + px);
	}
	public void move(){
		int dx=0,dy=0,d=0;
		int speedX=0, speedY=0;
		dx = (int) (destinationX-rx);
		dy = (int) (destinationY-ry);
		d  = (int) Math.pow(dx*dx+dy*dy, .5);

		if(d<speed) {
			rx = destinationX;
			ry = destinationY;
			speedX = 0;
			speedY = 0;
			System.out.println("at destination");
		}
		else{
			if (d>0)
			{
				speedX = speed*dx/d;
				speedY = speed*dy/d;
				System.out.println ("moving");
			}
		}
		rx+=speedX;
		ry+=speedY;
		size.set(rx-radius, ry-radius, rx+radius, ry+radius);
		System.out.println(rx + " " + ry);
	}
}