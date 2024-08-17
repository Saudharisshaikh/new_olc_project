//package com.app.onlinecare.quickblox;
//
//import android.app.Application;
//
//import com.quickblox.core.QBSettings;
//import com.quickblox.users.model.QBUser;
//
//public class VideoChatApplication extends Application {
//
//    public static final int FIRST_USER_ID = 2222774;
//    public static final String FIRST_USER_LOGIN = "user1";
//    public static final String FIRST_USER_PASSWORD = "12345678";
//    //
//    public static final int SECOND_USER_ID = 2222776;
//    public static final String SECOND_USER_LOGIN = "user2";
//    public static final String SECOND_USER_PASSWORD = "12345678";
//
//    private QBUser currentUser;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        
//
//        // Set QuickBlox credentials here
//        //
//        QBSettings.getInstance().fastConfigInit("18230", "MnuvMucdEDhJfhx", "kzSqHJVJtMW5KnX");
//    }
//
//    public void setCurrentUser(int userId, String userPassword) {
//        this.currentUser = new QBUser(userId);
//        this.currentUser.setPassword(userPassword);
//    }
//
//    public QBUser getCurrentUser() {
//        return currentUser;
//    }
//}
