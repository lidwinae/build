package com.example.praktikum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.praktikum.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BuildAdapter.OnBuildClickListener {
    private ActivityMainBinding binding;
    private BuildAdapter availableAdapter;
    private BuildAdapter collectionAdapter;
    private BuildItemRepository repository;
    private BuildItemViewModel viewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private Uri currentImageUri;
    private Uri dialogImageUri;
    private AlertDialog currentEditDialog;
    private ImageView currentEditPreview;
    private boolean useGambar1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database and repository
        AppDatabase database = AppDatabase.getDatabase(this);
        repository = new BuildItemRepository(database);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(BuildItemViewModel.class);
        viewModel.init(repository);

        setupRecyclerViews();
        setupAddButton();
        setupUploadButton();

        // Insert default items if database is empty
        viewModel.checkAndInsertDefaultItems();
    }

    private void setupRecyclerViews() {
        // Setup horizontal Available RecyclerView
        availableAdapter = new BuildAdapter(true, this);
        binding.rvAvailable.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvAvailable.setAdapter(availableAdapter);

        // Setup horizontal Collection RecyclerView
        collectionAdapter = new BuildAdapter(false, this);
        binding.rvCollection.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCollection.setAdapter(collectionAdapter);

        // Observe data from ViewModel
        viewModel.getAvailableItems().observe(this, buildItems ->
                availableAdapter.setBuildItems(buildItems));

        viewModel.getCollectionItems().observe(this, buildItems ->
                collectionAdapter.setBuildItems(buildItems));
    }

    private void setupAddButton() {
        binding.btnAdd.setOnClickListener(v -> {
            String input = binding.etInput.getText().toString().trim();
            if (!input.isEmpty()) {
                // Create new item (isDefault = false for user-created items)
                int imageRes = useGambar1 ? R.drawable.gambar1 : R.drawable.gambar2;
                useGambar1 = !useGambar1;

                BuildItem newItem = new BuildItem(input, imageRes, false, false);

                // If image was uploaded, set its path
                if (binding.ivPreview.getVisibility() == View.VISIBLE && currentImageUri != null) {
                    String imagePath = saveImageToInternalStorage(currentImageUri);
                    if (imagePath != null) {
                        newItem.setImagePath(imagePath);
                        newItem.setImageRes(0); // Reset imageRes since we're using imagePath
                    }
                }

                viewModel.insert(newItem);
                binding.etInput.setText("");
                binding.ivPreview.setVisibility(View.GONE);
                currentImageUri = null;
            } else {
                Toast.makeText(this, "Please enter a build name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUploadButton() {
        binding.btnUpload.setOnClickListener(v -> {
            if (checkPermissions()) {
                showImagePickerDialog();
            } else {
                requestPermissions();
            }
        });
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ needs READ_MEDIA_IMAGES
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-12
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 9 and below
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.CAMERA
                    },
                    PERMISSION_REQUEST_CODE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            switch (which) {
                case 0: // Gallery
                    openGallery();
                    break;
                case 1: // Camera
                    dispatchTakePictureIntent();
                    break;
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                currentImageUri = FileProvider.getUriForFile(this,
                        "com.example.praktikum.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir == null) {
                storageDir = getFilesDir();
            }
            return File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                // Untuk activity utama
                currentImageUri = data.getData();
                binding.ivPreview.setImageURI(currentImageUri);
                binding.ivPreview.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Untuk activity utama (kamera)
                binding.ivPreview.setImageURI(currentImageUri);
                binding.ivPreview.setVisibility(View.VISIBLE);
            } else if (requestCode == 3 && data != null) {
                // Untuk dialog (gallery)
                dialogImageUri = data.getData();
                if (currentEditPreview != null) {
                    Glide.with(this)
                            .load(dialogImageUri)
                            .into(currentEditPreview);
                }
            } else if (requestCode == 4) {
                // Untuk dialog (kamera)
                if (currentEditPreview != null) {
                    Glide.with(this)
                            .load(dialogImageUri)
                            .into(currentEditPreview);
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog();
            } else {
                Toast.makeText(this, "Permissions are required to upload images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAddClick(BuildItem item) {
        item.setAvailable(false);
        viewModel.update(item);
    }

    @Override
    public void onDeleteClick(BuildItem item) {
        if (item.isDefault()) {
            item.setAvailable(true);
            viewModel.update(item);
        } else {
            viewModel.delete(item);
        }
    }

    @Override
    public void onEditClick(BuildItem item) {
        if (!item.isDefault()) {
            showEditDialog(item);
        } else {
            Toast.makeText(this, "Default items cannot be modified", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog(BuildItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_build, null);
        builder.setView(dialogView);

        EditText etEditName = dialogView.findViewById(R.id.etEditName);
        ImageView ivEditPreview = dialogView.findViewById(R.id.ivEditPreview);
        Button btnChangeImage = dialogView.findViewById(R.id.btnChangeImage);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Simpan referensi ke dialog dan ImageView
        currentEditPreview = ivEditPreview;

        etEditName.setText(item.getName());

        // Display image
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Glide.with(this)
                    .load(new File(item.getImagePath()))
                    .into(ivEditPreview);
        } else {
            ivEditPreview.setImageResource(item.getImageRes());
        }

        currentEditDialog = builder.create();

        btnChangeImage.setOnClickListener(v -> {
            if (checkPermissions()) {
                showImagePickerDialogForEdit();
            } else {
                requestPermissions();
            }
        });

        btnCancel.setOnClickListener(v -> currentEditDialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            if (!newName.isEmpty()) {
                item.setName(newName);

                if (dialogImageUri != null) {
                    String imagePath = saveImageToInternalStorage(dialogImageUri);
                    if (imagePath != null) {
                        item.setImagePath(imagePath);
                        item.setImageRes(0);
                    }
                }

                viewModel.update(item);
                currentEditDialog.dismiss();
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        currentEditDialog.show();
    }

    private void showImagePickerDialogForEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            switch (which) {
                case 0: // Gallery
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 3); // Request code 3 untuk dialog
                    break;
                case 1: // Camera
                    dispatchTakePictureIntentForDialog();
                    break;
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntentForDialog() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                dialogImageUri = FileProvider.getUriForFile(this,
                        "com.example.praktikum.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, dialogImageUri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, 4); // Request code 4 untuk kamera di dialog
            }
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
            if (inputStream == null) return null;

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "build_image_" + timeStamp + ".jpg";

            File storageDir = new File(getFilesDir(), "build_images");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = new File(storageDir, imageFileName);
            try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}