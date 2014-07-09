package com.spixy.muni.is;

import com.spixy.muni.is.R;
import com.spixy.muni.is.MyService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity
{
    private Intent mServiceIntent;
    
    private void SaveSettings()
    {
	    EditText et = (EditText) findViewById(R.id.editText1);
	    SeekBar bar = (SeekBar) findViewById(R.id.seekBar1);
	    CheckBox check = (CheckBox) findViewById(R.id.checkBox1);
	    Switch sw1 = (Switch) findViewById(R.id.switch1);
	    Switch sw2 = (Switch) findViewById(R.id.switch2);
	    Switch sw3 = (Switch) findViewById(R.id.switch3);
	    Switch sw4 = (Switch) findViewById(R.id.switch4);	    
	    
    	SharedPreferences settings = getSharedPreferences("UserInfo", 0);
    	SharedPreferences.Editor editor = settings.edit();
    	
    	editor.putString("ID", et.getText().toString());
    	editor.putInt("Frequency", bar.getProgress());
    	editor.putBoolean("Autostart", check.isChecked());  	
    	editor.putBoolean("Switch 1", sw1.isChecked());
    	editor.putBoolean("Switch 2", sw2.isChecked());
    	editor.putBoolean("Switch 3", sw3.isChecked());
    	editor.putBoolean("Switch 4", sw4.isChecked());
    	
    	editor.commit();    	
    	et = null;
    	bar = null;
    	sw1 = null;
    	sw2 = null;
    	sw3 = null;
    	sw4 = null;
    	settings = null;
    	editor = null;
    }
    
    private void LoadSettings()
    {
	    EditText et = (EditText) findViewById(R.id.editText1);
	    SeekBar bar = (SeekBar) findViewById(R.id.seekBar1);
	    CheckBox check = (CheckBox) findViewById(R.id.checkBox1);
	    Switch sw1 = (Switch) findViewById(R.id.switch1);
	    Switch sw2 = (Switch) findViewById(R.id.switch2);
	    Switch sw3 = (Switch) findViewById(R.id.switch3);
	    Switch sw4 = (Switch) findViewById(R.id.switch4);
	    
    	SharedPreferences settings = getSharedPreferences("UserInfo", 0);
    	
    	et.setText(settings.getString("ID", "9rrv8k4cq5").toString());
    	bar.setProgress(settings.getInt("Frequency", 1));
    	check.setChecked(settings.getBoolean("Autostart", false));
    	sw1.setChecked(settings.getBoolean("Switch 1", false));
    	sw2.setChecked(settings.getBoolean("Switch 2", false));
    	sw3.setChecked(settings.getBoolean("Switch 3", false));
    	sw4.setChecked(settings.getBoolean("Switch 4", false));
    	
    	et = null;
    	bar = null;
    	sw1 = null;
    	sw2 = null;
    	sw3 = null;
    	sw4 = null;
    	settings = null;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LoadSettings();
		
		mServiceIntent = new Intent(this, MyService.class);

	    CheckBox check = (CheckBox) findViewById(R.id.checkBox1);
	    check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

		       @Override
		       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		    	   SaveSettings();
		       }
		   }
		);
		
	    SeekBar bar = (SeekBar) findViewById(R.id.seekBar1);
        
        bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private int progress = 1;

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				this.progress = progress;
				TextView tv = (TextView) findViewById(R.id.textView3);
				
				switch (progress)
				{
					case 0: tv.setText(R.string.text_30mins); break;
					case 1: tv.setText(R.string.text_1hour); break;
					case 2: tv.setText(R.string.text_2hours); break;
					case 3: tv.setText(R.string.text_3hours); break;
					case 4: tv.setText(R.string.text_6hours); break;
					case 5: tv.setText(R.string.text_12hours); break;
					case 6: tv.setText(R.string.text_1day); break;
				}
				
				tv = null;
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			    EditText et = (EditText) findViewById(R.id.editText1);
			    
				stopService(mServiceIntent);
		        mServiceIntent.putExtra("ID", et.getText().toString());
		        mServiceIntent.putExtra("timer", progress);
		        startService(mServiceIntent);
		        
		        SaveSettings();
		        et = null;
			}
		});     
        
        bar = null;
        check = null;
        
        Start();
	}
	
	private void Start()
	{
	    SeekBar bar = (SeekBar) findViewById(R.id.seekBar1);
		EditText et = (EditText) findViewById(R.id.editText1);
	    Switch sw1 = (Switch) findViewById(R.id.switch1);
	    Switch sw2 = (Switch) findViewById(R.id.switch2);
	    Switch sw3 = (Switch) findViewById(R.id.switch3);
	    Switch sw4 = (Switch) findViewById(R.id.switch4);
	    
    	mServiceIntent.putExtra("ID", et.getText().toString());
        mServiceIntent.putExtra("mails", sw1.isChecked());
    	mServiceIntent.putExtra("grades", sw2.isChecked());
        mServiceIntent.putExtra("notepad", sw3.isChecked());
        mServiceIntent.putExtra("exams", sw4.isChecked());
        mServiceIntent.putExtra("timer", bar.getProgress());
    	startService(mServiceIntent);

        bar = null;
    	et = null;
    	sw1 = null;
    	sw2 = null;
    	sw3 = null;
    	sw4 = null;
	}

    public void onTrimMemory(int level)
    {
    	super.onTrimMemory(level);
    	
    	switch (level)
    	{
    		//case TRIM_MEMORY_UI_HIDDEN:
    		//case TRIM_MEMORY_RUNNING_MODERATE:
			// ??
    	
    		case TRIM_MEMORY_RUNNING_CRITICAL:
    			SaveSettings();
    		    stopService(mServiceIntent);
    			break;
    	}
    }
	
	public void onToggleClicked1(View view)
	{
	    boolean on = ((Switch) view).isChecked();
	    EditText et = (EditText) findViewById(R.id.editText1);
	    
	    stopService(mServiceIntent);
        mServiceIntent.putExtra("ID", et.getText().toString());
        mServiceIntent.putExtra("mails", on);
        startService(mServiceIntent);
        
        SaveSettings();
        et = null;
	}
	
	public void onToggleClicked2(View view)
	{
	    boolean on = ((Switch) view).isChecked();
	    EditText et = (EditText) findViewById(R.id.editText1);
	    
	    stopService(mServiceIntent);
        mServiceIntent.putExtra("ID", et.getText().toString());
        mServiceIntent.putExtra("grades", on);
        startService(mServiceIntent);
        
        SaveSettings();
        et = null;
	}
	
	public void onToggleClicked3(View view)
	{
	    boolean on = ((Switch) view).isChecked();
	    EditText et = (EditText) findViewById(R.id.editText1);
	    
	    stopService(mServiceIntent);
        mServiceIntent.putExtra("ID", et.getText().toString());
        mServiceIntent.putExtra("notepad", on);
        startService(mServiceIntent);
        
        SaveSettings();
        et = null;
	}
	
	public void onToggleClicked4(View view)
	{
	    boolean on = ((Switch) view).isChecked();
	    EditText et = (EditText) findViewById(R.id.editText1);
	    
	    stopService(mServiceIntent);
        mServiceIntent.putExtra("ID", et.getText().toString());
        mServiceIntent.putExtra("exams", on);
        startService(mServiceIntent);
        
        SaveSettings();
        et = null;
	}
}
