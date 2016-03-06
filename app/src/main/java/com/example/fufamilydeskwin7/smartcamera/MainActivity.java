package com.example.fufamilydeskwin7.smartcamera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2,View.OnClickListener {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    int HSVs, G7_C, G11_C;
    StringBuilder value = new StringBuilder();
    int imgcount=0,updatespeed=100;
    int model=0;
    Size Camerasize;

    private TextView text;
    private Button hsvs_btn, G7C_btn, G11C_btn, sobelY, sobelX, clear, bodybtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = (Button) findViewById(R.id.clear);
        hsvs_btn = (Button) findViewById(R.id.hsv_s_btn);
        G7C_btn = (Button) findViewById(R.id.G7_C_btn);
        G11C_btn = (Button) findViewById(R.id.G11_C_btn);
        sobelY = (Button) findViewById(R.id.sobelY);
        sobelX = (Button) findViewById(R.id.sobelX);
        bodybtn = (Button) findViewById(R.id.bodybtn);

        clear.setOnClickListener(this);
        hsvs_btn.setOnClickListener(this);
        G7C_btn.setOnClickListener(this);
        G11C_btn.setOnClickListener(this);
        sobelY.setOnClickListener(this);
        sobelX.setOnClickListener(this);
        bodybtn.setOnClickListener(this);


        text = (TextView) findViewById(R.id.textview);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
     /*   if (imgcount == updatespeed) {
            Log.i(TAG, "text");
            value.setLength(0);
            value.append("HSV_S      : ").append(String.valueOf(HSVs)).append("\n");
            value.append("G7_C80100  : ").append(String.valueOf(G7_C)).append("\n");
            value.append("G11_C80100 : ").append(String.valueOf(G11_C)).append("\n");
            text.setText(value);
            if (HSVs > 2000 || G7_C > 200 || G11_C > 100) {
                text.setBackgroundColor(0xFFFF0000);
            } else {
                text.setBackgroundColor(0xFF00FF00);
            }
        }*/

    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        Camerasize = mRgba.size();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Log.i(TAG, "camera star");
        mRgba = inputFrame.rgba();
//        Log.i(TAG, "inputFrame.rgba()");
        Mat hsvvalue = new Mat();
        hsvvalue = mRgba.submat(mRgba.width()/2-1, mRgba.width()/2, mRgba.height()/2-1,mRgba.height()/2);
        Mat img = new Mat();
        Imgproc.resize(mRgba, img, new Size(320, 240));
//        Core.transpose(mRgba, mRgba);
//        Core.flip(mRgba, mRgba, 1);
        Mat halfimg = Imageprocessing.get_halfimg(img, 0);


        Mat hsv = Imageprocessing.RGB2HSV(halfimg);
        hsv = Imageprocessing.get_HSV_s(hsv);


//        Imgproc.cvtColor(one_channel_img, one_channel_img, Imgproc.COLOR_GRAY2RGBA);
//        Imageprocessing.RGB_cut_HSVs_return_rgb(hsv);
//        Imgproc.resize(halfimg_do_S, halfimg_do_S, new Size(mRgba.height(), mRgba.width() / 3));

        HSVs = Imageprocessing.get_HSVs_points_value(hsv);
        G7_C = Imageprocessing.get_G7_C80100_points_value(halfimg);
        G11_C = Imageprocessing.get_G11_C80100_points_value(halfimg);
        Mat sobel_tmp = new Mat();
        Boolean wall, pillar;
        Imgproc.Canny(Imageprocessing.sobel_outputgray_X(halfimg), sobel_tmp, 80, 100);
        wall = Imageprocessing.getcol(sobel_tmp);

        Imgproc.Canny(Imageprocessing.sobel_outputgray_Y(halfimg), sobel_tmp, 80, 100);
        pillar = Imageprocessing.getcol(sobel_tmp);
//        Log.i(TAG, "                      S " + String.valueOf(HSVs) + "  G7_C " + String.valueOf(G7_C) + "  G11_C " + String.valueOf(G11_C));


        Mat tmp = new Mat();
        if (model == 1) {
            mRgba = Imageprocessing.model_HSV_s(mRgba);
        }else if (model == 2) {
            mRgba = Imageprocessing.model_G7_C(mRgba);
        }else if (model == 3) {
            mRgba = Imageprocessing.model_G11_C(mRgba);
        }else if (model == 4) {
            Imgproc.cvtColor(Imageprocessing.sobel_outputgray_X(img), tmp, Imgproc.COLOR_GRAY2BGRA);
            Imgproc.resize(tmp, mRgba, Camerasize);
        }else if (model == 5) {
            Imgproc.cvtColor(Imageprocessing.sobel_outputgray_Y(img), tmp, Imgproc.COLOR_GRAY2BGRA);
            Imgproc.resize(tmp, mRgba, Camerasize);
        }
        else if (model == 6) {
//            mRgba = Imageprocessing.body_hsv(mRgba);
            mRgba = Imageprocessing.body_YCbCr(mRgba);
        }


        if (HSVs > 2000  ) {
            String txt = "HSVs: " + HSVs;
            Core.putText(mRgba, txt, new Point(20, mRgba.height()-20), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 0, 0));
        } else {
            String txt = "HSVs: " + HSVs;
            Core.putText(mRgba, txt, new Point(20, mRgba.height()-20), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 255, 255));
        }

        if (G7_C > 500) {
            String txt = "  G7_C: " + G7_C;
            Core.putText(mRgba, txt, new Point(mRgba.width() / 3, mRgba.height() - 20), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 0, 0));
        } else {
            String txt = "  G7_C: " + G7_C;
            Core.putText(mRgba, txt, new Point(mRgba.width() / 3, mRgba.height() - 20), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 255, 255));
        }

        if (G11_C > 150) {
            String txt = "  G11_C: " + G11_C;
            Core.putText(mRgba, txt, new Point(mRgba.width() / 3 * 2, mRgba.height() - 20), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 0, 0));
        } else {
            String txt = "  G11_C: " + G11_C;
            Core.putText(mRgba, txt, new Point(mRgba.width() / 3 * 2, mRgba.height() - 20), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 255, 255));
        }

        if (!wall) {
            String txt = " __ ";
            Core.putText(mRgba, txt, new Point(20, mRgba.height() - 80), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 0, 0));
        } else {
            String txt = " __ ";
            Core.putText(mRgba, txt, new Point(20, mRgba.height() - 80), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 255, 255));
        }
        if (!pillar) {
            String txt = " II ";
            Core.putText(mRgba, txt, new Point(120, mRgba.height() - 80), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 0, 0));
        } else {
            String txt = " II ";
            Core.putText(mRgba, txt, new Point(120, mRgba.height() - 80), Core.FONT_HERSHEY_DUPLEX, 1.2, new Scalar(255, 255, 255));
        }


//        Mat halfimg_do_S = mRgba.submat(0, mRgba.height() / 3, 0, mRgba.width());
//        Mat hsv = new Mat();
//        Imgproc.cvtColor(halfimg_do_S, hsv, Imgproc.COLOR_RGB2HSV);
//        Mat hsv_s = new Mat();
//        Core.extractChannel(hsv, hsv_s, 1);
//        Mat mask_s = new Mat(hsv_s.size(), CvType.CV_8UC1);
//
//        Core.inRange(hsv_s, new Scalar(76), new Scalar(255), mask_s);
//        halfimg_do_S.copyTo(halfimg_do_S, mask_s);



        Core.rectangle(mRgba,new Point(1,1),new Point(mRgba.width()-5, mRgba.height() / 3-5),new Scalar(255,255,0),5);

        Core.rectangle(mRgba,new Point(mRgba.width()/2-20-5,mRgba.height()/2-20-5),new Point(mRgba.width()/2+20-5, mRgba.height()/2+20-5),new Scalar(255,255,0),5);
//        halfimg_do_S.release();
        return mRgba;
    }

    public void settext() {
        if (imgcount == updatespeed) {
            Log.i(TAG, "text");
            value.setLength(0);
            value.append("HSV_S      : ").append(String.valueOf(HSVs)).append("\n");
            value.append("G7_C80100  : ").append(String.valueOf(G7_C)).append("\n");
            value.append("G11_C80100 : ").append(String.valueOf(G11_C)).append("\n");
            text.setText(value);
            if (HSVs > 2000 || G7_C > 200 || G11_C > 100) {
                text.setBackgroundColor(0xFFFF0000);
            } else {
                text.setBackgroundColor(0xFF00FF00);

            }
        }
    }
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Call on every application resume **/
    @Override
    protected void onResume()
    {
        Log.i(TAG, "Called onResume");
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mOpenCVCallBack);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
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


    @Override
    public void onClick(View v) {
        if (v == clear) {
            model = 0;
        }
        else if (v == hsvs_btn) {
            model = 1;
            text.setText("HsV_S:2000");
        }
        else if (v == G7C_btn) {
            model = 2;
            text.setText("G7_C:500");
        }
        else if (v == G11C_btn) {
            model = 3;
            text.setText("G11_C:150");
        }
        else if (v == sobelY) {
            model = 4;
            text.setText("sobel_Y");
        }
        else if (v == sobelX) {
            model = 5;
            text.setText("sobel_X");
        }
        else if (v == bodybtn) {
            model = 6;
            text.setText("boby");
        }

    }
}
