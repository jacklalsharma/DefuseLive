package com.app.defuse.helpers;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ScaleXSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by atulOholic on 21/2/15.
 */
public class GenerateRandomNumbers {

    public static int[] getRandomValues(int count) {

        // initialization
        Random generator = new Random();
        int randomIndex;
//        ArrayList<Integer> copyArray = new ArrayList<Integer>();
//        ArrayList<Integer> dest = new ArrayList<Integer>();
//        copyArray.addAll(src);


        int copyArray[] = new int[10];
        int dest[] = new int[count];

        for(int i=0;i<9;i++){
            copyArray[i] = i;
        }

        //shuffleList(copyArray);

        for (int i = 0; i < count; i++) {
               //TP
            randomIndex = generator.nextInt((9-1) + 1) + 1;
            System.out.print(""+randomIndex);
            dest[i] = randomIndex;

            if (i >= count) {
                break;
            }
//            copyArray.remove(randomIndex);

        }

        return dest;
    }

    public static void shuffleList(List<Integer> a) {
        int n = a.size();
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private static void swap(List<Integer> a, int i, int change) {
        int helper = a.get(i);
        a.set(i, a.get(change));
        a.set(change, helper);
    }

    public static void applySpacing(TextView textView) {

        String originalText = textView.getText().toString();

        if (TextUtils.isEmpty(originalText)) return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((0 + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(finalText, TextView.BufferType.SPANNABLE);
    }
}
