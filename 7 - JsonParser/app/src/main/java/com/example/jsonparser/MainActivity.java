package com.example.jsonparser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jsonparser.net.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<HashMap<String, String>> contactList;

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

        this.listView = (ListView) findViewById(R.id.listView);
        this.contactList = new ArrayList<>();

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            handler.post(() -> {
                Toast.makeText(MainActivity.this, "Json Data is downloading",Toast.LENGTH_LONG).show();
            });

            HttpHandler httpHandler = new HttpHandler();
            String url = "https://gist.githubusercontent.com/Baalmart/8414268/raw/43b0e25711472de37319d870cb4f4b35b1ec9d26/contacts";
            String jsonStr = httpHandler.makeServiceCall(url);

            Log.e("Main", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        contactList.add(contact);
                    }
                }
                catch (final JSONException e) {
                    Log.e("Main", "Json parsing error: " + e.getMessage());
                    handler.post(() -> {
                        Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            } else {
                Log.e("Main", "Couldn't get json from server.");
                handler.post(() -> {
                    Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                });
            }

            handler.post(() -> {
                ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                        R.layout.list_item, new String[]{"id","name","email","mobile"},
                        new int[]{R.id.id,R.id.name,R.id.email, R.id.mobile});
                listView.setAdapter(adapter);
            });
        });
    }
}