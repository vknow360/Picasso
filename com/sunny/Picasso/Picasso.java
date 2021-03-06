package com.sunny.Picasso;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

@DesignerComponent(version = 1,
        versionName = "1.1",
        description = "Extension to load images with Picasso <br> Developed by Sunny Gupta",
        nonVisible = true,
        iconName = "https://res.cloudinary.com/andromedaviewflyvipul/image/upload/c_scale,h_20,w_20/v1571472765/ktvu4bapylsvnykoyhdm.png",
        category = ComponentCategory.EXTENSION,
        helpUrl = "https://github.com/vknow360/Picasso")
@SimpleObject(external=true)
@UsesPermissions(permissionNames = "android.permission.INTERNET,android.permission.READ_EXTERNAL_STORAGE")
@UsesLibraries(libraries =  "picasso.jar")
public class Picasso extends AndroidNonvisibleComponent{
    public Activity activity;
    public Context context;
    public com.squareup.picasso.Picasso picasso;
    public View view;
    public Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, com.squareup.picasso.Picasso.LoadedFrom loadedFrom) {
            setBackground(new BitmapDrawable(form.getResources(),bitmap));
            postResult(null);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            setBackground(drawable);
            postResult("Failed to load image");
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            setBackground(drawable);
        }
    };
    public Picasso(ComponentContainer container){
        super(container.$form());
        context = container.$context();
        activity = (Activity) context;
        picasso = com.squareup.picasso.Picasso.with(context);
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
    public void LoadImage(final AndroidViewComponent component,final String path,final String placeholderImage,final String errorImage,final int height,final int width,final int rotateDegree,String transformation,final boolean enableIndicators) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                view = component.getView();
                try{
                    picasso.setIndicatorsEnabled(enableIndicators);
                    RequestCreator loader = picasso.load(path);
                    if (!errorImage.isEmpty()) {
                        loader.error(Drawable.createFromPath(errorImage));
                    }
                    if (!placeholderImage.isEmpty()) {
                        loader.placeholder(Drawable.createFromPath(placeholderImage));
                    }
                    if (height != 0 && width != 0) {
                        loader.resize(width,height);
                    }
                    if (rotateDegree != 0) {
                        loader.rotate(rotateDegree);
                    }
                    if (!transformation.isEmpty()) {
                        switch (transformation) {
                            case "CENTER_CROP":
                                loader.centerCrop();
                                break;
                            case "CENTER_INSIDE":
                                loader.centerInside();
                                break;
                            case "ONLY_SCALE_DOWN":
                                loader.onlyScaleDown();
                                break;
                            case "NO_FADE":
                                loader.noFade();
                                break;
                        }
                    }
                    loader.into(target);
                }catch (Exception e){
                    e.printStackTrace();
                    postResult(e.getMessage()!=null?e.getMessage():e.toString());
                }
            }
        });
    }
    @SimpleFunction(description = "Invalidates cache from the disk and removes if found.")
    public void InvalidateCache(String path){
        picasso.invalidate(path);
    }
    public void postResult(final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message == null){
                    Success();
                }else {
                    ErrorOccurred(message);
                }
            }
        });
    }
    public void setBackground(Drawable drawable){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setBackground(drawable);
            }
        });
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
