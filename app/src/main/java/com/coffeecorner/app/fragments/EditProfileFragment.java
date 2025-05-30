package com.coffeecorner.app.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.coffeecorner.app.R;
import com.coffeecorner.app.models.User;
import com.coffeecorner.app.repositories.UserRepository;
import com.coffeecorner.app.utils.PreferencesHelper;
import com.coffeecorner.app.utils.UserProfileManager;
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
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());

    // Views
    private CircleImageView imgProfile;
    private TextInputEditText etFullName, etEmail, etPhone, etDateOfBirth;
    private RadioGroup radioGroupGender;
    private ProgressBar progressBar; // State
    private User currentUser;
    private PreferencesHelper preferencesHelper;
    private UserRepository userRepository;
    private Uri selectedImageUri;
    private boolean isImageChanged = false; // Activity result launchers - initialized in onViewCreated to avoid
                                            // this-escape warnings
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private UserProfileManager userProfileManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize dependencies
        preferencesHelper = new PreferencesHelper(requireContext());
        userRepository = UserRepository.getInstance(requireContext());

        if (userProfileManager == null) {
            userProfileManager = new UserProfileManager(requireContext());
        }

        // Initialize activity result launchers after fragment is fully created
        initializeActivityResultLaunchers();

        initializeViews(view);
        setupListeners();
        loadUserData();
    }

    private void initializeViews(View view) {
        imgProfile = view.findViewById(R.id.imgProfile);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        progressBar = view.findViewById(R.id.progressBar);

        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        TextView btnSave = view.findViewById(R.id.btnSave);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        TextView btnChangePassword = view.findViewById(R.id.btnChangePassword);

        btnBack.setOnClickListener(v -> navigateBack());
        btnSave.setOnClickListener(v -> updateProfile());
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void setupListeners() {
        requireView().findViewById(R.id.btnChangePhoto).setOnClickListener(v -> showImagePickerDialog());
        etDateOfBirth.setOnClickListener(v -> showDatePicker());
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);

        // Load user data from preferences using UserRepository
        currentUser = userRepository.loadUserFromPreferences();

        if (currentUser != null && currentUser.getId() != null && !currentUser.getId().isEmpty()) {
            populateUserData();
        } else {
            // Create default user if no data exists
            currentUser = createDefaultUser();
            populateUserData();
        }

        progressBar.setVisibility(View.GONE);
    }

    private User createDefaultUser() {
        String userId = preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = "temp_user_" + System.currentTimeMillis();
        }

        User user = new User();
        user.setId(userId);
        user.setFullName(preferencesHelper.getUserName() != null ? preferencesHelper.getUserName() : "");
        user.setEmail(preferencesHelper.getUserEmail() != null ? preferencesHelper.getUserEmail() : "");
        user.setPhone(preferencesHelper.getUserPhone() != null ? preferencesHelper.getUserPhone() : "");
        user.setGender("other");
        user.setPhotoUrl("");
        user.setDateOfBirth("");

        return user;
    }

    private void populateUserData() {
        if (currentUser == null)
            return;

        etFullName.setText(currentUser.getFullName());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhone());
        etDateOfBirth.setText(currentUser.getDateOfBirth());

        // Null check for gender before using it in a switch statement
        String gender = currentUser.getGender();
        if (gender != null) {
            switch (gender) {
                case "male":
                    radioGroupGender.check(R.id.radioMale);
                    break;
                case "female":
                    radioGroupGender.check(R.id.radioFemale);
                    break;
                default:
                    radioGroupGender.check(R.id.radioOther);
            }
        } // Optionally, handle the case where gender is null, e.g., by setting a default

        if (!TextUtils.isEmpty(currentUser.getPhotoUrl())) {
            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(imgProfile);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        try {
            Date currentDate = dateFormat.parse(Objects.requireNonNull(etDateOfBirth.getText()).toString());
            if (currentDate != null)
                calendar.setTime(currentDate);
        } catch (ParseException ignored) {
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, day);
                    etDateOfBirth.setText(dateFormat.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showImagePickerDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_image_picker, (ViewGroup) requireView(), false);

        view.findViewById(R.id.layoutCamera).setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });

        view.findViewById(R.id.layoutGallery).setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            showError("Camera not available");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void handleGalleryResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                loadImage(selectedImageUri);
            }
        }
    }

    private void handleCameraResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bitmap = extras.getParcelable("data", Bitmap.class);
                } else {
                    @SuppressWarnings("deprecation")
                    Bitmap legacyBitmap = (Bitmap) extras.getParcelable("data");
                    bitmap = legacyBitmap;
                }

                if (bitmap != null) {
                    selectedImageUri = getImageUri(bitmap);
                    loadImage(selectedImageUri);
                }
            }
        }
    }

    private void loadImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(imgProfile);
        isImageChanged = true;
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        // Create a name for the image file
        String fileName = "profile_" + UUID.randomUUID().toString() + ".jpg";

        // Modern approach using ContentResolver and ContentValues
        Uri imageCollection;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        android.content.ContentValues contentValues = new android.content.ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri imageUri = requireActivity().getContentResolver().insert(imageCollection, contentValues);

        try {
            if (imageUri != null) {
                try (java.io.OutputStream outputStream = requireActivity().getContentResolver()
                        .openOutputStream(imageUri)) {
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                    }
                }
                return imageUri;
            }
        } catch (Exception e) {
            if (imageUri != null) {
                requireActivity().getContentResolver().delete(imageUri, null, null);
            }
        }

        // Fallback to the old method if the above fails
        @SuppressWarnings("deprecation")
        String path = MediaStore.Images.Media.insertImage(
                requireActivity().getContentResolver(),
                bitmap,
                fileName,
                null);

        return Uri.parse(Objects.requireNonNull(path));
    }

    private void updateProfile() {
        if (!validateInput())
            return;

        String fullName = Objects.requireNonNull(etFullName.getText()).toString().trim();
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        String phone = Objects.requireNonNull(etPhone.getText()).toString().trim();
        String dateOfBirth = Objects.requireNonNull(etDateOfBirth.getText()).toString().trim();
        String gender = getSelectedGender();

        progressBar.setVisibility(View.VISIBLE);

        if (isImageChanged && selectedImageUri != null) {
            uploadImageAndUpdateProfile(fullName, email, phone, dateOfBirth, gender);
        } else {
            String existingPhotoUrl = currentUser != null ? currentUser.getPhotoUrl() : "";
            updateUserData(fullName, email, phone, dateOfBirth, gender, existingPhotoUrl);
        }
    }

    private boolean validateInput() {
        boolean valid = true;
        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();

        if (TextUtils.isEmpty(etFullName.getText())) {
            etFullName.setError("Name is required");
            valid = false;
        }

        if (!Validator.isValidEmail(email)) {
            etEmail.setError("Invalid email format");
            valid = false;
        }

        return valid;
    }

    private String getSelectedGender() {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioMale)
            return "male";
        if (selectedId == R.id.radioFemale)
            return "female";
        return "other";
    }

    private void uploadImageAndUpdateProfile(String fullName, String email, String phone,
            String dateOfBirth, String gender) {
        try {
            Bitmap bitmap;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                // For API 28 and above
                ImageDecoder.Source source = ImageDecoder.createSource(
                        requireActivity().getContentResolver(),
                        selectedImageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                // For older versions
                @SuppressWarnings("deprecation")
                Bitmap tempBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        selectedImageUri);
                bitmap = tempBitmap;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] imageData = stream.toByteArray();

            String fileName = "profile_" + UUID.randomUUID() + ".jpg";

            // TODO: Implement actual image upload to Supabase storage
            // For now, use a placeholder URL
            String imageUrl = "temp_image_url_" + System.currentTimeMillis();

            // Update user data with temporary image URL
            updateUserData(fullName, email, phone, dateOfBirth, gender, imageUrl);

        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            showError("Error processing image: " + e.getMessage());
        }
    }

    private void updateUserData(String fullName, String email, String phone,
            String dateOfBirth, String gender, String imageUrl) {
        // Update user object
        String userId = currentUser != null ? currentUser.getId() : preferencesHelper.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = "temp_user_" + System.currentTimeMillis();
        }

        User updatedUser = new User(
                userId,
                fullName,
                email,
                phone,
                gender,
                imageUrl,
                dateOfBirth);

        // Use UserRepository to update profile with callback
        userRepository.updateUserProfileWithSupabase(updatedUser, new UserRepository.ProfileUpdateCallback() {
            @Override
            public void onSuccess(User user) {
                requireActivity().runOnUiThread(() -> {
                    currentUser = user;
                    progressBar.setVisibility(View.GONE);
                    showSuccess("Profile updated successfully");
                    // Sync user data to ensure all screens get the latest info
                    userProfileManager.syncUserData();
                    // Optionally, notify ProfileFragment to refresh (if using shared ViewModel or event bus)
                    navigateBack();
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    showError("Update failed: " + errorMessage);
                });
            }
        });
    }

    private void showChangePasswordDialog() {
        // Implement password change logic
        Toast.makeText(requireContext(), "Password change feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void navigateBack() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize activity result launchers after fragment is fully created
     * to avoid "this-escape" warnings
     */
    private void initializeActivityResultLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleGalleryResult(result.getResultCode(), result.getData()));

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleCameraResult(result.getResultCode(), result.getData()));
    }
}