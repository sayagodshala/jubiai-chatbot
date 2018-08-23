package com.jubi.ai.chatbot.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by yassinegharsallah on 01/04/2017.
 */

public class UiUtils {


    public static void showSnackbar(View view, String message, int length) {
        if (view != null)
            Snackbar.make(view, message, length).setAction("Action", null).show();
    }

    public static void showToast(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static GradientDrawable getToolbarGradient(Context context, int color1, int color2) {
        int colors[] = {
                context.getResources().getColor(color1),
                context.getResources().getColor(color2)};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradientDrawable.setShape(GradientDrawable.LINEAR_GRADIENT);
        return gradientDrawable;
    }

    public static GradientDrawable getCircularGradient(int color1, int color2) {
        int colors[] = {
                color1,
                color2};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradientDrawable.setShape(GradientDrawable.OVAL);
        return gradientDrawable;
    }

//    public static TextDrawable getMaterialDrawable(String name) {
//        ColorGenerator generator = ColorGenerator.MATERIAL;
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(name.toString().substring(0, 1), generator.getRandomColor());
//        return drawable;
//    }
//
//    public static TextDrawable getMaterialDrawableTwoLetters(String name) {
//        ColorGenerator generator = ColorGenerator.MATERIAL;
//        TextDrawable drawable = TextDrawable.builder()
//                .buildRound(name, Color.parseColor("#50A6C4"));
//        return drawable;
//    }

}
