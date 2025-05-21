package com.coffeecorner.app.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.coffeecorner.app.utils.SupabaseClientManager;
import com.coffeecorner.app.utils.Validator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.storage.Storage;
import io.github.jan.supabase.storage.StorageFile;
import io.github.jan.supabase.storage.UploadData;

public class EditProfileFragment extends Fragment {

    private static final int REQUEST_IMAGE_GALLERY = 100;
    private static final int REQUEST_IMAGE_CAMERA = 101;

    private CircleImageView imgProfile;
    private TextInputEditText etFullName, etEmail, etPhone, etDateOfBirth;
    private RadioGroup radioGroupGender;
    private RadioButton radioMale, radioFemale, radioOther;
    private Button btnUpdate;
    private ProgressBar progressBar;
    private ImageView btnBack;
    private TextView btnSave, btnChangePassword;

    private User currentUser;
    private PreferencesHelper preferencesHelper;
    private Uri selectedImageUri;
    private boolean isImageChanged = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferencesHelper = new PreferencesHelper(requireContext());

        // Initialize UI components
        imgProfile = view.findViewById(R.id.imgProfile);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        radioMale = view.findViewById(R.id.radioMale);
        radioFemale = view.findViewById(R.id.radioFemale);
        radioOther = view.findViewById(R.id.radioOther);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        progressBar = view.findViewById(R.id.progressBar);
        btnBack = view.findViewById(R.id.btnBack);
        btnSave = view.findViewById(R.id.btnSave);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        // Set listeners
        view.findViewById(R.id.btnChangePhoto).setOnClickListener(v -> showImagePickerDialog());
        btnBack.setOnClickListener(v -> navigateBack());
        btnSave.setOnClickListener(v -> updateProfile());
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Load user data
        loadUserData();
    }

    private void loadUserData() {
        // Show loading state
        progressBar.setVisibility(View.VISIBLE);

        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Unable to load user data", Toast.LENGTH_SHORT).show();
            navigateBack();
            return;
        }
        // Get Supabase client and fetch user data
        SupabaseClientManager.getInstance().getClient()
                .getSupabase()
                .getPlugin(Postgrest.class)
                .from("users")
                .select()
                .eq("id", userId)
                .single()
                .executeWithResponseHandlers(
                        response -> {
                            currentUser = response.getData(User.class);
                            requireActivity().runOnUiThread(() -> {
                                populateUserData();
                                progressBar.setVisibility(View.GONE);
                            });
                        },
                        throwable -> {
                            requireActivity().runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                                navigateBack();
                            });
                        });
    }

    private void populateUserData() {
        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
            etEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");

            if (currentUser.getDateOfBirth() != null && !currentUser.getDateOfBirth().isEmpty()) {
                etDateOfBirth.setText(currentUser.getDateOfBirth());
            }

            // Set gender selection
            if (currentUser.getGender() != null) {
                switch (currentUser.getGender()) {
                    case "male":
                        radioMale.setChecked(true);
                        break;
                    case "female":
                        radioFemale.setChecked(true);
                        break;
                    default:
                        radioOther.setChecked(true);
                        break;
                }
            }

            // Load profile image
            if (currentUser.getPhotoUrl() != null && !currentUser.getPhotoUrl().isEmpty()) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(imgProfile);
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // If date is already set, use it
        if (!TextUtils.isEmpty(etDateOfBirth.getText())) {
            try {
                Date date = dateFormat.parse(etDateOfBirth.getText().toString());
                calendar.setTime(date);
            } catch (ParseException e) {
                // Use current date if parsing fails
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    etDateOfBirth.setText(dateFormat.format(selectedDate.getTime()));
                }, year, month, day);

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showImagePickerDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_image_picker, null);

        bottomSheetView.findViewById(R.id.layoutCamera).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openCamera();
        });

        bottomSheetView.findViewById(R.id.layoutGallery).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openGallery();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                selectedImageUri = data.getData();
                Glide.with(this).load(selectedImageUri).into(imgProfile);
                isImageChanged = true;
            } else if (requestCode == REQUEST_IMAGE_CAMERA && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                // Convert bitmap to URI
                selectedImageUri = getImageUri(imageBitmap);
                Glide.with(this).load(selectedImageUri).into(imgProfile);
                isImageChanged = true;
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap,
                UUID.randomUUID().toString(), null);
        return Uri.parse(path);
    }

    private void updateProfile() {
        // Validate input
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            etEmail.setError("Invalid email format");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            return;
        }

        // Get gender
        String gender = "other";
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.radioMale) {
            gender = "male";
        } else if (selectedGenderId == R.id.radioFemale) {
            gender = "female";
        }

        // Show loading state
        progressBar.setVisibility(View.VISIBLE);

        // If image is changed, upload it first
        if (isImageChanged && selectedImageUri != null) {
            uploadProfileImage(fullName, email, phone, dateOfBirth, gender);
        } else {
            // Otherwise just update user data
            updateUserData(fullName, email, phone, dateOfBirth, gender, currentUser.getPhotoUrl());
        }
    }

    private void uploadProfileImage(String fullName, String email, String phone, String dateOfBirth, String gender) {
        String fileName = "profile_" + preferencesHelper.getUserId() + "_" + System.currentTimeMillis() + ".jpg";

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            byte[] imageData = outputStream.toByteArray();

            // Upload to Supabase storage
            SupabaseClientManager.getInstance().getClient()
                    .getSupabase()
                    .getPlugin(Storage.class)
                    .from("profile-images")
                    .upload(new StorageFile(fileName), UploadData.from(imageData))
                    .executeWithResponseHandlers(
                            response -> {
                                // Get public URL
                                String imageUrl = SupabaseClientManager.getInstance().getClient()
                                        .getSupabase()
                                        .getPlugin(Storage.class)
                                        .from("profile-images")
                                        .getPublicUrl(fileName);
                                // Update user data with new image URL
                                updateUserData(fullName, email, phone, dateOfBirth, gender, imageUrl);
                            },
                            throwable -> {
                                requireActivity().runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                                            .show();
                                });
                            });
        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData(String fullName, String email, String phone, String dateOfBirth, String gender,
            String profileImageUrl) {
        // Update user object
        User updatedUser = new User(
                currentUser.getId(),
                fullName,
                email,
                phone,
                gender,
                profileImageUrl,
                dateOfBirth);

        // Save to Supabase
        SupabaseClientManager.getInstance().getClient()
                .getSupabase()
                .getPlugin(Postgrest.class)
                .from("users")
                .update(updatedUser)
                .eq("id", currentUser.getId())
                .executeWithResponseHandlers(
                        response -> {
                            requireActivity().runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT)
                                        .show();
                                navigateBack();
                            });
                        },
                        throwable -> {
                            requireActivity().runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                            });
                        });
    }

    private void showChangePasswordDialog() {
        // Implement password change functionality
        // This would typically involve showing a dialog to enter old and new passwords
        // Then using Supabase auth to update the password
        Toast.makeText(requireContext(), "Change password functionality to be implemented", Toast.LENGTH_SHORT).show();
    }

    private void navigateBack() {
        Navigation.findNavController(requireView()).popBackStack();
    }
}
