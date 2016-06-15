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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CONTACT = 1;
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
                Snackbar
                        .make(coordinatorLayout, "Contacts Permission Required!!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.
                                        requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
                            }
                        })
                        .show();
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
                        Snackbar
                                .make(coordinatorLayout, "Contacts Permission Required!!", Snackbar.LENGTH_INDEFINITE)
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
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult: ELSE");
                        Snackbar
                                .make(coordinatorLayout, "Contacts Permission Required!!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Try Again", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.
                                                requestPermissions(MainActivity.this,
                                                        new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
                                    }
                                })
                                .show();
                    }

                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
