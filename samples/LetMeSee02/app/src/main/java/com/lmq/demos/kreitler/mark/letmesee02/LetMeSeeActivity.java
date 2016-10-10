package com.lmq.demos.kreitler.mark.letmesee02;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.craftar.CraftARActivity;
import com.craftar.CraftARError;
import com.craftar.CraftAROnDeviceCollection;
import com.craftar.CraftAROnDeviceCollectionManager;
import com.craftar.CraftAROnDeviceIR;
import com.craftar.CraftARResult;
import com.craftar.CraftARSDK;
import com.craftar.CraftARSearchResponseHandler;
import com.craftar.ImageRecognition;

import java.util.ArrayList;

public class LetMeSeeActivity extends CraftARActivity implements CraftARSearchResponseHandler,
        ImageRecognition.SetOnDeviceCollectionListener,
        CraftAROnDeviceCollectionManager.AddCollectionListener,
        CraftAROnDeviceCollectionManager.SyncCollectionListener {

    public final static String COLLECTION_TOKEN = "1f5cb6a2f9b24680";
    public static final int S1 = R.raw.see_it;

    private final static long FINDER_SESSION_TIME_MILLIS = 10000;
    private final static String TAG = "LMS2";

    private MediaPlayer mSeeItPlayer = null;

    protected CraftARSDK mCraftARSDK = null;
    protected CraftAROnDeviceIR mOnDeviceIR;
    protected long startFinderTimeMillis = 0;
    protected CraftAROnDeviceCollectionManager mCollectionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mainLayout= getLayoutInflater().inflate(R.layout.activity_let_me_see, null);
        setContentView(mainLayout);

        mSeeItPlayer = MediaPlayer.create(this, R.raw.see_it);
    }

    public void onDestroy() {
        mSeeItPlayer.stop();
        mSeeItPlayer.release();

        super.onDestroy();
    }

    // IMPLEMENTATION //////////////////////////////////////////////////////////////////////////////
    private void startFinding(){
        Log.d(TAG, ">>> Finding...");
        mCraftARSDK.startFinder(); //Start finder in the CraftARSDK.
        startFinderTimeMillis = System.currentTimeMillis();
    }

    private void stopFinding(){
        mCraftARSDK.stopFinder(); //Stop the finder in the CraftARSDK.
    }

    // INTERFACES //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void collectionReady() {
        Log.d(TAG, ">>> Collection ready!");
        mCraftARSDK.startCapture(this); //Pass a reference to the Activity
    }

    @Override
    public void setCollectionFailed(CraftARError error) {
        Toast.makeText(getApplicationContext(), "setCollection failed! ("+error.getErrorCode()+"):"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
        //Error loading the collection into memory. No recognition can be performed unless a collection has been set.
        Log.e(TAG, "SetCollectionFailed (" + error.getErrorCode() + "):" + error.getErrorMessage());
        Toast.makeText(getApplicationContext(), "Error loading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setCollectionProgress(double progress) {
        //The images from the collection are loading into memory. You will have to load the collections into memory every time you open the app.
        Log.d(TAG, "SetCollectionProgress:" + progress);
    }

    @Override
    public void collectionAdded(CraftAROnDeviceCollection collection) {
        //Collection bundle has been added. Set this collection as current collection.
        Toast.makeText(getApplicationContext(), "Collection "+collection.getName()+ " added!",Toast.LENGTH_SHORT).show();
        loadCollection(collection);
    }

    @Override
    public void addCollectionFailed(CraftARError error) {
        //Error adding the bundle to the device internal storage.
        Log.e(TAG, "AddCollectionFailed(" + error.getErrorCode() + "):" + error.getErrorMessage());
        Toast.makeText(getApplicationContext(), "Error adding collection", Toast.LENGTH_SHORT).show();

        switch(error.getErrorCode()){
            case COLLECTION_BUNDLE_SDK_VERSION_IS_OLD:
                //You are trying to add a bundle which version is newer than the SDK version.
                //You should either update the SDK, or download and add a bundle compatible with this SDK version.
                break;
            case COLLECTION_BUNDLE_VERSION_IS_OLD:
                //You are trying to add a bundle which is outdated, since the SDK version is newer than the bundleSDK
                //You should download a bundle compatible with the newer SDK version.
                break;
            default:
                break;
        }
    }

    @Override
    public void addCollectionProgress(float progress) {
        //Progress adding the collection to internal storage (de-compressing bundle and storing into the device storage).
        //Note that this might only happen once per app installation, or when the bundle is updated.
        Log.d(TAG, "AddCollectionProgress:" + progress);
    }

    private void loadCollection(CraftAROnDeviceCollection collection){
        mOnDeviceIR.setCollection(collection, (ImageRecognition.SetCollectionListener) this);
    }

    @Override
    public void syncSuccessful(CraftAROnDeviceCollection collection) {
        String text = "Sync succesful for collection "+collection.getName();
        Toast.makeText(getApplicationContext(),text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);

        loadCollection(collection);
    }

    @Override
    public void syncFinishedWithErrors(CraftAROnDeviceCollection collection, int itemDownloads, int itemErrors) {
        String text = "Sync Finished but  " + itemErrors + " of the " + itemDownloads + " items could not be synchronized";
        Toast.makeText(getApplicationContext(), text , Toast.LENGTH_SHORT).show();
        Log.e(TAG, text);
    }

    @Override
    public void syncProgress(CraftAROnDeviceCollection collection, float progress) {
        Log.e(TAG, "Sync progress for collection "+collection.getName() + ":"+progress);
    }


    @Override
    public void syncFailed(CraftAROnDeviceCollection collection, CraftARError error) {
        String text = "Sync failed for collection "+collection.getName();
        Toast.makeText(getApplicationContext(), text , Toast.LENGTH_SHORT).show();
        Log.e(TAG, text + ":"+error.getErrorMessage());
        loadCollection(collection);
    }

    // CraftARSearchResponseHandler Interface ------------------------------------------------------
    @Override
    public void searchFailed(CraftARError error, int requestCode) {
        Log.e(TAG, "Search failed("+error.getErrorCode()+"):"+error.getErrorMessage());
    }

    @Override
    public void searchResults(ArrayList<CraftARResult> results,
                              long searchTimeMillis,
                              int requestCode) {

        // Callback with the search results
        if(results.size() > 0){
            //We found something! Show the results
            stopFinding();

            Log.d(TAG, ">>> NOW I SEE IT! <<<");
            mSeeItPlayer.start();
        }else{
            //We have a timeout for the finder session (so the phone is not finding indefinitely, but stops after some time).
            long elapsedTime = System.currentTimeMillis() - startFinderTimeMillis;
            if (elapsedTime > FINDER_SESSION_TIME_MILLIS ){
                stopFinding();
                // No object were found during this session
                Log.d(TAG, "<<< NOW I DON'T >>>");
                startFinding();
            }
        }
    }

    // CraftAR Interface ---------------------------------------------------------------------------
    @Override
    public void onPostCreate() {
        mCraftARSDK = CraftARSDK.Instance();

        /**
         * Initialise the SDK with your Application Context before doing any other
         * operation with any other module. You can do this from any Activity
         * (for example the Splash Activity); */
        mCraftARSDK.init(getApplicationContext());

        mOnDeviceIR = CraftAROnDeviceIR.Instance();
        mCollectionManager = CraftAROnDeviceCollectionManager.Instance();

        if (mCollectionManager != null) {
            //Obtain the collection with your token.
            //This will lookup for the collection in the internal storage, and return the collection if it's available.
            CraftAROnDeviceCollection col =  mCollectionManager.get(COLLECTION_TOKEN);
            if (col == null){
                //Collection is not available. Add it from the CraftAR service using the collection token.
                mCollectionManager.addCollectionWithToken(COLLECTION_TOKEN, this);

                // Alternatively it can be added from assets using the collection bundle.
                //mCollectionManager.addCollection((AddCollectionListener)this,"craftarexamples_odir.zip");
            }
            else {
                //Collection is already available in the device.
                col.sync(this);
            }
        }
        else {
            Log.d(TAG, "!!! Failed to retrieve CollectionManager!");
        }
    }

    @Override
    public void onPreviewStarted(int frameWidth, int frameHeight) {
        // startCapture() was completed successfully, and the Camera Preview has been started

        //Tell the SDK that the OnDeviceIR who manage the calls to singleShotSearch() and startFinding().
        //In this case, as we are using on-device-image-recognition, we will tell the SDK that the OnDeviceIR singleton will manage this calls.
        mCraftARSDK.setSearchController(mOnDeviceIR.getSearchController());

        //Tell the SDK that we want to receive the search responses in this class.
        mOnDeviceIR.setCraftARSearchResponseHandler(this);

        startFinding();
    }

    @Override
    public void onCameraOpenFailed(){
        // startCapture() failed, so the Camera Preview could not be started
        Log.d(TAG, "!!! Camera open failed!");
    }
}
