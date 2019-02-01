package tiget.camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;

import java.util.List;

import androidx.annotation.Nullable;

import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.FACE;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.LEFT_EYE;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.LEFT_EYEBROW_TOP;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.LOWER_LIP_BOTTOM;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.LOWER_LIP_TOP;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.NOSE_BOTTOM;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.NOSE_BRIDGE;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.RIGHT_EYE;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.UPPER_LIP_BOTTOM;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.UPPER_LIP_TOP;

public class FaceView extends View {



    private static List<FirebaseVisionFace> mFaces;

    private Paint mStrokePaint; // кисть
    private Paint mFacePaint; // кисть


    private float mScale; // во сколько раз нужно увеличить те faces, который отдал ML Kit

    private float mStrokeWidth = 10;//ширина контура
    private int mStrokeAlpha = 255;


    private float mFaceRadius= 20;//радиус точек
    private int mFaceAlpha = 255;
    float faceDotsScale;



    private int ColorPrimary;
    private int ColorAccent;

    private String ColorRed = "#f70000";
    private String ColorOrange = "#f7a000";
    private String ColorYellow = "#ffe138";
    //private String ColorGreen = "#3eff37";

    private String ColorBlue = "#5fc0fc";
    private String ColorDarkBlue = "#2b13c6";

    private String ColorPurple = "#973ad1";

    private String ColorPink = "#fca5ff";

    private String ColorDarkGreen = "#1ecc5b";
    private String ColorLightGreen = "#72ffa3";



    public static float LeftEyeOpenedProb;
    public static float RightEyeOpenedProb;
    public static float SmilingProb;

    public static  float AngleY;
    public static float AngleZ;

    public static float LandmarkX;
    public static float LandmarkY;

    public float faceWidth;
    public float faceHeight;







    public FaceView(Context context) {
        super(context);
        initColors();
        initPaints();

    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initColors();
        initPaints();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initColors();
        initPaints();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initColors();
        initPaints();
    }



    public void showFaces(List<FirebaseVisionFace> faces, float scale) {
        mFaces = faces;
        mScale = scale;
        invalidate();
    }





    private void initColors() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        TypedArray b = getContext().obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });

        ColorPrimary = a.getColor(0, 0);
        ColorAccent = b.getColor(0, 0);

        a.recycle();
        b.recycle();
    }







    private void initPaints() {

        mStrokePaint = new Paint();
        mFacePaint = new Paint();


        mStrokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setColor(ColorPrimary);
        mStrokePaint.setAlpha(mStrokeAlpha);



        mFacePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mFacePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mFacePaint.setColor(ColorAccent);
        mFacePaint.setAlpha(mFaceAlpha);

    }









    public static float LeftEyeOpenedProb() {

        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                LeftEyeOpenedProb = face.getLeftEyeOpenProbability();
            }
        }
        return LeftEyeOpenedProb;
    }


    public static  float RightEyeOpenedProb() {
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                RightEyeOpenedProb = face.getLeftEyeOpenProbability();
            }
        }
        return RightEyeOpenedProb;
    }


    public static  float SmilingProb() {
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                SmilingProb = face.getSmilingProbability();
            }
        }
        return SmilingProb;
    }


    public static  float AngleZ() {
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                AngleZ = face.getHeadEulerAngleZ();
            }
        }
        return AngleZ;
    }


    public static float AngleY() {
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                AngleY = face.getHeadEulerAngleY();
            }
        }
        return AngleY;
    }






    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //drawStroke(canvas);

        getFaceSize(canvas);
        drawAll(canvas);
    }





    /*
    @part - часть, которую надо отрисовать
    @shape - тип(0 - ломанная, 1 - замкнутая
    @color - цвет части(точки и линии)
     */
    private void drawFace(Canvas canvas, int part, int shape, int color) {
        mFacePaint.setColor(color);

        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                final FirebaseVisionFaceContour contour = face.getContour(part);
                List<FirebaseVisionPoint> points = contour.getPoints();


                if(shape == 0) {//Ломанная

                    try {
                        //Рисуем опорные точки
                        for (int point = 0; point < points.size(); point++) {
                            canvas.drawCircle(points.get(point).getX() * mScale,
                                    points.get(point).getY() * mScale,
                                    faceDotsScale,
                                    mFacePaint);
                        }
                    } catch (Exception e) { }

                    try {
                        //Соединяем их
                        for (int point = 0; point < points.size() - 1; point++) {
                            canvas.drawLine(points.get(point).getX() * mScale,
                                    points.get(point).getY() * mScale,
                                    points.get(point + 1).getX() * mScale,
                                    points.get(point + 1).getY() * mScale,
                                    mFacePaint);
                        }
                    } catch (Exception e) { }



                } else if(shape == 1) {//Замкнутая


                    try {
                        //Рисуем опорные точки
                        for (int point = 0; point < points.size(); point++) {
                            canvas.drawCircle(points.get(point).getX() * mScale,
                                    points.get(point).getY() * mScale,
                                    faceDotsScale,
                                    mFacePaint);
                        }
                    } catch (Exception e) { }


                    try {
                        //Соединяем их
                        for (int point = 0; point < points.size() - 1; point++) {
                            canvas.drawLine(points.get(point).getX() * mScale,
                                    points.get(point).getY() * mScale,
                                    points.get(point + 1).getX() * mScale,
                                    points.get(point + 1).getY() * mScale,
                                    mFacePaint);
                        }
                    } catch (Exception e) { }


                    try {
                        //Соединяем первую и последнюю
                        canvas.drawLine(points.get(0).getX() * mScale,
                                points.get(0).getY() * mScale,
                                points.get(points.size() - 1).getX() * mScale,
                                points.get(points.size() - 1).getY() * mScale,
                                mFacePaint);
                    } catch (Exception e) { }
                }

            }
        }
    }




    private void drawStroke(Canvas canvas) {
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                final Rect boundingBox = face.getBoundingBox();

                try {
                    canvas.drawRect(boundingBox.left * mScale,
                            boundingBox.top * mScale,
                            boundingBox.right * mScale,
                            boundingBox.bottom * mScale,
                            mStrokePaint);    
                } catch (Exception e) {}

            }
        }
    }



    private void getFaceSize(Canvas canvas) {
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                final Rect boundingBox = face.getBoundingBox();
                faceHeight = boundingBox.height();
                faceWidth = boundingBox.width();
                faceDotsScale = (float) ((faceWidth / canvas.getWidth()) * 10 * 1.7);
                Log.e("fjafjafdafpa", String.valueOf(faceDotsScale));
            }
        }
    }





    void drawAll(Canvas canvas) {

        drawFace(canvas, FACE, 1, Color.parseColor(ColorPink));//Контур лица

        //Глаза
        drawFace(canvas, LEFT_EYE, 1, Color.parseColor(ColorLightGreen));//Левый глаз
        drawFace(canvas, RIGHT_EYE, 1, Color.parseColor(ColorDarkGreen));//Правый глаз


        //Губы
        drawFace(canvas, LOWER_LIP_TOP, 0, Color.parseColor(ColorBlue));//Низ нижней губы
        drawFace(canvas, LOWER_LIP_BOTTOM, 0, Color.parseColor(ColorBlue));//Верх нижней губы

        drawFace(canvas, UPPER_LIP_TOP,0,  Color.parseColor(ColorDarkBlue) );//Верх верхней губы
        drawFace(canvas, UPPER_LIP_BOTTOM,0,  Color.parseColor(ColorDarkBlue));//Низ верхней губы


        //Нос
        drawFace(canvas, NOSE_BOTTOM, 0, Color.parseColor(ColorPurple));//Ноздри
        drawFace(canvas, NOSE_BRIDGE, 0, Color.parseColor(ColorRed));//Переносица и кончик носа


        //Брови
        drawFace(canvas, LEFT_EYEBROW_TOP, 0, Color.parseColor(ColorYellow));//Верх левой брови
        drawFace(canvas, LEFT_EYEBROW_BOTTOM, 0, Color.parseColor(ColorYellow));//Низ левой брови
        drawFace(canvas, RIGHT_EYEBROW_TOP, 0, Color.parseColor(ColorOrange));//Верх правой брови
        drawFace(canvas, RIGHT_EYEBROW_BOTTOM, 0, Color.parseColor(ColorOrange));//Низ правой брови

    }
}
