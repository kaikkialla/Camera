package tiget.camera;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.RequiresApi;

import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;

public class MainActivity extends AppCompatActivity {


    int mCameraPermission;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //getSupportFragmentManager().beginTransaction().replace(R.id.f, new PhoneInfo()).commit();
        requestPermission();
        //checkPermission();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        mCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        if(mCameraPermission == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.f, new CameraFragment()).commit();
            Log.e("fafjiafafia", "MainActivity granted");
        } else if(mCameraPermission == -1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.f, new NoCameraPermissionFragment()).commit();
            Log.e("fafjiafafia", "MainActivity denied");
            //Ну вообще тут надо requestPermission() делать
        }
    }

/*
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }
    */

    public void requestPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.f, new CameraFragment()).commit();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        // TODO show message
                        //Если в диалоге отменить
                        getSupportFragmentManager().beginTransaction().replace(R.id.f, new NoCameraPermissionFragment()).commit();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        // TODO show message
                    }
                }).check();
    }

}
