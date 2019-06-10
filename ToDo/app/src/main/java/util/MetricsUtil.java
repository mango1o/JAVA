package util;

import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.regex.Pattern;

public class MetricsUtil {
    private final static String TAG = "MetricsUtil";

    public static Point getSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        Point size = new Point();
        size.x = displayMetrics.widthPixels;
        size.y = displayMetrics.heightPixels;
        return size;
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    public static Point getCameraSize(Camera.Parameters params, Point screenSize) {
        String previewSizeValueString = params.get("preview-size-values");
        if (previewSizeValueString == null) {
            previewSizeValueString = params.get("preview-size-value");
        }
        Point cameraSize = null;
        if (previewSizeValueString != null) {
            cameraSize = findBestPreviewSizeValue(previewSizeValueString, screenSize);
        }
        if (cameraSize == null) {
            cameraSize = new Point((screenSize.x >> 3) << 3, (screenSize.y >> 3) << 3);
        }
        return cameraSize;
    }

    public static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenSize) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                continue;
            }

            int newX;
            int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                continue;
            }
            int newDiff = Math.abs((newX - screenSize.x) + (newY - screenSize.y));
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }
        }
        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }
}
