package com.umutkina.findunfollowersapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.umutkina.findunfollowersapp.R;

public class RoundedImageView extends ImageView {

    Context context;
    String path;
    private int type = 0;
    public static final int FULL_ROUNDED = 0;
    public static final int ROUNDED_CORNER = 1;

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public RoundedImageView(Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
    }


    public void setImageUrlRounded(String path) {
        this.path = path;
        if (path!=null&&path.length()>0){
            LongOperation longOperation = new LongOperation();
            longOperation.execute() ;
        }
        else{
            setImageResource(R.drawable.profile );
        }
    }

    public void setImageUrlRoundedCorner(String path, int type) {
        this.path = path;
        this.type = type;
        LongOperation longOperation = new LongOperation();
        longOperation.execute();
    }

    public void setImageUrlRoundedCornerBitmap(Bitmap bitmap, int widthHeightValue, int type) {

        this.type = type;

        Bitmap roundedCornerImage;
        if (type == FULL_ROUNDED) {
            roundedCornerImage = getRoundedCornerImageFull(bitmap, widthHeightValue
            );
        } else {
            roundedCornerImage = getRoundedCornerImageLittle(bitmap
            );
        }


        setImageBitmap(roundedCornerImage);
    }

    private class LongOperation extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap bitmap = null;
            try {
                System.out.println("width - height :" + getWidth() + "--" + getHeight());
                System.out.println("path : " + path);
                int witdh = getWidth();
                if (witdh == 0) {
                    witdh = 200;
                }
                bitmap = Picasso
                        .with(context)
                        .load(path)
                        .centerCrop().resize(witdh, witdh).get();

                System.out.println("bitmap width - height :" + bitmap.getWidth() + "--" + bitmap.getHeight());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                Bitmap roundedCornerImage;
                if (type == 0) {
                    roundedCornerImage = getRoundedCornerImageFull(result, 0
                    );
                } else {
                    roundedCornerImage = getRoundedCornerImageLittle(result
                    );
                }

                setImageBitmap(roundedCornerImage);
            }
        }
    }


    public Bitmap getRoundedCornerImageFull(Bitmap bitmap, int widthHeightValue) {
        if (widthHeightValue == 0) {
            widthHeightValue = bitmap.getHeight();
        }

//        final float roundPx;
//        if (widthHeightValue==200){
//            roundPx=100;
//        }
//        else{
//            roundPx = getWidth() / 2;
//        }
        Bitmap output = Bitmap.createBitmap(widthHeightValue,
                widthHeightValue, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, widthHeightValue, widthHeightValue);

//        System.out.println("width - height :" + getWidth() + "--" + getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = widthHeightValue / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }

    public Bitmap getRoundedCornerImageLittle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getHeight() / 8;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }
}
