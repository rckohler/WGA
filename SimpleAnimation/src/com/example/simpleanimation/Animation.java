package com.example.simpleanimation;

enum AnimationModel{ninja,runningMan,captain};//add names to this in or
enum AnimationType{directional,single};
public class Animation {
	int columns, rows;
	int bitmapName;
	int animationCount=1;
	AnimationType animationType;
	int frames;
	public Animation(AnimationModel am,AnimationType at){
		animationType = at;
		switch(am){
		case ninja:
			bitmapName = R.drawable.ninja2;
			frames = 6;
			columns = 6;
			rows = 1;

			break;
		case runningMan:
			bitmapName = R.drawable.runningman;
			frames = 30;
			columns = 6;
			rows = 5;
			break;

		case captain:
			bitmapName = R.drawable.captain;
			frames = 4;
			columns = 8;
			rows = 7;
		default:
			break;
		}	
	}
}
