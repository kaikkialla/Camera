package tiget.camera;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NoCameraPermissionFragment extends Fragment {


    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@android.support.annotation.NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.no_camera_permission_fragment, container, false);
    }

    @Override
    public void onViewCreated(@android.support.annotation.NonNull View view, @android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
