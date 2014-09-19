package com.example.simpleanimation;

enum AnimationModel{ninja,runningMan,captain,mario,sprite};//add names to this in or
enum AnimationType{directional,single, directionalLR};
public class Animation {
	int columns, rows;
	int bitmapName;
	int animationCount=1; // number of frames in a single animation, if a NSEW 3x4 animation count = 3 because three images make the animation in each of the 4 directions. 
	int north,northeast,east,southeast, south, southwest, west, northwest;
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
			south = 0;
			southwest = 1;
			west = 2;
			northwest =3;
			north = 4;
			northeast = 5;
			east = 6;
			southeast=7;
		default:
			break;
			
		case sprite:
			bitmapName = R.drawable.sprite;
			frames = 3;
			columns = 3;
			rows = 4;
			south = 0;
			west = 1;
			east = 2;
			north =3;
			break;
			
		}	
	}
}
