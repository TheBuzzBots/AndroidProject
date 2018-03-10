package edu.gatech.team10.weshelter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ShelterCheckInActivity extends AppCompatActivity {

    private EditText neededBeds;
    private HomelessPerson user = (HomelessPerson) Model.getInstance().getActiveUser();
    private Shelter shelter = Model.getInstance().getActiveShelter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_check_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        neededBeds = (EditText) findViewById(R.id.editText_checkin_numbeds);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void verifyCheckIn(View v) {

        if (neededBeds.getText().toString().equals("")) {
            Snackbar.make(v, "Please select a number of beds.", Snackbar.LENGTH_LONG).show();
        } else {

            int numBeds = Integer.parseInt(neededBeds.getText().toString());

            if (user.hasReservation()) {
                Snackbar.make(v, "Please cancel existing reservation before making a new one.",
                        Snackbar.LENGTH_LONG).show();
            } else if (numBeds <= 0) {
                Snackbar.make(v, "Please select a valid number of beds.",
                        Snackbar.LENGTH_LONG).show();
            } else if (numBeds > shelter.getCapacityInt()) {
                Snackbar.make(v, "The number of beds selected exceeds the number of beds available.",
                        Snackbar.LENGTH_LONG).show();
            } else {
                user.makeReservation(shelter.getKey(), numBeds);
                int newCapacity = (shelter.getCapacityInt() - numBeds);
                shelter.setCapacityInt(newCapacity);
                //update user in database
                //  1) update reservation boolean
                //  2) update resKey
                //  3) update resBeds
                // maybe we could create an updateUser method in the Model if that is easier?
                finish();
            }
        }
    }

    public void cancelCheckIn(View v) {

        if (!user.hasReservation()) {
            Snackbar.make(v, "You have no existing reservation to cancel.",
                    Snackbar.LENGTH_LONG).show();
        } else if (user.getResKey() != shelter.getKey()) {
            Snackbar.make(v, "Your existing reservation is not with this shelter.",
                    Snackbar.LENGTH_LONG).show();
        } else {
            user.setReservation(false);
            int newCapacity = (shelter.getCapacityInt() + user.getResBeds());
            shelter.setCapacityInt(newCapacity);
            //update user in database
            // ***same needs as above
            finish();
        }

    }

}
