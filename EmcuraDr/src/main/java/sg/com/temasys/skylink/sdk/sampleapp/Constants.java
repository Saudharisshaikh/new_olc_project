package sg.com.temasys.skylink.sdk.sampleapp;

public final class Constants {


    public static String ROOM_NAME_MULTI = "";



    private Constants() {
    }

    private  static  Constants constants = null;
    public static Constants getInstance(){
        if(constants == null){
            constants = new Constants();
        }

        return constants;
    }

    //sg.com.temasys.skylink.sdk.sampleapp.Constants c = Constants.getInstance();//new sg.com.temasys.skylink.sdk.sampleapp.Constants();
}
