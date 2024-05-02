package com.example.listview;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.listview.data.Contact;
import com.example.listview.database.ContactDatabase;
import com.example.listview.layout.ContactAdapter;
import com.example.listview.layout.ImageSelectorListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper databaseHelper = null;
    private SQLiteDatabase database = null;
    private ContactDatabase contactDatabase = null;

    private ActivityResultLauncher<PickVisualMediaRequest> imageSelector = null;
    private ImageSelectorListener imageSelectorListener = null;

    private ListView listViewContactList = null;
    private TextView textViewEmptyContactList = null;
    private EditText editTextSearch = null;


    private final View.OnClickListener activityOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.buttonAdd) {
                MainActivity.this.showContactDialog(null);
            }
            else if (viewId == R.id.editTextSearch) {
                MainActivity.this.searchContact();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.databaseHelper = new SQLiteOpenHelper(this, "db.sql", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {}

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        };
        this.database = this.databaseHelper.getWritableDatabase();
        this.contactDatabase = new ContactDatabase(this.database);

        this.textViewEmptyContactList = (TextView) this.findViewById(R.id.textViewEmptyContactList);

        this.listViewContactList = (ListView) this.findViewById(R.id.listViewContactList);
        this.listViewContactList.setAdapter(new ContactAdapter(this, 0));
        this.registerForContextMenu(this.listViewContactList);

        ImageButton buttonAdd = this.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this.activityOnClickListener);

        this.imageSelector = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        this.imageSelectorListener.onSelectedListener(uri);
                    }
                });

        this.editTextSearch = (EditText) this.findViewById(R.id.editTextSearch);
        this.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.this.searchContact();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        this.searchContact();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean askStorageAccess() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"No permission to access Storage", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int viewId = v.getId();

        if (viewId == R.id.listViewContactList) {
            MenuInflater menuInflater = new MenuInflater(this);
            menuInflater.inflate(R.menu.contact_item_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = item.getItemId();
        try {
            if (id == R.id.menuEdit && info != null && info.targetView.getId() == R.id.item_contact) {
                this.showContactDialog((Contact) this.listViewContactList.getItemAtPosition(info.position));
                return true;
            }
            else if (id == R.id.menuDelete && info != null && info.targetView.getId() == R.id.item_contact) {
                new Thread(() -> {
                    Contact deletedContact = (Contact) this.listViewContactList.getItemAtPosition(info.position);
                    if (deletedContact != null) {
                        this.contactDatabase.delete(deletedContact.getId());
                    }

                    runOnUiThread(this::searchContact);
                }).start();

                return true;
            }
        }
        catch (Exception ignored) {
            Toast.makeText(this, "Action error!", Toast.LENGTH_LONG).show();
        }
        return super.onContextItemSelected(item);
    }

    private void showContactDialog(Contact contact) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View viewDialog = layoutInflater.inflate(R.layout.dialog_contact, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(viewDialog);

        final ImageView imageViewPhoto = viewDialog.findViewById(R.id.imageViewDialogUserProfileInput);
        final EditText editTextName = viewDialog.findViewById(R.id.editTextName);
        final EditText editTextContact = viewDialog.findViewById(R.id.editTextContact);

        if (contact != null) {
            imageViewPhoto.setImageBitmap(contact.getContactProfile());
            editTextName.setText(contact.getName());
            editTextContact.setText(contact.getContact());
        }

        imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.this.askStorageAccess()) {
                    MainActivity.this.imageSelectorListener = new ImageSelectorListener() {
                        @Override
                        public void onSelectedListener(Uri uri) {
                            new Thread(() -> {
                                try {
                                    Bitmap image = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), uri);
                                    if (image != null) {
                                        image = Bitmap.createScaledBitmap(image, 256, 256, true);
                                    }

                                    Bitmap finalImage = image;
                                    runOnUiThread(() -> {
                                        imageViewPhoto.setImageBitmap(finalImage);
                                    });
                                }
                                catch (Exception ignored) {}
                            }).start();
                        }
                    };

                    MainActivity.this.imageSelector.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            }
        });

        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case -1:
                        try {
                            BitmapDrawable imageViewPhotoDrawable = (BitmapDrawable) imageViewPhoto.getDrawable();

                            Contact newContact = new Contact(
                                    contact != null ? contact.getId() : null,
                                    editTextName.getText().toString(),
                                    editTextContact.getText().toString(),
                                    imageViewPhotoDrawable != null ? imageViewPhotoDrawable.getBitmap() : null
                            );

                            new Thread(() -> {
                                if (contact == null) {
                                    MainActivity.this.contactDatabase.save(newContact);
                                }
                                else {
                                    MainActivity.this.contactDatabase.update(newContact);
                                }

                                runOnUiThread(MainActivity.this::searchContact);

                            }).start();
                        }
                        catch (Exception ignored) {
                            Toast.makeText(MainActivity.this, "Error saving the contact", Toast.LENGTH_LONG).show();
                        }

                        break;
                    case -2:
                        break;
                }
            }
        };

        dialog
                .setCancelable(false)
                .setPositiveButton(R.string.save, dialogListener)
                .setNegativeButton(R.string.cancel, dialogListener);

        dialog.show();
    }

    private void searchContact() {
        final String searchText = this.editTextSearch.getText().toString();

        new Thread(() -> {
            List<Contact> contactList = this.contactDatabase.find(searchText);

            runOnUiThread(() -> {
                ContactAdapter listViewContactListAdapter = (ContactAdapter)this.listViewContactList.getAdapter();
                listViewContactListAdapter.clear();
                listViewContactListAdapter.addAll(contactList);
                listViewContactListAdapter.notifyDataSetChanged();

                if (contactList.isEmpty()) {
                    MainActivity.this.textViewEmptyContactList.setVisibility(View.VISIBLE);
                    MainActivity.this.listViewContactList.setVisibility(View.GONE);
                }
                else {
                    MainActivity.this.textViewEmptyContactList.setVisibility(View.GONE);
                    MainActivity.this.listViewContactList.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }
}