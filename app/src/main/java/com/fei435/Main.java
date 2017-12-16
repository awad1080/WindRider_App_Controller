package com.fei435;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fei435.Constant.CommandArray;

import static com.fei435.Constant.COMM_CAMERA_OFF;
import static com.fei435.Constant.COMM_CAMERA_ON;
import static com.fei435.Constant.COMM_SERVO;
import static com.fei435.Constant.COMM_SUCTION_OFF;
import static com.fei435.Constant.COMM_SUCTION_ON;
import static com.fei435.Constant.DEFAULT_VALUE_CAMERA_URL;

public class Main extends Activity implements
        com.fei435.SeekBar.OnSeekBarChangeListener,android.widget.SeekBar.OnSeekBarChangeListener  //horizontal and vertical respectively SeekBar
{
    private final int MIN_GEAR_STEP = 5;
    private final int MAX_GEAR_VALUE = 180;
    private final int INIT_GEAR_VALUE = 50;

    private final int MIN_GEAR_STEP_1 = 1;
    private final int MAX_GEAR_VALUE_1 = 100;
    private final int INIT_GEAR_VALUE_1 = 50;

    private final int MIN_GEAR_STEP_2 = 1;
    private final int MAX_GEAR_VALUE_2 = 100;
    private final int INIT_GEAR_VALUE_2 = 50;

    private final int Vac_Min_Value=1;
    private final int Vac_Int_Value= 20;
    private final int Vac_Max_Value=100;

    private final int Water_Min_Value=1;
    private final int Water_Int_Value=50;
    private final int Water_Max_Value=100;

    private final int Light_Min_Value=1;
    private final int Light_Int_Value=50;
    private final int Light_Max_Value=100;

    private final int WARNING_ICON_OFF_DURATION_MSEC = 600;
    private final int WARNING_ICON_ON_DURATION_MSEC = 800;

    public static int flagsuction = 0;
    public static int flagcamera = 0;
    public static int flagspeed = 0;
    public static int flagLED = 0;
    public static int flagVac=0;

    private ImageButton ForWard;  //button class, representing a button
    private ImageButton BackWard;
    private ImageButton TurnLeft;
    private ImageButton TurnRight;
    private ImageButton TakePicture;

    //insert the camera control button
    private ImageButton CameraUp;  //button class, representing the button
    private ImageButton CameraDown;
    private ImageButton CameraLeft;
    private ImageButton CameraRight;
    private ImageButton CameraSwitch;

    //insert my button
    private ImageButton Servo;
    private ImageButton Suction;
    private ImageButton Light;

    private ImageView mAnimIndicator;
    private boolean bAnimationEnabled = true;
    private Drawable mWarningIcon;
    private TextView mLogText;
    private boolean bGravityDetectOn = false;

    private Drawable ForWardon;
    private Drawable ForWardoff;
    private Drawable BackWardon;
    private Drawable BackWardoff;
    private Drawable TurnLefton;
    private Drawable TurnLeftoff;
    private Drawable TurnRighton;
    private Drawable TurnRightoff;
    private Drawable buttonLenon;
    private Drawable buttonLenoff;

    //insert my button
    private Drawable Servoon;
    private Drawable Servooff;
    private Drawable Suctionon;
    private Drawable Suctionoff;

    private Drawable CameraUpon;
    private Drawable CameraUpoff;
    private Drawable CameraDownon;
    private Drawable CameraDownoff;
    private Drawable CameraLefton;
    private Drawable CameraLeftoff;
    private Drawable CameraRighton;
    private Drawable CameraRightoff;
    private Drawable CameraSwitchon;
    private Drawable CameraSwitchoff;

    private Drawable SpdSettingoff;
    private Drawable SpdSettingon;

    private Drawable Lightsettingon;
    private Drawable Lightsettingoff;

    private Drawable Vacsettingson;
    private Drawable Vacsettingsoff;


    private com.fei435.SeekBar mSeekBar1;
    private com.fei435.SeekBar mSeekBar2;            //longitudinal realization of their own seekbar,this is a progress bar
    private android.widget.SeekBar mSpeedSeekBar1;
    private android.widget.SeekBar mSpeedSeekBar2;   //the system comes with a horizontal seekbar
    private int  mSeekBarValue1 = -1;
    private int  mSeekBarValue2 = -1;
    private int  mSpeedSeekBarValue1 = -1;
    private int  mSpeedSeekBarValue2 = -1;
    private EditText editTextSpeed1;
    private EditText editTextSpeed2;
   ////////////////////////////////////////////////////////////////////////////
    private android.widget.SeekBar VacSeekBar;
    private int VacSeekBar_Value = -1;
    private EditText editTextVac;
    private ImageButton Vac;

    private android.widget.SeekBar WaterSeekBar;
    private int WaterSeekBar_Value = -1;
    private CheckBox Water_Box;
    private EditText editTextWater;

    private android.widget.SeekBar LightSeekBar;
    private int LightSeekBar_Value =-1;
    private EditText editTextLight;

    ///////////////////////////////////////////////////////////////////////////

    private ToggleButton gravityDetectToggle;

    private ImageButton buttonCus1;
    private ImageButton buttonLen;
    private boolean bCaptureOn = false;

    private ImageButton SpdSetting;

    private boolean mQuitFlag = false;
    private boolean bHeartBreakFlag = false;//only for a test by fei435
    private int mHeartBreakCounter = 0;     //car heartbeat packet count
    private int mLastCounter = 0;

    private Vibrator mVibrator= null;
    private SensorManager mSensorMgr = null;
    private Sensor sensor = null;
    private int lastCommand = 0x0;

    private WiFiCarController mWiFiCarControler = null;//network connection thread class
    private Context mContext;
    MjpegView backgroundView = null;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg)
        {
            //Log.i("Handle", "handle internal Message, id=" + msg.what);

            switch (msg.what) {
                case Constant.MSG_ID_ERR_RECEIVE:
                    Log.i("socket", "MSG_ID_ERR_RECEIVE");
                    break;
                case Constant.MSG_ID_CON_READ:
                    byte[] command = (byte[])msg.obj;
                    //Log.i("mLogText","handle response from router: " + command.toString() );
                    handleCallback(command);
                    break;
                case Constant.MSG_ID_ERR_INIT_READ:
                    mLogText.setText("Connecting to WiFiCar failed!");
                    Log.i("mLogText","Connecting to WiFiCar failed!");
                    break;
                case Constant.MSG_ID_CON_SUCCESS:
                    mLogText.setText("Connecting to WiFiCar succeed!");
                    Log.i("mLogText","Connecting to WiFiCar succeed!");
                    //The connection is successful, delay 2 seconds to send speed setting instructions
                    Message msgChangeSpeed1 = new Message();
                    msgChangeSpeed1.what = Constant.MSG_ID_SET_SPEED;
                    msgChangeSpeed1.obj = Constant.COMM_SPEED_VALUE_1;

                    Message msgChangeSpeed2 = new Message();
                    msgChangeSpeed2.what = Constant.MSG_ID_SET_SPEED;
                    msgChangeSpeed2.obj = Constant.COMM_SPEED_VALUE_2;

                    mHandler.sendMessageDelayed(msgChangeSpeed1, 2000);
                    mHandler.sendMessageDelayed(msgChangeSpeed2, 2500);

                    break;
                case Constant.MSG_ID_SET_SPEED:
                    mWiFiCarControler.sendCommand((byte[])msg.obj);
                    break;
                case Constant.MSG_ID_SET_UI_INFO:      //别的类给mLogText显示消息
                    String str = (String)msg.obj;
                    mLogText.setText(str);

//    			Message msgStartCheck = new Message();
//    			msgStartCheck.what = MSG_ID_START_CHECK;
//    			mHandler.sendMessageDelayed(msgStartCheck, 3000);

                    Message msgHB1 = new Message();
                    msgHB1.what = Constant.MSG_ID_HEART_BREAK_RECEIVE;//启动心跳包检测循环
                    //mHandler.sendMessage(msgHB1);

                    Message msgHB2 = new Message();
                    msgHB2.what = Constant.MSG_ID_HEART_BREAK_SEND;//启动心跳包循环发送
                    //mHandler.sendMessage(msgHB2);

                    break;
                case Constant.MSG_ID_ERR_CONN:
                    //mLogText.setText("连接控制地址失败!");
                    //Log.i("mLogText","连接控制地址失败!");
                    mLogText.setText("Establish connection failed!");
                    Log.i("mLogText","Establish connection failed!");
                    break;
                case Constant.MSG_ID_CLEAR_QUIT_FLAG:
                    mQuitFlag = false;
                    break;
                case Constant.MSG_ID_START_CHECK:
                    //mLogText.setText("开始进行自检，请稍等。。。。!!");
                    //Log.i("mLogText","开始进行自检，请稍等。。。。!!");
                    mLogText.setText("Checking Please wait!!");
                    Log.i("mLogText","Checking Please wait!!");
                    //TODO:be ReadyToSendCmd where should I put it？
                    //bReaddyToSendCmd = true;
                    mWiFiCarControler.selfcheck();
                    break;
                case Constant.MSG_ID_HEART_BREAK_RECEIVE:
                    if (mHeartBreakCounter == 0) {
                        bHeartBreakFlag = false;

                    } else if (mHeartBreakCounter > 0) {
                        bHeartBreakFlag = true;
                    } else {
                        //mLogText.setText("心跳包出现异常，已经忽略...");
                        //Log.i("heart","心跳包出现异常，已经忽略...");
                        mLogText.setText("Heartbeat error");
                        Log.i("heart","Heartbeat error");
                    }
                    Log.i("heart", "handle MSG_ID_HEART_BREAK_RECEIVE :flag=" + bHeartBreakFlag);

                    if (mLastCounter == 0 && mHeartBreakCounter > 0) {
                        startIconAnimation();
                    }
                    mLastCounter = mHeartBreakCounter;
                    mHeartBreakCounter = 0;
                    Message msgHB = new Message();
                    msgHB.what = Constant.MSG_ID_HEART_BREAK_RECEIVE;//start the heartbeat packet detection loop
                    mHandler.sendMessageDelayed(msgHB, Constant.HEART_BREAK_CHECK_INTERVAL);
                    break;
                case Constant.MSG_ID_HEART_BREAK_SEND:
                    Message msgSB = new Message();
                    msgSB.what = Constant.MSG_ID_HEART_BREAK_SEND;//loop send heartbeat packets to the router
                    Log.i("heart", "handle MSG_ID_HEART_BREAK_SEND");
                    mWiFiCarControler.sendCommand(Constant.COMM_HEART_BREAK);
                    mHandler.sendMessageDelayed (msgSB, Constant.HEART_BREAK_SEND_INTERVAL);
                    break;
                default :
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {   //each one android App一the function that will be called when started
        super.onCreate(savedInstanceState);
        Log.i("SurfaceStatus","onCreate");
        mContext = this;
        //TODO: after mHandler、mLogText can join mContext
        mWiFiCarControler = new WiFiCarController(mHandler, mLogText, mContext);
        mVibrator = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);
        mSensorMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);    //initialize the sensor
        sensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);    //initiate a gravity sensor

        initSettings(); //initialize the network connection

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//hidden title (application name must be setContentView before, otherwise there will be abnormal）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main); //used to display layout

        //get button
        ForWard= (ImageButton)findViewById(R.id.btnForward);
        TurnLeft= (ImageButton)findViewById(R.id.btnLeft);
        TurnRight=(ImageButton)findViewById(R.id.btnRight);
        BackWard= (ImageButton)findViewById(R.id.btnBack);
        gravityDetectToggle = (ToggleButton)findViewById(R.id.gravityToggleButton);
        ////////////////////////////////////////////////////////////////////////////////////

        Water_Box=(CheckBox)findViewById(R.id.Water_Box);

        SpdSetting=(ImageButton)findViewById(R.id.spdsettingbtn);
        ////////////////////////////////////////////////////////////////////////////////////

        CameraUp = (ImageButton)findViewById(R.id.btnCamUp);
        /*CameraLeft = (ImageButton)findViewById(R.id.btnCamLeft);
        CameraRight = (ImageButton)findViewById(R.id.btnCamRight); */
        CameraDown = (ImageButton)findViewById(R.id.btnCamDown);
        CameraSwitch = (ImageButton)findViewById(R.id.btnCamSwitch);

        //my button
        Servo = (ImageButton)findViewById(R.id.btnServo);
        Suction = (ImageButton)findViewById(R.id.btnStop);
        Light = (ImageButton)findViewById(R.id.Lightbtn);
        Vac= (ImageButton)findViewById(R.id.Vac_setting);

        buttonCus1= (ImageButton)findViewById(R.id.ButtonCus1);
        buttonCus1.setOnClickListener(buttonCus1ClickListener);
        //buttonCus1.setOnLongClickListener(buttonCus1ClickListener2);

        buttonLen= (ImageButton)findViewById(R.id.btnLen);
        buttonLen.setOnClickListener(buttonLenClickListener);
        buttonLen.setLongClickable(true);


        TakePicture = (ImageButton)findViewById(R.id.ButtonTakePic);
        TakePicture.setOnClickListener(buttonTakePicClickListener);//buttonTakePicClickListener is the incident after the call handler
        mAnimIndicator = (ImageView)findViewById(R.id.btnIndicator);
        mWarningIcon = getResources().getDrawable(R.drawable.sym_indicator1);

        ForWardon = getResources().getDrawable(R.drawable.sym_forward_1);
        ForWardoff = getResources().getDrawable(R.drawable.sym_forward);

        TurnLefton = getResources().getDrawable(R.drawable.sym_left_1);
        TurnLeftoff = getResources().getDrawable(R.drawable.sym_left);

        TurnRighton = getResources().getDrawable(R.drawable.sym_right_1);
        TurnRightoff = getResources().getDrawable(R.drawable.sym_right);

        BackWardon = getResources().getDrawable(R.drawable.sym_backward_1);
        BackWardoff = getResources().getDrawable(R.drawable.sym_backward);

        buttonLenon = getResources().getDrawable(R.drawable.sym_light);
        buttonLenoff = getResources().getDrawable(R.drawable.sym_light_off);

        Servoon = getResources().getDrawable(R.drawable.sym_stop_1);
        Servooff = getResources().getDrawable(R.drawable.sym_stop);

        Suctionon = getResources().getDrawable(R.drawable.sym_suction_1);
        Suctionoff = getResources().getDrawable(R.drawable.sym_suction);

        //My Button
        CameraUpon = getResources().getDrawable(R.drawable.sym_forward_1);
        CameraUpoff = getResources().getDrawable(R.drawable.sym_forward);

        CameraLefton = getResources().getDrawable(R.drawable.sym_left_1);
        CameraLeftoff = getResources().getDrawable(R.drawable.sym_left);

        CameraRighton = getResources().getDrawable(R.drawable.sym_right_1);
        CameraRightoff = getResources().getDrawable(R.drawable.sym_right);

        CameraDownon = getResources().getDrawable(R.drawable.sym_backward_1);
        CameraDownoff = getResources().getDrawable(R.drawable.sym_backward);

        CameraSwitchon = getResources().getDrawable(R.drawable.sym_stop_1);
        CameraSwitchoff = getResources().getDrawable(R.drawable.sym_stop);

        SpdSettingon= getResources().getDrawable(R.drawable.sym_speed_1);
        SpdSettingoff= getResources().getDrawable(R.drawable.sym_speed);

        Lightsettingon=getResources().getDrawable(R.drawable.sym_light_1);
        Lightsettingoff=getResources().getDrawable(R.drawable.sym_light);

        Vacsettingson=getResources().getDrawable(R.drawable.sym_vac_1);
        Vacsettingsoff=getResources().getDrawable(R.drawable.sym_vac);



        //show video and buttons view,which is MjpegView，backgroundView yes MjpegView example
        backgroundView = (MjpegView)findViewById(R.id.mySurfaceView1);
        backgroundView.setHandler(mHandler);

        mLogText = (TextView)findViewById(R.id.logTextView);
        if (null != mLogText) {
            mLogText.setBackgroundColor(Color.argb(0, 0, 0, 0));//0~255透明度值  255不透明
            mLogText.setTextColor(Color.argb(255, 255, 255, 255));
            mLogText.setTextSize(10);
            mLogText.setText("");
        }

        //start the thread

        //thread start completed



        //*******************
        //the first of these scroll bars don't matter
        //********************
        mSeekBar1 = (com.fei435.SeekBar)findViewById(R.id.gear1);
        mSeekBar1.setMax(MAX_GEAR_VALUE);
        mSeekBar1.setProgress(INIT_GEAR_VALUE);
        mSeekBar1.setOnSeekBarChangeListener(this);

        mSeekBar2 = (com.fei435.SeekBar)findViewById(R.id.gear2);
        mSeekBar2.setMax(MAX_GEAR_VALUE);
        mSeekBar2.setProgress(INIT_GEAR_VALUE);
        mSeekBar2.setOnSeekBarChangeListener(this);

        mSpeedSeekBar1 = (android.widget.SeekBar)findViewById(R.id.seekBarSpeed1);
        mSpeedSeekBar1.setMax(MAX_GEAR_VALUE_1);
        mSpeedSeekBar1.setProgress(INIT_GEAR_VALUE_1);
        mSpeedSeekBar1.setOnSeekBarChangeListener(this);
        editTextSpeed1 = (EditText)findViewById(R.id.editTextSpeed1);
        editTextSpeed1.setText(INIT_GEAR_VALUE_1+"");

        /*
        mSpeedSeekBar2 = (android.widget.SeekBar)findViewById(R.id.seekBarSpeed2);
        mSpeedSeekBar2.setMax(MAX_GEAR_VALUE_2);
        mSpeedSeekBar2.setProgress(INIT_GEAR_VALUE_2);
        mSpeedSeekBar2.setOnSeekBarChangeListener(this);
        editTextSpeed2 = (EditText)findViewById(R.id.editTextSpeed2);
        editTextSpeed2.setText(INIT_GEAR_VALUE_2+""); */

        ////////////////////////////////////////////////////////////////////////////////////////
        VacSeekBar = (android.widget.SeekBar)findViewById(R.id.VacSeekBar);
        VacSeekBar.setMax(100);
        VacSeekBar.setProgress(Vac_Int_Value);
        VacSeekBar.setOnSeekBarChangeListener(this);
        editTextVac = (EditText)findViewById(R.id.editTextVac);
        editTextVac.setText(Vac_Int_Value+"");

        WaterSeekBar = (android.widget.SeekBar)findViewById(R.id.WaterSeekBar);
        WaterSeekBar.setMax(100);
        WaterSeekBar.setProgress(Water_Int_Value);
        WaterSeekBar.setOnSeekBarChangeListener(this);
        editTextWater = (EditText)findViewById(R.id.editTextWater);
        editTextWater.setText(Water_Int_Value+"");

        LightSeekBar = (android.widget.SeekBar)findViewById(R.id.LightSeekBar);
        LightSeekBar.setMax(100);
        LightSeekBar.setProgress(Light_Int_Value);
        LightSeekBar.setOnSeekBarChangeListener(this);
        editTextLight = (EditText)findViewById(R.id.editTextLight);
        editTextLight.setText(Light_Int_Value+"");

        ////////////////////////////////////////////////////////////////////////////////////////


        buttonLen.setKeepScreenOn(true); //keep the screen long



        ForWard.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_FORWARD);   //send forward command
                        ForWard.setImageDrawable(ForWardon);
                        ForWard.invalidateDrawable(ForWardon);
                        break;
                    case MotionEvent.ACTION_UP:
                        mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        ForWard.setImageDrawable(ForWardoff);
                        ForWard.invalidateDrawable(ForWardoff);
                        break;
                }

                return false;
            }
        });

        BackWard.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_BACKWARD);  //send back command
                        BackWard.setImageDrawable(BackWardon);
                        BackWard.invalidateDrawable(BackWardon);
                        break;
                    case MotionEvent.ACTION_UP:
                        mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        BackWard.setImageDrawable(BackWardoff);
                        BackWard.invalidateDrawable(BackWardoff);
                        break;
                }
                return false;
            }

        });

        TurnRight.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_RIGHT);
                        TurnRight.setImageDrawable(TurnRighton);
                        TurnRight.invalidateDrawable(TurnRighton);
                        break;
                    case MotionEvent.ACTION_UP:
                        mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        TurnRight.setImageDrawable(TurnRightoff);
                        TurnRight.invalidateDrawable(TurnRightoff);
                        break;
                }
                return false;
            }
        });

        TurnLeft.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_LEFT);
                        TurnLeft.setImageDrawable(TurnLefton);
                        TurnLeft.invalidateDrawable(TurnLefton);
                        break;
                    case MotionEvent.ACTION_UP:
                        mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        TurnLeft.setImageDrawable(TurnLeftoff);
                        TurnLeft.invalidateDrawable(TurnLeftoff);
                        break;
                }
                return false;
            }
        });

        Servo.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        Constant.COMM_SERVO[2] = (byte)(Constant.COMM_SERVO[2] + Constant.COMM_SERVO[3]);
                        mWiFiCarControler.sendCommand(COMM_SERVO);   //Send Sever Position
                        Servo.setImageDrawable(Servoon);
                        Servo.invalidateDrawable(Servoon);
                        break;
                    case MotionEvent.ACTION_UP:
                        Servo.setImageDrawable(Servooff);
                        Servo.invalidateDrawable(Servooff);
                        break;
                }
                return false;
            }
        });

        // Suction Start
        Suction.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        if(flagsuction == 0)
                        {
                            flagsuction = 1;
                            mVibrator.vibrate(100);
                            mWiFiCarControler.sendCommand(COMM_SUCTION_ON);   //Send Sever Position
                            Suction.setImageDrawable(Suctionon);
                            Suction.invalidateDrawable(Suctionon);
                            break;
                        }
                        else
                        {
                            flagsuction = 0;
                            mVibrator.vibrate(100);
                            mWiFiCarControler.sendCommand(COMM_SUCTION_OFF);   //Send Sever Position
                            Suction.setImageDrawable(Suctionoff);
                            Suction.invalidateDrawable(Suctionoff);
                            break;
                        }
                }
                return false;
            }
        });

        CameraUp.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_CAMERA_UP);   //send camera Up command
                        CameraUp.setImageDrawable(CameraUpon);
                        CameraUp.invalidateDrawable(CameraUpon);
                        break;
                    case MotionEvent.ACTION_UP:
                        //mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        CameraUp.setImageDrawable(CameraUpoff);
                        CameraUp.invalidateDrawable(CameraUpoff);
                        break;
                }
                return false;
            }
        });

        CameraDown.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_CAMERA_DOWN);   //send camera down command
                        CameraDown.setImageDrawable(CameraDownon);
                        CameraDown.invalidateDrawable(CameraDownon);
                        break;
                    case MotionEvent.ACTION_UP:
                        //mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        CameraDown.setImageDrawable(CameraDownoff);
                        CameraDown.invalidateDrawable(CameraDownoff);
                        break;
                }
                return false;
            }
        });

        /*

        CameraLeft.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_CAMERA_LEFT);   //send camera left command
                        CameraLeft.setImageDrawable(CameraLefton);
                        CameraLeft.invalidateDrawable(CameraLefton);
                        break;
                    case MotionEvent.ACTION_UP:
                        //mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        CameraLeft.setImageDrawable(CameraLeftoff);
                        CameraLeft.invalidateDrawable(CameraLeftoff);
                        break;
                }
                return false;
            }
        });



        CameraRight.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_CAMERA_RIGHT);   //send camera right command
                        CameraRight.setImageDrawable(CameraRighton);
                        CameraRight.invalidateDrawable(CameraRighton);
                        break;
                    case MotionEvent.ACTION_UP:
                        //mWiFiCarControler.sendCommand(Constant.COMM_STOP);
                        CameraRight.setImageDrawable(CameraRightoff);
                        CameraRight.invalidateDrawable(CameraRightoff);
                        break;
                }
                return false;
            }
        });

        */

        CameraSwitch.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        if(flagcamera == 0)
                        {
                            flagcamera = 1; //variable used to ditermine
                            mVibrator.vibrate(100);
                            mWiFiCarControler.sendCommand(COMM_CAMERA_ON);   //Send Sever Position
                            CameraSwitch.setImageDrawable(CameraSwitchon);
                            CameraSwitch.invalidateDrawable(CameraSwitchon);
                            break;
                        }
                        else
                        {
                            flagcamera = 0;
                            mVibrator.vibrate(100);
                            mWiFiCarControler.sendCommand(COMM_CAMERA_OFF);   //Send Sever Position
                            CameraSwitch.setImageDrawable(CameraSwitchoff);
                            CameraSwitch.invalidateDrawable(CameraSwitchoff);
                            break;
                        }
                }
                return false;
            }
        });

        SpdSetting.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        if(flagspeed == 0)
                        {
                            flagspeed = 1;
                            mVibrator.vibrate(100);
                            SpdSetting.setImageDrawable(SpdSettingon);
                            SpdSetting.invalidateDrawable(SpdSettingon);
                            mSpeedSeekBar1.setVisibility(View.VISIBLE);
                            //mSpeedSeekBar2.setVisibility(View.VISIBLE);
                            editTextSpeed1.setVisibility(View.VISIBLE);
                            //editTextSpeed2.setVisibility(View.VISIBLE);
                            break;
                        }
                        else
                        {
                            flagspeed = 0;
                            mVibrator.vibrate(100);
                            SpdSetting.setImageDrawable(SpdSettingoff);
                            SpdSetting.invalidateDrawable(SpdSettingoff);
                            mSpeedSeekBar1.setVisibility(View.INVISIBLE);
                            //mSpeedSeekBar2.setVisibility(View.INVISIBLE);
                            editTextSpeed1.setVisibility(View.INVISIBLE);
                            //editTextSpeed2.setVisibility(View.INVISIBLE);
                            break;
                        }
                }
                return false;
            }
        });


        editTextSpeed1.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String str = editTextSpeed1.getText().toString();
                int value = Integer.parseInt(str);
                mSpeedSeekBar1.setProgress(value);

                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                //split and convert the int into 2 byte hex
                //Constant.COMM_SPEED_VALUE_1[2] = (byte)(value >> 8);
                Constant.COMM_SPEED_VALUE_1[2] = 0x01;
                Constant.COMM_SPEED_VALUE_1[3] = (byte)(value);
                Log.i("speed", "set speed(decimal):"+value);
                msg.obj = Constant.COMM_SPEED_VALUE_1;
                mHandler.sendMessage(msg);
                return false;
            }
        });



        // Light switch

        Light.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        if(flagLED == 0)
                        {
                            flagLED = 1;
                            mVibrator.vibrate(100);
                            Light.setImageDrawable(Lightsettingon);
                            Light.invalidateDrawable(Lightsettingon);
                            LightSeekBar.setVisibility(View.VISIBLE);
                            editTextLight.setVisibility(View.VISIBLE);

                            break;
                        }
                        else
                        {
                            flagLED = 0;
                            mVibrator.vibrate(100);
                            Light.setImageDrawable(Lightsettingoff);
                            Light.invalidateDrawable(Lightsettingoff);
                            LightSeekBar.setVisibility(View.INVISIBLE);
                            editTextLight.setVisibility(View.INVISIBLE);
                            break;
                        }
                }
                return false;
            }
        });

        editTextLight.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String str = editTextLight.getText().toString();
                int value = Integer.parseInt(str);
                LightSeekBar.setProgress(value);

                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                Constant.COMM_SPEED_VALUE_3[2] = 0x03;
                Constant.COMM_SPEED_VALUE_3[3] = (byte)(value);
                Log.i("speed", "set speed(decimal):"+value);
                msg.obj = Constant.COMM_SPEED_VALUE_3;
                mHandler.sendMessage(msg);

                return false;
            }
        });

        /*


        Vac.setOnTouchListener( new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch(action)
                {
                    case MotionEvent.ACTION_DOWN:
                        if(flagVac == 0)
                        {
                            flagVac = 1;
                            mVibrator.vibrate(100);
                            Vac.setImageDrawable(Vacsettingson);
                            Vac.invalidateDrawable(Vacsettingson);
                            VacSeekBar.setVisibility(View.VISIBLE);
                            editTextVac.setVisibility(View.VISIBLE);

                            break;
                        }
                        else
                        {
                            flagVac = 0;
                            mVibrator.vibrate(100);
                            Vac.setImageDrawable(Vacsettingsoff);
                            Vac.invalidateDrawable(Vacsettingsoff);
                            VacSeekBar.setVisibility(View.INVISIBLE);
                            editTextVac.setVisibility(View.INVISIBLE);
                            break;
                        }
                }
                return false;
            }
        });
        */


        editTextVac.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String str = editTextVac.getText().toString();
                int value = Integer.parseInt(str);
                VacSeekBar.setProgress(value);

                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                //split and convert an INT to a 2 byte Hex
                //Constant.COMM_SPEED_VALUE_2[2] = (byte)(value >> 8);
                Constant.COMM_SPEED_VALUE_2[2] = 0x02;
                Constant.COMM_SPEED_VALUE_2[3] = (byte)(value);
                Log.i("speed", "set speed(decimal):"+value);
                msg.obj = Constant.COMM_SPEED_VALUE_2;
                mHandler.sendMessage(msg);
                return false;
            }
        });






        //***********************
        //temporary unclear the meaning of this method
        //***********************
        SensorEventListener lsn = new SensorEventListener(){
            public void onSensorChanged (SensorEvent e){
                if(bGravityDetectOn){
                    float x = e.values[SensorManager.DATA_X];
                    float y = e.values[SensorManager.DATA_Y];
                    float z = e.values[SensorManager.DATA_Z];

                    if (x < 2)
                    {
                        //don't always send commands repeadetly caused by the micro controller
                        if(lastCommand != Constant.COMM_FORWARD[2]){
                            mWiFiCarControler.sendCommand(Constant.COMM_FORWARD);   //send forward command //forward
                        }
                        lastCommand = Constant.COMM_FORWARD[2];
                    }
                    else if (x > 7)
                    {
                        if(lastCommand != Constant.COMM_FORWARD[2]){
                            mWiFiCarControler.sendCommand(Constant.COMM_BACKWARD);  //send back command //Backward
                        }
                        lastCommand = Constant.COMM_BACKWARD[2];
                    }
                    else if (y < -1)
                    {
                        if(lastCommand != Constant.COMM_LEFT[2]){
                            mWiFiCarControler.sendCommand(Constant.COMM_LEFT);  //send left command //left
                        }
                        lastCommand = Constant.COMM_LEFT[2];
                    }
                    else if (y > 1)
                    {
                        if(lastCommand != Constant.COMM_RIGHT[2]){
                            mWiFiCarControler.sendCommand(Constant.COMM_RIGHT);  //send Right command //Right
                        }
                        lastCommand = Constant.COMM_RIGHT[2];
                    }
                    else
                    {
                        if(lastCommand != Constant.COMM_STOP[2]){
                            mWiFiCarControler.sendCommand(Constant.COMM_STOP);  //Send Back command //Stop
                        }
                        lastCommand = Constant.COMM_STOP[2];
                    }
                }
            }
            public void onAccuracyChanged (Sensor s, int accuracy){
            }
        };

        mSensorMgr.registerListener (lsn, sensor, SensorManager.SENSOR_DELAY_GAME);

        gravityDetectToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bGravityDetectOn = true;
                }
                else {
                    bGravityDetectOn = false;
                }
            }}
        );





        //connect
        //connectToRouter(m4test);   //Connection to router here no longer connected   在onResume中连接
        //245368746(little white love QQ)


        ///////////////////////////////////////////////////////////////////////////////////////////





        Water_Box.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked){
                    WaterSeekBar.setVisibility(View.VISIBLE);
                    editTextWater.setVisibility(View.VISIBLE);
                }
                else{
                    WaterSeekBar.setVisibility(View.INVISIBLE);
                    editTextWater.setVisibility(View.INVISIBLE);
                }
            }
        });

        //TODO: create the hex code for water

        editTextWater.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String str = editTextWater.getText().toString();
                int value = Integer.parseInt(str);
                WaterSeekBar.setProgress(value);
                /*
                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                Constant.COMM_SPEED_VALUE_1[2] = 0x01;
                Constant.COMM_SPEED_VALUE_1[3] = (byte)(value);
                Log.i("speed", "set speed(decimal):"+value);
                msg.obj = Constant.COMM_SPEED_VALUE_1;
                mHandler.sendMessage(msg);
                */
                return false;
            }
        });


        //TODO: create the hex code for water


        WebView mWebView=(WebView) findViewById(R.id.webview);

        //allow zoom in and out controls
        mWebView.getSettings().setJavaScriptEnabled(true);

        //zoom out to best fit the screen
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        //load the stream link
        mWebView.loadUrl(DEFAULT_VALUE_CAMERA_URL);

        //set the view to be explicitly on the webview widget
        mWebView.setWebViewClient(new InsideWebViewClient());



        ////////////////////////////////////////////////////////////////////////////////////////////
    }


    private OnClickListener buttonLenClickListener = new OnClickListener() {
        public void onClick(View arg0) {
            mVibrator.vibrate(100);
            if (bCaptureOn) {
                bCaptureOn = false;
                //sendCommand(COMM_LEN_OFF);
                Log.i("ScreenCapture", "button turn off capture clicked");
                backgroundView.toggleVideoCapture();
                buttonLen.setImageDrawable(buttonLenoff);
                buttonLen.invalidateDrawable(buttonLenon);
            } else  {
                bCaptureOn = true;
                //sendCommand(COMM_LEN_ON);
                Log.i("ScreenCapture", "button turn on capture clicked");
                backgroundView.toggleVideoCapture();
                buttonLen.setImageDrawable(buttonLenon);
                buttonLen.invalidateDrawable(buttonLenon);
            }
        }
    };

    //Camera
    private OnClickListener buttonTakePicClickListener = new OnClickListener() {
        public void onClick(View arg0) {
            mVibrator.vibrate(100);
            if (null != backgroundView) {
                backgroundView.saveBitmap();
            }
        }
    };

    private OnClickListener buttonCus1ClickListener = new OnClickListener() {
        public void onClick(View arg0) {
            mVibrator.vibrate(100);
            Log.i("settingclick","buttonCus1ClickListener");

            mWiFiCarControler.disconnFromRouter();

            Intent setIntent = new Intent();
            setIntent.setClass(mContext, WifiCarSettings.class);
            startActivity(setIntent);
        }
    };

/*    private OnLongClickListener buttonCus1ClickListener2 = new OnLongClickListener() {
        public boolean onLongClick(View arg0) {
        	Log.i("settingclick","buttonCus1ClickListener2");
            mThreadFlag = false;
            try {
                if (null != mThreadClient)
                    mThreadClient.join(); // wait for second to finish
            } catch (InterruptedException e) {
                Log.i("mLogText","close the router, listening process failed。。。" +  e.getMessage());
            }
            return false;
        }
    };*/

    private void inspectParam(){
        Log.i("inspect", "CAMERA_VIDEO_URL"+Constant.CAMERA_VIDEO_URL);
        Log.i("inspect", "ROUTER_CONTROL_URL"+Constant.ROUTER_CONTROL_URL);
        Log.i("inspect", "ROUTER_CONTROL_PORT"+Constant.ROUTER_CONTROL_PORT);
        Log.i("inspect", "m4test"+Constant.m4test);
    }

    private void initSettings () {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //DEFAULT_VALUE_CAMERA_URL,DEFAULT_VALUE_CAMERA_URL_TEST都是获取失败后默认的地址
        Constant.CAMERA_VIDEO_URL = settings.getString(Constant.PREF_KEY_CAMERA_URL, Constant.DEFAULT_VALUE_CAMERA_URL);
        Constant.CAMERA_VIDEO_URL_TEST = settings.getString(Constant.PREF_KEY_CAMERA_URL_TEST, Constant.DEFAULT_VALUE_CAMERA_URL_TEST);
//		 if (!settings.contains(Constant.PREF_KEY_CAMERA_URL)) {
//			settings.edit().putString(Constant.PREF_KEY_CAMERA_URL, Constant.DEFAULT_VALUE_CAMERA_URL);
//		 }
//		 if(!settings.contains(Constant.PREF_KEY_CAMERA_URL_TEST)){
//			 settings.edit().putString(Constant.PREF_KEY_CAMERA_URL_TEST, Constant.DEFAULT_VALUE_CAMERA_URL_TEST);
//		 }

        //DEFAULT_VALUE_ROUTER_URL is to get sharedPreference the default address after failure
        String RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL, Constant.DEFAULT_VALUE_ROUTER_URL);
        int index = RouterUrl.indexOf(":");
        String routerIP = "";
        String routerPort = "";
        int port = 0;
        if (index > 0) {
            routerIP = RouterUrl.substring(0, index);
            routerPort = RouterUrl.substring(index+1, RouterUrl.length() );
            port = Integer.parseInt(routerPort);
        }
        Constant.ROUTER_CONTROL_URL = routerIP;
        Constant.ROUTER_CONTROL_PORT = port;
//		 if (!settings.contains(Constant.PREF_KEY_ROUTER_URL)) {
//			 settings.edit().putString(Constant.PREF_KEY_ROUTER_URL, Constant.DEFAULT_VALUE_ROUTER_URL);
//		 }

        RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL_TEST, Constant.DEFAULT_VALUE_ROUTER_URL_TEST);
        index = RouterUrl.indexOf(":");
        if (index > 0) {
            routerIP = RouterUrl.substring(0, index);
            routerPort = RouterUrl.substring(index+1, RouterUrl.length() );
            port = Integer.parseInt(routerPort);
        }
        Constant.ROUTER_CONTROL_URL_TEST = routerIP;
        Constant.ROUTER_CONTROL_PORT_TEST = port;
//		 if(!settings.contains(Constant.PREF_KEY_ROUTER_URL_TEST)){
//			 settings.edit().putString(Constant.PREF_KEY_ROUTER_URL_TEST, Constant.DEFAULT_VALUE_ROUTER_URL_TEST);
//		 }

        Constant.m4test =  settings.getBoolean(Constant.PREF_KEY_TEST_MODE_ENABLED, false);
//		 if(!settings.contains(Constant.PREF_KEY_TEST_MODE_ENABLED)){
//			 settings.edit().putBoolean(Constant.PREF_KEY_TEST_MODE_ENABLED, false);
//		 }

        initLenControl(Constant.PREF_KEY_LEN_ON, Constant.DEFAULT_VALUE_LEN_ON);
        initLenControl(Constant.PREF_KEY_LEN_OFF, Constant.DEFAULT_VALUE_LEN_OFF);

        //inspectParam();
    }

    void initLenControl (String prefKey, String defaultValue) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        String comm = settings.getString(prefKey, defaultValue);
        CommandArray cmd = new CommandArray(comm);
        if (cmd.isValid() ) {
            if (Constant.PREF_KEY_LEN_ON.equalsIgnoreCase(prefKey)) {
                Constant.COMM_LEN_ON[1] = cmd.mCmd1;
                Constant.COMM_LEN_ON[2] = cmd.mCmd2;
                Constant.COMM_LEN_ON[3] = cmd.mCmd3;
            } else if (Constant.PREF_KEY_LEN_OFF.equalsIgnoreCase(prefKey)) {
                Constant.COMM_LEN_OFF[1] = cmd.mCmd1;
                Constant.COMM_LEN_OFF[2] = cmd.mCmd2;
                Constant.COMM_LEN_OFF[3] = cmd.mCmd3;
            } else {
                Log.i("Main", "unknow prefKey:" + prefKey);
            }
        } else {
            Log.i("Main", "error format of command:" + comm);
        }
    }

    private void handleCallback(byte[] command) {
        if (null == command || command.length != Constant.COMMAND_LENGTH) {
            return;
        }

        byte cmd1 = command[1];
        byte cmd2 = command[2];
        //byte cmd3 = command[3];

        if (command[0] != Constant.COMMAND_PERFIX || command[Constant.COMMAND_LENGTH-1] !=  Constant.COMMAND_PERFIX) {
            return;
        }

        if (cmd1 != 0xEE) {
            Log.i("Socket", "unknow command from router, ignore it! cmd1=" + cmd1);
            return;
        }

        switch (cmd2) {
            case (byte)0xE1:
                //Log.i("heart","Recived Car Heart Beat Package ！");
                Log.i("heart","Received Heartbeat");
                handleHeartBreak();
                break;
//        case (byte)0xE2:
//            handleHeartBreak();
//            break;
            default:
                break;
        }
    }


    private boolean isIconAnimationEnabled () {
        //return bAnimationEnabled && bHeartBreakFlag;
        return bAnimationEnabled;
    }
    private boolean mIconAnimationState = false;

    /** Icon animation handler for flashing warning alerts. */
    private final Handler mAnimationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mIconAnimationState) {
                mAnimIndicator.setAlpha(255);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_ON_DURATION_MSEC);
                }
            } else {
                mAnimIndicator.setAlpha(0);
                if (isIconAnimationEnabled()) {
                    mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_OFF_DURATION_MSEC);
                }
            }
            mIconAnimationState = !mIconAnimationState;
            mAnimIndicator.invalidateDrawable(mWarningIcon);
        }
    };

    private void startIconAnimation() {
        Log.i("Animation", "startIconAnimation handler : " + mAnimationHandler);
        if (mAnimIndicator != null) {
            mAnimIndicator.setImageDrawable(mWarningIcon);
        }
        if (isIconAnimationEnabled())
            mAnimationHandler.sendEmptyMessageDelayed(0, WARNING_ICON_ON_DURATION_MSEC);
    }

    private void handleHeartBreak() {
        Log.i("heart", "handleHeartBreak");
        mHeartBreakCounter++;
        bHeartBreakFlag = true;
    }

    private void stopIconAnimation() {
        mAnimationHandler.removeMessages(0);
    }

    public void onProgressChanged(com.fei435.SeekBar seekBar, int progress, boolean fromUserh) {

        if(seekBar == mSeekBar1){
            if (Math.abs(progress - mSeekBarValue1) > MIN_GEAR_STEP) {
                Log.i("mLogText","change angle: " + progress);
                mSeekBarValue1 = progress;
                Constant.COMM_GEAR_CONTROL_1[3] = (byte)progress;
                mWiFiCarControler.sendCommand(Constant.COMM_GEAR_CONTROL_1);
            }
        }else if(seekBar == mSeekBar2){
            if (Math.abs(progress - mSeekBarValue2) > MIN_GEAR_STEP) {
                Log.i("mLogText","change angle: " + progress);
                mSeekBarValue2 = progress;
                Constant.COMM_GEAR_CONTROL_2[3] = (byte)progress;
                mWiFiCarControler.sendCommand(Constant.COMM_GEAR_CONTROL_2);
            }
        }
    }



    // this is to display the value change on Seekbar and sending the change in value to the DSP
    public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == mSpeedSeekBar1){
            if (Math.abs(progress - mSpeedSeekBarValue1) > MIN_GEAR_STEP_1) {
                Log.i("mLogText","change speed: " + progress);
                editTextSpeed1.setText(progress+"");
                mSpeedSeekBarValue1 = progress;

                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                //split and convert an Int to 2 byte Hex
                //Constant.COMM_SPEED_VALUE_1[2] = (byte)(progress >> 8);
                Constant.COMM_SPEED_VALUE_1[2] = 0x01;
                Constant.COMM_SPEED_VALUE_1[3] = (byte)(progress);
                //Log.i("speed", "set speed(十进制):"+progress);
                Log.i("speed", "set speed:"+progress);
                msg.obj = Constant.COMM_SPEED_VALUE_1;
                mHandler.sendMessage(msg);
            }
        }
        else if(seekBar == mSpeedSeekBar2){
            if (Math.abs(progress - mSpeedSeekBarValue2) > MIN_GEAR_STEP_2) {
                Log.i("mLogText","change speed: " + progress);
                editTextSpeed2.setText(progress+"");
                mSpeedSeekBarValue2 = progress;

                /*
                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                //split and convert an Int to 2 byte Hex
                //Constant.COMM_SPEED_VALUE_2[2] = (byte)(progress >> 8);
                Constant.COMM_SPEED_VALUE_2[2] = 0x02;
                Constant.COMM_SPEED_VALUE_2[3] = (byte)(progress);
                //Log.i("speed", "set speed(十进制):"+progress);
                Log.i("speed", "set speed:"+progress);
                msg.obj = Constant.COMM_SPEED_VALUE_2;
                mHandler.sendMessage(msg);
                */
            }
        }
        else if(seekBar == VacSeekBar) {
            if (Math.abs(progress - VacSeekBar_Value) > Vac_Min_Value) {
                Log.i("mLogText", "change speed: " + progress);
                editTextVac.setText(progress + "");
                VacSeekBar_Value = progress;


                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                //split and convert an Int to 2 byte Hex
                //Constant.COMM_SPEED_VALUE_2[2] = (byte)(progress >> 8);
                Constant.COMM_SPEED_VALUE_2[2] = 0x02;
                Constant.COMM_SPEED_VALUE_2[3] = (byte) (progress);
                //Log.i("speed", "set speed(十进制):"+progress);
                Log.i("speed", "set speed:" + progress);
                msg.obj = Constant.COMM_SPEED_VALUE_2;
                mHandler.sendMessage(msg);

            }
        }
        else if(seekBar == WaterSeekBar) {
            if (Math.abs(progress - WaterSeekBar_Value) > Water_Min_Value) {
                Log.i("mLogText", "change speed: " + progress);
                editTextWater.setText(progress + "");
                WaterSeekBar_Value = progress;

                /*
                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                //split and convert an Int to 2 byte Hex
                //Constant.COMM_SPEED_VALUE_2[2] = (byte)(progress >> 8);
                Constant.COMM_SPEED_VALUE_2[2] = 0x02;
                Constant.COMM_SPEED_VALUE_2[3] = (byte) (progress);
                //Log.i("speed", "set speed(十进制):"+progress);
                Log.i("speed", "set speed:" + progress);
                msg.obj = Constant.COMM_SPEED_VALUE_2;
                mHandler.sendMessage(msg);
                */
            }
        }
        else if(seekBar == LightSeekBar) {
            if (Math.abs(progress - LightSeekBar_Value) > Light_Min_Value) {
                Log.i("mLogText", "change speed: " + progress);
                editTextLight.setText(progress + "");
                LightSeekBar_Value = progress;


                Message msg = new Message();
                msg.what = Constant.MSG_ID_SET_SPEED;
                //split and convert an Int to 2 byte Hex
                //Constant.COMM_SPEED_VALUE_2[2] = (byte)(progress >> 8);
                Constant.COMM_SPEED_VALUE_3[2] = 0x03;
                Constant.COMM_SPEED_VALUE_3[3] = (byte) (progress);
                //Log.i("speed", "set speed(十进制):"+progress);
                Log.i("speed", "set speed:" + progress);
                msg.obj = Constant.COMM_SPEED_VALUE_3;
                mHandler.sendMessage(msg);

            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onDestroy() {
        super.onDestroy();
        Log.i("SurfaceStatus","onDestroy");
        mWiFiCarControler.disconnFromRouter();

        stopIconAnimation();
    }
    protected void onResume() {
        super.onResume();
        Log.i("SurfaceStatus","onResume");

        if(Constant.CURRENT_WIFI_STATE == Constant.WIFI_STATE_CONNECTED){
            String cameraUrl = null;
            if (Constant.m4test) {
                cameraUrl = Constant.CAMERA_VIDEO_URL_TEST;
            } else {
                cameraUrl = Constant.CAMERA_VIDEO_URL;
            }
            if (null != cameraUrl && cameraUrl.length() > 4) {
                backgroundView.setSource(cameraUrl);//初始化Camera ,设置视频流读取地址
                backgroundView.resumePlayback();//启动线程，播放视频
            }
        }

        mWiFiCarControler.connectToRouter(); //连接小车
    }

    protected void onPause() {
        super.onPause();
        Log.i("SurfaceStatus", "onPause");
        mWiFiCarControler.disconnFromRouter();
    }

    @Override
    public void onBackPressed() {
        if (mQuitFlag) {
            finish();
        } else {
            mQuitFlag = true;
            Toast.makeText(mContext, "press again to exit application", Toast.LENGTH_LONG).show();
            Message msg = new Message();
            msg.what = Constant.MSG_ID_CLEAR_QUIT_FLAG;
            mHandler.sendMessageDelayed(msg, Constant.QUIT_BUTTON_PRESS_INTERVAL);
        }
    }

    public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    private class InsideWebViewClient extends WebViewClient {
        // Force links to be opened inside WebView and not in Default Browser
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;

        }
    }

    //*******************
    //read the handle method
    //********************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        int deviceId = event.getDeviceId();
        if (deviceId != -1) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        //mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_LEFT);    //发送左转命令
                        TurnLeft.setImageDrawable(TurnLefton);
                        TurnLeft.invalidateDrawable(TurnLefton);
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        //mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_RIGHT);    //发送右转命令
                        TurnRight.setImageDrawable(TurnRighton);
                        TurnRight.invalidateDrawable(TurnRighton);
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        //mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_FORWARD);   //发送前进命令
                        ForWard.setImageDrawable(ForWardon);
                        ForWard.invalidateDrawable(ForWardon);
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        //mVibrator.vibrate(100);
                        mWiFiCarControler.sendCommand(Constant.COMM_BACKWARD);  //发送后退命令
                        BackWard.setImageDrawable(BackWardon);
                        BackWard.invalidateDrawable(BackWardon);
                        handled = true;
                        break;
                    default:
                        handled = true;
                        break;
                }
        }

        return handled;

        //return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean handled = false;
        int deviceId = event.getDeviceId();
        if (deviceId != -1) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    //mVibrator.vibrate(100);//震动，准备关掉这个函数
                    mWiFiCarControler.sendCommand(Constant.COMM_STOP);    //发送停止命令
                    TurnLeft.setImageDrawable(TurnLeftoff);
                    TurnLeft.invalidateDrawable(TurnLeftoff);
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    //mVibrator.vibrate(100);
                    mWiFiCarControler.sendCommand(Constant.COMM_STOP);    //发送停止命令
                    TurnRight.setImageDrawable(TurnRightoff);
                    TurnRight.invalidateDrawable(TurnRightoff);
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    //mVibrator.vibrate(100);
                    mWiFiCarControler.sendCommand(Constant.COMM_STOP);   //发送停止命令
                    ForWard.setImageDrawable(ForWardoff);
                    ForWard.invalidateDrawable(ForWardoff);
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    //mVibrator.vibrate(100);
                    mWiFiCarControler.sendCommand(Constant.COMM_STOP);  //发送停止命令
                    BackWard.setImageDrawable(BackWardoff);
                    BackWard.invalidateDrawable(BackWardoff);
                    handled = true;
                    break;
                default:
                    handled = true;
                    break;
            }
        }

        return handled;
    }

    //*******************
    //reed the handle method is finished
    //********************
}