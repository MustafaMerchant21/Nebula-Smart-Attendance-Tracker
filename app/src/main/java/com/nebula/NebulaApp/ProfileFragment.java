package com.nebula.NebulaApp;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nebula.NebulaApp.databinding.FragmentLeaveBinding;
import com.nebula.NebulaApp.databinding.FragmentProfileBinding;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    public static final String SHARED_PREFS = "shared_prefs";
    LinearLayout logout,changePwd;
    TextView Name,p_email,p_phone,p_date_of_birth,p_course,p_college;
    ImageView edit_profile_picture_button;
    ShapeableImageView profile_picture;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FragmentProfileBinding binding;
    SharedPreferences sharedPref;

    private Uri cameraFileUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;
    private static final int REQUEST_STORAGE_PERMISSION = 4;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        Name = v.findViewById(R.id.textView2);
        p_email = v.findViewById(R.id.textView9);
        p_phone = v.findViewById(R.id.textView10);
        p_date_of_birth = v.findViewById(R.id.textView11);
        p_course = v.findViewById(R.id.textView12);
        p_college = v.findViewById(R.id.textView14);
        logout = v.findViewById(R.id.logout);
        changePwd = v.findViewById(R.id.changePwd);
        profile_picture = v.findViewById(R.id.profile_picture);
        edit_profile_picture_button = v.findViewById(R.id.edit_profile_picture_button);
        sharedPref = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String login_email,fname,lname,DOB,selectedcourse,college,mobile,selectedsemester,selectedyear,course;
        login_email =  sharedPref.getString("Email", "");
        fname = sharedPref.getString("Firstname","");
        lname = sharedPref.getString("Lastname","");
        DOB = sharedPref.getString("Date_of_birth","");
        selectedcourse = sharedPref.getString("SelectedCourse","");
        selectedsemester = sharedPref.getString("SelectedSemester","");
        selectedyear = sharedPref.getString("Selected_year_of_study","");
        mobile = sharedPref.getString("Mobile","");
        String input = fname + " " + lname;
        String capitalized = capitalizeFirstLetterOfWords(input);
        Name.setText(capitalized);
        p_email.setText(login_email);
        p_phone.setText(mobile);
        p_date_of_birth.setText(DOB);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(selectedyear);
        stringBuilder.append(selectedcourse);
        stringBuilder.append("-");
        stringBuilder.append(selectedsemester);
        String result = stringBuilder.toString();
        Log.w("Info",selectedsemester);
        Log.w("Info",selectedyear);
        p_course.setText(result);

        // Fetch profile image URL from Firebase Realtime Database
        String profileImageUrl = sharedPref.getString("profile_image_url", "");

        // Load profile image into the ShapeableImageView using Glide
        if (!profileImageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(profileImageUrl)
                    .placeholder(R.drawable.avatar_default) // Placeholder image while loading
                    .error(R.drawable.avatar_default) // Error image if loading fails
                    .into(profile_picture);
        }

        reference.child("Institute").child(sharedPref.getString("Institute_id","")).child("College Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve college name from dataSnapshot
                String collegeName = dataSnapshot.getValue(String.class);
                // Set college name to TextView
                p_college.setText(collegeName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("Firebase", "Error retrieving college name: " + databaseError.getMessage());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthUI.getInstance()
                                .signOut(requireActivity())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(requireActivity().getApplicationContext(), Nebula_login.class));
                                        requireActivity().finish();
                                    }
                                });
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Scale down when button is pressed
                        scaleButton(logout, 0.9f);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Scale back to normal when button is released
                        scaleButton(logout, 1.0f);
                        break;
                }
                // Return false to allow the event to continue to the next listener
                return false;
            }
            
        });
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_changepwd, null);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(binding.getRoot(),"✓ Request Submitted. You'll be notified shortly",Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        changePwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Scale down when button is pressed
                        scaleButton(changePwd, 0.9f);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Scale back to normal when button is released
                        scaleButton(changePwd, 1.0f);
                        break;
                }
                // Return false to allow the event to continue to the next listener
                return false;
            }
        });
        edit_profile_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for camera and storage permissions
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request camera permission if not granted
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request storage permission if not granted
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else {
                    // Permissions are granted, open image picker or camera intent
                    openImagePicker();
                }
            }
        });
        edit_profile_picture_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Scale down when button is pressed
                        scaleProfileButton(edit_profile_picture_button, 1.3f);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        // Scale back to normal when button is released
                        scaleProfileButton(edit_profile_picture_button, 1.0f);
                        break;
                }
                // Return false to allow the event to continue to the next listener
                return false;
            }
        });


        return v;
    }
    public String capitalizeFirstLetterOfWords(String input) {
        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+"); // Split the input string into words

        for (String word : words) {
            if (!word.isEmpty()) { // Check if the word is not empty
                // Capitalize the first letter of the word and append the rest of the word
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim(); // Trim any leading/trailing spaces and return the result
    }
    private void scaleButton(LinearLayout linearLayout, float scale) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(linearLayout, "scaleX", scale);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(linearLayout, "scaleY", scale);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        scaleDownX.start();
        scaleDownY.start();
    }
    private void scaleProfileButton(ImageView imageView, float scale) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageView, "scaleX", scale);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageView, "scaleY", scale);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        scaleDownX.start();
        scaleDownY.start();
    }
    private void openImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file: " + ex.getMessage());
            }
            if (photoFile != null) {
                Uri cameraFileUri = FileProvider.getUriForFile(requireContext(),
                        "com.nebula.Nebula", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            }
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();

            // Upload the image to Firebase Storage
            uploadImageToFirebase(imageUri);
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            // Upload the captured image to Firebase Storage
            uploadImageToFirebase(cameraFileUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("students").child(sharedPref.getString("Email",""));

        // Upload the image to Firebase Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully, get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Update the user's profile image URL in the database
                                updateProfileImageInDatabase(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle image upload failure
                        Log.e(TAG, "Failed to upload image to Firebase Storage: " + e.getMessage());
                    }
                });
    }

    // Method to update user's profile image URL in the database
    private void updateProfileImageInDatabase(String imageUrl) {
        // Update the user's profile image URL in the database
        // For example, if you're using Firebase Realtime Database:
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("profile_image_url",imageUrl);
        editor.apply();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("profile_images").child("students").child(sharedPref.getString("sanitized_email",""));
        userRef.child("profile_image_url").setValue(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Profile image URL updated successfully
                        // Update the profile image view with the new image
                        // For example:
                        Glide.with(requireContext()).load(imageUrl).into(profile_picture);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle database update failure
                        Log.e(TAG, "Failed to update profile image URL in the database: " + e.getMessage());
                    }
                });
    }

}