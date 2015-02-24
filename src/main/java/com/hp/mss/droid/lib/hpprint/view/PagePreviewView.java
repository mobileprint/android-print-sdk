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

package com.hp.mss.droid.lib.hpprint.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.hp.mss.droid.lib.hpprint.R;

public class PagePreviewView extends View {
    private static final int LAYOUT_MARGIN_RATIO = 8;
    private static final int DEFAULT_PAGE_WIDTH = 5;
    private static final int DEFAULT_PAGE_HEIGHT = 7;
    private static final int MEASUREMENT_FONT_SIZE = 15;
    private static final int CARD_SHADOW_OFFSET = 10;
    private static final int CARD_SHADOW_RADIUS = 20;

    private Paint solidPaint;

    private Paint dottedPaint;

    private Paint textPaint;

    private Drawable photo;

    private final Rect pageBounds = new Rect();
    private float pxOffset;
    private Path leftArrow;
    private Path rightArrow;
    private Path upArrow;
    private Path downArrow;
    private float pageWidth = DEFAULT_PAGE_WIDTH;
    private float pageHeight = DEFAULT_PAGE_HEIGHT;
    private boolean landscape;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_CROP;
    private int paperColor = Color.WHITE;
    private Paint paperPaint;
    private Point photoSize;
    private String widthText;
    private String heightText;
    Rect textWidthBounds = new Rect();
    Rect textHeightBounds = new Rect();

    public PagePreviewView(Context context) {
        this(context, null);
    }

    public PagePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        pxOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

        paperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paperPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        paperPaint.setColor(Color.BLACK);
        paperPaint.setStrokeWidth(1);

        solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        solidPaint.setStyle(Paint.Style.FILL);
        solidPaint.setColor(Color.BLACK);

        dottedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dottedPaint.setStyle(Paint.Style.STROKE);
        dottedPaint.setColor(Color.BLACK);
        dottedPaint.setAlpha(120);
        dottedPaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0));

        //draw text
        textPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        if (!isInEditMode()) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/HPSimplified_Rg.ttf");
            textPaint.setTypeface(typeface);
        }

        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MEASUREMENT_FONT_SIZE, getResources().getDisplayMetrics()));

        photo = getResources().getDrawable(R.drawable.ann1);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        pageBounds.set(getPreviewImageRect());
        updatePaths();

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw lines
        float bottomOffset = pageBounds.bottom + pxOffset;
        float rightOffset = pageBounds.right + pxOffset;

        canvas.drawLine(pageBounds.left, bottomOffset, pageBounds.right, bottomOffset, solidPaint);
        canvas.drawLine(rightOffset, pageBounds.top, rightOffset, pageBounds.bottom, solidPaint);

        canvas.drawLine(pageBounds.left, pageBounds.bottom, pageBounds.left, bottomOffset, dottedPaint);
        canvas.drawLine(pageBounds.right, pageBounds.bottom, pageBounds.right, bottomOffset, dottedPaint);
        canvas.drawLine(pageBounds.right, pageBounds.bottom, rightOffset, pageBounds.bottom, dottedPaint);
        canvas.drawLine(pageBounds.right, pageBounds.top, rightOffset, pageBounds.top, dottedPaint);

        canvas.drawPath(leftArrow, solidPaint);
        canvas.drawPath(rightArrow, solidPaint);
        canvas.drawPath(upArrow, solidPaint);
        canvas.drawPath(downArrow, solidPaint);


        final int widthXOrigin = pageBounds.left + pageBounds.width() / 2 - textWidthBounds.centerX();
        final int widthYOrigin = pageBounds.bottom + (int) pxOffset * 2;
        canvas.drawText(widthText, widthXOrigin, widthYOrigin, textPaint);

        final int heightYOrigin = pageBounds.top + pageBounds.height() / 2 - textHeightBounds.centerY();
        final int heightXOrigin = pageBounds.right + (int) pxOffset + (int) (pxOffset/2);
        canvas.drawText(heightText, heightXOrigin, heightYOrigin, textPaint);

        if (photo == null) {
            return;
        }

        findPhotoBounds();

//        paperPaint.setStyle(Paint.Style.FILL);
//        paperPaint.setColor(Color.BLACK);
//        canvas.drawRect(pageBounds, paperPaint);

        float cardShadowRadiuspx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CARD_SHADOW_RADIUS, getResources().getDisplayMetrics());
        float cardShadowOffsetpx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CARD_SHADOW_OFFSET, getResources().getDisplayMetrics());

        paperPaint.setShadowLayer(cardShadowRadiuspx,cardShadowOffsetpx,cardShadowOffsetpx,Color.BLACK);
        setLayerType(LAYER_TYPE_SOFTWARE, paperPaint);
        canvas.drawRect(pageBounds, paperPaint);

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
                //setImageBoundsToCenter();
                break;
            case CENTER_CROP:
                setImageBoundsToCenterCrop();
                break;
            case CENTER_INSIDE:
                //setImageBoundsToCenterInside();
                break;
            case FIT_XY:
                photo.setBounds(pageBounds);
                break;
        }
    }

    private void setImageBoundsToCenterCrop() {
        float pageboundswDPI = pageBounds.width() / pageWidth;

        int photoWidth = (int) (photoSize.x * pageboundswDPI);
        int photoHeight = (int) (photoSize.y * pageboundswDPI);

        float scale = getImageScale(photoWidth, photoHeight, pageBounds.width(), pageBounds.height());

        photoWidth *= scale;
        photoHeight *= scale;

        final int left = pageBounds.centerX() - photoWidth / 2;
        final int right = left + photoWidth;
        final int top = pageBounds.centerY() - photoHeight / 2;
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

    private void setImageBoundsToCenter() {

        final int intrinsicWidth = photo.getIntrinsicWidth();
        final int intrinsicHeight = photo.getIntrinsicHeight();

        final int left = pageBounds.left + (pageBounds.width() - intrinsicWidth) / 2;
        final int right = pageBounds.top + (pageBounds.height() - intrinsicHeight) / 2;
        photo.setBounds(left, right, left+intrinsicWidth, right+ intrinsicHeight);
    }

    private void setImageBoundsToCenterInside() {
        final int intrinsicWidth = photo.getIntrinsicWidth();
        final int intrinsicHeight = photo.getIntrinsicHeight();
        if (landscape) {
            int heightbound = pageBounds.height();
            int widthBound = heightbound * intrinsicWidth / intrinsicHeight;
            int left = pageBounds.centerX() - widthBound / 2;
            int right = pageBounds.centerX() + widthBound / 2;
            photo.setBounds(left, pageBounds.top, right, pageBounds.bottom);
        } else {
            int widthBound = pageBounds.width();
            int heightbound = widthBound * intrinsicHeight / intrinsicWidth;
            int top = pageBounds.centerY() - heightbound / 2;
            int bottom = pageBounds.centerY() + heightbound / 2;
            photo.setBounds(pageBounds.left, top, pageBounds.right, bottom);
        }
    }

    private void updatePaths() {
        //draw triangles
        leftArrow = new Path();
        leftArrow.moveTo(pageBounds.left, pageBounds.bottom + pxOffset);
        leftArrow.lineTo(pageBounds.left + pxOffset / 2, pageBounds.bottom + pxOffset - pxOffset / 4);
        leftArrow.lineTo(pageBounds.left + pxOffset / 2, pageBounds.bottom + pxOffset + pxOffset / 4);
        leftArrow.close();

        rightArrow = new Path();
        rightArrow.moveTo(pageBounds.right, pageBounds.bottom + pxOffset);
        rightArrow.lineTo(pageBounds.right - pxOffset / 2, pageBounds.bottom + pxOffset - pxOffset / 4);
        rightArrow.lineTo(pageBounds.right - pxOffset / 2, pageBounds.bottom + pxOffset + pxOffset / 4);
        rightArrow.close();

        upArrow = new Path();
        upArrow.moveTo(pageBounds.right + pxOffset, pageBounds.top);
        upArrow.lineTo(pageBounds.right + pxOffset + pxOffset / 4, pageBounds.top + pxOffset / 2);
        upArrow.lineTo(pageBounds.right + pxOffset - pxOffset / 4, pageBounds.top + pxOffset / 2);
        upArrow.close();

        downArrow = new Path();
        downArrow.moveTo(pageBounds.right + pxOffset, pageBounds.bottom);
        downArrow.lineTo(pageBounds.right + pxOffset + pxOffset / 4, pageBounds.bottom - pxOffset / 2);
        downArrow.lineTo(pageBounds.right + pxOffset - pxOffset / 4, pageBounds.bottom - pxOffset / 2);
        downArrow.close();
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


        return new Rect((int) (left - pxOffset + 15), (int) (top - pxOffset / 2),
                (int) (right - pxOffset + 15), (int) (bottom - pxOffset / 2));
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

    public void setPageSize(float width, float height) {
        pageWidth = width;
        pageHeight = height;

        widthText = pageWidth + "\"";
        textPaint.getTextBounds(widthText, 0, widthText.length() - 1, textWidthBounds);

        heightText = pageHeight + "\"";
        textPaint.getTextBounds(heightText, 0, heightText.length() - 1, textHeightBounds);

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

    public void setPhotoSize(Point size) {
        this.photoSize = size;
    }
}
