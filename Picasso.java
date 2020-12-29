//package com.google.appinventor.components.runtime;
package com.sunny.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

@DesignerComponent(version = 1,
        description = "Extension to load images with Picasso <br> Developed by Sunny Gupta",
        nonVisible = true,
        iconName = "https://res.cloudinary.com/andromedaviewflyvipul/image/upload/c_scale,h_20,w_20/v1571472765/ktvu4bapylsvnykoyhdm.png",
        category = ComponentCategory.EXTENSION,
        helpUrl = "https://github.com/vknow360/Picasso")
@SimpleObject(external=true)
@UsesPermissions(permissionNames = "android.permission.INTERNET,android.permission.READ_EXTERNAL_STORAGE")
@UsesLibraries(libraries =  "picasso.jar")
public class Picasso extends AndroidNonvisibleComponent{
    public Context context;
    public View view;
    public Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, com.squareup.picasso.Picasso.LoadedFrom loadedFrom) {
            view.setBackground(new BitmapDrawable(form.getResources(),bitmap));
            Success();
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            view.setBackground(drawable);
            ErrorOccurred("Failed to load image");
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            view.setBackground(drawable);
        }
    };
    public Picasso(ComponentContainer container){
        super(container.$form());
        context = container.$context();
    }
    @SimpleProperty(description = "Attempt to resize the image to fit exactly into the target view")
    public String Fit(){
        return "FIT";
    }
    @SimpleProperty(description = "Crops an image inside of the bounds")
    public String CenterCrop(){
        return "CENTER_CROP";
    }
    @SimpleProperty(description = "Centers an image inside of the bounds ")
    public String CenterInside(){
        return "CENTER_INSIDE";
    }
    @SimpleProperty(description = "Only resize an image if the original image size is bigger than the target size")
    public String OnlyScaleDown(){
        return "ONLY_SCALE_DOWN";
    }
    @SimpleProperty(description = "Disable brief fade in of images loaded from the disk cache or network")
    public String NoFade(){
        return "NO_FADE";
    }
    @SimpleFunction(description = "Tries to load image from path on given component with transformation options")
    public void LoadImage(AndroidViewComponent component,String path,String placeholderImage,String errorImage,int height,int width,int rotateDegree,String transformation,boolean enableIndicators) {
        view = component.getView();
        try{
            RequestCreator picasso = com.squareup.picasso.Picasso.with(context).load(path);
            if (!errorImage.isEmpty()) {
                picasso.error(Drawable.createFromPath(errorImage));
            }
            if (!placeholderImage.isEmpty()) {
                picasso.placeholder(Drawable.createFromPath(placeholderImage));
            }
            if (height != 0 && width != 0) {
                picasso.resize(width,height);
            }
            if (rotateDegree != 0) {
                picasso.rotate(rotateDegree);
            }
            if (!transformation.isEmpty()) {
                switch (transformation) {
                    case "CENTER_CROP":
                        picasso.centerCrop();
                        break;
                    case "CENTER_INSIDE":
                        picasso.centerInside();
                        break;
                    case "ONLY_SCALE_DOWN":
                        picasso.onlyScaleDown();
                        break;
                    case "NO_FADE":
                        picasso.noFade();
                        break;
                }
            }
            picasso.into(target);
        }catch (Exception e){
            e.printStackTrace();
            ErrorOccurred(e.getMessage()!=null?e.getMessage():e.toString());
        }
    }
    @SimpleEvent(description = "Event invoked if image has been loaded successfully")
    public void Success(){
        EventDispatcher.dispatchEvent(this,"Success");
    }
    @SimpleEvent(description = "Event invoked when any error occurs and provides error message")
    public void ErrorOccurred(String message){
        EventDispatcher.dispatchEvent(this,"ErrorOccurred",message);
    }
}
