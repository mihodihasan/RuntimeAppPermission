package droiddigger.com.runtimeapppermission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "amartag";
    private static final String MY_PREFS_NAME = "droiddigger.com.runtimeapppermission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //For RuntimePermission I am saving first run status in shared pref
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("askingPermissionFirst", true);
        editor.commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ans=AskPermission();
                Toast.makeText(getApplicationContext(),""+ans,Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean AskPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                if(!prefs.getBoolean("askingPermissionFirst", true)){
                    if (shouldShowRequestPermissionRationale(Manifest.permission.
                            WRITE_EXTERNAL_STORAGE)) {
//                    Explain
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setTitle("Need Permission!")
                                .setMessage("App needs permission to write in your SD card so that app can download " +
                                        "images into your storage and you can read the posts offline. \n If you want " +
                                        "this offline feature, you have to give permission!\n Wanna permit us??")
                                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                })
                                .setNegativeButton("NO!", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setTitle("Permission Denied!")
                                .setMessage("Sir, Previously You Denied to Access Us To Write On Your Storage, " +
                                        "We cant ask you for this, please go to settings and permit us manually if you " +
                                        "need this feature, We only Can show you the pathway!\nJust Tap Settings and then " +
                                        "go to permissions and tap on the togglebar")
                                .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent();
                                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        i.addCategory(Intent.CATEGORY_DEFAULT);
                                        i.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton("NO!", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();
                    }
                    return false;
                }else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("askingPermissionFirst", false);
                    editor.commit();
                }
            }
        }
        return true;
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
        }

        return super.onOptionsItemSelected(item);
    }
}
