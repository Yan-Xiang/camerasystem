package com.example.fufamilydeskwin7.smartcamera;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by fufamilyDeskWin7 on 2016/2/3.
 */
public class Imageprocessing {
    private static final String TAG = "OCVSample::Activity";
    //取我們想要的區域
    public static Mat get_halfimg(Mat orgimg, int vertical) {
        //vertical=1 horizontal=0
//        Mat halforg = new Mat(orgimg.size(), CvType.CV_8UC4);
        if (vertical == 1) {
//            halforg = orgimg.submat(0, orgimg.height(), 0, orgimg.width() / 3);
            return orgimg.submat(0, orgimg.height(), 0, orgimg.width() / 3);

        } else {
//            halforg = orgimg.submat(0, orgimg.height() / 3, 0, orgimg.width());
            return orgimg.submat(0, orgimg.height() / 3, 0, orgimg.width());

        }
//        return halforg;
    }

    //RGB 轉換成 HSV
    public static Mat RGB2HSV(Mat Rgb) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(Rgb, hsv, Imgproc.COLOR_RGB2HSV);
        return hsv;
    }

    //從HSV提出HSV_S
    public static Mat get_HSV_s(Mat HSV) {
        Mat hsv_s = new Mat();
        Core.extractChannel(HSV, hsv_s, 1);
        return hsv_s;
    }

    //HSV_S points value
    public static int get_HSVs_points_value(Mat HSV_s) {
        Mat mask_s = new Mat();
        Core.inRange(HSV_s, new Scalar(76), new Scalar(255), mask_s);
        mask_s = hsv_s_erode_dilate(mask_s);
//        get_HSV_s.copyTo(get_HSV_s, mask_s); //將原圖片經由遮罩過濾後，得到結果dst
        return Core.countNonZero(mask_s);
    }

    //Gauss7 Canny(80,100)
    public static int get_G7_C80100_points_value(Mat img) {
        Mat G7_C80100 = new Mat();
        Imgproc.GaussianBlur(img, G7_C80100, new Size(5, 5), 3, 3);
        Imgproc.Canny(G7_C80100, G7_C80100, 80, 100);
        return Core.countNonZero(G7_C80100);
    }

    //Gauss11 Canny(80,100)
    public static int get_G11_C80100_points_value(Mat img) {
        Mat G11_C80100 = new Mat();
        Imgproc.GaussianBlur(img, G11_C80100, new Size(11, 11), 3, 3);
        Imgproc.Canny(G11_C80100, G11_C80100, 80, 100);
        return Core.countNonZero(G11_C80100);
    }

    public static Mat RGB_cut_HSVs_return_rgb(Mat one_channel_img) {
        Mat mask_s = new Mat(one_channel_img.size(), CvType.CV_8UC1);
//        Core.extractChannel(hsv, hsv_s, 1);
//        Log.i(TAG, "HSV: do mask");
        Core.inRange(one_channel_img, new Scalar(76), new Scalar(255), mask_s);
//        Log.i(TAG, "HSV: copy to");

        one_channel_img.copyTo(one_channel_img, mask_s); //將原圖片經由遮罩過濾後，得到結果dst
        Imgproc.cvtColor(one_channel_img, one_channel_img, Imgproc.COLOR_GRAY2RGBA);
//        Log.i(TAG, "HSV: finish!");
        //output Mat is hsv_s
        return one_channel_img;
    }
    public static Mat hsv_s_erode_dilate(Mat img) {
        Imgproc.erode(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 4)), new Point(-1, -1), 1);
        Imgproc.dilate(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(4, 4)), new Point(-1, -1), 1);
        return img;
    }
    //----------------------------------------------------------------------------------------------
    //取膚色區域↓↓↓ 用hsv
    public static Mat body_hsv(Mat img) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_RGB2HSV);
        Mat hsv_h = new Mat();
        Mat hsv_s = new Mat();
        Core.extractChannel(hsv, hsv_h, 0);
        Core.extractChannel(hsv, hsv_s, 1);

        Mat hsv_h_mask = new Mat();
        Mat hsv_s_mask = new Mat();
        Core.inRange(hsv_h, new Scalar(4), new Scalar(70), hsv_h_mask);
        Core.inRange(hsv_s, new Scalar(20), new Scalar(128), hsv_s_mask);
        Imgproc.erode(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.erode(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_h_mask, hsv_h_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);

        Imgproc.erode(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.erode(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)), new Point(-1, -1), 1);
        Imgproc.dilate(hsv_s_mask, hsv_s_mask, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2)), new Point(-1, -1), 1);

        Mat bodyrgb = new Mat();
        img.copyTo(bodyrgb, hsv_h_mask);
//        bodyrgb.copyTo(bodyrgb, hsv_s_mask);
//        img.copyTo(bodyrgb, hsv_s_mask);
        return bodyrgb;
    }
    //取膚色區域↓↓↓ 用YCbCr
    public static Mat body_YCbCr(Mat img) {
        int avg_cb = 120;//YCbCr顏色空間膚色cb的平均值
        int avg_cr = 155;//YCbCr顏色空間膚色cr的平均值
        int skinRange = 22;//YCbCr顏色空間膚色的範圍


        Mat hsv = new Mat();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_RGB2YCrCb);
        Mat cr = new Mat();
        Mat cb = new Mat();
        Core.extractChannel(hsv, cr, 1);
        Core.extractChannel(hsv, cb, 2);

        Mat cr_mask = new Mat();
        Mat cb_mask = new Mat();
        Core.inRange(cr, new Scalar(avg_cr - skinRange), new Scalar(avg_cr + skinRange), cr_mask);
        Core.inRange(cb, new Scalar(avg_cb - skinRange), new Scalar(avg_cb + skinRange), cb_mask);

        Mat bodyrgb = new Mat();
        img.copyTo(bodyrgb, cr_mask);
        bodyrgb.copyTo(bodyrgb, cb_mask);
        return bodyrgb;
    }
//----------------------------------------------------------------------------------------------------------

    public static Mat sobel_outputgray_Y(Mat img) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(img, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(tmp, tmp, new Size(5, 5), 3, 3);
        Imgproc.Sobel(tmp, tmp, CvType.CV_8U, 0, 1);
        Core.convertScaleAbs(tmp, tmp, 10, 0);
        Mat onelayer = new Mat();
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.erode(tmp, tmp, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(4, 2)), new Point(-1, -1), 3);
        Imgproc.erode(tmp, tmp, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 1)), new Point(-1, -1), 1);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.dilate(tmp, tmp, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(5, 2)), new Point(-1, -1), 2);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        return onelayer;
    }

    public static Mat sobel_outputgray_X(Mat img) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(img, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(tmp, tmp, new Size(5, 5), 3, 3);
        Imgproc.Sobel(tmp, tmp, CvType.CV_8U, 1, 0);
        Core.convertScaleAbs(tmp, tmp, 10, 0);
        Mat onelayer = new Mat();
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.erode(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(2, 4)), new Point(-1, -1), 3);
        Imgproc.erode(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 2)), new Point(-1, -1), 1);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        Imgproc.dilate(img, img, Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(2, 5)), new Point(-1, -1), 2);
        Core.inRange(tmp, new Scalar(200), new Scalar(255), onelayer);
        tmp.copyTo(tmp, onelayer);

        return onelayer;
    }
//有無地面與牆壁接縫↓↓↓
    public static Boolean getcol(Mat img) {//垂直
        int widthvalue = img.width()/10;
        int[] num = new int[9];
        num[0] = Core.countNonZero(img.col(widthvalue));
        num[1] = Core.countNonZero(img.col(widthvalue * 2));
        num[2] = Core.countNonZero(img.col(widthvalue * 3));
        num[3] = Core.countNonZero(img.col(widthvalue * 4));
        num[4] = Core.countNonZero(img.col(widthvalue * 5));
        num[5] = Core.countNonZero(img.col(widthvalue * 6));
        num[6] = Core.countNonZero(img.col(widthvalue * 7));
        num[7] = Core.countNonZero(img.col(widthvalue * 8));
        num[8] = Core.countNonZero(img.col(widthvalue * 9));
        int min, max, value;
        min=max=num[0];
        for(char i=0;i<num.length;i++) {
            if(num[i]>max)
                max=num[i];
            if(num[i]<min)
                min=num[i];
        }
        value = max - min;
        if (value < 7 && (max + min) /2 < 5 && (max + min) /2 > 1) {
            //有牆壁跟地面的線
            return true;
        }
        else {
            return false;
        }
    }
    //有無柱子↓↓↓
    public static Boolean getrow(Mat img) {//水平

        int widthvalue = img.width()/6;
        int[] num = new int[6];
        num[0] = Core.countNonZero(img.row(widthvalue));
        num[1] = Core.countNonZero(img.row(widthvalue * 2));
        num[2] = Core.countNonZero(img.row(widthvalue * 3));
        num[3] = Core.countNonZero(img.row(widthvalue * 4));
        num[4] = Core.countNonZero(img.row(widthvalue * 5));

        int min, max, value;
        min=max=num[0];
        for(char i=0;i<num.length;i++) {
            if(num[i]>max)
                max=num[i];
            if(num[i]<min)
                min=num[i];
        }
        value = max - min;
        if (value < 7 && (max + min) /2 < 7 && (max + min) /2 > 1) {
            //有柱子的線
            return true;
        }
        else {
            return false;
        }
    }

//去除小石頭↓↓↓

    public static Mat clear_tile(Mat img) {
        Mat tmp = new Mat();
        Imgproc.cvtColor(img, tmp, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Sobel(tmp, tmp, CvType.CV_8U, 1, 1);
        Core.convertScaleAbs(tmp, tmp, 10, 0);
        Mat onelayer = new Mat();
        Core.inRange(tmp, new Scalar(240), new Scalar(255), onelayer);

        Imgproc.dilate(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3)), new Point(-1, -1), 3);
        Core.inRange(onelayer, new Scalar(250), new Scalar(255), onelayer);
        Imgproc.erode(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10, 10)), new Point(-1, -1), 1);
        Core.inRange(onelayer, new Scalar(253), new Scalar(255), onelayer);
        Imgproc.dilate(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7, 7)), new Point(-1, -1), 3);
        Core.inRange(onelayer, new Scalar(250), new Scalar(255), onelayer);
        Imgproc.erode(onelayer, onelayer, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(20, 20)), new Point(-1, -1), 1);
        Core.inRange(onelayer, new Scalar(253), new Scalar(255), onelayer);
        Core.bitwise_not(onelayer, onelayer);
//        Mat output = new Mat();
//        img.copyTo(output, onelayer);
//        Imgproc.cvtColor(onelayer, output, Imgproc.COLOR_GRAY2BGRA);
        return onelayer;

    }

//去除小石頭↑↑↑

//取線
    public static Mat HoughLines(Mat img, Mat mask) {
        Mat doimg = new Mat();
        Mat G7_C80100 = new Mat();


        Imgproc.GaussianBlur(img, G7_C80100, new Size(5, 5), 3, 3);
        Imgproc.Canny(G7_C80100, G7_C80100, 80, 100);

        G7_C80100.copyTo(doimg, mask);

        Mat lines = new Mat();
        int threshold = 36;//40
        int minLineSize = 40;
        int lineGap = 5;//5

        Imgproc.HoughLinesP(doimg, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);
        Imgproc.cvtColor(doimg, doimg, Imgproc.COLOR_GRAY2BGRA);
        for (int x = 0; x < lines.cols(); x++) {
            double[] vec = lines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Core.line(doimg, start, end, new Scalar(255, 0, 0), 2);

        }
        return doimg;
    }
//---------------------------------------------------------------------------------------------------------------------------















//這是以前我用的很亂不用看 ↓↓↓

    public static void getimagevalue(Mat orgimageMat) {
        StringBuilder valuetext = new StringBuilder();

        Log.i(TAG, "getimagevalue star");

        int HSV_Spoint_value, G7_C80100point_value, G11_C80100point_value;
        Mat halforg = orgimageMat.submat(0, orgimageMat.height(), 0, orgimageMat.width() / 3);

        Mat dst = new Mat();

//        valuetext.append("hsv_h,hsv_s,hsv_v,rgbcuthsv_s,G7_C80100, G11_C80100\n\n");
//        valuetext.append("Data ID : ").append(Dataname).append("\n");
        //HSV---------------------------------------------------------------------------
//        Log.i(TAG, "HSV: star");
        Mat hsv = new Mat();
        Mat hsv_s = new Mat();
        Mat mask_s = new Mat(halforg.size(), CvType.CV_8UC1);
//        Log.i(TAG, "HSV: RGB2HSV");
        Imgproc.cvtColor(halforg, hsv, Imgproc.COLOR_RGB2HSV);
//            Core.split(makeMat, 1);
//        Log.i(TAG, "HSV: take one channel");
        //HSV_H---------------------------------------------------------------------------
        Mat hsv_h = new Mat();
        Core.extractChannel(hsv, hsv_h, 0);
        Imgproc.cvtColor(hsv_h, hsv_h, Imgproc.COLOR_GRAY2RGBA);
        //HSV_S---------------------------------------------------------------------------
        //多層矩陣轉換成幾個單一矩陣
        Core.extractChannel(hsv, hsv_s, 1);
//        Log.i(TAG, "HSV: do mask");
        Core.inRange(hsv_s, new Scalar(76), new Scalar(255), mask_s);
//        Log.i(TAG, "HSV: copy to");

        hsv_s.copyTo(hsv_s, mask_s); //將原圖片經由遮罩過濾後，得到結果dst
        Imgproc.cvtColor(hsv_s, hsv_s, Imgproc.COLOR_GRAY2RGBA);
//        Log.i(TAG, "HSV: finish!");
        //output Mat is hsv_s
        //HSV_V---------------------------------------------------------------------------
        Mat hsv_v = new Mat();
        Core.extractChannel(hsv, hsv_v, 2);
        Imgproc.cvtColor(hsv_v, hsv_v, Imgproc.COLOR_GRAY2RGBA);


        //RGB cut HSV_S--------------------------------------------------------------------
//        Log.i(TAG, "RGB cut get_HSV_s: star");
        Mat rgbcuthsv_s = new Mat();
//        Log.i(TAG, "RGB cut get_HSV_s: copy to");
        halforg.copyTo(rgbcuthsv_s, mask_s);
//        Log.i(TAG, "RGB cut get_HSV_s: finish");
        Scalar maskvalue = Core.sumElems(mask_s);

//            valuetext.append("mask count: " + String.valueOf(maskvalue));
//            valuetext.append("\nmask count: " + maskvalue.toString());
        HSV_Spoint_value = Core.countNonZero(mask_s);
        valuetext.append(String.valueOf(HSV_Spoint_value) + "  ");
        //output Mat is rgbcuthsv_s

        //Gauss and Canny  ==================================================================
        //Gauss3 Canny(80,100)
        Mat G7_C80100 = new Mat();
//        Log.i(TAG, "Gauss: star");
        Imgproc.GaussianBlur(halforg, G7_C80100, new Size(5, 5), 3, 3);
//
//        Log.i(TAG, "Canny: star");
        Imgproc.Canny(G7_C80100, G7_C80100, 80, 100);
//            Log.i(TAG, "Canny: do canny count");
//            cannycount1 = Core.countNonZero(makeMat);
//            countT.setText("canny count" + String.valueOf(cannycount1));
        G7_C80100point_value = Core.countNonZero(G7_C80100);
        valuetext.append(String.valueOf(G7_C80100point_value) + "  ");
        Imgproc.cvtColor(G7_C80100, G7_C80100, Imgproc.COLOR_GRAY2RGBA);
//        Log.i(TAG, "Canny: canny finish");
        //Scalar G7_C80100value= Core.sumElems(G7_C80100);

        //output Mat is G7_C80100

        //Gauss5 Canny(80,100)
        Mat G11_C80100 = new Mat();
//        Log.i(TAG, "Gauss: star");
        Imgproc.GaussianBlur(halforg, G11_C80100, new Size(11, 11), 3, 3);

//        Log.i(TAG, "Canny: star");
        Imgproc.Canny(G11_C80100, G11_C80100, 80, 100);
//            Log.i(TAG, "Canny: do canny count");
        G11_C80100point_value = Core.countNonZero(G11_C80100);
        valuetext.append(String.valueOf(G11_C80100point_value) + "  ");
        Imgproc.cvtColor(G11_C80100, G11_C80100, Imgproc.COLOR_GRAY2RGBA);
//        Log.i(TAG, "Canny: canny finish");

        //output Mat is G11_C80100

        //============================================================
//            Mat space =  Mat.zeros(halforg.width(), halforg.height(), CvType.CV_8UC1);
//            Imgproc.cvtColor(space, space, Imgproc.COLOR_GRAY2RGBA);

//        Log.i(TAG, "hconcat: star new list");
//        List<Mat> src = Arrays.asList(hsv_h, hsv_s, hsv_v, rgbcuthsv_s, G7_C80100, G11_C80100);
//        Log.i(TAG, "hconcat: do hconcat");
//        Core.hconcat(src, dst);
//        Log.i(TAG, "hconcat: finish!");
//        all_image_value[filenumber][0]=filenumber;
//        all_image_value[filenumber][1]=TorF;
//        all_image_value[filenumber][2]=(float)HSV_Spoint_value;
//        all_image_value[filenumber][3]=(float)G7_C80100point_value;
//        all_image_value[filenumber][4]=(float)G11_C80100point_value;
//        filenumber++;
        Log.i(TAG, "getimagevalue finish");
    }
//這是以前我用的很亂不用看 ↑↑↑

//為顯示結果特別而外做的 showmodel
    public static Mat showmodel_HSV_s(Mat img) {
        Mat hsv = new Mat();
        Mat rbgcut = new Mat();
        Mat hsv_s = new Mat();

        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_RGB2HSV);
        Core.extractChannel(hsv, hsv_s, 1);
        hsv.release();
        Mat mask_s = new Mat(hsv_s.size(), CvType.CV_8UC1);
        Core.inRange(hsv_s, new Scalar(76), new Scalar(255), mask_s);
        mask_s = hsv_s_erode_dilate(mask_s);
        img.copyTo(rbgcut, mask_s);
//        Imgproc.cvtColor(mask_s, rbgcut, Imgproc.COLOR_GRAY2BGRA);
        mask_s.release();
        hsv_s.release();
        return rbgcut;
    }

    public static Mat showmodel_G7_C(Mat img) {
        Imgproc.GaussianBlur(img, img, new Size(5, 5), 3, 3);
        Imgproc.Canny(img, img, 80, 100);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGRA);
        return img;
    }
    public static Mat showmodel_G11_C(Mat img) {
        Imgproc.GaussianBlur(img, img, new Size(11, 11), 3, 3);
        Imgproc.Canny(img, img, 80, 100);
        Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGRA);
        return img;
    }
}
