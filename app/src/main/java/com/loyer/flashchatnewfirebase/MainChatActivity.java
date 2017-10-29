package com.loyer.flashchatnewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);


            //klavyeden enter tuşuna basılınca mesajı göndericek
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                sendMessage();
                return true;
            }
        });

        // burda da gönder butonuna basılınca mesajı göndericek
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });



    }




    // sharedpreferencese attığımız veriyi geri çekiyoruz
    //ve oncreatein içinde yeniden çağırıyoruz

    private void setupDisplayName(){

        SharedPreferences preferences = getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);

        mDisplayName = preferences.getString(RegisterActivity.DISPLAY_NAME_KEY,null);

        if(mDisplayName == null) mDisplayName = "Anonymous";

    }


    private void sendMessage() {

        // TODO: Grab the text the user typed in and push the message to Firebase
        Log.d("FlashChat","I send something.");
        String input = mInputText.getText().toString();
        //mesajı aldıktan sonra eğer boş değilse
        //oluşturduğumuz consturctor ile mesaj ve mesaj sahibini de aldık
        //sonra da databasee gönderdik
        if(!input.equals("")){
            InstantMessage chat = new InstantMessage(input,mDisplayName);
            mDatabaseReference.child("messages").push().setValue(chat);
            //gönderdikten sonrada mesaj alanını temizledik
            mInputText.setText("");
        }

    }


    // TODO: Override the onStart() lifecycle method. Setup the adapter here.

    @Override
    public void onStart(){
        super.onStart();
        mAdapter = new ChatListAdapter(this,mDatabaseReference,mDisplayName);
        mChatListView.setAdapter(mAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.
        mAdapter.cleanUp();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu
    ){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.logouttt:

                return true;
            default:
                return false;
        }
    }

}
