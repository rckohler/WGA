package com.example.simpleanimation;

import android.os.Bundle;
import android.app.Activity;

public class SimpleAnimation extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AnimationView v = new AnimationView(this);
		setContentView(v);
	}
}
