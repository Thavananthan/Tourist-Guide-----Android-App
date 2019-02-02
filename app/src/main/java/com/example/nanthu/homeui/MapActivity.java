package com.example.nanthu.homeui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


public class MapActivity extends AppCompatActivity {

    private  static final String TAG="MapActivity";
    private static final int Error_Dialog_request=9001;

    private static final String FINELOCATION=Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String ACCESS_COARSE=Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int requestt_permission_code=1234;

    private boolean mlocationpermissiongreand=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);



      /*  if(isServicesOK()){
            init();
        }*/
    }

    private void intimap(){

    }

   /*private void init(){
       btn=(Button)findViewById(R.id.mapbtn);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });
   }
    public boolean isServicesOK(){
        Log.d(TAG,"isServiesOK:check in google service version");
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);
        if(available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServiesOK:check in google map working good");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServiesOK: Error");
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this,available,Error_Dialog_request);
            dialog.show();

        }else{
            Toast.makeText(this,"we can't make map request",Toast.LENGTH_LONG).show();
        }
        return false;
    }*/

   private void getlocation(){
       String[]permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

       if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINELOCATION)== PackageManager.PERMISSION_GRANTED){
           if(ContextCompat.checkSelfPermission(this.getApplicationContext(),ACCESS_COARSE)== PackageManager.PERMISSION_GRANTED){

               mlocationpermissiongreand=true;
           }else{
               ActivityCompat.requestPermissions(this,permission,requestt_permission_code);
           }

       }
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mlocationpermissiongreand=false;

        switch(requestCode){
            case requestt_permission_code :{
                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults.length>0 &&grantResults[i]==PackageManager.PERMISSION_GRANTED){
                            mlocationpermissiongreand=false;
                            return;
                        }
                    }
                    mlocationpermissiongreand=true;
                }
            }
        }
    }
}
