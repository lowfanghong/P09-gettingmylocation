package com.myapplicationdev.android.p09_gettingmylocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient client ;
    TextView tvLat, tvLong;
    Button btnRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLat = findViewById(R.id.textViewLat);
        tvLong = findViewById(R.id.textViewLo);

        client = LocationServices.getFusedLocationProviderClient(this);

        if(checkPermission() == true){
            Task<Location> task = client.getLastLocation() ;
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        tvLat.setText("Latitude :" + String.valueOf(location.getLatitude()));
                        tvLong.setText("Longtitude :" + String.valueOf(location.getLongitude()));
                    }else {
                        String msg = "No Last known location found";
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

btnRecord.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String folderlocation = Environment.
                getExternalStorageDirectory().getAbsolutePath() + "/P09";
        File targetfile = new File(folderlocation,"data.txt");
        if(targetfile.exists()){
            String data = "";
            try {
                FileReader reader = new FileReader(targetfile);
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line != null){
                    data += line + "\n";
                    line = br.readLine();

                }

                br.close();
                reader.close();



            }catch (Exception e){
                Toast.makeText(MainActivity.this,
                        "failed to read! ",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this,
                    data,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this,
                    "Record file does not exist ",Toast.LENGTH_SHORT).show();
        }

    }
});


}
    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_Storage = ContextCompat.checkSelfPermission
                (this,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
                && permissionCheck_Storage == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            String msg = "permission not granted";
            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();

            return false;
        }
    };}