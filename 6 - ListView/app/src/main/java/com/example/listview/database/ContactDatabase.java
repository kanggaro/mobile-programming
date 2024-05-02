package com.example.listview.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.listview.data.Contact;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactDatabase {

    private final SQLiteDatabase database;

    public ContactDatabase(SQLiteDatabase database) {
        this.database = database;

        synchronized (this.database) {
            this.database.execSQL("create table if not exists contact(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, contact TEXT, photo BLOB );");
        }
    }

    public void save(Contact contact) {
        try {
            ContentValues newContact = new ContentValues();
            byte[] photoByte = null;

            if (contact.getContactProfile() != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                contact.getContactProfile().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                photoByte = byteArrayOutputStream.toByteArray();
            }

            newContact.put("name", contact.getName());
            newContact.put("contact", contact.getContact());
            newContact.put("photo", photoByte);
            synchronized (this.database) {
                this.database.insert("contact", null, newContact);
            }
        }
        catch (Exception ignored) {}
    }

    public List<Contact> find(String search) {
        search = "%" + search + "%";
        Cursor cursor = null;

        synchronized (this.database) {
            cursor = this.database.rawQuery("select * from contact where name like ? or contact like ?;", new String[]{search, search});
        }

        ArrayList<Contact> result = new ArrayList<Contact>();
        if (cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex("id");
            int nameColumnIndex = cursor.getColumnIndex("name");
            int contactColumnIndex = cursor.getColumnIndex("contact");
            int photoColumnIndex = cursor.getColumnIndex("photo");

            do {
                Bitmap photo = null;
                try {
                    if (!cursor.isNull(photoColumnIndex)) {
                        byte[] photoBlob = cursor.getBlob(photoColumnIndex);
                        photo = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
                    }
                    result.add(new Contact(cursor.getInt(idColumnIndex), cursor.getString(nameColumnIndex), cursor.getString(contactColumnIndex), photo));
                }
                catch (Exception ignored) {}
            } while (cursor.moveToNext());
        }

        return result;
    }

    public void update(Contact contact) {
        try {
            ContentValues updatedContact = new ContentValues();
            byte[] photoByte = null;

            if (contact.getContactProfile() != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                contact.getContactProfile().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                photoByte = byteArrayOutputStream.toByteArray();
            }

            updatedContact.put("id", contact.getId());
            updatedContact.put("name", contact.getName());
            updatedContact.put("contact", contact.getContact());
            updatedContact.put("photo", photoByte);
            synchronized (this.database) {
                this.database.update("contact", updatedContact, "id = ?", new String[]{contact.getId().toString()});
            }
        }
        catch (Exception ignored) {}
    }

    public void delete(Integer id) {
        try {
            synchronized (this.database) {
                this.database.delete("contact", "id = ?", new String[]{id.toString()});
            }
        }
        catch (Exception ignored) {}
    }
}