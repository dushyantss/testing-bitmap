package aditya.facerecog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {

  String mCurrentPhotoPath;

  static final int REQUEST_TAKE_PHOTO = 1;

  public static ImageFragment newInstance() {
    Bundle args = new Bundle();
    ImageFragment fragment = new ImageFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public ImageFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_image, container, false);

    checkPermissions();

    view.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dispatchTakePictureIntent();
      }
    });
    return view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    byte[] imageData = decodeFile();
    if (isAdded()){
      ((NetworkUplinkContract) getActivity()).sendImage(imageData);
    }
  }


  private byte[] decodeFile() {
    int targetL = 720;
    int targetS = 480;

    // Get the dimensions of the bitmap
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    bmOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    int photoW = bmOptions.outWidth;
    int photoH = bmOptions.outHeight;

    boolean isPortrait = photoH >= photoW;

    // Determine how much to scale down the image
    long scaleFactor = isPortrait ? photoH / targetL : photoW / targetL;

    // Decode the image file into a Bitmap sized to fill the View
    bmOptions.inJustDecodeBounds = false;
    bmOptions.inSampleSize = (int) scaleFactor;
    bmOptions.inPurgeable = true;

    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, isPortrait ? targetS : targetL,
        isPortrait ? targetL : targetS, false);
    return getBytesFromBitmap(newBitmap);
  }

  public byte[] getBytesFromBitmap(Bitmap bitmap) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    return stream.toByteArray();
  }


  private void checkPermissions() {
    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

        ActivityCompat.requestPermissions(getActivity(),
            new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
      } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(getActivity(),
            new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    }
  }

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      // Create the File where the photo should go
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // Error occurred while creating the File
        ex.printStackTrace();
      }
      // Continue only if the File was successfully created
      if (photoFile != null) {
        Uri photoURI = Uri.fromFile(photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
      }
    }
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String imageFileName = "FaceRecog";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */);

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }
}
