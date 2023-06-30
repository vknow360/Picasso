package com.sunny.Picasso;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Picasso extends AndroidNonvisibleComponent {
    public Activity activity;
    public Context context;
    public com.squareup.picasso.Picasso picasso;
    public Picasso(ComponentContainer container) {
        super(container.$form());
        context = container.$context();
        activity = (Activity) context;
        java.io.File cacheDir = context.getCacheDir();
        OkHttpClient client = new OkHttpClient();
        client.setCache(new Cache(cacheDir,Integer.MAX_VALUE));
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().header("Cache-Control","max-age=" + (60 * 60 * 24 * 365)).build();
            }
        });
        OkHttpDownloader httpDownloader = new OkHttpDownloader(client);
        picasso = new com.squareup.picasso.Picasso.Builder(context).downloader(httpDownloader).build();
		picasso.setLoggingEnabled(true);
    }

    @SimpleProperty(description = "Crops an image inside of the bounds")
    public String CenterCrop() {
        return "CENTER_CROP";
    }

    @SimpleProperty(description = "Centers an image inside of the bounds ")
    public String CenterInside() {
        return "CENTER_INSIDE";
    }

    @SimpleProperty(description = "Only resize an image if the original image size is bigger than the target size")
    public String OnlyScaleDown() {
        return "ONLY_SCALE_DOWN";
    }

    @SimpleProperty(description = "Disable brief fade in of images loaded from the disk cache or network")
    public String NoFade() {
        return "NO_FADE";
    }
    @SimpleProperty(description = "Provides circle image transformation")
    public String CircleImage(){
        return "CIRCLE_IMAGE";
    }
    @SimpleProperty(description = "Provides square image transformation")
    public String SquareImage(){
        return "SQUARE_IMAGE";
    }
    @SimpleProperty(description = "Provides greyscale image transformation")
    public String GreyscaleImage(){
        return "GREYSCALE";
    }
    @SimpleFunction(description = "Tries to load image from path on given component with transformation options")
    public void LoadImage(final Object component,
                          final String path,
                          final String placeholderImage,
                          final String errorImage,
                          final int height,
                          final int width,
                          final int rotateDegree,
                          final String transformation,
                          final boolean enableIndicators) {
        final View view = ((AndroidViewComponent)component).getView();
        try {
            picasso.setIndicatorsEnabled(enableIndicators);
            RequestCreator loader = picasso.load(path);
            if (!errorImage.isEmpty()) {
                loader.error(Drawable.createFromPath(errorImage));
            }
            if (!placeholderImage.isEmpty()) {
                loader.placeholder(Drawable.createFromPath(placeholderImage));
            }
            if (height != 0 && width != 0) {
                loader.resize(width, height);
            }
            if (rotateDegree != 0) {
                loader.rotate(rotateDegree);
            }
            if (!transformation.isEmpty() && !transformation.contains(",")) {
                switch (transformation) {
                    case "FIT":
                        loader.fit();
                        break;
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
                    case "CIRCLE_IMAGE":
                        loader.transform(new CircleImageTransformation());
                        break;
                    case "SQUARE_IMAGE":
                        loader.transform(new SquareImageTransformation());
                        break;
                    case "GREYSCALE":
                        loader.transform(new GreyscaleImageTransformation());
                        break;
                }
            }else if (transformation.contains(",")){
                String[] str = transformation.split(",");
                List<Transformation> transformationList = new ArrayList<>();
                for (String s:str){
                    switch (s) {
                        case "FIT":
                            loader.fit();
                            break;
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
                        case "CIRCLE_IMAGE":
                            transformationList.add(new CircleImageTransformation());
                            break;
                        case "SQUARE_IMAGE":
                            transformationList.add(new SquareImageTransformation());
                            break;
                        case "GREYSCALE":
                            transformationList.add(new GreyscaleImageTransformation());
                            break;
                    }
                }
                loader.transform(transformationList);
            }
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, com.squareup.picasso.Picasso.LoadedFrom loadedFrom) {
                    view.setBackground(new BitmapDrawable(form.getResources(), bitmap));
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
            loader.into(target);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorOccurred(e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    @SimpleFunction(description = "Invalidates cache from the disk and removes if found.")
    public void InvalidateCache(String path) {
        picasso.invalidate(path);
    }

    @SimpleEvent(description = "Event invoked if image has been loaded successfully")
    public void Success() {
        EventDispatcher.dispatchEvent(this, "Success");
    }

    @SimpleEvent(description = "Event invoked when any error occurs and provides error message")
    public void ErrorOccurred(String message) {
        EventDispatcher.dispatchEvent(this, "ErrorOccurred", message);
    }

    /*
    taken from https://github.com/wasabeef/picasso-transformations
     */
    public static class CircleImageTransformation implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int width = (source.getWidth() - size) / 2;
            int height = (source.getHeight() - size) / 2;
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader =
                    new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            if (width != 0 || height != 0) {
                // source isn't square, move viewport to center
                Matrix matrix = new Matrix();
                matrix.setTranslate(-width, -height);
                shader.setLocalMatrix(matrix);
            }
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            source.recycle();

            return bitmap;
        }

        @Override
        public String key() {
            return "CircleImageTransformation";
        }
    }
        public static class SquareImageTransformation implements Transformation {

            @Override
            public Bitmap transform(Bitmap source) {
                int size = Math.min(source.getWidth(), source.getHeight());
                int mWidth = (source.getWidth() - size) / 2;
                int mHeight = (source.getHeight() - size) / 2;
                Bitmap bitmap = Bitmap.createBitmap(source, mWidth, mHeight, size, size);
                if (bitmap != source) {
                    source.recycle();
                }
                return bitmap;
            }
            @Override
            public String key() {
                return "SquareImageTransformation";
            }
        }
    public static class GreyscaleImageTransformation implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {

            int width = source.getWidth();
            int height = source.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            ColorMatrix saturation = new ColorMatrix();
            saturation.setSaturation(0f);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(saturation));
            canvas.drawBitmap(source, 0, 0, paint);
            source.recycle();

            return bitmap;
        }

        @Override
        public String key() {
            return "GrayscaleImageTransformation";
        }
    }
}
