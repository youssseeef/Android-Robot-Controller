package ml.joesalama.robotroller.robotroller;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import static java.lang.Thread.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Robotroller_main extends AppCompatActivity implements SensorEventListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

    private static final boolean AUTO_HIDE = true;
    public final String TAG = "Main";
    private TextView debug;
    private Bluetooth bt;
    private TextView mTextView;
    private SeekBar mSeekBar;
    private Button mButton;
    private MultiStateToggleButton mMultiStageToggleButton;
    private TextView mStatusTextView;
    private Button mParkButton;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private String currentState = "aoasjkdpoasi";
    private TriggerEventListener mTriggerEventListener;
    private final static String STOP_STATE = "S";
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.

        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    public Robotroller_main() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_robotroller_main);
        mStatusTextView = (TextView) findViewById(R.id.statusTextView);
        mVisible = true;
        debug = (TextView) findViewById(R.id.accelerometer_textview);
        mTextView = (TextView) findViewById(R.id.messageTextView);
        mButton = (Button) findViewById(R.id.sendButton);
        mMultiStageToggleButton = (MultiStateToggleButton) findViewById(R.id.mstb_multi_id);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        mParkButton = (Button) findViewById(R.id.park_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.sendMessage(mTextView.getText().toString());
            }
        });
        bt = new Bluetooth(this, mHandler);
        connectService();
        findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectService();
            }
        });
        mMultiStageToggleButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                mStatusTextView.setText("TBpos: " + value + " SB: " +getSeekBarSpeed());
                switch (value) {
                    case 0:
                        switch(getSeekBarSpeed()){
                            case 0:
                                bt.sendMessage("a");
                                break;
                            case 1:
                                bt.sendMessage("b");
                                break;
                            case 2:
                                bt.sendMessage("c");
                                break;
                            case 3:
                                bt.sendMessage("d");
                                break;
                            case 4:
                                bt.sendMessage("e");
                                break;
                        }
                        break;
                    case 1:
                        bt.sendMessage(STOP_STATE);
                        break;
                    case 2:
                        switch(getSeekBarSpeed()){
                            case 0:
                                bt.sendMessage("A");
                                break;
                            case 1:
                                bt.sendMessage("B");
                                break;
                            case 2:
                                bt.sendMessage("C");
                                break;
                            case 3:
                                bt.sendMessage("D");
                                break;
                            case 4:
                                bt.sendMessage("E");
                                break;
                        }
                        break;
                }

            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStatusTextView.setText("TBpos: " + getThreeWayTogglePosition() + " SB: " +progress);

                switch (progress){
                    case 0:
                        switch (getThreeWayTogglePosition()){
                            case 0:
                                bt.sendMessage("a");
                                break;
                            case 1:
                                //bt.sendMessage(STOP_STATE);
                                break;
                            case 2:
                                bt.sendMessage("A");
                                break;
                        }
                        break;
                    case 1:
                        switch (getThreeWayTogglePosition()){
                            case 0:
                                bt.sendMessage("b");
                                break;
                            case 1:
                               // bt.sendMessage(STOP_STATE);
                                break;
                            case 2:
                                bt.sendMessage("B");
                                break;
                        }
                        break;
                    case 2:
                        switch (getThreeWayTogglePosition()){
                            case 0:
                                bt.sendMessage("c");
                                break;
                            case 1:
                              //  bt.sendMessage("s");
                                break;
                            case 2:
                                bt.sendMessage("C");
                                break;
                        }
                        break;
                    case 3:
                        switch (getThreeWayTogglePosition()){
                            case 0:
                                bt.sendMessage("d");
                                break;
                            case 1:
                              //  bt.sendMessage(STOP_STATE);
                                break;
                            case 2:
                                bt.sendMessage("D");
                                break;
                        }
                        break;
                    case 4:
                        switch (getThreeWayTogglePosition()){
                            case 0:
                                bt.sendMessage("e");
                                break;
                            case 1:
                              //  bt.sendMessage(STOP_STATE);
                                break;
                            case 2:
                                bt.sendMessage("E");
                                break;
                        }
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMultiStageToggleButton.setValue(1);
        mParkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.sendMessage("P");
                mStatusTextView.setText("Parking Mode");
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(Robotroller_main.this,mSensor,100000);

        final Handler h2 = new Handler();
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                if(mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0){
                    mSensorManager.registerListener(Robotroller_main.this,mSensor,100000);
                    Log.d("EDITING","REGISTERING");
                }else {
                    try{
                        sleep(10);
                    }catch (InterruptedException ex){

                    }
                    mSensorManager.unregisterListener(Robotroller_main.this);
                    Log.d("EDITING","UNREGISTERING");
                    Log.d("TESTT",mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() +"");
                    try {
                        sleep(1000);
                    }catch (InterruptedException ex){

                    }
                    mSensorManager.registerListener(Robotroller_main.this,mSensor,100000);
                }

                h2.postDelayed(this,500);
            }
        };

        h2.post(r);

    }

    public void connectService() {
        try {
            debug.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-05");
                Log.d(TAG, "Btservice started - listening");
                debug.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                debug.setText("Bluetooth Not enabled");
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to start bt ", e);
            debug.setText("Unable to connect " + e);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME " + msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST " + msg);
                    break;
            }
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    private int getSeekBarSpeed(){
        return mSeekBar.getProgress();
    }
    private int getThreeWayTogglePosition(){
        return mMultiStageToggleButton.getValue();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        mStatusTextView.setText("Accelerometer: " +(int)event.values[0] +" TBPOS: " + getThreeWayTogglePosition()+" SB: "+getSeekBarSpeed());
        switch((int)event.values[0]){
            case 0:
            case 1:
            case 2:
            case 3:
                if(currentState != "StraightLine") {
                    currentState = "StraightLine";
                    StraightLineFunction();

                }
                break;
            case 4:
            case 5:
            case 6:
                //speeds not needed here since I'll always use the initial speed to drift..
                if(currentState != "leftSmallSpeed") {
                    currentState = "leftSmallSpeed";
                    switch (getThreeWayTogglePosition()) {
                        case 0:
                            bt.sendMessage("l");
                            break;
                        case 1:
                           // bt.sendMessage(STOP_STATE);
                            break;
                        case 2:
                            bt.sendMessage("y");
                            break;
                    }
                }
                break;
            case 7:
            case 8:
            case 9:
                if(currentState != "leftHighSpeed") {
                    currentState = "leftHighSpeed";
                    switch (getThreeWayTogglePosition()) {
                        case 0:
                            bt.sendMessage("L");
                            break;
                        case 1:
                           // bt.sendMessage(STOP_STATE);
                            break;
                        case 2:
                            bt.sendMessage("Y");
                            break;
                    }
                }
                break;
            case -1:
            case -2:
            case -3:
                if(currentState != "StraightLine") {
                    currentState  = "StraightLine";
                    StraightLineFunction();
                }
                break;
            case -4:
            case -5:
            case -6:
                if(currentState != "backwardRightSmallSpeed") {
                    currentState  = "backwardRightSmallSpeed";
                    switch (getThreeWayTogglePosition()) {
                        case 0:
                            bt.sendMessage("r");
                            break;
                        case 1:
                           // bt.sendMessage(STOP_STATE);
                            break;
                        case 2:
                            bt.sendMessage("z");
                            break;
                    }
                }
                break;
            case -7:
            case -8:
            case -9:
                if(currentState != "backwardRightHighSpeed") {
                    currentState = "backwardRightHighSpeed";
                    switch (getThreeWayTogglePosition()) {
                        case 0:
                            bt.sendMessage("R");
                            break;
                        case 1:
                            //bt.sendMessage(STOP_STATE);
                            break;
                        case 2:
                            bt.sendMessage("Z");
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void StraightLineFunction(){
        switch (getSeekBarSpeed()){
            case 0:
                switch (getThreeWayTogglePosition()){
                    case 0:
                        bt.sendMessage("a");
                        break;
                    case 1:
                      //  bt.sendMessage(STOP_STATE);
                        break;
                    case 2:
                        bt.sendMessage("A");
                        break;
                }
                break;
            case 1:
                switch (getThreeWayTogglePosition()){
                    case 0:
                        bt.sendMessage("b");
                        break;
                    case 1:
                       // bt.sendMessage(STOP_STATE);
                        break;
                    case 2:
                        bt.sendMessage("B");
                        break;
                }
                break;
            case 2:
                switch (getThreeWayTogglePosition()){
                    case 0:
                        bt.sendMessage("c");
                        break;
                    case 1:
                       // bt.sendMessage(STOP_STATE);
                        break;
                    case 2:
                        bt.sendMessage("C");
                        break;
                }
                break;
            case 3:
                switch (getThreeWayTogglePosition()){
                    case 0:
                        bt.sendMessage("d");
                        break;
                    case 1:
                        //bt.sendMessage(STOP_STATE);
                        break;
                    case 2:
                        bt.sendMessage("D");
                        break;
                }
                break;
            case 4:
                switch (getThreeWayTogglePosition()){
                    case 0:
                        bt.sendMessage("e");
                        break;
                    case 1:
                        //bt.sendMessage(STOP_STATE);
                        break;
                    case 2:
                        bt.sendMessage("E");
                        break;
                }
                break;
        }
    }
}
