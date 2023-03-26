package com.example.qrhunter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class CharacterImage {
    private Bitmap body;
    private Bitmap arms;
    private Bitmap legs;
    private Bitmap eyes;
    private Bitmap mouth;
    private Bitmap hat;

    public CharacterImage(Context context,int radius,String armsFileName,String legsFileName,
                     String eyesFileName,String mouthFileName,String hatFileName, String firstSixDigitsString) {
        // Create a new bitmap for the body with the given radius
        this.body = Bitmap.createBitmap(radius * 2,radius * 2,Bitmap.Config.ARGB_8888);

        // Get the canvas for the body bitmap
        Canvas canvas = new Canvas(this.body);

        // Create a paint object with a colour based on first five digits of hash code
        Paint paint = new Paint();
//        int redValue = Integer.parseInt(firstSixDigitsString.substring(0,1)) * Integer.parseInt(firstSixDigitsString.substring(1,2)) + 128;
//        int greenValue = Integer.parseInt(firstSixDigitsString.substring(2,3))  * Integer.parseInt(firstSixDigitsString.substring(3,4)) + 128;
//        int blueValue = Integer.parseInt(firstSixDigitsString.substring(4,5)) * Integer.parseInt(firstSixDigitsString.substring(5,6)) + 128;

        float[] hsl = new float[3];  // Hue-Saturation-Lightness
        hsl[0] = (float) Math.random() * 360;  // Hue
        hsl[1] = (float) (0.6 + Math.random() * (0.7 - 0.6));  // Saturation
        hsl[2] = (float) (0.75 + Math.random() * (0.9 - 0.75));  // Lightness
//        paint.setColor(Color.rgb((new Random().nextInt(14-6) + 6) * new Random().nextInt(14-6) + 56,(new Random().nextInt(14-6) + 6) * new Random().nextInt(14-6) + 56,(new Random().nextInt(14-6) + 6) * new Random().nextInt(14-6) + 56));
        paint.setColor(Color.HSVToColor(hsl));
//        paint.setColor(Color.rgb(redValue,greenValue,blueValue));

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
        int width = 450;
        int height = 450;
        int radius = 120;
        Bitmap characterImage = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        // Get canvas for new bitmap
        Canvas canvas=new Canvas(characterImage);

        // Draw bitmaps onto canvas in desired order and positions
        canvas.drawBitmap(body,radius - radius/8,radius/2 + hat.getHeight()/4 + radius/4,null);
        canvas.drawBitmap(arms,radius + arms.getWidth()/2 - radius/8,2*radius + radius/4,null);
        canvas.drawBitmap(legs,radius + radius/7,2*radius + radius/4,null);
        canvas.drawBitmap(eyes,radius + radius/7,radius/2 + radius/5 + radius/4,null);
        canvas.drawBitmap(mouth, radius + radius/7,radius + 2*legs.getHeight()/3 + radius/4,null);
        canvas.drawBitmap(hat,radius + radius/7,hat.getHeight() - radius + hat.getHeight()/4 + radius/4,null);

        return characterImage;
    }
}