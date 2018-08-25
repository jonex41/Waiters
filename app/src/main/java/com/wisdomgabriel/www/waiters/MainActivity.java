package com.wisdomgabriel.www.waiters;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wisdomgabriel.www.waiters.Registration.SignInActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int mNotificationsCount = 0;

  private LinearLayout drinks, foods, fruits, deserts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){

            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }

       findViewById(R.id.drinks).setOnClickListener(this);
       findViewById(R.id.foods).setOnClickListener(this);
        findViewById(R.id.fruits).setOnClickListener(this);
        findViewById(R.id.deserts).setOnClickListener(this);

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrdersActivity.class));
            }
        });



    }







    @Override
    protected void onStart() {
        super.onStart();
       // new FetchCountTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.drinks:
                startActivity(new Intent(getApplicationContext(), DrinksActivity.class));
                break;

            case R.id.foods:
                startActivity(new Intent(getApplicationContext(), FoodsActivity.class));

                break;
            case R.id.fruits:
                startActivity(new Intent(getApplicationContext(), FruitsActivity.class));

                break;
            case R.id.deserts:
                startActivity(new Intent(getApplicationContext(), DesertsActivity.class));

                break;
        }
    }

   /* class FetchCountTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {


          //  int counts = (int) cartDatabase.getProfilesCount();

            return counts;

        }
        private void updateNotificationsBadge(int count) {
            mNotificationsCount = count;

            // force the ActionBar to relayout its MenuItems.
            // onCreateOptionsMenu(Menu) will be called again.
            invalidateOptionsMenu();

        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }*/
}
