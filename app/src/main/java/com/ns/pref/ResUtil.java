package com.ns.pref;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ResUtil {


    public static float dpFromPx(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static String resolution(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        //return "xhdpi";
        if(density < 1.1) {
            return "mdpi";
        }
        else if(density < 1.6) {
            return "hdpi";
        }
        else if(density < 2.1) {
            return "xhdpi";
        }
        else if(density < 3.1) {
            return "xxhdpi";
        }
        else if(density < 4.1) {
            return "xxxhdpi";
        } else {
            return "xhdpi";
        }
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    public static void setBackgroundDrawable(Resources resources, View layout, int drawable) {
        int sdk = Build.VERSION.SDK_INT;
        if(sdk <= Build.VERSION_CODES.LOLLIPOP) {
            layout.setBackgroundDrawable(resources.getDrawable(drawable));
        } else {
            layout.setBackground(resources.getDrawable(drawable, null));
        }
    }

    public static Drawable getBackgroundDrawable(Resources resources, int drawable) {
        int sdk = Build.VERSION.SDK_INT;
        if(sdk <= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(drawable);
        } else {
            return resources.getDrawable(drawable, null);
        }
    }

    public static void setBackgroundDrawable(Drawable drawable, View layout) {
        int sdk = Build.VERSION.SDK_INT;
        if(sdk <= Build.VERSION_CODES.LOLLIPOP) {
            layout.setBackgroundDrawable(drawable);
        } else {
            layout.setBackground(drawable);
        }
    }

    public static int getColor(Resources resources, int colorRes) {
        int sdk = Build.VERSION.SDK_INT;
        if(sdk <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return resources.getColor(colorRes);
        } else {
            return resources.getColor(colorRes, null);
        }
    }

    public static int getDrawableResId(Context context, String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resourceId;
    }



    public static final void doColorSpanForFirstString(Context context, String firstString,
                                                       String lastString, TextView txtSpan, int colorResId) {

        String changeString = (firstString != null ? firstString : "");

        String totalString = changeString + lastString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(ResUtil.getColor(context.getResources(), colorResId)), 0, changeString.length(), 0);

        txtSpan.setText(spanText);
    }
    public static final void doColorSpanForSecondString(Context context, String firstString,
                                                        String lastString, TextView txtSpan, int colorResId) {
        String changeString = (lastString != null ? lastString : "");
        String totalString = firstString + changeString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(ResUtil.getColor(context.getResources(), colorResId)), String.valueOf(firstString)
                .length(), totalString.length(), 0);
        txtSpan.setText(spanText);
    }

    public static final void doColorSpanForSecondThirdString(Context context, String firstString,
                                                             String lastString, String thirdString, TextView txtSpan, int colorResId) {
        String changeString = (lastString != null ? lastString : "");
        String totalString = firstString +thirdString+ changeString ;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(ResUtil.getColor(context.getResources(), colorResId)), String.valueOf(firstString)
                .length(), totalString.length(), 0);
        txtSpan.setText(spanText);
    }
    public static final void doSizeSpanForFirstString(Context context, String firstString,
                                                      String lastString, TextView txtSpan, int colorResId) {
        String changeString = (firstString != null ? firstString : "");

        String totalString = changeString + lastString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(ResUtil.getColor(context.getResources(), colorResId)), 0, changeString.length(),  0);
        spanText.setSpan(new RelativeSizeSpan(1.5f), 0, changeString.length(), 0);
        txtSpan.setText(spanText);
    }
    public static final void doSizeSpanForSecondString(Context context, String firstString,
                                                       String lastString, TextView txtSpan, int colorResId) {
        String changeString = (lastString != null ? lastString : "");
        String totalString = firstString + changeString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(ResUtil.getColor(context.getResources(), colorResId)),
                String.valueOf(firstString)
                .length(),totalString.length(), 0);
        spanText.setSpan(new RelativeSizeSpan(1.5f), String.valueOf(firstString)
                .length(), totalString.length(), 0);
        txtSpan.setText(spanText);
    }
    public static final void doStyleSpanForFirstString(String firstString,
                                                       String lastString, TextView txtSpan) {
        String changeString = (firstString != null ? firstString : "");
        String totalString = changeString + lastString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new StyleSpan(Typeface.BOLD), 0,
                changeString.length(), 0);
        txtSpan.setText(spanText);
    }
    public static final void doStyleSpanForSecondString(String firstString,
                                                        String lastString, TextView txtSpan) {
        String changeString = (lastString != null ? lastString : "");
        String totalString = firstString + changeString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new StyleSpan(Typeface.BOLD),
                String.valueOf(firstString).length(),
                totalString.length(), 0);
        txtSpan.setText(spanText);
    }


    public static final void doSizeAndThinSpanForSecondString(String firstString,
                                                              String lastString, TextView txtSpan, int colorResId, float size) {
        String changeString = (lastString != null ? lastString : "");
        String totalString = firstString + changeString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new RelativeSizeSpan(size), String.valueOf(firstString).length(),totalString.length(), 0); // set size
        spanText.setSpan(new ForegroundColorSpan(colorResId), String.valueOf(firstString).length(),totalString.length(), 0); // set color
        txtSpan.setText(spanText);
    }

    private static class MyClickableSpan extends ClickableSpan {

        private final Context context;
        private final int colorResId;
        private final TextSpanCallback textSpanCallback;

        public MyClickableSpan(String string, Context context, int colorResId, TextSpanCallback textSpanCallback) {
            super();
            this.context = context;
            this.colorResId = colorResId;
            this.textSpanCallback = textSpanCallback;
        }
        public void onClick(View tv) {
            if(textSpanCallback != null) {
                textSpanCallback.onTextSpanClick();
            }
        }
        public void updateDrawState(TextPaint ds) {

            ds.setColor(ResUtil.getColor(context.getResources(), colorResId));
            ds.setUnderlineText(false); // set to true to show underline
        }
    }

    /**
     * Get App version name
     */
    public static String getVersionName(Context context) {
        String versionName = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            versionName = info.versionName;
        }
        return versionName;
    }

    /**
     * Gets App version code
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            versionCode = info.versionCode;
        }
        return versionCode;
    }


    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String getOsReleaseVersion() {
        return ""+ Build.VERSION.RELEASE;
    }

    public static String getOsVersion() {
        return ""+ Build.VERSION.SDK_INT;
    }



    public static Spanned htmlText(String text) {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY); // for 24 api and more
        } else {
            return Html.fromHtml(text); // or for older api
        }
    }


    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    public static String capitalizeFirstLetter(String str) {
        if (isEmpty(str)) {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str : new StringBuilder(str.length())
                .append(Character.toUpperCase(c)).append(str.substring(1)).toString();
    }


    // Validation Email
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Validation Name
    public static boolean isValidName(String name) {
        boolean valid_firstname;
        if (name == null) {
            // edt.setError("Accept Alphabets Only.");
            valid_firstname = false;
        } else valid_firstname = name.matches("[a-zA-Z ]+");
        return valid_firstname;
    }

    public static boolean isEmpty(String string) {
        return string == null || TextUtils.isEmpty(string.trim());
    }

    public static boolean isValidMobile(String mobile) {
        String MOBILE_PATTERN = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{9}$";
        Pattern pattern = Pattern.compile(MOBILE_PATTERN);
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }



    public static boolean isValidPassword(String password) {
        /*Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(password);
        boolean containSpecialChar = m.find();

        String numRegex   = ".*[0-9].*";
        String alphaRegex = ".*[A-Z].*";
        String alphaSmallRegex = ".*[a-z].*";

        return !containSpecialChar && password.matches(numRegex) && (password.matches(alphaRegex) || password.matches(alphaSmallRegex));*/
        return true;
    }



}
