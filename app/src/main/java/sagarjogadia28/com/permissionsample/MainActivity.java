package sagarjogadia28.com.permissionsample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_LOCATION = 2;
    private static final int REQUEST_MESSAGE = 3;
    private static final int REQUEST_MULTIPLE = 4;

    private static final String LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String SMS = Manifest.permission.SEND_SMS;
    private CoordinatorLayout coordinatorLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        /**
         * shouldShowRequestPermissionRationale() returns true if the app has requested this permission previously and the user
         * denied the request
         * false in DONT ASK AGAIN
         * */

        checkForPermission();

        /**
         * requestPermissions() shows a standard dialog box to the user
         * */
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkForPermission() {
        int permissionCheck = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Granted");
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                Log.d(TAG, "Contacts Permission Required!!");
                createSnackbar("Contacts Permission Required!!", "Try Again");
            }
            ActivityCompat.
                    requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onRestart() {
        super.onRestart();
        checkForPermission();
        checkMultiplePermissions();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Granted");
                } else {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale
                            (MainActivity.this, Manifest.permission.READ_CONTACTS);

                    if (!showRationale) {
                        Log.d(TAG, "onRequestPermissionsResult: IF");
                        createSettingsSnackbar("Contacts Permission Required!!");
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult: ELSE");
                        createSnackbar("Contacts Permission Required!!", "Try Again");
                    }

                }
                break;

            case REQUEST_MULTIPLE:
                Map<String, Integer> permissionMap = new HashMap<>();
                permissionMap.put(SMS, PackageManager.PERMISSION_GRANTED);
                permissionMap.put(LOCATION, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        Log.d(TAG, "onRequestPermissionsResult: PERMISSION: " + permissions[i] + " GRANT:" + grantResults[i]);
                        permissionMap.put(permissions[i], grantResults[i]);
                    }

                    if (permissionMap.get(SMS) == PackageManager.PERMISSION_GRANTED
                            && permissionMap.get(LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Granted");
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION)) {
                            //createSnackbar
                            createMultiplePermSnackbar("Permissions Required!!", "Try Again");
                        } else {
                            //goto settings
                            createSettingsSnackbar("Permissions Required!!");
                        }
                    }
                }


                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void createSnackbar(String message, String action) {
        Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.
                                requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
                    }
                })
                .show();
    }

    private void createMultiplePermSnackbar(String message, String action) {
        Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkMultiplePermissions();
                    }
                })
                .show();
    }

    private void createSettingsSnackbar(String message) {
        Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: Settings!!");
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .show();

    }

    public void multiplePermission(View view) {
        checkMultiplePermissions();
    }

    private void checkMultiplePermissions(){
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, LOCATION);
        List<String> multiplePermissions = new ArrayList<>();
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED)
            multiplePermissions.add(LOCATION);
        if (locationPermission != PackageManager.PERMISSION_GRANTED)
            multiplePermissions.add(SMS);

        if (!multiplePermissions.isEmpty())
            ActivityCompat.requestPermissions(this, multiplePermissions.toArray(new String[multiplePermissions.size()]), REQUEST_MULTIPLE);
    }
}
