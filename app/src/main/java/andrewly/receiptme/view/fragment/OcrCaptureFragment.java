package andrewly.receiptme.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import andrewly.receiptme.R;
import andrewly.receiptme.controller.camera.CameraSource;
import andrewly.receiptme.controller.camera.CameraSourcePreview;
import andrewly.receiptme.controller.camera.GraphicOverlay;
import andrewly.receiptme.controller.camera.OcrDetectorProcessor;
import andrewly.receiptme.model.ocr.OcrGraphic;
import andrewly.receiptme.view.ParseImageActivity;
import andrewly.receiptme.model.ocr.TextBlockReader;

import static android.app.Activity.RESULT_OK;

//import static andrewly.receiptme.view.fragment.ScaleListener.decodeFile;
//import static andrewly.receiptme.view.fragment.ScaleListener.decodeStream;

/**
 * Created by Andrew Ly on 5/8/2017.
 */

public class OcrCaptureFragment extends Fragment {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_OCR_CAPTURE = 9003;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int RECEIPT_WRITE_EXTERNAL_STORAGE = 21;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    //Picture callback
    private CameraSource.PictureCallback mPicture;

    private ImageView showImageView;
    protected String imageFilePath;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public OcrCaptureFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OcrCaptureFragment newInstance(int sectionNumber) {
        OcrCaptureFragment fragment = new OcrCaptureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;

        rootView = inflater.inflate(R.layout.fragment_main2_ocr_capture, container, false);

        mPreview = (CameraSourcePreview) rootView.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) rootView.findViewById(R.id.graphicOverlay);
        mPicture = createPictureCallBack();

        showImageView = (ImageView) rootView.findViewById(R.id.imagePreview);

        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int storage = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false);
        }
        else {
            requestCameraPermission();
        }

        if (storage != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECEIPT_WRITE_EXTERNAL_STORAGE);
        }

        FloatingActionButton takePictureFab = (FloatingActionButton) rootView.findViewById(R.id.fab_ocr);
        takePictureFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get an image from the camera
                Intent parseDataIntent = new Intent(getActivity(), ParseImageActivity.class);

                ArrayList<OcrGraphic> graphics = mGraphicOverlay.getAllGraphcs();
                ArrayList<TextBlock> textBlockHolders = new ArrayList<>();

                for (int i = 0; i < graphics.size(); i++) {
                    textBlockHolders.add(graphics.get(i).getTextBlock());
                }

                TextBlockReader.iterateAllTextBlocks(textBlockHolders);

                parseDataIntent.putExtra("photo_capture_bool", true);

                mCameraSource.takePicture(null, mPicture);

                startActivity(parseDataIntent);

            }
        });

        FloatingActionButton uploadFab = (FloatingActionButton) rootView.findViewById(R.id.fab_upload);
        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadPhotoIntent = new Intent(Intent.ACTION_PICK);

                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();

                Uri data = Uri.parse(pictureDirectoryPath);

                uploadPhotoIntent.setDataAndType(data, "image/*");

                startActivityForResult(uploadPhotoIntent, IMAGE_GALLERY_REQUEST);

                Snackbar.make(view, "Upload a photo from gallery", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return rootView;
    }


    private CameraSource.PictureCallback createPictureCallBack() {
        CameraSource.PictureCallback retCallBack = new CameraSource.PictureCallback() {
            public void onPictureTaken(byte[] data) {
                FileOutputStream outStream = null;
                try

                {
                    Log.d("Taking Photo", "Inside the photo callback phase 0");
                    File miDirs = new File(
                            Environment.getExternalStorageDirectory() + "/ReceiptMe");

                    if (!miDirs.exists()) {
                        miDirs.mkdirs();
                        Log.d("Taking Photo", "Created the directory for ReceiptMe");
                    }

                    Log.d("Taking Photo", "Inside the photo callback phase 1");
                    final Calendar c = Calendar.getInstance();
                    String new_Date = c.get(Calendar.DAY_OF_MONTH) + "-"
                            + ((c.get(Calendar.MONTH)) + 1) + "-"
                            + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR)
                            + "-" + c.get(Calendar.MINUTE) + "-"
                            + c.get(Calendar.SECOND);

                    Log.d("Taking Photo", "Inside the photo callback phase 2");
                    imageFilePath = String.format(
                            Environment.getExternalStorageDirectory() + "/ReceiptMe"
                                    + "/%s.jpg", "te1t(" + new_Date + ")");

                    Log.d("Taking Photo", "Inside the photo callback phase 3");
                    Uri selectedImage = Uri.parse(imageFilePath);
                    File file = new File(imageFilePath);
                    String path = file.getAbsolutePath();
                    Bitmap bitmap = null;

                    Log.d("Taking Photo", "Inside the photo callback phase 4" + file);
                    outStream = new FileOutputStream(file);
                    Log.d("Taking Photo", "Issue happens after creating the outStream");
                    outStream.write(data);
                    outStream.close();

                    Log.d("Taking Picture", "The path value is now " + path);
                    if (path != null) {
                        if (path.startsWith("content")) {
                            bitmap = decodeStream(file, selectedImage,
                                    getActivity().getApplicationContext());
                        } else {
                            bitmap = decodeFile(file, 10);
                        }
                    }
                    if (bitmap != null) {
                        showImageView.setImageBitmap(bitmap);
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Picture Captured Successfully:", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Failed to Capture the picture. kindly Try Again:",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (
                        FileNotFoundException e)

                {
                    e.printStackTrace();
                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                } finally

                {
                    //if (mPreview != null) {
                    //    mPreview.release();
                    //}
                    //if (mCameraSource != null) {
                    //    mCameraSource.release();
                    //}
                }
            }
        };

        return retCallBack;
    };

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = getActivity();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || getActivity().onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getActivity().getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getActivity().registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getActivity(), R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getActivity().getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getActivity().getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getActivity().getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length
                + " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                Intent data = new Intent();
                data.putExtra(TextBlockObject, text.getValue());
                getActivity().setResult(CommonStatusCodes.SUCCESS, data);
                getActivity().finish();
            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Picture Taken", "On Activity Result method rn");

        if (resultCode == RESULT_OK && requestCode == IMAGE_GALLERY_REQUEST) {
            Uri imageURI = data.getData();

            //reading image data from SD card
            InputStream inputStream;

            try {
                Intent parseImageActivityIntent = new Intent(getActivity(), ParseImageActivity.class);
                parseImageActivityIntent.setData(imageURI);
                inputStream = getActivity().getContentResolver().openInputStream(imageURI);

                // get a bitmap from the stream
                Bitmap image = BitmapFactory.decodeStream(inputStream);

                getActivity().findViewById(R.id.imgPicture);
                //show image to user
                //imgPicture.setImageBitmap(image);
                startActivity(parseImageActivityIntent);
                getActivity().finish();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Unable to open image", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Camera Output information

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ReceiptMe");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        Log.d(TAG, "Directory already exists");
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){

            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ "RECEIPT" + ".jpg");
            if (mediaFile.exists()) {
                mediaFile.delete();
            }
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

        /**
         * Decode strem.
         *
         * @param fil
         *            the fil
         * @param selectedImage
         *            the selected image
         * @param mContext
         *            the m context
         * @return the bitmap
         */
        public static Bitmap decodeStream(File fil, Uri selectedImage,
                                         Context mContext) {

            Bitmap bitmap = null;
            try {

                bitmap = BitmapFactory.decodeStream(mContext.getContentResolver()
                        .openInputStream(selectedImage));

                //final int THUMBNAIL_SIZE = getThumbSize(bitmap);

                //bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE,
                //        THUMBNAIL_SIZE, false);

                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos
                 //       .toByteArray()));

                return bitmap; //= rotateImage(bitmap, fil.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        /**
         * Decode file.
         *
         * @param f
         *            the f
         * @param sampling
         *            the sampling
         * @return the bitmap
         */
        public static Bitmap decodeFile(File f, int sampling) {
            try {
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(
                        new FileInputStream(f.getAbsolutePath()), null, o2);

                o2.inSampleSize = sampling;
                o2.inTempStorage = new byte[48 * 1024];

                o2.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(
                        new FileInputStream(f.getAbsolutePath()), null, o2);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //bitmap = rotateImage(bitmap, f.getAbsolutePath());
                return bitmap;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            return null;
        }
}
