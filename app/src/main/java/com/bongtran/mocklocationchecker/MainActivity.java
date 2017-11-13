package com.bongtran.mocklocationchecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private TextView txtVersion, txtMocked, txtMockedApp, txtMockedLocation;

    private String TAG = "MOCKING CHECK";
    LocationManager lm;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMocked = findViewById(R.id.txtIsMocked);
        txtVersion = findViewById(R.id.txtVersion);
        txtMockedApp = findViewById(R.id.txtMockedApp);
        txtMockedLocation = findViewById(R.id.txtMockedLocation);

        txtVersion.setText("API Version " + Build.VERSION.CODENAME + " " + Build.VERSION.SDK_INT);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);



        if(checkPermissions()){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }else {
            setPermissions();
        }
//        try {
//            Log.d(TAG, "Removing Test providers");
//            lm.removeTestProvider(LocationManager.GPS_PROVIDER);
//        } catch (IllegalArgumentException error) {
//            Log.d(TAG, "Got exception in removing test  provider");
//        }
        boolean isMock = isMockSettingsON(this);

        boolean mockApp = areThereMockPermissionApps(this);

        if(isMock){
            txtMocked.setText("HAS MOCKED SETTING");
        }

        if(mockApp){
            txtMockedApp.setText("HAS MOCKED APP");
        }
    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            location.isFromMockProvider();
            txtMockedLocation.setText("FROM MOCKED LOCATION");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean mIsSettingPermission = false;

    private boolean checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
//        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MOCK_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        }


        return true;
    }

    private final int MY_PERMISSIONS_REQUEST_CODE = 1;

    private void setPermissions() {
        String[] requestPermission = new String[]{
//                Manifest.permission.INTERNET,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.EXPAND_STATUS_BAR

        };

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            requestPermission[requestPermission.length - 1] = Manifest.permission.READ_EXTERNAL_STORAGE;
//        }

        ActivityCompat.requestPermissions(this, requestPermission, MY_PERMISSIONS_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSIONS_REQUEST_CODE) {
            return;
        }

        boolean isGranted = true;

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }

        if (isGranted) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        } else {
            Toast.makeText(this, "DENIED", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
