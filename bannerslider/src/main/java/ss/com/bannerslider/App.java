package ss.com.bannerslider;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


/**
 * Created by Engr G M on 12/28/2016.
 */

public class App extends Application {

    private static App instance;

    public static synchronized App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        //initImageLoader(getApplicationContext());
    }


    public static void loadImageFromURL(String imageURL, final ImageView iv, int placeholder, Context context) {
        try {
            Glide.with(App.getInstance().getApplicationContext()).setDefaultRequestOptions(new RequestOptions().placeholder(placeholder).error(placeholder)).load(imageURL).into(iv);
        }catch (Exception e){e.printStackTrace();}
    }


    /*public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        //config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


    public static void loadImageFromURL(String imageURL, final ImageView iv, int placeholder,Context context) {
        if(!ImageLoader.getInstance().isInited()){
            initImageLoader(context.getApplicationContext());
        }
        //int placeHolder;
        if(placeholder == 0){
            //placeHolder = R.mipmap.ic_default_user_squire;
        }else {
            //placeHolder = R.mipmap.ic_placeholder_2;
        }
        try {
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true)
                    .showImageOnLoading(placeholder)
                    .showImageForEmptyUri(placeholder)
                    .showImageOnFail(placeholder)
                    .resetViewBeforeLoading(true).build();

            imageLoader.displayImage(imageURL, iv, options);
        } catch (Exception e) {

        }
    }



    public static void loadImageFromDrawable(int resID, final ImageView iv, int placeholder,Context context) {
        if(!ImageLoader.getInstance().isInited()){
            initImageLoader(context.getApplicationContext());
        }
        //int placeHolder;
        if(placeholder == 0){
            //placeHolder = R.mipmap.ic_default_user_squire;
        }else {
            //placeHolder = R.mipmap.ic_placeholder_2;
        }
        try {
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true)
                    .showImageOnLoading(placeholder)
                    .showImageForEmptyUri(placeholder)
                    .showImageOnFail(placeholder)
                    .resetViewBeforeLoading(true).build();

            imageLoader.displayImage("drawable://" + resID, iv, options);
        } catch (Exception e) {

        }
    }*/
}
