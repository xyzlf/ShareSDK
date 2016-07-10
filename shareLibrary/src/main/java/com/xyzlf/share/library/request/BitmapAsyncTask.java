package com.xyzlf.share.library.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by zhanglifeng on 1/6/17
 */
public class BitmapAsyncTask extends AbstractAsyncTask<Bitmap> {

    private String urlStr;
    private OnBitmapListener listener;

    public BitmapAsyncTask(String urlStr, OnBitmapListener listener) {
        this.urlStr = urlStr;
        this.listener = listener;
    }

    @Override
    protected Bitmap doLoadData() throws Exception {

        URL url = new URL(urlStr);
        InputStream is = url.openStream();
        // 将InputStream变为Bitmap
        Bitmap bitmap = getSampleBitmap(is, 640, 640);
        is.close();

        return bitmap;
    }

    private Bitmap getSampleBitmap(InputStream is, int width, int height) {

        BufferedInputStream stream = new BufferedInputStream(is);
        stream.mark(4 * 1024);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        calculateInSampleSize(width, height, options, true);
        try {
            stream.reset();
        } catch (IOException e) {
        }
        return BitmapFactory.decodeStream(stream, null, options);
    }

    static void calculateInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options, boolean centerInside) {
        calculateInSampleSize(reqWidth, reqHeight, options.outWidth, options.outHeight, options,
                centerInside);
    }

    static void calculateInSampleSize(int reqWidth, int reqHeight, int width, int height,
                                      BitmapFactory.Options options, boolean centerInside) {
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio;
            final int widthRatio;
            if (reqHeight == 0) {
                sampleSize = (int) Math.floor((float) width / (float) reqWidth);
            } else if (reqWidth == 0) {
                sampleSize = (int) Math.floor((float) height / (float) reqHeight);
            } else {
                heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                sampleSize = centerInside
                        ? Math.max(heightRatio, widthRatio)
                        : Math.min(heightRatio, widthRatio);
            }
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        super.onSuccess(bitmap);
        if (null != listener) {
            listener.onSuccess(bitmap);
        }
    }

    @Override
    public void onException(Exception exception) {
        super.onException(exception);
        if (null != listener) {
            listener.onException(exception);
        }
    }

    @Override
    public void onFinally() {
        super.onFinally();
    }


    public interface OnBitmapListener {

        void onSuccess(Bitmap bitmap);

        void onException(Exception exception);
    }

}
