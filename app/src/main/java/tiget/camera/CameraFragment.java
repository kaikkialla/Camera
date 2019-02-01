package tiget.camera;



import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.CAMERA_SERVICE;
import static android.hardware.camera2.CameraCharacteristics.LENS_FACING;
import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;
import static android.hardware.camera2.CameraMetadata.CONTROL_AF_MODE_AUTO;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;
import static java.lang.System.currentTimeMillis;
import static tiget.camera.FaceView.AngleY;
import static tiget.camera.FaceView.AngleZ;
import static tiget.camera.FaceView.LandmarkX;
import static tiget.camera.FaceView.LandmarkY;
import static tiget.camera.FaceView.LeftEyeOpenedProb;
import static tiget.camera.FaceView.RightEyeOpenedProb;
import static tiget.camera.FaceView.SmilingProb;
import static tiget.camera.ImageClassifier.DIM_IMG_SIZE_X;
import static tiget.camera.ImageClassifier.DIM_IMG_SIZE_Y;

public class CameraFragment extends Fragment {

    public static final String TAG = CameraFragment.class.getSimpleName();

    //private static final float FACE_IMAGE_SCALE = 0.3f; // во сколько раз нужно уменьшить картинку с камеры для детектора лиц

    private TextureView mTextureView; // для превью с камеры
    private FaceView mFaceView; // для контуров лиц

    private TextView mLabelView; // для результата Image Labeling
    private TextView mFaceInfoView; // для результата Image Labeling
    private TextView mCameraSpeedInfo; // для результата Image Labeling
    ImageView mChangeCameraButton;


    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private FirebaseVisionLabelDetector mFirebaseLabelDetector; // детектор предметов
    private FirebaseVisionFaceDetector mFirebaseFaceDetector; // детектор лиц

    CameraCharacteristics characteristics;
    CameraManager cameraManager;
    CameraDevice mCamera;

    int mCameraPermission;

    int FrontCameraId = LENS_FACING_FRONT;
    int BackCameraId = LENS_FACING_BACK;

    private String backCameraTag = "BACK_CAMERA";
    private String frontCameraTag = "FRONT_CAMERA";




    ImageClassifier mImageClassifier;


    public CameraFragment() {
        // stub
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextureView = view.findViewById(R.id.texture_view);
        mFaceView = view.findViewById(R.id.face_view);

        mCameraSpeedInfo = view.findViewById(R.id.camera_speed_info);
        mLabelView = view.findViewById(R.id.label);
        mFaceInfoView = view.findViewById(R.id.face_info);

        mChangeCameraButton = view.findViewById(R.id.changeCamera);

        mChangeCameraButton.setTag(backCameraTag);
        mChangeCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mChangeCameraButton.getTag().equals(backCameraTag)) {
                    mCamera.close();
                    openCamera(LENS_FACING_FRONT);
                    mChangeCameraButton.setTag(frontCameraTag);
                } else if(mChangeCameraButton.getTag().equals(frontCameraTag)) {
                    mCamera.close();
                    openCamera(LENS_FACING_BACK);
                    mChangeCameraButton.setTag(backCameraTag);
                }

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();

        mCameraPermission = getActivity().checkSelfPermission(Manifest.permission.CAMERA);

        if(mCameraPermission == 0) {
            if (mTextureView.isAvailable()) {
                startThread();
                openCamera(BackCameraId);
            } else {

                mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        startThread();
                        openCamera(BackCameraId);
                    }
                    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }
                    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        return false;
                    }
                    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                    }
                });
            }

        } else if(mCameraPermission == -1) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.f, new NoCameraPermissionFragment()).commit();
        }

        initFirebaseLabelDetector();
        initFirebaseFaceDetector();
        //initTenserflowDetecter();

    }


    private void initFirebaseLabelDetector() {
        // создаём настройки
        final FirebaseVisionLabelDetectorOptions options =
                new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.8f)
                        .build();
        // создаём FirebaseVisionLabelDetector
        mFirebaseLabelDetector = FirebaseVision.getInstance()
                .getVisionLabelDetector(options);
    }


    private void initFirebaseFaceDetector() {
        // настраиваем детектор лиц
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
                        .setMinFaceSize(0.1f)
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .build();
        // создаём детектор лиц
        mFirebaseFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
    }



    private void initTenserflowDetecter() {
        try {
            mImageClassifier = new ImageClassifier(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean mClassifierStarted;

    final Runnable mClassifierRunnable = new Runnable() {
        @Override
        public void run() {
            if (mClassifierStarted) {
                final Bitmap bitmap = mTextureView.getBitmap(DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y);
                final String result = mImageClassifier.classifyFrame(bitmap);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLabelView.setText(result);
                        }
                    });
                }
                mBackgroundHandler.post(mClassifierRunnable);
            }
        }
    };

    private void startClassifier() {
        if (!mClassifierStarted) {
            mClassifierStarted = true;
            mBackgroundHandler.post(mClassifierRunnable);
        }
    }
















    private void startThread() {
        // создаём поток
        mBackgroundThread = new HandlerThread(TAG);
        // стартуем его
        mBackgroundThread.start();
        // создаёми Handler, связываем его с тем потоком, который создали
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }




    private void captureCamera( final CameraDevice camera) {
        // получаем Surface из mTextureView
        // Surface - поверхность для отображения разных данных (просто видео, превью из камеры и так далее)
        final Surface surface = new Surface(mTextureView.getSurfaceTexture());
        // createCaptureSession принимает только список из Surface((, поэтому нужно создавать список из одного элемента
        final List<Surface> surfaces = Arrays.asList(surface);
        try {
            // создаём сессию захвата картинки с камеры
            // сессия примерно = подключение
            camera.createCaptureSession(surfaces
                    , new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            // когда сессия настроена - запускаем превью с камеры
                            startRecord(camera, session, surface);
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            // TODO показать ошибку
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "captureCamera", e);
        }
    }


    private void startRecord(CameraDevice camera, CameraCaptureSession session, Surface surface) {
        try {
            // ок, сессия настроена, теперь создаём запрос картинки с камеры
            final CaptureRequest.Builder requestBuilder = camera.createCaptureRequest(TEMPLATE_PREVIEW);
            // картинка должна передаваться в Surface (который с mTextureView)
            requestBuilder.addTarget(surface);
            // автофокус - авто
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CONTROL_AF_MODE_AUTO);
            // setRepeatingRequest означает, что видео записываем, а не фото снимаем
            session.setRepeatingRequest(requestBuilder.build()
                    , new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                            startFirebaseLabelDetecting();
                            startFirebaseFaceDetecting();
                        }
                    }
                    , mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "captureCamera", e);
        }
    }


    private boolean mFirebaseLabelDetectingStarted = false;
    private boolean mFirebaseFaceDetectingStarted = false;


    private void startFirebaseLabelDetecting() {
        // если ещё не стартовали обработку
        if (!mFirebaseLabelDetectingStarted) {
            // то стартуем её (меняем флаг на true)
            mFirebaseLabelDetectingStarted = true;
            processFirebaseLabelDetecting();
        }
    }


    private void processFirebaseLabelDetecting() {
        final long now = currentTimeMillis(); // запоминаем время старта обработки
        Bitmap bitmap = mTextureView.getBitmap(); // получаем битмап из TextureView
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap); // преобразовываем в нужный формат
        mFirebaseLabelDetector.detectInImage(image) // запускаем детектирование
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionLabel> firebaseLabels) {
                        String result = (currentTimeMillis() - now) + "ms\n"; // в текстовое поле пишем время, которое потратили на запрос
                        String object = "";
                        final List<String> labels = new ArrayList<>(); // создаём список пустой
                        for (FirebaseVisionLabel firebaseLabel : firebaseLabels) { // смотрим на то, что получили от ML Kit
                            // и пишем название предмета и его confidence
                            object += firebaseLabel.getLabel() + " - " + firebaseLabel.getConfidence() + "\n";
                        }
                        mCameraSpeedInfo.setText(result); // отображаем из в TextView через запятую
                        mLabelView.setText(object);
                        processFirebaseLabelDetecting();
                    }
                });
    }


    private void startFirebaseFaceDetecting() {
        // если ещё не стартовали обработку
        if (!mFirebaseFaceDetectingStarted) {
            // то стартуем её (меняем флаг на true)
            mFirebaseFaceDetectingStarted = true;
            processFirebaseFaceDetecting();
        }
    }


    private void processFirebaseFaceDetecting() {
        final long now = currentTimeMillis(); // запоминаем время старта обработки
        Bitmap bitmap = mTextureView.getBitmap(); // получаем битмап из TextureView
        // сжимаем картинку, чтобы обработка проходила быстрее

        int width = 400;
        final float scale = (float) width / bitmap.getWidth();
        int height = (int) (bitmap.getHeight() * scale);


        bitmap = Bitmap.createScaledBitmap(bitmap
                , width
                , height
                , true);

        byte[] bytes = getYV12(width, height, bitmap);

       FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder().setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12).setWidth(width).setHeight(height).build();


        FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(bytes, metadata); // преобразовываем в нужный формат

        mFirebaseFaceDetector.detectInImage(image) // запускаем детектирование
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseFaces) {
                        Log.v(TAG, "processFirebaseFaceDetecting: time=" + (currentTimeMillis() - now) + "ms\n");
                        // рисуем контуры лиц
                        mFaceView.showFaces(firebaseFaces, 1 / scale);


                        if (firebaseFaces != null) {
                            for(FirebaseVisionFace face : firebaseFaces) {
                                mFaceInfoView.setText("Left Eye: " + face.getLeftEyeOpenProbability() + "\n" +
                                        "Right eye: " + face.getRightEyeOpenProbability() + "\n" +
                                        "Smiling: " +face.getSmilingProbability());
                            }


                        }
                        processFirebaseFaceDetecting();

                    }
                });
    }




    @SuppressLint("MissingPermission")
    private void openCamera(int camera) {
        // получаем CameraManager
        cameraManager = (CameraManager) getActivity().getSystemService(CAMERA_SERVICE);
        if (cameraManager != null) { // система может отдать null вместо cameraManager
            try {
                // получаем идентификаторы камеры
                final String[] cameraIds = cameraManager.getCameraIdList();
                // перебираем их


                String cameraId = null;
                for (String i : cameraIds) {
                    // получаем характеристики камеры
                    characteristics = cameraManager.getCameraCharacteristics(i);
                    if (characteristics.get(LENS_FACING) == camera) {
                        cameraId = i;
                        break;
                    }
                }


                // открываем камеру (если нашли заднюю)
                if (cameraId != null) {
                    cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            mCamera = camera;
                            captureCamera(camera);
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            camera.close();
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            Log.v(TAG, "onError: error=" + error);
                        }
                    }, mBackgroundHandler);
                }
            } catch (CameraAccessException e) {
                Log.e(TAG, "openCamera", e);
            }
        }
    }












    private byte [] getYV12(int inputWidth, int inputHeight, Bitmap scaled) {

        int [] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte [] yuv = new byte[inputHeight * inputWidth + 2 * (int) Math.ceil(inputHeight/2.0) *(int) Math.ceil(inputWidth/2.0)];
        encodeYV12(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuv;
    }

    private void encodeYV12(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uIndex = frameSize;
        int vIndex = frameSize + (frameSize / 4);

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // YV12 has a plane of Y and two chroma plans (U, V) planes each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[vIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }
    }

}
