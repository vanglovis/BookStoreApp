package com.example.a2hergasiaaleph;

import static android.content.ContentValues.TAG;
import static android.view.Gravity.CENTER;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> link = new ArrayList<String>();   //Κανει initialise οτι θα χρειαστουμε στον κωδικα
    ArrayList<Long> price = new ArrayList<Long>();
    ArrayList<String> title = new ArrayList<String>();
    ArrayList<Long> availability = new ArrayList<Long>();
    ArrayList<Integer> number_picker_checker = new ArrayList<>();
    LinearLayout layout,l, firstlayout, secondlayout;
    ScrollView scrollView;
    long complete_price;
    TextView complete_textprice;
    Button paybutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paybutton  = findViewById(R.id.paybtn);
        complete_textprice = findViewById(R.id.complete_price);
        paybutton.setEnabled(false);
        DatabaseReference myRef;    //Συνδεση της βασης ωστε να παρουμε τα δεδομενα απο την βαση
        myRef = FirebaseDatabase.getInstance("https://hergasiaaleph-cd544-default-rtdb.firebaseio.com/").getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getPriority();
                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {  //Παιρνουμε ολα τα δεδομενα που χρειαζομαστε απο την βαση
                    title.add(snapshot.getKey());   //τιτλος βιβλιων
                    link.add((String) snapshot.child("link").getValue());   //λινκ των εικονων που παιρνει απο το storage ης firebase
                    price.add((Long) snapshot.child("price").getValue());   //παιρνει την τιμη καθε βιβλιου απο την βαση
                    availability.add((Long)snapshot.child("availability").getValue());  //παιρνει την διαθεσιμοητα
                    number_picker_checker.add(0);   //η number_picker_checker παιρνει σαν μεγεθος το πληθος των βιβλιων που υπαρχουν στην βαση
                    getAllData(i); //Μπαινει στην getAllData

                    i++;
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }}); }

    public void logout(View view){  //κανει logout aπο την mainactivity
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,StartActivity.class));
        finish(); }
    public void pay(View view){ //Οταν ο χρηστης πατησει το κουμπι της πληρωμης τοτε γινεται εξαρχης συνδεση της βασης και κανει update την διαθεσιμοτητα των βιβλιων
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hergasiaaleph-cd544-default-rtdb.firebaseio.com/");
        for(int j=0;j<number_picker_checker.size();j++){
            DatabaseReference Ref =database.getReference(title.get(j));
            DatabaseReference ref1 = Ref.child("availability");
            ref1.setValue(availability.get(j)-number_picker_checker.get(j)); }
        Toast.makeText(MainActivity.this, "Your purchase is completed. Come by from the store to collect your order!", Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent()); }


    public void getAllData(int i){
        //hardcoded components
        layout = findViewById(R.id.layout_parent);
        scrollView = findViewById(R.id.scrollview_parent);

        //Δημιουργει 2 layout στα οποια θα μπουν ολα τα imageview, button, textview
        firstlayout = new LinearLayout(this);
        firstlayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        secondlayout = new LinearLayout(this);
        //Δημιουργια ολων των textview τα οποια παιρνουν σαν text τον τιτλο των βιβλιων
        TextView t1 = new TextView(this);
        t1.setLayoutParams(lparams);
        t1.setText(title.get(i));
        secondlayout.addView(t1);

        LinearLayout l1h = new LinearLayout(this);
        l1h.setOrientation(LinearLayout.HORIZONTAL);
        l1h.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        l1h.setGravity(CENTER);
        //Δημιουργια ολων των imageview τα οποια παιρνουν σαν image τις εικονες των βιβλιων
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        Picasso.get().load(link.get(i)).into(imageView);
        l1h.addView(imageView);

        LinearLayout l1v = new LinearLayout(this);
        l1v.setOrientation(LinearLayout.VERTICAL);
        //Δημιουργια ολων textview τα οποια παιρνουν σαν text ην τιμη των βιλιων
        TextView title1 = new TextView(this);
        title1.setLayoutParams(lparams);
        title1.setText(String.valueOf( Math.toIntExact(price.get(i)))+"€");
        l1v.addView(title1);

        l1h.addView(l1v);
        //Δημιουργια ολων των numberpicker τα οποια παιρνουν την διαθεσιμοτητα των βιβλιων
        NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(availability.get(i).intValue());
        numberPicker.setId(i);
        l1h.addView(numberPicker);
        //Δημιουργια των κουμπιων add to cart τα οποια προσθετουν στο καλαθι τα βιβλια που θα επιλεξει ο χρηστης και την ποσοτητα τους
        Button buttonCart = new Button(this);
        buttonCart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonCart.setText("Add to cart");
        buttonCart.setId(i);
        buttonCart.setEnabled(false);
        buttonCart.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.purple_500), PorterDuff.Mode.DARKEN);
        buttonCart.setTextColor(getColor(R.color.white));
        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Οταν ο χρηστης κλικαρει στο κουμπι τοτε:
                if(buttonCart.getText().equals("Add to cart")) {
                    number_picker_checker.set(i,numberPicker.getValue()); //η number_picker_checker λιστα παιρνει την ποσοτητα του βιβλιου που επελεξε ο χρηστης
                    complete_price += numberPicker.getValue() * price.get(i);   //προσθετει σε μια μεταβλητη την ποσοτητα επι την τιμη του βιβλιου που επελεξε
                    complete_textprice.setText(String.valueOf(complete_price)+"€"); //εμφανιζει σε textview την τιμη που θα πληρωσει ο χρηστης
                    buttonCart.setText("Remove from cart"); //Το κουμπι add to cart παιρνει σαν text την τιμη remove from cart
                }
                else if(buttonCart.getText().equals("Remove from cart")){   //εαν πατησει το κουμπι αφου εχει βαλει στο καλαθι το βιβλιο τοτε το βγαζει απο το καλαθι με τον ιδιο τροπο
                    complete_price -= number_picker_checker.get(i)*price.get(i);
                    complete_textprice.setText(String.valueOf(complete_price)+"€");
                    buttonCart.setText("Add to cart");
                    number_picker_checker.set(i,0);}
                if(!complete_textprice.getText().equals("0€"))
                    paybutton.setEnabled(true);
                else
                    paybutton.setEnabled(false);
            }});
        l1h.addView(buttonCart);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if(numberPicker.getValue()!=0){
                    buttonCart.setEnabled(true);
                }}});

        //adds l1h (horizontal) in
        firstlayout.addView(l1h);

        //adds text in second layout

        //adds both layouts in parent layout
        this.layout.addView(secondlayout);
        this.layout.addView(firstlayout);
    }}