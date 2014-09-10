package com.example.dsix;
//current errors: frieball blows up world. 

import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
enum Suit {Fighter, Mage, Cleric, Archer};
enum InputType {Move, Act, Confirm, Cancel, AdvanceTurn};

public class GameView extends View 
{	
	Vector<Tile> tiles 		= new Vector<Tile>	();
	Vector<Guy>  guys   	= new Vector<Guy>	();
	Vector<Button> buttons 	= new Vector<Button>();

	Random rand = new Random();
	String status;
	int gameBoardWidth=10, gameBoardHeight=10;float wwidth,blockPercentage=25, wheight;float tileSize;
	boolean needInput=true,needTarget=true;Tile selectedTile;Guy  targetedGuy;int turn =0, input; //in thirds

	public GameView(Context context) 
	{
		super(context); // super comes first   
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();

		wwidth = metrics.widthPixels;
		wheight = metrics.heightPixels;				
		createBoard(gameBoardWidth,gameBoardHeight);
		createButtons(gameBoardWidth,gameBoardHeight);
		createGuys();

		this.setFocusable(true);
		this.requestFocus();// needed for keyboard debugging :)
	}

	public void createButtons(int w, int h)
	{
		buttons.add(new Button("Move",		  0,			wheight-(wheight-tileSize*h)));//String s, float x, float y, float w, float h)
		buttons.add(new Button("Attack",	  wwidth/4,		wheight-(wheight-tileSize*h)));//String s, float x, float y, float w, float h)
		buttons.add(new Button("End Turn",	  3*wwidth/5,		wheight-(wheight-tileSize*h)));//String s, float x, float y, float w, float h)
	}
	public void createBoard(int w, int h){
		if ((wwidth/w)<(wheight/h)) 
			tileSize = wwidth/w;
		else tileSize = wheight/h;
		for (int i = 0; i<w*h; i++){
			tiles.add(new Tile());
			tiles.elementAt(i).x = i-((int)i/w)*w;
			tiles.elementAt(i).y= (int)i/w;
			if(rand.nextInt(100)<blockPercentage&&(i>gameBoardWidth&&i<gameBoardWidth*gameBoardHeight-gameBoardWidth))
			{
				tiles.elementAt(i).isOccupied = true;
				tiles.elementAt(i).isBlock = true;		
			}
			tiles.elementAt(i).tileSize=tileSize;
		}
	}
	public void createGuys(){
		for (int i = 0; i <9; i++){
			Suit s= Suit.Mage;
			int r = rand.nextInt()*4;
			if (r ==0) s = Suit.Fighter;
			if (r ==1) s = Suit.Mage;
			if (r ==2) s = Suit.Cleric;
			if (r ==3) s = Suit.Archer;

			if(i<4){

				guys.add(new Guy(i,s,3,tileSize));
				guys.elementAt(i).x=i;
				tiles.elementAt(guys.elementAt(i).y*gameBoardWidth+guys.elementAt(i).x).isOccupied=true;
				guys.elementAt(0).isTurn=true;
				guys.elementAt(i).team =1;
			}

			else{
				guys.add(new Guy(i-4,s,3,tileSize));
				guys.elementAt(i).x=i;
				guys.elementAt(i).y=gameBoardHeight-1;
				tiles.elementAt(guys.elementAt(i).y*gameBoardWidth+guys.elementAt(i).x).isOccupied=true;
				guys.elementAt(i).team =2;

			}	
		}
	}
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_D: 
			for(int i = 0; i < gameBoardWidth*gameBoardHeight;i++){
				tiles.elementAt(i).isPath=false;
			}
		}
		return true;  // Event handled
	}	
	public boolean onTouchEvent(MotionEvent event) {

		int eventAction = event.getAction();   

		switch (eventAction) {
		case MotionEvent.ACTION_DOWN:         
			//finger touches the screen
			boolean needProcess=true;
			if (needInput&&needTarget&&needProcess){ //select action
				if(event.getY()>tileSize*gameBoardHeight){
					input = (int) (event.getX()*3/(wwidth)); // takes one of three inputs
					needInput = false;
					needProcess = false;
					buttons.elementAt(0).name = "Click to cancel action";
					buttons.elementAt(1).name = "";
					buttons.elementAt(2).name = "";
					if(input ==2){
						advanceTurn();		
						askForInput();}
					update();
				}
			}	    			
			if(!needInput && needTarget && needProcess){//select target
				if(input ==0){ // if moving	
					if(event.getY()>tileSize*gameBoardHeight){ //clicked off of gameboard
						//input = (int) (event.getX()*3/(wwidth)); // takes one of three inputs
						Toast.makeText(getContext(), "cancelled action" , Toast.LENGTH_SHORT).show();
						askForInput();
						needInput = true;
						needTarget = true;
						clearTileSelection();
						clearGuySelection();
						needProcess = false;
						update();
					}
					else{ //clicked on gameboard										
						selectTarget(event.getX(),event.getY());
						needProcess = false;
						act(0); //move 
						update();
					}
				}
				else //if original input = 1 or 2
				{
					if(event.getY()<tileSize*gameBoardHeight)
					{ //clicked on gameboard	
						selectTarget(event.getX(),event.getY());
						buttons.elementAt(0).name = "Confirm";
						buttons.elementAt(1).name = "Cancel";
						buttons.elementAt(2).name = "Advance Turn";
						needProcess = false;
						update();
					}
					else { // clicked off gameBoard.
						Toast.makeText(getContext(), "cancelled action" , Toast.LENGTH_SHORT).show();
						if(guys.elementAt(turn).speed != guys.elementAt(turn).movesLeft)guys.elementAt(turn).hasMoved=true;
						askForInput();
						needInput = true;
						needTarget = true;
						clearTileSelection();
						clearGuySelection();
						needProcess = false;
						update();
					}

				}

			}
			if (needInput&&!needTarget && needProcess && event.getY()>tileSize*gameBoardHeight){// confirm and act
				int action = input; // sets action to origninal input
				input = (int) (event.getX()*3/(wwidth));
				if (input ==0)
					act(action);
				askForInput();
				update();
				if (input ==1){
					Toast.makeText(getContext(), "cancelled action",Toast.LENGTH_SHORT).show();
					needInput =true;
					needTarget=true;
					askForInput();
					update();			    
				}
				if (input ==2){
					advanceTurn();
				}

			}

		case MotionEvent.ACTION_MOVE:
			// finger moves on the screen
			break;
		case MotionEvent.ACTION_UP:   
			// finger leaves the screen
			break;        
		}
		return true;// tell the system that we handled the event and no further processing is required
	}
	private void selectTarget(float rx, float ry){
		int x = (int) (rx/tileSize);		int y = (int) (ry/(tileSize));		int tileI = y*gameBoardWidth+x;
		if (tileI < gameBoardWidth*gameBoardHeight)//if clicked on gameboard
		{
			if (input ==0 && guys.elementAt(turn).movesLeft>0)
			{//if moving and have not finished movement
				needInput = false;
				needTarget = true;
				if(!tiles.elementAt(tileI).isOccupied)
				{ // is not occupied
					clearTileSelection();
					tiles.elementAt(tileI).isSelected = true;
					selectedTile = tiles.elementAt(tileI);
					act(0);
				}
			}

			else //if not moving
			{
				needInput = true;
				needTarget = false;

				if (tiles.elementAt(tileI).isOccupied)
					for (int i = 0; i < guys.size(); i++)
						if (guys.elementAt(i).x==tiles.elementAt(tileI).x&&guys.elementAt(i).y == tiles.elementAt(tileI).y)//uses adjusted x,y
						{
							clearGuySelection();
							guys.elementAt(i).isTargeted=true;
							targetedGuy = guys.elementAt(i);
						}
				if(!tiles.elementAt(tileI).isOccupied)
				{ // is not occupied
					clearTileSelection();
					tiles.elementAt(tileI).isSelected = true;
					selectedTile = tiles.elementAt(tileI);

				}				
			}					
		}
	}
	private void askForInput(){
		if(guys.elementAt(turn).suit !=Suit.Cleric){
			buttons.elementAt(0).name = "Move";
			buttons.elementAt(1).name = "Attack";
			buttons.elementAt(2).name = "Advance Turn";
		}
		else{
			buttons.elementAt(0).name = "Move";
			buttons.elementAt(1).name = "Attack/Heal";
			buttons.elementAt(2).name = "Advance Turn";
		}


	}
	private void clearGuySelection (){for (int i = 0; i < guys.size(); i++ )	{guys.elementAt(i).isTargeted  = false;}}
	private void clearTileSelection(){for (int i = 0; i < tiles.size(); i++ )	{tiles.elementAt(i).isSelected = false;}}
	private void advanceTurn(){
		guys.elementAt(turn).isTurn = false;guys.elementAt(turn).hasActed=false; guys.elementAt(turn).hasMoved=false; 

		if(turn == guys.size()-1) turn = 0;
		else turn++; 

		while (guys.elementAt(turn).currentDice<1) turn++;
		needInput = true;	needTarget = true;	selectedTile = null;	targetedGuy = null; 
		guys.elementAt(turn).isTurn = true;
		guys.elementAt(turn).movesLeft=guys.elementAt(turn).currentDice;
		askForInput();
		clearGuySelection(); clearTileSelection();	
	}
	private void act(int i){
		boolean fireballGo=false;
		if(i==0 && selectedTile!=null)//move
		{
			if (!selectedTile.isOccupied&&guys.elementAt(turn).movesLeft > 0 && (Math.abs(selectedTile.x-guys.elementAt(turn).x)<=1)&&(Math.abs(selectedTile.y-guys.elementAt(turn).y)<=1&&!guys.elementAt(turn).hasMoved))
			{	
				int tileAt=guys.elementAt(turn).y*gameBoardWidth+guys.elementAt(turn).x;
				guys.elementAt(turn).move(selectedTile);
				tiles.elementAt(tileAt).isOccupied=false;
				selectedTile.isOccupied=true;
				guys.elementAt(turn).movesLeft--;
				if (guys.elementAt(turn).movesLeft ==0){ askForInput(); guys.elementAt(turn).hasMoved=true;}

			}	 
			if (guys.elementAt(turn).movesLeft==0){				
				guys.elementAt(turn).hasMoved=true;
				guys.elementAt(turn).movesLeft = guys.elementAt(turn).speed;
				if(guys.elementAt(turn).suit==Suit.Fighter)needInput = true;
			}

		}	
		if((i==1&&targetedGuy!=null&&guys.elementAt(turn).hasActed==false)
				||(i==1&&guys.elementAt(turn).suit==Suit.Mage&&selectedTile!=null&&guys.elementAt(turn).hasActed==false)
				)//attack
		{
			if(guys.elementAt(turn).speed!=guys.elementAt(turn).movesLeft)guys.elementAt(turn).hasMoved=true;
			update(); say("update"); 
			if ((guys.elementAt(turn).suit==Suit.Fighter 	|| guys.elementAt(turn).suit ==Suit.Cleric && targetedGuy!=null) //if fighter or cleric
					&& (Math.abs(targetedGuy.x-guys.elementAt(turn).x)<=1)
					&& (Math.abs(targetedGuy.y-guys.elementAt(turn).y)<=1)
					&& targetedGuy.team != guys.elementAt(turn).team
					)
			{
				if(guys.elementAt(turn).attack(targetedGuy))say("hit");else say("miss");
				guys.elementAt(turn).hasActed=true;
			}
			if (guys.elementAt(turn).suit==Suit.Archer && (targetedGuy!=null) && guys.elementAt(turn).hasActed==false && targetedGuy.team != guys.elementAt(turn).team )
			{
				if(isInRange(targetedGuy.x,targetedGuy.y, guys.elementAt(turn), 7)
						&&checkLOS(targetedGuy.x,targetedGuy.y, guys.elementAt(turn)))
				{
					checkForAttackOfOpportunity(guys.elementAt(turn));
					if(guys.elementAt(turn).attack(targetedGuy))say("hit");else say ("miss");
					guys.elementAt(turn).hasActed=true;				
				}
				else
				{
					needInput = true;
					needTarget = true;
				}
				if(!isInRange(targetedGuy.x,targetedGuy.y, guys.elementAt(turn), 7))
					say ("not in range");	
			}

			if (guys.elementAt(turn).suit==Suit.Mage && (targetedGuy!=null || selectedTile!=null) && guys.elementAt(turn).hasActed==false)
			{
				if(targetedGuy!=null&&!isInRange(targetedGuy.x,targetedGuy.y, guys.elementAt(turn), 5))say("not in range");
				if(selectedTile!=null&&!isInRange(selectedTile.x,selectedTile.y, guys.elementAt(turn), 5))say("not in range");

				say("try fb");
				if(targetedGuy!=null && isInRange(targetedGuy.x,targetedGuy.y, guys.elementAt(turn), 5)
						&&checkLOS(targetedGuy.x,targetedGuy.y, guys.elementAt(turn)))fireballGo=true;
				if(selectedTile!=null&& isInRange(selectedTile.x,selectedTile.y, guys.elementAt(turn), 5)
						&&checkLOS(selectedTile.x,selectedTile.y, guys.elementAt(turn)))fireballGo=true;
			}
			if (fireballGo)
			{
				say("fireball");				
				checkForAttackOfOpportunity(guys.elementAt(turn));
				int fireballX=0,fireballY=0;
				if (targetedGuy!=null){ //if targeting a guy with fireball
					fireballX = targetedGuy.x;
					fireballY = targetedGuy.y;
				}
				if (selectedTile!=null) //if targeting a tile with fireball
				{
					fireballX = selectedTile.x;
					fireballY = selectedTile.y;
				}

				for (int j = 0; j < guys.size(); j++)
				{
					if(Math.abs(guys.elementAt(j).x-fireballX)<=1 && Math.abs(guys.elementAt(j).y-fireballY)<=1)
					{
						guys.elementAt(turn).attack(guys.elementAt(j));
					}
				}
				guys.elementAt(turn).hasActed=true;
			}
			else 
			{
				needInput = true;
				needTarget = true;
			}

			if (guys.elementAt(turn).suit==Suit.Cleric 	&& (targetedGuy!=null)&&guys.elementAt(turn).hasActed==false)
			{
				if(Math.pow((Math.pow((targetedGuy.x-guys.elementAt(turn).x),2)+Math.pow(targetedGuy.y-guys.elementAt(turn).y,2)),.5)<=5)
					//in range to heal
					if(targetedGuy.currentDice!=targetedGuy.maxDice)	
					{
						if( rand.nextInt()*6+1>4)
							targetedGuy.currentDice++;
						guys.elementAt(turn).hasActed=true;
					}
					else 
					{	
						needInput = true;
						needTarget = true;
					}	
			}
		}

		if(i==1)
		{	int cd=0, nd=0;
		if (guys.elementAt(turn).suit==Suit.Cleric 	&& (targetedGuy!=null)&&guys.elementAt(turn).hasActed==false)
		{
			checkForAttackOfOpportunity(guys.elementAt(turn));
			if(isInRange(targetedGuy.x,targetedGuy.y, guys.elementAt(turn), 5)&&checkLOS(targetedGuy.x,targetedGuy.y, guys.elementAt(turn))
					&&(targetedGuy.team==guys.elementAt(turn).team)&& targetedGuy.currentDice!=targetedGuy.maxDice) 
				//in range to heal and opponent and can be healed and los 
				for (i = 0; i<guys.elementAt(turn).currentDice;i++)
				{
					nd = rand.nextInt(6)+1;
					if (nd>cd) cd = nd;
				}
			if (nd>4) {targetedGuy.currentDice++;say ("healed by the power of the tree");}

			else{say("heal failed");	
			}
		}
		else{
			needInput = true;
			needTarget = true;
		}
		}
	}
	private void say(String s){
		Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
	}

	private boolean checkLOS(int tx, int ty, Guy o) {
		int x0 = o.x;
		int y0 = o.y;
		double dx = tx-x0;
		double dy = ty-y0;
		double slope;
		int x, y, increment;
		boolean hasLOS = true;

		if (dx == 0 && dy == 0)
		{
			hasLOS = true;
		} else
		{
			if( Math.abs(dx) > Math.abs(dy) )
			{
				//x is greatest change, so iterate over x and calc y, then check
				slope = (double)dy/(double)dx;
				increment = (int) (dx / Math.abs(dx));
				for(x = x0+increment; (x != tx) && hasLOS; x += increment)	//count until reach end or obstacle
				{
					y = (int) (y0 + slope * x + 0.5);	
					// ints will be cast to doubles, and result rounded down
					int checkTile = x + y*gameBoardWidth;
					tiles.elementAt(checkTile).isPath = true;
					if(tiles.elementAt(checkTile).isOccupied) hasLOS = false;
				}
			} else
			{
				//y is greatest change
				slope = (double)dx/(double)dy;
				increment = (int) (dy / Math.abs(dy));
				for(y = y0+increment; (y != ty) && hasLOS; y += increment)
				{
					x = (int) (x0 + slope * y + 0.5);	
					int checkTile = x + y*gameBoardWidth;
					tiles.elementAt(checkTile).isPath = true;
					if(tiles.elementAt(checkTile).isOccupied) hasLOS = false;
				}

			}
		}

		return hasLOS;

	}

	private boolean losCheck(int targetX,int targetY, Guy actor){
		boolean returnValue=true;
		double distanceX = targetX-actor.x;
		double distanceY = targetY-actor.y;
		double smallerX,yOfSmallerX;
		double smallerY,xOfSmallerY;
		int tileAtCross;
		double my=0;
		double mx=0;
		double x,y,tx,ty;

		if(targetX>actor.x)smallerX 	= actor.x*tileSize+tileSize*.5; else smallerX = targetX*tileSize+tileSize*.5;//confirmed  
		if(targetX>actor.x)yOfSmallerX 	= actor.y*tileSize+tileSize*.5; else yOfSmallerX = targetY*tileSize+tileSize*.5;//confirmed
		if(targetY>actor.y)smallerY 	= actor.y*tileSize+tileSize*.5; else smallerY = targetY*tileSize+tileSize*.5;//confirmed  
		if(targetY>actor.y)xOfSmallerY 	= actor.x*tileSize+tileSize*.5; else xOfSmallerY = targetX*tileSize+tileSize*.5;//confirmed

		int startingTile = (int) ((smallerX-.5*tileSize)/tileSize + ((yOfSmallerX-.5*tileSize)/tileSize)*gameBoardWidth); 
		if (Math.abs(distanceX)>0)my = distanceY/distanceX;
		if (Math.abs(distanceY)>0)mx = distanceX/distanceY;
		if(mx>0&&mx!=1){ //*

			for (int i = 0; i<Math.abs(distanceY);i++)
			{
				x = smallerX + tileSize*.5*mx +i*mx*tileSize;
				y = yOfSmallerX + tileSize*.5 + i*tileSize;
				tx = x/tileSize;
				ty = y/tileSize;
				tileAtCross=(int) (tx+ty*gameBoardWidth);
				tiles.elementAt(tileAtCross).isPath=true;
				tiles.elementAt(tileAtCross-gameBoardWidth).isPath=true;
			}
		}

		if(mx<0&&mx!=-1){ //*

			for (int i = 0; i<Math.abs(distanceY);i++)
			{
				x = smallerX - tileSize*.5*mx -i*mx*tileSize;
				y = yOfSmallerX - tileSize*.5 - i*tileSize;
				tx = x/tileSize;
				ty = y/tileSize;
				tileAtCross=(int) (tx+ty*gameBoardWidth);
				tiles.elementAt(tileAtCross).isPath=true;
				tiles.elementAt(tileAtCross-gameBoardWidth).isPath=true;	
			}
		}

		if(my>0&&my!=1){//*

			for (int i = 0; i<Math.abs(distanceX);i++)
			{
				y = smallerY + tileSize*.5*my +i*my*tileSize;
				x = xOfSmallerY + tileSize*.5 + i*tileSize;
				tx = x/tileSize;
				ty = y/tileSize;
				tileAtCross=(int)tx+(int)ty*gameBoardWidth;
				tiles.elementAt(tileAtCross).isPath=true;
				tiles.elementAt(tileAtCross-1).isPath=true;		
			}
		}

		if(my<0&&my!=-1){//*

			for (int i = 0; i<Math.abs(distanceX);i++)
			{
				y = smallerY - tileSize*.5*my -i*my*tileSize;
				x = xOfSmallerY - tileSize*.5 - i*tileSize;
				tx = x/tileSize;
				ty = y/tileSize;
				tileAtCross=(int)tx+(int)ty*gameBoardWidth;
				tiles.elementAt(tileAtCross).isPath=true;
				tiles.elementAt(tileAtCross-1).isPath=true;
			}
		}
		if(mx == 1) //diagonal down *
		{
			for(int i = 0;i<Math.abs(distanceX)-1;i++)
			{
				if(actor.x>targetX)tiles.elementAt(startingTile+i+1+(i+1)*gameBoardWidth).isPath=true;
				else tiles.elementAt(startingTile + (i+1) + (i+1)*gameBoardWidth).isPath=true;
			}
		}

		if(mx==-1)for(int i = 0;i<Math.abs(distanceX)-1;i++){//diagonal up *
			if(actor.x>targetX)	tiles.elementAt(startingTile + (i+1) - (i+1)*gameBoardWidth).isPath=true;
			else tiles.elementAt(startingTile + (i+1) - (i+1)*gameBoardWidth).isPath=true;
		}

		if(distanceY ==0)for(int i = 0;i<Math.abs(distanceX);i++)//*
		{
			if (actor.x>targetX)tiles.elementAt(startingTile + i).isPath=true;
			else tiles.elementAt(startingTile+1 + i).isPath=true;
		}

		if(distanceX==0)for(int i = 0;i<Math.abs(distanceY)-1;i++)//*
		{
			if(actor.y<targetY)
				tiles.elementAt(startingTile - (i+1)*gameBoardWidth).isPath=true;
			else tiles.elementAt(startingTile + (i+1)*gameBoardWidth).isPath=true;

		}
		int actorTile = actor.x+actor.y*gameBoardWidth;
		tiles.elementAt(actorTile).isPath =false;
		tiles.elementAt(targetX+targetY*gameBoardWidth).isPath=false;
		for(int i = 0; i < gameBoardWidth*gameBoardHeight;i++){
			//if (tiles.elementAt(i).isPath&&tiles.elementAt(i).isOccupied)returnValue = false;
			//tiles.elementAt(i).isPath=false;
		}
		if (!returnValue)say("no los");
		return returnValue;
	}
	private boolean isInRange(int targetX,int targetY,Guy actor, int actorsRange){
		boolean returnValue = true;
		int distanceX = targetX-actor.x;
		int distanceY = targetY-actor.y;
		if (Math.pow(distanceX*distanceX+distanceY*distanceY,.5)>actorsRange)returnValue = false;;
		return returnValue;		
	}
	private void update(){
		int guysAlive=0;
		for (int i = 0; i<guys.size();i++)
			if(guys.elementAt(i).currentDice>0)guysAlive++;
		if(guys.elementAt(turn).suit!=Suit.Fighter && (guys.elementAt(turn).hasActed || guys.elementAt(turn).hasMoved))
		{//if turn over advance turn ... not fighter
			if (guysAlive>0)advanceTurn();
		}

		if (guys.elementAt(turn).suit==Suit.Fighter && guys.elementAt(turn).hasActed && guys.elementAt(turn).hasMoved)
		{//if turn over advance turn ... fighter
			if (guysAlive>0)advanceTurn();
		}
		if (guys.elementAt(turn ).suit==Suit.Fighter 	&& ((guys.elementAt(turn).hasActed && !guys.elementAt(turn).hasMoved)
				|| (!guys.elementAt(turn).hasActed && guys.elementAt(turn).hasMoved))
				)
		{//if fighter has not done both actions
			clearTileSelection();
			clearGuySelection();
		}
	}
	public void checkForAttackOfOpportunity(Guy actor){
		for (int i = 0; i < guys.size(); i++){
			if(isInRange(actor.x,actor.y,guys.elementAt(i),1)
					&& actor.team != guys.elementAt(i).team)
			{guys.elementAt(i).attack(actor);
			say("aoo");}
		}
	}
	protected void onDraw(Canvas canvas)
	{
		// Draw the components		   

		for (int i = 0; i < tiles.size(); i++ ){ 
			tiles.elementAt(i).draw(canvas);
		}
		for (int i = 0; i < guys.size(); i++ ){ 
			guys.elementAt(i).draw(canvas);
		}
		for (int i = 0; i < buttons.size(); i++){
			buttons.elementAt(i).draw(canvas);	
		}
		try {  
			Thread.sleep(300);  
		} catch (InterruptedException e) { }      
		invalidate();  // Force a re-draw
	}

	//enum Suit {Clubs, Diamonds, Hearts, Spades};
}