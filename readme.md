#About

This document describes how to integrate [XtremePush](http://xtremepush.com) library with all its functionality into your Android projects.

##Preriqusites

1. Create a project on XtremePush Dashboard.<br />
2. Download the [latest version](https://github.com/xtremepush/XtremePush_Android/archive/master.zip) or clone the library as a submodule from [Github](https://github.com/xtremepush/XtremePush_Android).
3. Register a project on [Google Developers Console](https://cloud.google.com/console/project). Please, copy your Project Number from the top of the page.<br />
	3.1. From APIs & auth page under you project settings on Google Developer Console enable Google Cloud Messaging for Android.<br />
	3.2. Navigate to Credentials page within APIs and auth on Developer Console. Click Create New Key -> Select Server Key. Copy API Key you received on this page. Navigate to application setting on XtremePush Dashboard -> Go to Application Keys and enable Android. Paste the API Key you received from Google Developer Console into the Android application key field.<br />

##How to include the library
==========================

1. Add xtremepush-lib.jar to your project build path.
	Marge res/ folder with your project' files (string.xml, drawable, raw etc)
	Marge libs/ folder with your project and add there to build path
 
2. In the main activity initialize the PushConnector fragment. "Main activity" it's an activity on which you want alerts appear:

        private PushConnector pushConnector;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_main); // Put here your xml layout. activity_main here just for example

           pushConnector = PushConnector.initPushConnector(getSupportFragmentManager(), XPUSH_APP_KEY, GOOGLE_PROJECT_NUMBER);
        }

    Where:

    *   XPUSH_APP_KEY - is App Key from your application settings page on XtremePush Dashboard.
    *   GOOGLE_PROJECT_NUMBER - ProjectID you receive from Google Developer Console.


3. Add these lines to the same activity:

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

4. Add these lines your AndroidManifest.xml inside <manifest</manifest>. Replace PACKAGE_NAME with the package name for your application:
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

5. Add these lines into <application></application>:

        <receiver
           android:name="ie.imobile.extremepush.GCMReceiver"
           android:permission="com.google.android.c2dm.permission.SEND" >
           <intent-filter>
               <action android:name="com.google.android.c2dm.intent.RECEIVE" />
               <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
               <category android:name="YOUR_PACKAGE" />
           </intent-filter>
        </receiver>

        <service android:name="ie.imobile.extremepush.GCMIntentService" />

        <receiver android:name="ie.imobile.extremepush.location.ProxymityAlertReceiver" />

        <activity
           android:name="ie.imobile.extremepush.ui.WebViewActivity"
           android:exported="false" />

##How to use Tags functionality
===============================

To use a _hitTag_ functionality just call `pushManager.hitTag(String tag)` method:
    
        pushConnector.hitTag("your_tag_name");
		
		
##How to start Inbox activity		
===============================

if you want to start Inbox activity, you should use the following code snippet(activity must be registred in manifest file)
 
 		startActivity(this, XPushLogActivity.class);

