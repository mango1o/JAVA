package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Locale;

public class BitmapUtil {
    public static void saveBitMap(String path, Bitmap bitmap, String format, int quality) {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        if (format.toUpperCase(Locale.getDefault()).equals("PNG") == true) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            bitmap.compress(compressFormat, quality, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(String path, ByteBuffer buffer, int sample_size, String format, int quality) {
        try {
            byte[] buff = new byte[buffer.remaining()];
            buffer.get(buff);
            BitmapFactory.Options ontain = new BitmapFactory.Options();
            ontain.inSampleSize = sample_size;
            Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length, ontain);
            saveBitMap(path, bitmap, format, quality);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap openBitmap(String path) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    public static String getCachePath(Context context) {
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";
        return path;
    }

    public static Bitmap zoomImage(Bitmap origImage, double newWidth, double newHeight) {
        float width = origImage.getWidth();
        float height = origImage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newImage = Bitmap.createBitmap(origImage, 0, 0, (int) width, (int) height, matrix, true);
        return newImage;
    }
}
