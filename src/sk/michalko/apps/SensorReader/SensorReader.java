package sk.michalko.apps.SensorReader;

import sk.michalko.apps.SensorReader.egl.EGLView;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SensorReader extends Activity implements SensorEventListener {
	
    final String TAG = "SensorReader";
    SensorManager sensorManager;
	TextView valuesAccelerometerView = null;
    TextView valuesOrientationView = null;
    TextView valuesTemperatureView = null;
    TextView valuesMagneticView = null;
    TextView textSensorListView = null;
    ProgressBar progressbarOX = null;
    ProgressBar progressbarOY = null;
    ProgressBar progressbarOZ = null;
    
    int orientationXMax = 0;
    int orientationYMax = 0;
    int orientationZMax = 0;
	/** The OpenGL View */
	private GLSurfaceView glSurface;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		glSurface = (GLSurfaceView) findViewById(R.id.eglview);
		//Set our own Renderer
		glSurface.setRenderer(new EGLView());
		
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        valuesAccelerometerView = (TextView) findViewById(R.id.textviewAccelerometer);
        valuesOrientationView = (TextView) findViewById(R.id.textviewCompass);
        valuesTemperatureView = (TextView) findViewById(R.id.textviewTemperature);
        valuesMagneticView = (TextView) findViewById(R.id.textviewMagnetometer);
        textSensorListView = (TextView) findViewById(R.id.textviewSensors);
        progressbarOX = (ProgressBar) findViewById(R.id.progressbarOX);
        progressbarOY = (ProgressBar) findViewById(R.id.progressbarOY);
        progressbarOZ = (ProgressBar) findViewById(R.id.progressbarOZ);

    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	Log.d(TAG,"onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
		
	}

	public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
        	
            Log.d(TAG, "onSensorChanged: " + event.sensor + ", x: " + event.values[0] + ", y: " + event.values[1] + ", z: " + event.values[2]);
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            	valuesOrientationView.setText("Orientation X,Y,Z: " + event.values[0] 
            	    + "," + event.values[1] + "," + event.values[2]);
            	int intValue = new Float(event.values[0]).intValue();
            	if (intValue > orientationXMax ){
            		orientationXMax = intValue;
            		Log.d(TAG,"XMax new max: " + orientationXMax);
            	}
        		progressbarOX.setProgress(intValue);
            	
            	intValue = new Float(event.values[1]).intValue();
            	if (intValue > orientationYMax ){
            		orientationYMax = intValue;
            		Log.d(TAG,"YMax new max: " + orientationYMax);
            	}
        		progressbarOY.setProgress(intValue);
            	
            	intValue = new Float(event.values[2]).intValue();
            	if (intValue > orientationZMax ){
            		orientationZMax = intValue;
            		Log.d(TAG,"ZMax new max: " + orientationZMax);
            	}
        		progressbarOZ.setProgress(intValue);
            	
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            	valuesAccelerometerView.setText("Accel X,Y,Z: " + event.values[0]
                	+ "," + event.values[1] + "," + event.values[2]);
            }            
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD ) {
            	valuesMagneticView.setText("Magnetometer X,Y,Z: " + event.values[0]
                	+ "," + event.values[1] + "," + event.values[2]);
            }            
            if (event.sensor.getType() == Sensor.TYPE_TEMPERATURE) {
            	valuesTemperatureView.setText("Temperature : " + event.values[0]);
            }            
        }
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		glSurface.onResume();
		
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
		String sensors = "Sensors: ";
		
		for (Sensor sensor : sensorList) {
			sensors = sensors + sensor.getName() + "#" + sensor.getType() + ",\n";
	        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		textSensorListView.setText(sensors);
	}

	@Override
	protected void onStop() {
		sensorManager.unregisterListener(this);
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		glSurface.onPause();
	}

}