package rkr.binatestation.dreammanager.fragments.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rkr.binatestation.dreammanager.BuildConfig;
import rkr.binatestation.dreammanager.R;

import static android.app.Activity.RESULT_OK;

/**
 * Dialog fragment to pick image.
 */
public class ImagePickerFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ImagePickerFragment";

    private static final String KEY_IMAGE_PATH = "image_path";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private String mSelectedImagePath = "";
    private ImagePickerListener mImagePickerListener;

    public ImagePickerFragment() {
        // Required empty public constructor
    }

    public static ImagePickerFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        ImagePickerFragment fragment = new ImagePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImagePickerListener) {
            mImagePickerListener = (ImagePickerListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mImagePickerListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_IMAGE_PATH, mSelectedImagePath);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mSelectedImagePath = savedInstanceState.getString(KEY_IMAGE_PATH);
        }
        View actionCameraView = view.findViewById(R.id.FIP_camera_layout);
        View actionGalleryView = view.findViewById(R.id.FIP_gallery_layout);
        View actionCancelView = view.findViewById(R.id.FIP_cancel);

        actionCameraView.setOnClickListener(this);
        actionGalleryView.setOnClickListener(this);
        actionCancelView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.FIP_camera_layout) {
            dispatchTakePictureIntent();
        } else if (id == R.id.FIP_gallery_layout) {
            dispatchPickFromGalleryIntent();
        } else {
            dismiss();
        }
    }

    private void dispatchPickFromGalleryIntent() {
        Intent pickFromGalleryIntent = new Intent();
        pickFromGalleryIntent.setType("image/*");
        pickFromGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        FragmentActivity activity = getActivity();
        if (activity != null && pickFromGalleryIntent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(pickFromGalleryIntent, REQUEST_IMAGE_PICK);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        FragmentActivity activity = getActivity();
        if (activity != null && takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "dispatchTakePictureIntent: ", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mImagePickerListener != null) {
                mImagePickerListener.onImagePicked(mSelectedImagePath);
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            savePickedFile(data.getData());
        }
        dismiss();
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void savePickedFile(Uri data) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(data);
            File file = createImageFile();
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;
                if (inputStream != null) {
                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                }
                output.flush();
            } catch (Exception e) {
                e.printStackTrace(); // handle exception, define IOException and others
            } finally {
                output.close();
            }
            mSelectedImagePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mImagePickerListener != null) {
            mImagePickerListener.onImagePicked(mSelectedImagePath);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mSelectedImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public interface ImagePickerListener {
        void onImagePicked(String imageFilePath);
    }
}
