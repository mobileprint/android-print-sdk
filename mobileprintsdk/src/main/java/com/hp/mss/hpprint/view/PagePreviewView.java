/*
 * Hewlett-Packard Company
 * All rights reserved.
 *
 * This file, its contents, concepts, methods, behavior, and operation
 * (collectively the "Software") are protected by trade secret, patent,
 * and copyright laws. The use of the Software is governed by a license
 * agreement. Disclosure of the Software to third parties, in any form,
 * in whole or in part, is expressly prohibited except as authorized by
 * the license agreement.
 */

package com.hp.mss.hpprint.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.model.PrintItem;
import com.hp.mss.hpprint.model.PrintJobData;
import com.hp.mss.hpprint.util.FontUtil;

import org.w3c.dom.Attr;

import java.lang.ref.WeakReference;

/**
 * This is used inside the PrintPreview Activity. You do not need to create this by yourself.
 */
public class PagePreviewView extends View {
    private static final float LAYOUT_MARGIN_RATIO = 9;
    private static final int MEASUREMENT_FONT_SIZE = 20;

    private Paint textPaint;

    private Bitmap photo;

    private final RectF pageBounds = new RectF();
    private float pxOffset;
    private float pageWidth;
    private float pageHeight;
    private int paperColor = Color.WHITE;
    private Paint paperPaint;
    private Rect textBounds = new Rect();
    private String dimens = "";
    private PrintJobData printJobData;
    private PrintItem.ScaleType scaleType;
    private PrintItem printItem;
    private int textColor;

    public PagePreviewView(Context context) {
        this(context, null);
    }

    public PagePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
        init(context);

    }

    private void readAttrs(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PagePreviewView,0,0);
        try{
            textColor = a.getColor(R.styleable.PagePreviewView_sizeFontColor, Color.WHITE);
        } finally {
            a.recycle();
        }
    }

    private void init(Context context) {
        pxOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        paperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paperPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        paperPaint.setColor(Color.BLACK);
        paperPaint.setStrokeWidth(1);

        textPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if (!isInEditMode()) {
            Typeface typeface = FontUtil.getDefaultFont(context);
            textPaint.setTypeface(typeface);
        }

        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MEASUREMENT_FONT_SIZE, getResources().getDisplayMetrics()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        pageBounds.set(getPreviewImageRect());

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw label
        final float labelXOrigin = pageBounds.centerX();
        final float labelYOrigin = pageBounds.bottom + (int) pxOffset * 2;
        canvas.saveLayer(pageBounds.left, pageBounds.top, pageBounds.right, pageBounds.bottom, null, Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.MATRIX_SAVE_FLAG);

        paperPaint.setStyle(Paint.Style.FILL);
        paperPaint.setColor(Color.WHITE);
        RectF pageBoundsTemp = new RectF(pageBounds.left, pageBounds.top, pageBounds.right, pageBounds.bottom);
        canvas.drawRect(pageBoundsTemp, paperPaint);

        if (printItem != null) {
            float dpi = pageBounds.width() / pageWidth;
            printItem.drawPage(canvas, dpi, pageBounds);
        }
        canvas.restore();
    }

    private RectF getPreviewImageRect() {

        final float left;
        final float top;
        final float right;
        final float bottom;
        final float resultWidth;
        final float resultHeight;
        final float verticalPadding;
        final float horizontalPadding;
        final float width = pageWidth;
        final float height = pageHeight;

        boolean widthLimiting = (width / (float) getMeasuredWidth() > height / (float) getMeasuredHeight());

        if (widthLimiting) {
            resultWidth = getMeasuredWidth() * (LAYOUT_MARGIN_RATIO - 2) / LAYOUT_MARGIN_RATIO;
            resultHeight = resultWidth * height / width;
            verticalPadding = (getMeasuredHeight() - resultHeight) / 2;

            left = getMeasuredWidth() / LAYOUT_MARGIN_RATIO;
            top = verticalPadding;
            right = getMeasuredWidth() * (LAYOUT_MARGIN_RATIO - 1) / LAYOUT_MARGIN_RATIO;
            bottom = verticalPadding + resultHeight;
        } else {
            resultHeight = getMeasuredHeight() * (LAYOUT_MARGIN_RATIO - 2) / LAYOUT_MARGIN_RATIO;
            resultWidth = resultHeight * width / height;

            horizontalPadding = (getMeasuredWidth() - resultWidth) / 2;

            left = horizontalPadding;
            top = getMeasuredHeight() / LAYOUT_MARGIN_RATIO;
            right = horizontalPadding + resultWidth;
            bottom = getMeasuredHeight() * (LAYOUT_MARGIN_RATIO - 1) / LAYOUT_MARGIN_RATIO;
        }

        return new RectF(left, top - pxOffset / 2, right, bottom - pxOffset / 2);
    }

    public void setPrintItem(PrintItem printItem) {
        this.printItem = printItem;
    }

    public void setPhoto(Bitmap photo) {
        if (this.photo != null) {
            this.photo.recycle();
        }
        this.photo = photo;
        invalidate();
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPaperColor(int color) {
        paperColor = color;
        postInvalidate();
    }

    public void setScaleType(PrintItem.ScaleType scaleType) {
        this.scaleType = scaleType;
        postInvalidate();
    }

    public void setPageSize(float width, float height) {
        pageWidth = width;
        pageHeight = height;

        dimens = String.format("%s x %s", fmt(pageWidth), fmt(pageHeight));
        textPaint.getTextBounds(dimens, 0, dimens.length() - 1, textBounds);
    }

    private static String fmt(float d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    public static class ImageLoaderTask extends AsyncTask<LoaderParams, Void, LoaderParams> {
        private static final String TAG = "ImageLoaderTask";
        private WeakReference<Context> contextRef;

        public ImageLoaderTask(Context context) {
            contextRef = new WeakReference<>(context.getApplicationContext());
        }


        @Override
        protected LoaderParams doInBackground(LoaderParams... params) {
            LoaderParams param = params[0];
            final Context context = getContext();
            if (context == null) {
                return null;
            }

            final PagePreviewView view = param.target.get();
            if (view != null) {
                if (view.photo != null) {
                    view.photo.recycle();
                    view.photo = null;
                }
            } else {
                return null;
            }
            return param;
        }

        @Override
        protected void onPostExecute(LoaderParams result) {
            final LoaderParams params = result;
            final PagePreviewView view = params.target.get();
            if (view != null) {
                view.setScaleType(params.printItem.getScaleType());
                view.setPrintItem(params.printItem);
                view.requestLayout();
            }
        }

        private Context getContext() {
            return contextRef != null ? contextRef.get() : null;
        }
    }

    public static class LoaderParams {
        public final PrintItem printItem;
        public final WeakReference<PagePreviewView> target;

        public LoaderParams(PrintItem printItem, PagePreviewView target) {
            this.printItem = printItem;
            this.target = new WeakReference<>(target);
        }
    }


}
