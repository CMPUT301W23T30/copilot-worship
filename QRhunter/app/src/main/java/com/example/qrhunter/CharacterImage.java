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

import androidx.annotation.ColorInt;

import com.google.firebase.firestore.CollectionReference;

import java.util.Random;

/**
 * Class that represents the QReatures character image. This class is used to create a bitmap for the QReatures.
 */
public class CharacterImage {
    private Context context;
    private Bitmap body;
    private Bitmap arms;
    private Bitmap legs;
    private Bitmap eyes;
    private Bitmap mouth;
    private Bitmap hat;

    /**
     * Constructor
     * @param context Context of the activity that is creating the CharacterImage object
     * @param armsFileName File name of the arms image (String)
     * @param legsFileName File name of the legs image (String)
     * @param eyesFileName File name of the eyes image (String)
     * @param mouthFileName File name of the mouth image (String)
     * @param hatFileName File name of the hat image (String)
     * @param firstSixDigitsString First six digits of the hash code of the username (String)
     */
    public CharacterImage(Context context, String armsFileName,String legsFileName,
                     String eyesFileName,String mouthFileName,String hatFileName, String firstSixDigitsString) {

        // Create a new bitmap for the body with the given radius
        int radius = (int) (43 * context.getResources().getDisplayMetrics().density);
        this.body = Bitmap.createBitmap(radius * 2,radius * 2,Bitmap.Config.ARGB_8888);
        this.context = context;

        // Get the canvas for the body bitmap
        Canvas canvas = new Canvas(this.body);

        // Create a paint object with a colour based on first six digits of hash code
        // Hue, Saturation, Lightness used to ensure that the colour is not too dark or too light, and is visually appealing
        Paint paint = new Paint();
        float hueValue = ((Float.parseFloat(firstSixDigitsString.substring(0,1)) * Float.parseFloat(firstSixDigitsString.substring(1,2))) % 36) * 10;
        float satValue = (float) 0.65;
        float lightnessValue = (float) 0.85;

        float[] hsl = new float[3];  // Hue-Saturation-Lightness
        hsl[0] = (float) (hueValue);  // Hue
        hsl[1] = satValue;  // Saturation
        hsl[2] = lightnessValue;  // Lightness

        paint.setColor(Color.HSVToColor(hsl));

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

    /**
     * Method that creates the QReature character image and returns it as a bitmap
     * @return Bitmap of the QReature
     */
    public Bitmap getCharacterImage() {
        // Create a new bitmap with same dimensions as body bitmap
        // Test pixel density (Pixel 3 API 31): 2.75
        // 450 px = 164 dp, 120 px = 43 dp

        // Makes sure it displays correctly for devices with different pixel densities
        int width = (int) (164 * context.getResources().getDisplayMetrics().density);
        int height = (int) (164 * context.getResources().getDisplayMetrics().density);
        int radius = (int) (43 * context.getResources().getDisplayMetrics().density);
        Bitmap characterImage = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        // Get canvas for new bitmap
        Canvas canvas = new Canvas(characterImage);

        //Set to white for jpeg purpose
        canvas.drawColor(Color.WHITE);

        // Draw bitmaps onto canvas in desired order and positions
        canvas.drawBitmap(body,radius - radius/8,radius/2 + hat.getHeight()/4 + radius/4,null);
        canvas.drawBitmap(arms,radius + arms.getWidth()/2 - radius/8,2*radius + radius/4,null);
        canvas.drawBitmap(legs,radius + radius/8,2*radius + radius/4,null);
        canvas.drawBitmap(eyes,radius + radius/8,radius/2 + radius/5 + radius/4,null);
        canvas.drawBitmap(mouth, radius + radius/8,radius + 2*legs.getHeight()/3 + radius/4,null);
        canvas.drawBitmap(hat,radius + radius/8,hat.getHeight() - radius + hat.getHeight()/4 + radius/4,null);

        return characterImage;
    }
}