How to include the library
==========================

0. Add XtremePush_lib as library project to your project.
 
1. In the main activity initialize the PushConnector fragment. "Main activity" it's an activity on which you want alerts appear:

        private PushConnector pushConnector;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main); // Put here your xml layout. activity_main here just for example

           pushConnector = PushConnector.initPushConnector(getSupportFragmentManager(), SERVER_URL, APP_KEY, SENDER_ID);
        }

    Where:

    *   SERVER_URL - our server url
    *   APP_ID - our server application id
    *   SENDER_ID - ProjectID you receive from Google 


2. Add these lines to the same activity:

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            pushConnector.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            pushConnector.onNewIntent(intent);
        }

3. Add these lines your AndroidManifest.xml inside <manifest</manifest>. Replace PACKAGE_NAME with the package name for your application:
        <!-- GCM connects to Google Services. -->
        <uses-permission android:name="android.permission.INTERNET" />

        <!-- GCM requires a Google account. -->
        <uses-permission android:name="android.permission.GET_ACCOUNTS" />

        <!-- Keeps the processor from sleeping when a message is received. -->
        <uses-permission android:name="android.permission.WAKE_LOCK" />

        <!-- Other -->
        <uses-permission android:name="android.permission.READ_PHONE_STATE" />
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

        <!--
        Creates a custom permission so only this app can receive its messages.

        NOTE: the permission *must* be called PACKAGE.permission.C2D_MESSAGE,
              where PACKAGE is the application's package name.
        -->
        <permission
           android:name="YOUR_PACKAGE.permission.C2D_MESSAGE"
           android:protectionLevel="signature" />

        <uses-permission android:name="YOUR_PACKAGE.permission.C2D_MESSAGE" />

        <!-- This app has permission to register and receive data message. -->
        <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />

4. Add these lines into <application></application>:

        <receiver
           android:name="com.the_roberto.gcmlib.GCMReceiver"
           android:permission="com.google.android.c2dm.permission.SEND" >
           <intent-filter>
               <action android:name="com.google.android.c2dm.intent.RECEIVE" />
               <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
               <category android:name="ie.imobile.extremepush.gcmlib" />
           </intent-filter>
        </receiver>

        <service android:name="ie.imobile.extremepush.GCMIntentService" />

        <receiver android:name="ie.imobile.extremepush.location.ProxymityAlertReceiver" />

        <activity
           android:name="ie.imobile.extremepush.ui.WebViewActivity"
           android:exported="false" />

How to use hitTag functionality
===============================

To use a _hitTag_ functionality just call `pushManager.hitTag(String tag)` method:
    
        pushConnector.hitTag("your_tag_name");
