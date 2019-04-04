package guc.edu.thermonitor;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listActivity extends AppCompatActivity {
    int[] images={R.drawable.esp};
    String[] names={"Android","iPhone","Windows","Blackberry","Linux"};
    private Button Logout;
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient mGoogleSignInClient;
    LocationManager locationManager;
    String provider;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 102;
    private LocationRequest locationRequest;
    private boolean permissionIsGranted = false;
    private Button Settings;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private ArrayList<String> ssid =new ArrayList<>();
    private ArrayList<String> bssid =new ArrayList<>();
    private ArrayList<String> data=new ArrayList<>();
    private CustomAdapter customAdapter=new CustomAdapter();
    private Button scan;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Mac Address");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        requestLocationUpdates();


        //creating variables
        firebaseAuth=FirebaseAuth.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        Logout=(Button)findViewById(R.id.edtLogoutL);
        ListView CustomList=(ListView)findViewById(R.id.edtCustomList);

        Settings=(Button)findViewById(R.id.edtSettingL);
        scan=(Button)findViewById(R.id.edtScanL);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(15 * 1000);

        wifiManager=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(!(wifiManager.isWifiEnabled())){
            Toast.makeText(this,"WiFi is disabled... We need to enable it",Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        //creating and setting custom adaptor
        //bridge between an AdapterView and the underlying data for that view

        CustomList.setAdapter(customAdapter);
        CustomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(getApplicationContext(),DeviceDetailActivity.class);
                intent.putExtra("name", ssid.get(position));
                //intent.putExtra("image",images[0]);
                startActivity(intent);
            }
        });

        scanWifi();
        //logout process
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                                Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                            }
                        });
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_FINE_LOCATION);}
                else{
                    permissionIsGranted=true;
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });
    }
    private void ListenFromDatabase() {
        //database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                data.clear();
                for ( DataSnapshot dataSnapshot1 : dataSnapshot.getChildren() ) {
                    String value = dataSnapshot1.getValue(String.class);
                    data.add(value);
                    Log.d("file", "Value is: " + value);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("file", "Failed to read value.", error.toException());
            }
        });
    }

    //scan wifi method
    private void scanWifi(){
        ssid.clear();
        bssid.clear();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this,"Scanning WiFi...",Toast.LENGTH_LONG).show();
    }

    BroadcastReceiver wifiReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results=wifiManager.getScanResults();
            unregisterReceiver(this);
            ListenFromDatabase();

            for(ScanResult scanResult: results){
                if(scanResult.SSID.equals("MyESPT") | scanResult.SSID.equals("MyESPR")) {
                    ssid.add(scanResult.SSID);
                    bssid.add(scanResult.BSSID);
                    customAdapter.notifyDataSetChanged();

                }
            }

        }
    };

    //Preventing back pressing


    //Custom adapter class
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return ssid.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //LayoutInflater is a class used to instantiate layout XML file into its corresponding view objects which can be used in java programs.
            convertView=getLayoutInflater().inflate(R.layout.customlayout,null);

            ImageView imageView=(ImageView)convertView.findViewById(R.id.edtImage);
            TextView textView=(TextView)convertView.findViewById(R.id.edtText);
            TextView textBSSID=(TextView)convertView.findViewById(R.id.edtBSSID);

            imageView.setImageResource(images[0]);
            textView.setText("SSID: "+ssid.get(position));
            textBSSID.setText("Mac Address: "+bssid.get(position));


            return convertView;
        }
    }

    //getting user
    @Override
    protected void onStart(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("359486822120-7737qdnoa5uar7f4jrk4kemg818jbn8r.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignInClient.connect();
        super.onStart();

    }

    private void requestLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_REQUEST_FINE_LOCATION);}
            else{
                permissionIsGranted=true;
            }
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(permissionIsGranted){
            if(mGoogleSignInClient.isConnected()){
                requestLocationUpdates();
            }
            if(firebaseAuth.getCurrentUser()!=null){
                requestLocationUpdates();
            }
        }
    }

    //@Override
    // protected void onPause() {
    //   super.onPause();
    //}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //permission is granted
                    permissionIsGranted=true;
                }
                else{
                    //permission is denied
                    permissionIsGranted=false;
                    Toast.makeText(this,"The App Requires Location Permission Access",Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSION_REQUEST_COARSE_LOCATION:
                //repeat
                break;
        }
    }

}