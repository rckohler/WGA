package com.example.dsix;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class DsixActivity extends Activity {
	 public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      View gameView = new GameView(this);
	      setContentView(gameView);
	    }
	   
	   
	}