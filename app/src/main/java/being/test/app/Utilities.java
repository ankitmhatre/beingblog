package being.test.app;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;


import org.json.JSONArray;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Utilities {

    public static final String FIRST_LAUNCH = "first_launch";
    public static final int GPS_REQUEST = 56;
    public static final int UPDATE_APP_CODE = 72;

    public static boolean IsJsonString(String str) {
        try {
            new JSONArray(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void changeLanguage(Context context, String lang) {
        PrefUtils.setString(context, "lang", lang);
        System.exit(0);


    }

    public static String getCurrentDateTimeInNmmcFormat() {
        Date myDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String date = dateFormat.format(myDate);
        String time = timeFormat.format(myDate);
        return date + "T" + time;
    }


    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String getDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp * 1000);

        String month = c.get(Calendar.MONTH) + 1 + "";
        String date = c.get(Calendar.DAY_OF_MONTH) + "";
        String year = c.get(Calendar.YEAR) + "";
        String hh = c.get(Calendar.HOUR_OF_DAY) + "";
        String mm = c.get(Calendar.MINUTE) + "";


        return hh + ":" + mm + " " + date + "/" + month + "/" + year;
    }


    public static String getDateampm(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp * 1000);

        String month = c.get(Calendar.MONTH) + 1 + "";
        String date = c.get(Calendar.DAY_OF_MONTH) + "";
        String year = c.get(Calendar.YEAR) + "";
        String hh = c.get(Calendar.HOUR) + "";
        int ap_pm = c.get(Calendar.AM_PM);
        String mm;
        if (c.get(Calendar.MINUTE) < 10) {
            mm = "0" + c.get(Calendar.MINUTE);
        } else {
            mm = c.get(Calendar.MINUTE) + "";

        }

        String ampm = ap_pm == 0 ? "am" : "pm";

        return hh + ":" + mm + " " + ampm + " " + date + "/" + month + "/" + year;
    }


    public static long formatCurrentTimeStamp(long timestamp) {
        String time = timestamp + "";
        return Long.parseLong(time.substring(0, 10));
    }

    public static long getOldTimeStamp(long daysBefore) {
        long l = Long.parseLong((System.currentTimeMillis() + "").substring(0, 10));
        long oneday = 86400;
        l = l - (oneday * daysBefore);
        return l;
    }

    public static String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }


    public static long getTimestampFrom(int noOfDays) {
        long fromDate = System.currentTimeMillis() - (noOfDays * 86400000);
        return formatCurrentTimeStamp(fromDate);
    }



    public static String encodetoBase64(String id) {
        return Base64.encodeToString(id.getBytes(), Base64.DEFAULT);
    }

    public static String resolveIdFromUrl(Uri link) {
        String idPar = link.getQueryParameter("id");
        return idPar;
    }




    public static String convertMToHM(String mins) {
        int hours = Integer.parseInt(mins) / 60; //since both are ints, you get an int
        int minutes = Integer.parseInt(mins) % 60;
        if (hours > 0) {
            return hours + "h." + minutes + "m";
        } else {
            return minutes + "m";
        }

    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


}
