package chicken.head.espassist;

import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import chicken.head.espassist.R;
import android.R.drawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class MainActivity extends AppCompatActivity implements  OnBluetoothDeviceClickedListener {
    private TextView txtView;
    private NotificationReceiver nReceiver;
    public static  TextView tvdev;
    public static  String BLEname;
    public static String BLEaddress;
    CheckBox checkBoxApp;
    EditText editText;


    //=bluetooth
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION=1;


    private static final int REQUEST_CONNECT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    public static final String EXTRAS_DEVICE_NAME = "extras_device_name";
    public static final String EXTRAS_DEVICE_ADDRESS = "extras_device_address";
    private String mConnectionState = BluetoothLeService.ACTION_GATT_DISCONNECTED;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 1000 * 2;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private MyBluetoothDeviceAdapter mBluetoothDeviceAdapter;
    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<>();
    private MyBluetoothScanCallBack mBluetoothScanCallBack = new MyBluetoothScanCallBack();
    private Handler mHandler;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceName;
    private String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextInputLayout textInputLayout;
        txtView=findViewById(R.id.notifytxt);
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("chicken.head.espassist.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);
        checkBoxApp=findViewById(R.id.checkBoxAPP);
        editText=findViewById(R.id.edittxt);

        tvdev=findViewById(R.id.tvdevice_holded);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tvinfo=findViewById(R.id.notifytxt);

                if(tvinfo.getText().equals("no captured notification")){
                    alertmsg();
                }else {

                    Snackbar.make(view, "Test to create notify", Snackbar.LENGTH_LONG)
                            .setAction("Testing", null).show();
                    NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
                    NotificationCompat.Builder ncomp=new NotificationCompat.Builder(getApplicationContext());
                    ncomp.setContentTitle("ESP");
                    if(editText.getText()!=null){

                        ncomp.setContentText(editText.getText());
                    }else {

                        ncomp.setContentText("Notification Listener Service Example to display on esp device");
                    }
//                ncomp.setTicker("Notification Listener Service Example");
                    ncomp.setSmallIcon(R.mipmap.ic_launcher);
                    ncomp.setAutoCancel(true);
                    nManager.notify((int)System.currentTimeMillis(),ncomp.build());
                }
            }
        });

//        Button btncreate=findViewById(R.id.btn_create);
//        Button btnclear=findViewById(R.id.btn_clear);
//        Button btnlist=findViewById(R.id.btn_list);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
////        SimpleDateFormat sdf = new SimpleDateFormat("ssmmHH");
//        final String currentDateandTime = "TT"+sdf.format(new Date());
//        Button btnts=findViewById(R.id.btnsync);
//        btnts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btSendBytes(currentDateandTime.getBytes());
//            }
//        });


//        btncreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////                NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
//                NotificationCompat.Builder ncomp=new NotificationCompat.Builder(getApplicationContext());
//                ncomp.setContentTitle("ESP");
//                if(editText.getText()!=null){
//
//                    ncomp.setContentText(editText.getText());
//                }else {
//
//                    ncomp.setContentText("Notification Listener Service Example to display on esp device");
//                }
////                ncomp.setTicker("Notification Listener Service Example");
//                ncomp.setSmallIcon(R.mipmap.ic_launcher);
//                ncomp.setAutoCancel(true);
//                nManager.notify((int)System.currentTimeMillis(),ncomp.build());
//            }
//        });

//        btnclear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i = new Intent("chicken.head.espassist.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
//                i.putExtra("command","clearall");
//                sendBroadcast(i);
//            }
//        });
//        btnlist.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent i = new Intent("chicken.head.espassist.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
//                i.putExtra("command","list");
//                sendBroadcast(i);
//            }
//        });




        //====bluetooth

        initView();
        requestPermission();
        initData();
        initService();
        scanLeDevice(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initReceiver();
//        scanLeDevice(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MainActivity", "unregisterReceiver()");
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.exit) {
            System.exit(0);
        }else if (id == R.id.nacces) {
            startActivity( new  Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }else if(id==R.id.synctime){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
////        SimpleDateFormat sdf = new SimpleDateFormat("ssmmHH");
//            final String currentDateandTime = "TT"+sdf.format(new Date());
            btSendBytes(getTime().getBytes());

        }

        return super.onOptionsItemSelected(item);
    }
    private String getTime(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        SimpleDateFormat sdf = new SimpleDateFormat("ssmmHH");
        final String time = "TT"+sdf.format(new Date());
        return time;
    }

//     public void buttonClicked(View v){
//        if(v.getId()==R.id.btn_create){
//
//        }
//        else if(v.getId()==R.id.btn_clear){
//
//        }
//        else if(v.getId()==R.id.btn_list){
//        }
//    }
    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") ;
            Bundle bundle = intent.getExtras();
            String string=(String) bundle.get("notification_event");

//            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            if(temp.length()>70){
                temp=temp.substring(0,67)+"...";
            }
            txtView.setText(temp);
            if(checkBoxApp.isChecked()){

                if(!string.equals(null)&&onWhiteList(string)){
                    if(!passWhiteContent(string))
                        btSendBytes(string.getBytes());
                }
            }else{

                if(!string.equals(null)){
                    if(!passWhiteContent(string))
                        btSendBytes(string.getBytes());
                }

            }
        }
    }

    private boolean onWhiteList(String wl){
        String title=wl.substring(0,wl.indexOf("\n"));
        String applist[]={"WhatsApp", "Message", "Call", "KDE Connect","Maps"};
        int applistcount=applist.length;
        Boolean inlist=false;
        boolean exitfor=false;
        for(int i=0;i<applistcount;i++ ){
//            if (applist[i].equals("")){
//              exitfor=true;
//              break;
//            }
            if (NLserv.Appname.equals(applist[i])){
                inlist =true;
                exitfor=true;
                break;
            }
            if (exitfor)break;
        }
//        showMsg("inlist = "+ inlist);
        return inlist ;
    }


    private boolean passWhiteContent(String wc){
    if(wc.contains("messages"))
        return true;
    else return false;
    }
    //========all bluetooth begin======




    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check.
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
//        if(this.checkSelfPermission(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)!=PackageManager.PERMISSION_GRANTED){
//
//        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    }

    private void initService() {
        Log.i("MainActivity", "initService()");

        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private void initData() {
        mHandler = new Handler();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        mBluetoothDeviceAdapter = new MyBluetoothDeviceAdapter(mBluetoothDeviceList, this);
        recyclerView.setAdapter(mBluetoothDeviceAdapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mBluetoothDeviceList != null) {
                    mBluetoothDeviceList.clear();
                }
                scanLeDevice(true);
            }
        });


    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("MainActivity","Unable to initialize Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };



    private void scanLeDevice(boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(false);
                    BluetoothScan.stopScan();
                }
            }, SCAN_PERIOD);
            swipeRefresh.setRefreshing(true);
            BluetoothScan.startScan(true, mBluetoothScanCallBack);
        } else {
            swipeRefresh.setRefreshing(false);
            BluetoothScan.stopScan();
        }
    }

    @Override
    public void onBluetoothDeviceClicked(String name, String address) {

        Log.i("MainActivity","Attempt to connect device : " + name + "(" + address + ")");
        mDeviceName = name;
        mDeviceAddress = address;

        if (mBluetoothLeService != null) {

            if (mBluetoothLeService.connect(mDeviceAddress)) {
                showMsg("Attempt to connect device : " + name);

                mConnectionState = BluetoothLeService.ACTION_GATT_CONNECTING;
                swipeRefresh.setRefreshing(true);
            }
        }
    }

    private void initReceiver() {
        Log.i("MainActivity", "initReceiver()");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

        registerReceiver(mGattUpdateReceiver, intentFilter);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i("MainActivity", "ACTION_GATT_CONNECTED!!!");
                showMsg("Connected to  : "+ mDeviceName);

                mConnectionState = BluetoothLeService.ACTION_GATT_CONNECTED;
                swipeRefresh.setRefreshing(false);

//                inputMessage();

//                if (mDeviceName.equals("MyESP32"))
//                    onBluetoothDeviceClicked(mDeviceName,mDeviceAddress);
                Log.i("MainActivity","device result recognized and auto try to connect");
                String dev=tvdev.getText().toString();
                tvdev.setText(mDeviceName + " > Connected");
                btSendBytes(getTime().getBytes());
                final Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            btSendBytes(getTime().getBytes());
                                        }
                                    },2000);

                        Log.i("MainActivity", "sent synctime info");



            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i("MainActivity", "ACTION_GATT_DISCONNECTED!!!");
                showMsg("disconnected");
                tvdev.setText(mDeviceName + " > disconnected");
                mConnectionState = BluetoothLeService.ACTION_GATT_DISCONNECTED;
                swipeRefresh.setRefreshing(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mBluetoothLeService.getSupportedGattServices();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

                showMsg("Got string : " + new String(data));

                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }

                    Log.i("MainActivity","Get string(ASCII) : " + stringBuilder.toString());
                }
            }
        }
    };

    private void inputMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message to send");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                txtView.setText(text);
                if(text!=""&&text!=null){
                    btSendBytes(text.getBytes());
                }
            }
        });


        builder.show();
    }
    private void alertmsg(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage(R.string.alert_nottify_failed);
        builder.setIcon(drawable.ic_dialog_alert);
        builder.setPositiveButton(" OK. Bring me there", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                startActivity( new  Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }
    public void btSendBytes(byte[] data) {
        if (mBluetoothLeService != null &&
                mConnectionState.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
            mBluetoothLeService.writeCharacteristic(data);
        }
    }

    private class MyBluetoothScanCallBack implements BluetoothScan.BluetoothScanCallBack {
        @Override
        public void onLeScanInitFailure(int failureCode) {
            Log.i("MainActivity", "onLeScanInitFailure()");
            switch (failureCode) {
                case BluetoothScan.SCAN_FEATURE_ERROR :
                    showMsg("scan_feature_error");
                    break;
                case BluetoothScan.SCAN_ADAPTER_ERROR :
                    showMsg("scan_adapter_error");
                    break;
                default:
                    showMsg("unKnow_error");
            }
        }

        @Override
        public void onLeScanInitSuccess(int successCode) {
            Log.i("MainActivity", "onLeScanInitSuccess()");
            switch (successCode) {
                case BluetoothScan.SCAN_BEGIN_SCAN :
                    Log.i("MainActivity","successCode : " + successCode);
                    break;
                case BluetoothScan.SCAN_NEED_ENADLE :
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    break;
                case BluetoothScan.AUTO_ENABLE_FAILURE :
                    showMsg("auto_enable_bluetooth_error");
                    break;
                default:
                    showMsg("unKnow_error");
            }
        }

        @Override
        public void onLeScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(!mBluetoothDeviceList.contains(device) && device != null) {
                mBluetoothDeviceList.add(device);
                mBluetoothDeviceAdapter.notifyDataSetChanged();

                Log.i("MainActivity","notifyDataSetChanged() " + "BluetoothName :　" + device.getName() +
                        "  BluetoothAddress :　" + device.getAddress());
                if (device.getName().equals("MyESP32"))
                    swipeRefresh.setRefreshing(false);
                    BluetoothScan.stopScan();
                    onBluetoothDeviceClicked(device.getName(),device.getAddress());
                    Log.i("MainActivity","device result recognized and auto try to connect");
                    tvdev.setText(mDeviceName + " > connected");
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                showMsg("enable_bluetooth_error");
                return;
            } else if (resultCode == Activity.RESULT_OK) {
                if (mBluetoothDeviceList != null) {
                    mBluetoothDeviceList.clear();
                }
                scanLeDevice(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static Toast toast = null;

    public static void showMsg(String msg) {


        try {
            if (toast == null) {
                toast = Toast.makeText(MyApplication.context(), msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


