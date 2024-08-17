package com.app.mhcsn_cp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.app.mhcsn_cp.util.DATA;

public class HeratBeatActivity extends Activity implements SensorEventListener{
	Sensor mHeartRateSensor;
    SensorManager mSensorManager;
    
    
    @Override
    protected void onResume() {
        super.onResume();
        //Register the listener
        if (mSensorManager != null){
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else {
			DATA.print("--sensor is null");
		}
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_herat_beat);
		
		
		//Sensor and sensor manager
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    //    DATA.print("--senson"+mHeartRateSensor.getName());
	}
	
	
	 @Override
     protected void onPause() {
         super.onPause();
         //Unregister the listener
         if (mSensorManager!=null)
             mSensorManager.unregisterListener(this);
     }

     @Override
     public void onSensorChanged(SensorEvent event) {
         //Update your data. 
         if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {            
             int heartrate = (int) event.values[0];
             
             DATA.print("--heartrate"+heartrate);
             }
         }
   

     @Override
     public void onAccuracyChanged(Sensor sensor, int accuracy) {

     }

	 
}
