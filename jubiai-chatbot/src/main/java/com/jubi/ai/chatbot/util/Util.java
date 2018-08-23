package com.jubi.ai.chatbot.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.fujiyuu75.sequent.Animation;
import com.fujiyuu75.sequent.Direction;
import com.fujiyuu75.sequent.Sequent;
import com.google.gson.Gson;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.models.BasicResponse;
import com.jubi.ai.chatbot.models.RestError;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;

public class Util {

    public static String getKeyHash(Context context) {
        String keyHash = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        return keyHash;
    }


    public static String getCertificateSHA1Fingerprint(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }

    }

    public static boolean textIsEmpty(String value) {

        if (value == null)
            return true;

        boolean empty = false;

        String message = value.trim();

        if (TextUtils.isEmpty(message)) {
            empty = true;
        }

        boolean isWhitespace = message.matches("^\\s*$");

        if (isWhitespace) {
            empty = true;
        }

        return empty;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void changeSystemBarColor(Activity activity, int color) {
//        SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
//        mTintManager.setStatusBarTintEnabled(true);
//        mTintManager.setTintColor(color);


        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(activity.getResources().getColor(color));
        //window.setStatusBarColor(ContextCompat.getColor(activity,color));
    }

    public static void hideInputMethod(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean intToBool(int input) {
        if (input < 0 || input > 1) {
            throw new IllegalArgumentException("input must be 0 or 1");
        }

        // Note we designate 1 as true and 0 as false though some may disagree
        return input == 1;
    }

    public static BigDecimal getPercentage(BigDecimal base, BigDecimal raised) {
        BigDecimal perct = new BigDecimal(100);
        return raised.divide(base, 2, RoundingMode.HALF_UP).multiply(perct);
    }

    public static void setLayoutParams(LinearLayout view, float value) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = value;
        view.setLayoutParams(params);
    }

    public static Intent sharingIntent(String shareContent) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text that will be shared.</p>"));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        return Intent.createChooser(sharingIntent, "Impactguru");
    }

    public static Intent shareInWhatsApp(String value) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, value);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        return sendIntent;
    }

    public static Intent shareOnFacebook(Context context, String value) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "text to be shared");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("facebook")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                return shareIntent;
            }
        }
        return null;
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static String uniqueDeviceID(Context context) {


        UUID deviceUuid;
        String tmDevice = "", tmSerial = "", androidId = "";
        String deviceIdStr = "";

        TelephonyManager telephonyManager = null;

        PackageManager packageManager = context.getPackageManager();

        int hasPhoneStatePerm = packageManager.checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName());
        if (hasPhoneStatePerm == PackageManager.PERMISSION_GRANTED) {
            telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }

        try {
            if (telephonyManager != null) {
                tmDevice = "" + telephonyManager.getDeviceId();
                tmSerial = "" + telephonyManager.getSimSerialNumber();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if (androidId == null) {
            androidId = "";
        }


        String build = "";
        if (Build.SERIAL != null) {
            build = Build.SERIAL;
        }

        try {
            if (!tmDevice.equalsIgnoreCase("") && !tmSerial.equalsIgnoreCase("")) {
                deviceUuid = new UUID(androidId.hashCode(),
                        ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                deviceIdStr = deviceUuid.toString();
            } else {
                deviceUuid = new UUID(androidId.hashCode(), build.hashCode());
                deviceIdStr = deviceUuid.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceIdStr;
    }

    public static RestError handleError(ResponseBody response, int status) {

        RestError restError = new RestError();
        restError.setMessage("Something Went Wrong!");
        restError.setError("Something Went Wrong!");
        if (response instanceof ResponseBody) {
            ResponseBody responseBody = response;
            try {
                restError = new Gson().fromJson(new String(responseBody.bytes()), RestError.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        restError.setStatus(status);
        return restError;
    }

    public static StateListDrawable selectorRoundedBackground(int normal, int pressed, boolean stroke) {
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(normal);
        normalDrawable.setCornerRadius(16);
        GradientDrawable pressedDrawable = new GradientDrawable();
        pressedDrawable.setColor(pressed);
        pressedDrawable.setCornerRadius(16);
        if (stroke == true) {
            normalDrawable.setStroke(4, pressed);
            pressedDrawable.setStroke(4, pressed);
        }
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                pressedDrawable);
        states.addState(new int[]{}, normalDrawable);
        return states;
    }

    public static StateListDrawable selectorOptionsBackground(int normal, int pressed, boolean stroke) {
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(normal);
        normalDrawable.setCornerRadius(16);
        GradientDrawable pressedDrawable = new GradientDrawable();
        pressedDrawable.setColor(pressed);
        pressedDrawable.setCornerRadius(16);
        if (stroke == true) {
            normalDrawable.setStroke(4, normal);
            pressedDrawable.setStroke(4, pressed);
        }
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                pressedDrawable);
        states.addState(new int[]{}, normalDrawable);
        return states;
    }

    public static StateListDrawable selectorBackground(int normal, int pressed, boolean stroke) {
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setColor(normal);
        GradientDrawable pressedDrawable = new GradientDrawable();
        pressedDrawable.setColor(pressed);
        if (stroke == true) {
            normalDrawable.setStroke(4, pressed);
            pressedDrawable.setStroke(4, pressed);
        }
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                pressedDrawable);
        states.addState(new int[]{}, normalDrawable);
        return states;
    }

    public static ColorStateList textColorStates(int normal, int pressed) {
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{
                        pressed,
                        normal
                }
        );
        return myColorStateList;
    }

    public static BasicResponse handleError(ResponseBody response) {
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setStatus("failed");
        basicResponse.setError("Something Went Wrong!");
        if (response instanceof ResponseBody) {
            ResponseBody responseBody = response;
            try {
                basicResponse = new Gson().fromJson(new String(responseBody.bytes()), BasicResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return basicResponse;
    }

    public static ShapeDrawable drawCircle(int color) {
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.getPaint().setColor(color);
        return oval;
    }

    public static void defaultAnimateView(Context context, ViewGroup view) {
        Sequent
                .origin(view)
                .duration(500) // option.
                .delay(200) // option. StartOffSet time.
                .offset(200) // option. Interval time.
                .flow(Direction.FORWARD) // option. Flow of animations in (FORWARD/BACKWARD/RANDOM).
                .anim(context, Animation.FADE_IN_UP)// option. implemented Animation or ObjectAnimator xml resource.
                .start();
    }

    public static String getFileExtensionByUrl(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        return extension;
    }

    public static URL checkURL(String input) {
        try {
            URL url = new URL(input);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}