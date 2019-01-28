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

import static com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.ALL_POINTS;
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

    private float mStrokeWidth = 7;//ширина контура
    private int mStrokeAlpha = 255;


    private float mFaceRadius= 6;//ширина контура
    private int mFaceAlpha = 255;



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

    private String ColorDarkGreen = "#0d682d";
    private String ColorLightGreen = "#72ffa3";



    public static float LeftEyeOpenedProb;
    public static float RightEyeOpenedProb;
    public static float SmilingProb;

    public static  float AngleY;
    public static float AngleZ;





    public FaceView(Context context) {
        super(context);
        initialize();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initColors();
        initialize();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initColors();
        initialize();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initColors();
        initialize();
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




    private void initialize() {

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




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //drawStroke(canvas);

        drawAll(canvas);
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


/*
    private void drawFace(Canvas canvas, int part, int color) {
        mFacePaint.setColor(color);

        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                final FirebaseVisionFaceContour contour = face.getContour(part);
                List<FirebaseVisionPoint> points = contour.getPoints();

                for(FirebaseVisionPoint point : points) {
                    canvas.drawCircle(point.getX() * mScale, point.getY() * mScale, mFaceRadius, mFacePaint);
                }
            }
        }
    }
*/



    private void drawFace(Canvas canvas, int part, int color) {
        mFacePaint.setColor(color);

        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                final FirebaseVisionFaceContour contour = face.getContour(part);
                List<FirebaseVisionPoint> points = contour.getPoints();

                //Рисуем опорные точки
                for (int point = 0; point < points.size(); point++) {
                    canvas.drawCircle(points.get(point).getX() * mScale, points.get(point).getY() * mScale, mFaceRadius, mFacePaint);
                }
                //Соединяем их
                for (int point = 0; point < points.size() - 1; point++) {
                    canvas.drawLine(points.get(point).getX() * mScale, points.get(point).getY() * mScale, points.get(point + 1).getX() * mScale, points.get(point + 1).getY() * mScale, mFacePaint);
                }
            }
        }
    }


    private void drawStroke(Canvas canvas) {
        if (mFaces != null) {
            for (FirebaseVisionFace face : mFaces) {
                final Rect boundingBox = face.getBoundingBox();
                canvas.drawRect(boundingBox.left * mScale,
                        boundingBox.top * mScale,
                        boundingBox.right * mScale,
                        boundingBox.bottom * mScale,
                        mStrokePaint);
            }
        }
    }






    void drawAll(Canvas canvas) {
        drawFace(canvas, FACE, Color.parseColor(ColorPink));//Контур лица

        //Глаза
        drawFace(canvas, LEFT_EYE, Color.parseColor(ColorLightGreen));//Левый глаз
        drawFace(canvas, RIGHT_EYE, Color.parseColor(ColorDarkGreen));//Правый глаз


        //Губы
        drawFace(canvas, LOWER_LIP_TOP, Color.parseColor(ColorBlue));//Низ нижней губы
        drawFace(canvas, LOWER_LIP_BOTTOM, Color.parseColor(ColorBlue));//Верх нижней губы

        drawFace(canvas, UPPER_LIP_TOP, Color.parseColor(ColorDarkBlue) );//Верх верхней губы
        drawFace(canvas, UPPER_LIP_BOTTOM, Color.parseColor(ColorDarkBlue));//Низ верхней губы

        //Нос
        drawFace(canvas, NOSE_BOTTOM, Color.parseColor(ColorPurple));//Ноздри
        drawFace(canvas, NOSE_BRIDGE, Color.parseColor(ColorRed));//Переносица и кончик носа

        //Брови
        drawFace(canvas, LEFT_EYEBROW_TOP, Color.parseColor(ColorYellow));//Верх левой брови
        drawFace(canvas, LEFT_EYEBROW_BOTTOM, Color.parseColor(ColorYellow));//Низ левой брови
        drawFace(canvas, RIGHT_EYEBROW_TOP, Color.parseColor(ColorOrange));//Верх правой брови
        drawFace(canvas, RIGHT_EYEBROW_BOTTOM, Color.parseColor(ColorOrange));//Низ правой брови

    }
}
