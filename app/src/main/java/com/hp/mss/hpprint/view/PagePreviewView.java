//
// Hewlett-Packard Company
// All rights reserved.
//
// This file, its contents, concepts, methods, behavior, and operation
// (collectively the "Software") are protected by trade secret, patent,
// and copyright laws. The use of the Software is governed by a license
// agreement. Disclosure of the Software to third parties, in any form,
// in whole or in part, is expressly prohibited except as authorized by
// the license agreement.
//

package com.hp.mss.hpprint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.hp.mss.hpprint.R;
import com.hp.mss.hpprint.util.FontUtil;
import com.hp.mss.hpprint.util.ImageLoaderUtil;
import com.hp.mss.hpprint.util.PrintUtil;


public class PagePreviewView extends View {
    private static final float LAYOUT_MARGIN_RATIO = 9;
    private static final int MEASUREMENT_FONT_SIZE = 20;

    private Paint textPaint;

    private Drawable photo;

    private final Rect pageBounds = new Rect();
    private float pxOffset;
    private float pageWidth;
    private float pageHeight;
    private boolean landscape;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
    private int paperColor = Color.WHITE;
    private Paint paperPaint;
    private boolean multiFile;
    Rect textBounds = new Rect();
    Context context;
    String dimens;

    public PagePreviewView(Context context) {
        this(context, null);
    }

    public PagePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init(context);
    }

    private void init(Context context) {
        pxOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        paperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paperPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        paperPaint.setColor(Color.BLACK);
        paperPaint.setStrokeWidth(1);

        textPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if (!isInEditMode()) {
            Typeface typeface = FontUtil.getDefaultFont(context);
            textPaint.setTypeface(typeface);
        }

        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MEASUREMENT_FONT_SIZE, getResources().getDisplayMetrics()));

        photo = getResources().getDrawable(R.drawable.ann1);

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

        if (multiFile) {
            Bitmap bitmap;
            if (pageHeight == 7) {
                bitmap = ImageLoaderUtil.getImage(context, PrintUtil.IMAGE_SIZE_5x7);
            } else {
                bitmap = ImageLoaderUtil.getImage(context, PrintUtil.IMAGE_SIZE_4x5);
            }
            photo = new BitmapDrawable(context.getResources(), bitmap);
        }

        //draw label
        final int labelXOrigin = pageBounds.centerX();
        final int labelYOrigin = pageBounds.bottom + (int) pxOffset * 2;
        canvas.drawText(dimens, labelXOrigin, labelYOrigin, textPaint);

        if (photo == null) {
            return;
        }
        findPhotoBounds();

        canvas.saveLayer(pageBounds.left, pageBounds.top, pageBounds.right, pageBounds.bottom, null, Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.MATRIX_SAVE_FLAG);

        paperPaint.setStyle(Paint.Style.FILL);
        paperPaint.setColor(Color.WHITE);
        canvas.drawRect(pageBounds, paperPaint);
        photo.draw(canvas);
        canvas.restore();
    }

    //This method needs corresponding one for printdocument adapter to make the result print same as the preview.
    private void findPhotoBounds() {
        switch (scaleType) {
            default:
            case CENTER:
                //TODO: Not required for current use case
                break;
            case CENTER_CROP:
                setImageBoundsToCenterCrop();
                break;
            case CENTER_INSIDE:
                //TODO: Not required for current use case
                break;
            case FIT_XY:
                photo.setBounds(pageBounds);
                break;
        }
    }

    private void setImageBoundsToCenterCrop() {
        float pageboundswDPI = pageBounds.width() / pageWidth;

        int photoWidth = (int) (pageWidth * pageboundswDPI);

        // This condtional is for printing 4x5 template on all media types 4 in. wide.
        int photoHeight = (int) (pageWidth == 4 ? 5 * pageboundswDPI : pageHeight * pageboundswDPI);

        float scale;

        if (((pageHeight == 6 || pageHeight == 5) && pageWidth == 4) || (pageHeight == 7 && pageWidth == 5)) {
            scale = pageBounds.width() / ((float) photoWidth);
        } else {
            scale = pageBounds.width() / ((float) photoWidth);
            scale = scale / (pageWidth / 4);
        }

        photoWidth *= scale;
        photoHeight *= scale;

        final int left = pageBounds.centerX() - photoWidth / 2;
        final int right = left + photoWidth;
        final int top;

        if (pageWidth == 4) {
            top = pageBounds.top;
        } else {
            top = pageBounds.centerY() - photoHeight / 2;
        }

        final int bottom = top + photoHeight;

        photo.setBounds(new Rect(left, top, right, bottom));
    }

    public static float getImageScale(int photoWidth, int photoHeight, int canvasWidth, int canvasHeight) {
        float scale = 1;

        if (photoWidth > canvasWidth && photoHeight > canvasHeight) {

            float wScale = canvasWidth / (float) photoWidth;
            float hScale = canvasHeight / (float) photoHeight;

            scale = (hScale > wScale) ? hScale : wScale;
        } else if (photoHeight > canvasHeight) {
            scale = canvasHeight / (float) photoHeight;
        } else if (photoWidth > canvasWidth) {
            scale = canvasWidth / (float) photoWidth;
        }
        return scale;
    }

    private Rect getPreviewImageRect() {

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

        boolean widthLimiting = (width / (float) getMeasuredWidth() > height / (float) getMeasuredHeight()) ? true : false;

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


        return new Rect((int) (left), (int) (top - pxOffset / 2),
                (int) (right), (int) (bottom - pxOffset / 2));
    }

    public void setPhoto(Drawable photo) {
        this.photo = photo;
        postInvalidate();
    }

    public void setPaperColor(int color) {
        paperColor = color;
        postInvalidate();
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        postInvalidate();
    }

    public void setOrientation(boolean landscape) {
        this.landscape = landscape;
        requestLayout();
    }

    public void setMultiFile(boolean multiFile) {
        this.multiFile = multiFile;
    }

    public boolean getMultiFile() {
        return multiFile;
    }

    public void setPageSize(float width, float height) {
        pageWidth = width;
        pageHeight = height;

        dimens = String.format("%s x %s", fmt((double)pageWidth), fmt((double)pageHeight));
        textPaint.getTextBounds(dimens, 0, dimens.length() - 1, textBounds);

        if (Looper.myLooper() == Looper.getMainLooper()) {
            requestLayout();
            postInvalidate();

        } else {
            this.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                    postInvalidate();

                }
            });
        }
    }
    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
}
