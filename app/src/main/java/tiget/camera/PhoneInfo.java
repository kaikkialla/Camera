package tiget.camera;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PhoneInfo extends Fragment {



    TextView mDeviceName;
    TextView mAndroidVersion;
    TextView mApiVersion;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.phone_info_fragment, container, false);
    }








    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*
        mDeviceName = view.findViewById(R.id.device_name);
        mAndroidVersion = view.findViewById(R.id.android_version);
        mApiVersion = view.findViewById(R.id.sdk_version);

        mDeviceName.setText(getDeviceName());
        mAndroidVersion.setText(getAndroidVersion());
        mApiVersion.setText(getApiVersion());
        */

    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        return release ;
    }

    public String getApiVersion() {
        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        return sdkVersion;
    }


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }


}
