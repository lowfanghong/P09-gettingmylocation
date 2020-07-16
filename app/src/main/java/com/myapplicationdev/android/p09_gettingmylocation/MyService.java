package com.myapplicationdev.android.p09_gettingmylocation;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {

    boolean started;
    private FusedLocationProviderClient client;
    private LocationCallback mLocationCallback;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        Log.d("Service", "Service created");
        super.onCreate();

        client = LocationServices.getFusedLocationProviderClient(this);

       String folderlocation = Environment.
               getExternalStorageDirectory().getAbsolutePath() + "/P09";
       File folder = new File(folderlocation);
       if (folder.exists() == false){
           boolean result = folder.mkdir();
           if (result == false ){
               Toast.makeText(MyService.this,
                       "Folder cannot be created in External memory",
                       Toast.LENGTH_SHORT).show();
               stopSelf();
           }
       }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service","OnStart");

           if (checkPermission() == true) {
               LocationRequest mLocationRequest = new LocationRequest();
               mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
               mLocationRequest.setInterval(1000);
               mLocationRequest.setFastestInterval(5000);
               mLocationRequest.setSmallestDisplacement(100);
               client.requestLocationUpdates(mLocationRequest,mLocationCallback,null);

           }else{
               stopSelf();
           }

        return Service.START_STICKY;
    }

    private boolean checkPermission() {

        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_Storage = ContextCompat.checkSelfPermission
                (MyService.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Storage == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            String msg = "permission not granted";
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();

            return false;
        }
    }
    private void createLocationCallback(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult != null){
                    Location locData = locationResult.getLastLocation();
                    String data = locData.getLatitude() + "," + locData.getLongitude();
                    Log.d("Service-loc changed",data);
                    String folderlocation = Environment.
                            getExternalStorageDirectory().getAbsolutePath() + "/P09";
                    File targetfile = new File(folderlocation,"data.txt");
                    try{
                        FileWriter writer = new FileWriter(targetfile,true);
                        writer.write(data +"\n");
                        writer.flush();
                        writer.close();

                    }catch (Exception e){
                        Toast.makeText(MyService.this,"failed to write! ",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            };
        };


    }


}
