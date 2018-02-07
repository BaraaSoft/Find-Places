package com.baraa.bsoft.epnoxlocation.Views;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by baraa on 06/01/2018.
 */

public class PlacesRecyclerViewListener extends RecyclerView.SimpleOnItemTouchListener {
    private GestureDetectorCompat gestureDetector;
    private OnPlacesRecyclerViewListener onPlacesRecyclerViewListener;

    public interface OnPlacesRecyclerViewListener{
        void onItemPressed(View view,int position);
        void onItemLongPress(View view, int position);

    }
    public PlacesRecyclerViewListener(Context context, final RecyclerView recyclerView, OnPlacesRecyclerViewListener callback) {
        super();
        this.onPlacesRecyclerViewListener = callback;
        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View view = recyclerView.findChildViewUnder(e.getX(),e.getY());
                int position = recyclerView.getChildAdapterPosition(view);
                onPlacesRecyclerViewListener.onItemPressed(view,position);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return gestureDetector.onTouchEvent(e);

    }
}
