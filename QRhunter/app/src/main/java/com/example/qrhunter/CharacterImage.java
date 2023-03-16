package com.example.qrhunter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class CharacterImage {
    private Bitmap body;
    private Bitmap arms;
    private Bitmap legs;
    private Bitmap eyes;
    private Bitmap mouth;
    private Bitmap hat;

    public CharacterImage(Context context,int radius,String armsFileName,String legsFileName,
                     String eyesFileName,String mouthFileName,String hatFileName) {
        // Create a new bitmap for the body with the given radius
        this.body = Bitmap.createBitmap(radius * 2,radius * 2,Bitmap.Config.ARGB_8888);

        // Get the canvas for the body bitmap
        Canvas canvas = new Canvas(this.body);

        // Create a paint object with a random color
        Paint paint = new Paint();
        paint.setColor(Color.rgb((int)(Math.random() * 256),(int)(Math.random() * 256),(int)(Math.random() * 256)));

        // Draw a circle onto the canvas to represent the body
        canvas.drawCircle(radius,radius,radius,paint);

        // Get resources object from context
        Resources resources=context.getResources();

        // Get package name from context
        String packageName=context.getPackageName();

        // Get resource IDs for drawables based on their file names
        int armsResId=resources.getIdentifier(armsFileName,"drawable",packageName);
        int legsResId=resources.getIdentifier(legsFileName,"drawable",packageName);
        int eyesResId=resources.getIdentifier(eyesFileName,"drawable",packageName);
        int mouthResId=resources.getIdentifier(mouthFileName,"drawable",packageName);
        int hatResId=resources.getIdentifier(hatFileName,"drawable",packageName);

        // Load drawables from resources using their resource IDs
        Drawable armsDrawable=resources.getDrawable(armsResId);
        Drawable legsDrawable=resources.getDrawable(legsResId);
        Drawable eyesDrawable=resources.getDrawable(eyesResId);
        Drawable mouthDrawable=resources.getDrawable(mouthResId);
        Drawable hatDrawable=resources.getDrawable(hatResId);

        // Convert drawables to bitmaps
        this.arms=((BitmapDrawable)armsDrawable).getBitmap();
        this.legs=((BitmapDrawable)legsDrawable).getBitmap();
        this.eyes=((BitmapDrawable)eyesDrawable).getBitmap();
        this.mouth=((BitmapDrawable)mouthDrawable).getBitmap();
        this.hat=((BitmapDrawable)hatDrawable).getBitmap();
    }

    public Bitmap getCharacterImage() {
        // Create a new bitmap with same dimensions as body bitmap
        int width = body.getWidth();
        int height = body.getHeight();
        Bitmap characterImage = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        // Get canvas for new bitmap
        Canvas canvas=new Canvas(characterImage);

        // Draw bitmaps onto canvas in desired order and positions
        canvas.drawBitmap(body,0f,0f,null);
        canvas.drawBitmap(arms,width/2-arms.getWidth()/2,height/2-arms.getHeight()/2,null);
        canvas.drawBitmap(legs,width/2-legs.getWidth()/2,height/2+body.getHeight()/4,null);
        canvas.drawBitmap(eyes,width/2-eyes.getWidth()/2,height/4,null);
        canvas.drawBitmap(mouth,width/2-mouth.getWidth()/2,height*3/5,null);
        canvas.drawBitmap(hat,width/2-hat.getWidth()/2,-hat.getHeight()/4,null);

        return characterImage;
    }
}