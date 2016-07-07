package de.halfbit.featured.sample.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Utils {

    public static <T> T findAndShowView(@NonNull View parent, int id) {
        View view = parent.findViewById(id);
        if (view == null) {
            throw new IllegalStateException("Missing required view with id: " + id);
        }
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        //noinspection unchecked
        return (T) view;
    }

    public static <T> T findAndShowView(@NonNull View parent, int id, @NonNull View.OnClickListener listener) {
        View view = findAndShowView(parent, id);
        view.setOnClickListener(listener);
        //noinspection unchecked
        return (T) view;
    }

    public static AppCompatActivity getActivity(Context context) {
        if (!(context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("Context is not an activity");
        }
        return (AppCompatActivity) context;
    }

}
