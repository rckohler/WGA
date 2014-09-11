package com.example.simpleanimation;

public class Thing {
	int x,y;
	int vx,vy;
	Animation a;
	//String BrannonBlows;
	public Thing(int x, int y, Animation a){
		this.x = x;
		this.y = y;
		this.a = a;
	}
	public void move(){
		x +=vx;
		y +=vy;
	}
	
}
