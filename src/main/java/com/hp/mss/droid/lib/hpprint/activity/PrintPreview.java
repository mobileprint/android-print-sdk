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

package com.hp.mss.droid.lib.hpprint.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v4.print.PrintHelper;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.hp.mss.droid.lib.hpprint.R;
import com.hp.mss.droid.lib.hpprint.adapter.PhotoPrintDocumentAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class PrintPreview extends Activity{

    private static int LAYOUT_MARGIN_RATIO = 8;
    static String HP_ANDROID_MOBILE_SITE = "http://www8.hp.com/us/en/ads/mobility/overview.html?jumpid=va_r11400_eprint";
    String photoFileName = null;
    Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        String photoFileName = (String) getIntent().getExtras().get("photoFileUri");
        photo = getImageBitmap(this, photoFileName);

        Spinner size_spinner = (Spinner) findViewById(R.id.paper_size_spinner);
        ArrayAdapter<CharSequence> size_adapter = ArrayAdapter.createFromResource(this,
                R.array.paper_size_array, android.R.layout.simple_spinner_item);
        size_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        size_spinner.setAdapter(size_adapter);

        Spinner type_spinner = (Spinner) findViewById(R.id.paper_type_spinner);
        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(this,
                R.array.paper_type_array, android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(type_adapter);
        drawTest();

        LinearLayout supportLayout = (LinearLayout) findViewById(R.id.support_layout);
        supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mobileSiteIntent = new Intent(Intent.ACTION_VIEW);
                mobileSiteIntent.setData(Uri.parse(HP_ANDROID_MOBILE_SITE));
                startActivity(mobileSiteIntent);
            }
        });
    }

    private void performPrint() {

        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = this.getString(R.string.app_name);
        PrintDocumentAdapter adapter = new PhotoPrintDocumentAdapter(this, photo);
        printManager.print(jobName, adapter, null );

//        PrintHelper printHelper = new PrintHelper(this);
//        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//        printHelper.printBitmap("Print Photo", photo);

    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;


    private void drawTest(){
        final ImageView previewImageView = (ImageView) findViewById(R.id.preview_image_view);

        ViewTreeObserver observer = previewImageView.getViewTreeObserver();

        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                previewImageView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);


                Bitmap tempBitmap = Bitmap.createBitmap(previewImageView.getWidth(), previewImageView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas tempCanvas = new Canvas(tempBitmap);

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLACK);

                Bitmap bitmap32 = photo.copy(Bitmap.Config.ARGB_8888, true);
                RectF rect = getPreviewImageRect(previewImageView);
                tempCanvas.drawBitmap(bitmap32, null, rect, null);

                //draw lines
                Resources r = getResources();
                float pxOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());

                float bottomOffset = rect.bottom+pxOffset;
                float rightOffset = rect.right+pxOffset;

                tempCanvas.drawLine(rect.left, bottomOffset, rect.right, bottomOffset, paint);
                tempCanvas.drawLine(rightOffset, rect.top, rightOffset, rect.bottom, paint);

                //draw dotted lines
                Paint dottedLines = new Paint();
                dottedLines.setPathEffect(new DashPathEffect(new float[] {5,5}, 2));
                
                tempCanvas.drawLine(rect.left, rect.bottom, rect.left, bottomOffset, dottedLines);
                tempCanvas.drawLine(rect.right, rect.bottom, rect.right, bottomOffset, dottedLines);
                tempCanvas.drawLine(rect.right, rect.bottom, rightOffset, rect.bottom, dottedLines);
                tempCanvas.drawLine(rect.right, rect.top, rightOffset, rect.top, dottedLines);

                //draw triangles
                Path leftArrow = new Path();
                leftArrow.moveTo(rect.left, rect.bottom + pxOffset);
                leftArrow.lineTo(rect.left + pxOffset / 2, rect.bottom + pxOffset - pxOffset / 4);
                leftArrow.lineTo(rect.left + pxOffset / 2, rect.bottom + pxOffset + pxOffset / 4);
                leftArrow.close();
                tempCanvas.drawPath(leftArrow, paint);

                Path rightArrow = new Path();
                rightArrow.moveTo(rect.right, rect.bottom + pxOffset);
                rightArrow.lineTo(rect.right - pxOffset / 2, rect.bottom + pxOffset - pxOffset / 4);
                rightArrow.lineTo(rect.right-pxOffset/2, rect.bottom+pxOffset+pxOffset/4);
                rightArrow.close();
                tempCanvas.drawPath(rightArrow, paint);

                Path upArrow = new Path();
                upArrow.moveTo(rect.right+pxOffset, rect.top);
                upArrow.lineTo(rect.right + pxOffset + pxOffset / 4, rect.top + pxOffset / 2);
                upArrow.lineTo(rect.right+pxOffset-pxOffset/4, rect.top+pxOffset/2);
                upArrow.close();
                tempCanvas.drawPath(upArrow, paint);

                Path downArrow = new Path();
                downArrow.moveTo(rect.right+pxOffset, rect.bottom);
                downArrow.lineTo(rect.right + pxOffset + pxOffset / 4, rect.bottom - pxOffset / 2);
                downArrow.lineTo(rect.right+pxOffset-pxOffset/4, rect.bottom-pxOffset/2);
                downArrow.close();
                tempCanvas.drawPath(downArrow, paint);

                //draw text
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/HPSimplified_Rg.ttf");
                Paint textPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(Color.BLACK);
                textPaint.setStyle(Paint.Style.FILL);
                textPaint.setTypeface(typeface);
                textPaint.setTextSize(40);

                tempCanvas.drawText("5\"", rect.left + rect.width()/2, rect.bottom + pxOffset*2, textPaint);
                tempCanvas.drawText("7\"", rect.right + pxOffset *2, rect.top +rect.height()/2, textPaint);

                previewImageView.setImageBitmap(tempBitmap);
            }
        };
        observer.addOnGlobalLayoutListener(globalLayoutListener);
    }

    private RectF getPreviewImageRect(ImageView previewImageView){
        int resultHeight;
        int resultWidth;
        int left;
        int top;
        int right;
        int bottom;

        if(photo.getHeight() > photo.getWidth()){
            if(previewImageView.getHeight() < previewImageView.getWidth()){
                resultHeight = previewImageView.getHeight() * (LAYOUT_MARGIN_RATIO - 2) / LAYOUT_MARGIN_RATIO;
                resultWidth = resultHeight * 5/7;

                int horizontalPadding = (previewImageView.getWidth() - resultWidth)/2;

                left = horizontalPadding;
                top = previewImageView.getHeight() / LAYOUT_MARGIN_RATIO;
                right = horizontalPadding+resultWidth;
                bottom = previewImageView.getHeight() * (LAYOUT_MARGIN_RATIO - 1) / LAYOUT_MARGIN_RATIO;
            } else {
                resultWidth = previewImageView.getWidth() * (LAYOUT_MARGIN_RATIO - 2) / LAYOUT_MARGIN_RATIO;
                resultHeight = resultWidth * 7/5;

                int verticalPadding = (previewImageView.getHeight() - resultHeight) / 2;

                left = previewImageView.getWidth() / LAYOUT_MARGIN_RATIO;
                top = verticalPadding;
                right = previewImageView.getWidth() * (LAYOUT_MARGIN_RATIO - 1) / LAYOUT_MARGIN_RATIO;
                bottom = verticalPadding+resultHeight;
            }
        }else{
            left = 0;
            top = 0;
            right = 0;
            bottom = 0;
            Toast.makeText(this, "Preview to be implemented", Toast.LENGTH_SHORT).show();
        }

        return new RectF(left, top, right, bottom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_print_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_print) {
            performPrint();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap getImageBitmap(Activity activity, String photoFileName) {
        Bitmap b = null;

        try {
            File file = new File(photoFileName);

            InputStream inputStream = new FileInputStream(file);
            b = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( photoFileName != null ) {
            File photoFile = new File(photoFileName);
            if ( photoFile.exists() )
                photoFile.deleteOnExit();
        }
    }


}
