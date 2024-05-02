package com.example.listview.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.listview.R;
import com.example.listview.data.Contact;

import java.util.Objects;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    private static class ViewHolderItemContact {
        ImageView imageViewContactProfile;
        TextView textViewName;
        TextView textViewContact;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Contact contact = Objects.requireNonNull(this.getItem(position));

        ViewHolderItemContact viewHolderItemContact = null;
        if (convertView == null) {
            viewHolderItemContact = new ViewHolderItemContact();
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_contact, parent, false);
            convertView.setLongClickable(true);

            viewHolderItemContact.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            viewHolderItemContact.textViewContact = (TextView) convertView.findViewById(R.id.textViewPhoneNumber);
            viewHolderItemContact.imageViewContactProfile = (ImageView) convertView.findViewById(R.id.imageViewUserProfile);

            convertView.setTag(viewHolderItemContact);
        }
        else {
            viewHolderItemContact = (ViewHolderItemContact) convertView.getTag();
        }

        viewHolderItemContact.textViewName.setText(contact.getName());
        viewHolderItemContact.textViewContact.setText(contact.getContact());
        try {
            if (contact.getContactProfile() != null) {
                viewHolderItemContact.imageViewContactProfile.setImageBitmap(contact.getContactProfile());
            }
            else {
                viewHolderItemContact.imageViewContactProfile.setImageResource(R.drawable.sentiment_satisfied_alt_32);
            }
        }
        catch (Exception e) {
            viewHolderItemContact.imageViewContactProfile.setImageResource(R.drawable.sentiment_satisfied_alt_32);
        }

        return convertView;
    }
}