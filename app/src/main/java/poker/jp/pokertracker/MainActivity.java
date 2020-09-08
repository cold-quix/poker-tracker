/*
FILENAME:       MainActivity.java
DATE:           2020-08-30
PROGRAMMER:     Jack Parkinson
DESCRIPTION:
    VERSION 1.0
    
    This is the main activity for the Poker Tracker App.  This file handles all the behaviour
    of elements and layout which are defined in content_main.xml.
    
    This app allows the user to track various things that would be useful in a game of poker,
    such as M-factor, # of big blinds remaining, and the time left before the blinds increase.
 */

package poker.jp.pokertracker;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import android.content.Context;

public class MainActivity extends AppCompatActivity {
    // Public variables
    public boolean isMFactor = true;
    public int hoursDisplay = 0;
    public int minutesDisplay = 0;
    public Activity mainAct = this;
    
    // Timer control used in the timer section
    TextView timerTV;
    long startTimeMillis = 0;
    
    // Handler and Runnable which are used to run the timer via a thread.
    // The handler runs by being re/posted at the end of the runnable after a delay of 1000ms.
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            // Copy the number of milliseconds, then calculate each subdivision of hr/min/sec
            // Then subtract whatever milliseconds made up the subdivision
            long millis = startTimeMillis;
            int hours = (int) millis / 3600000;
            millis -= (hours * 3600000);
            int minutes = (int) millis / 60000;
            millis -= (minutes * 60000);
            int seconds = (int) millis / 1000;
            millis -= (seconds * 1000);
            startTimeMillis -= 1000;
           
            timerTV.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            // If the loop has not yet reached 0, repost this handler
            if (startTimeMillis > 0) {
                timerHandler.postDelayed(this, 1000);
            }
            // If the loop reached 0, don't repost anything and the runnable will end
            else {
                /*
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.divider_stack),
                        "the timer finished counting down", Snackbar.LENGTH_SHORT);
                mySnackbar.show();7
                */
                // Reset button
                Button tempButton = findViewById(R.id.button_timer_control);
                tempButton.setText("Start");
                // Enable timer fields
                enableTimerFields();
            }
        }
    };
    
    // onCreate method for Poker Tracker app.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // OnClick handling for buttons
        
        // "Start/Stop" button for timer
        // Starts and pauses the timer based on whether or not their is time remaining,
        // then updates the button label appropriately.
        timerTV = findViewById(R.id.timer_display);
        Button timerButton = findViewById(R.id.button_timer_control);
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.divider_stack),
                        "you pushed the timer button", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                */
                
                // If there is no time remaining, get it from the user input fields
                if (startTimeMillis <= 0) {
                    
                    // Set the time based on user input, then clear fields and start/stop countdown
                    TextView tempTV = findViewById(R.id.field_hours);
                    int tempHours = 0;
                    // If the hour field is not null and not empty, use it
                    if (!tempTV.getText().toString().matches("")) {
                        tempHours = Integer.valueOf(tempTV.getText().toString());
                    }
                    // Clear hour field
                    tempTV.setText("");
    
                    tempTV = findViewById(R.id.field_minutes);
                    int tempMinutes = 0;
                    // If the minute field is not null and not empty, use it
                    if (!tempTV.getText().toString().matches("")) {
                        tempMinutes = Integer.valueOf(tempTV.getText().toString());
                    }
                    // Clear the minute field
                    tempTV.setText("");
    
                    // Convert time from user into milliseconds
                    startTimeMillis = (tempMinutes * 60000) + (tempHours * 3600000);
                }
                
                // Switch label on start/stop button accordingly
                Button tempButton = findViewById(R.id.button_timer_control);
                if (tempButton.getText().equals("Stop")) {
                    // Stop timer
                    timerHandler.removeCallbacks(timerRunnable);
                    tempButton.setText("Start");
                    // Do not enable the timer fields here, because they should only be
                    // enabled when the timer reaches 0 or the user resets the timer.
                }
                else if (tempButton.getText().equals("Start")) {
                    // Start timer
                    timerHandler.postDelayed(timerRunnable, 0);
                    tempButton.setText("Stop");
                    // Disable timer fields
                    disableTimerFields();
                }
                
                // Dismiss the keyboard
                dismissKeyboard();
            }
        });
        
        // "Calculate" button
        // Calculates and displays M-factor or # of big blinds remaining.
        final Button button_calculate = findViewById(R.id.button_calculate);
        button_calculate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.divider_stack),
                        "you calculated", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                */
                // Values default to 0.0.  Changed only if the field is not empty and not null.
                double tempStack = 0;
                double tempBig = 0;
                double tempSmall= 0;
                double tempAnte = 0;
                
                if (isMFactor) {
                    // Calculate and display M-factor
                    // Get the values the user entered for stack, big/small blinds, and ante
                    TextView tempTV = findViewById(R.id.field_stack);
                    // If the field is not null or empty, use its value
                    if (!tempTV.getText().toString().matches("")) {
                        // Not null, not empty
                        tempStack = Integer.valueOf(tempTV.getText().toString());
                    }
    
                    tempTV = findViewById(R.id.field_big_blind);
                    // If the field is not null or empty, use its value
                    if (!tempTV.getText().toString().matches("")) {
                        // Not null, not empty
                        tempBig = Integer.valueOf(tempTV.getText().toString());
                    }
    
                    tempTV = findViewById(R.id.field_small_blind);
                    // If the field is not null or empty, use its value
                    if (!tempTV.getText().toString().matches("")) {
                        // Not null, not empty
                        tempSmall = Integer.valueOf(tempTV.getText().toString());
                    }
    
                    tempTV = findViewById(R.id.field_ante);
                    // If the field is not null or empty, use its value
                    if (!tempTV.getText().toString().matches("")) {
                        // Not null, not empty
                        tempAnte = Integer.valueOf(tempTV.getText().toString());
                    }
    
                    // Calculate M-factor, then display truncated to 2 decimals
                    double MFactor = calculateMFactor(tempStack, tempBig, tempSmall, tempAnte);
                    tempTV = findViewById(R.id.value_mfactor_bb_remaining);
                    tempTV.setText(String.format("%.2f", MFactor));
                }
                else {
                    // Calculate and display # of big blinds remaining
                    // Get the values the user entered for stack and big blind
                    TextView tempTV = findViewById(R.id.field_stack);
                    // If the field is not null or empty, use its value
                    if (!tempTV.getText().toString().matches("")) {
                        // Not null, not empty
                        tempStack = Integer.valueOf(tempTV.getText().toString());
                    }
    
                    tempTV = findViewById(R.id.field_big_blind);
                    // If the field is not null or empty, use its value
                    if (!tempTV.getText().toString().matches("")) {
                        // Not null, not empty
                        tempBig = Integer.valueOf(tempTV.getText().toString());
                    }
                    // Calculate big blinds remaining, then display truncated to 2 decimals
                    double bigBlindsRemaining = calculateBigBlinds(tempStack, tempBig);
                    tempTV = findViewById(R.id.value_mfactor_bb_remaining);
                    tempTV.setText(String.format("%.2f", bigBlindsRemaining));
                }
                // Dismiss the keyboard
                dismissKeyboard();
            }
        });
        
        // "Switch" button
        // Toggles calculation between M-factor and # of big blinds remaining, then calculates.
        final Button button_toggle_mfactor = findViewById(
                R.id.button_toggle_mfactor_bb_remaining);
        button_toggle_mfactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMFactor) {
                    //change to big blinds
                    TextView tempTV = findViewById(R.id.text_mfactor_bb_remaining);
                    tempTV.setText("Big Blinds");
                    //clear field
                    tempTV = findViewById(R.id.value_mfactor_bb_remaining);
                    tempTV.setText("");
                    isMFactor = false;
                }
                else if (!isMFactor) {
                    //change to mfactor
                    TextView tempTV = findViewById(R.id.text_mfactor_bb_remaining);
                    tempTV.setText("M Factor");
                    tempTV = findViewById(R.id.value_mfactor_bb_remaining);
                    tempTV.setText("");
                    isMFactor = true;
                }
                // Once the mode has been adjusted, calculate the new value
                button_calculate.callOnClick();
                // No need to attempt keyboard dismissal from here
            }
        });
        
        // Stack Reset Button
        // Calls the resetStack() method to clear and rest the stack management section.
        final Button button_reset_stack = findViewById(R.id.button_reset_stack);
        button_reset_stack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.divider_stack),
                        "you reset the stack fields", Snackbar.LENGTH_SHORT);
                mySnackbar.show();
                */
                resetStack();
            }
        });
    
        // Timer Reset Button
        // Calls the resetTimer() method to pause the timer, reduce remaining time to 0, and
        // reset timer display.
        final Button button_reset_timer = findViewById(R.id.button_reset_timer);
        button_reset_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }
    
    /*
    NAME:           resetStack
    PARAMETERS:     None.
    RETURN:         None.
    DESCRIPTION:
        Reset the values used to calculate the big blinds/m-factor, and the
        stack management fields.
     */
    public void resetStack() {
        TextView tempTV = findViewById(R.id.value_mfactor_bb_remaining);
        tempTV.setText("0.00");
        EditText tempET = findViewById(R.id.field_stack);
        tempET.setText("");
        tempET = findViewById(R.id.field_big_blind);
        tempET.setText("");
        tempET = findViewById(R.id.field_small_blind);
        tempET.setText("");
        tempET = findViewById(R.id.field_ante);
        tempET.setText("");
        // Dismiss the keyboard
        dismissKeyboard();
    }
    
    
    /*
    NAME:           resetTimer
    PARAMETERS:     None.
    RETURN:         None.
    DESCRIPTION:
        Ends the timer, resets the values and fields used by the timer section, and
        enables the hour/minute fields for user input.
     */
    public void resetTimer() {
        // Stop timer
        timerHandler.removeCallbacks(timerRunnable);
        // Reset values
        startTimeMillis = 0;
        // Reset fields
        TextView tempTV = findViewById(R.id.field_hours);
        tempTV.setText("");
        tempTV = findViewById(R.id.field_minutes);
        tempTV.setText("");
        tempTV = findViewById(R.id.timer_display);
        tempTV.setText(String.format("%02d:%02d:%02d", 0, 0, 0));
        // Enable hr/min fields
        enableTimerFields();
        // Reset timer button to "Start"
        Button tempBtn = findViewById(R.id.button_timer_control);
        tempBtn.setText("Start");
        // Dismiss keyboard
        dismissKeyboard();
    }
    
    
    /*
    NAME:           calculateMFactor
    PARAMETERS:     double stack, double big_blind, double small_blind, double ante
    RETURN:         double MFactor
    DESCRIPTION:
        Function for calculating M-factor based on the user's stack and the size of the blinds and
        ante.  If the divisor would be 0, then it just returns 0 instead.
     */
    public double calculateMFactor(double stack, double big_blind, double small_blind, double ante) {
        double MFactor = 0;
        if ((small_blind + big_blind + ante) == 0) {
            return MFactor;
        }
        else {
            MFactor = (stack / (small_blind + big_blind + ante));
        }
        return MFactor;
    }
    
    
    /*
    NAME:           calculateBigBlinds
    PARAMETERS:     double stack, double big_blind
    RETURN:         double bigBlindsRemaining
    DESCRIPTION:
        Function for calculating number of big blinds remaining in stack based on stack and big
        blind size.  If the divisor would be 0, then it just returns 0 instead.
     */
    public double calculateBigBlinds(double stack, double big_blind) {
        double bigBlindsRemaining = 0;
        if (big_blind == 0) {
            return bigBlindsRemaining;
        }
        else {
            bigBlindsRemaining = (stack/big_blind);
            return bigBlindsRemaining;
        }
    }
    
    
    /*
    NAME:           onPause
    PARAMETERS:     None.
    RETURN:         None.
    DESCRIPTION:
        Function for pausing timer.  Stops the timer runnable, then sets the timer control button
        label to "Start".
     */
    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Button tempButton = findViewById(R.id.button_timer_control);
        tempButton.setText("Start");
        // Dismiss the keyboard
        dismissKeyboard();
    }
    
    
    /*
    NAME:           dismissKeyboard
    PARAMETERS:     None.
    RETURN:         None.
    DESCRIPTION:
        Dismisses the keyboard.  Adapted from a StackOverflow answer:
        https://stackoverflow.com/a/22671813/11228976
     */
    public void dismissKeyboard() {
        // Only one activity, so we can just get it within this function to avoid
        // scope problems that might arise from calling this function within other blocks
        Activity mainAct = this;
        View v = mainAct.getWindow().getCurrentFocus();
        // If a keyboard exists, dismiss it
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) mainAct.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    
    
    /*
    NAME:           disableTimerFields
    PARAMETERS:     None.
    RETURN:         None.
    DESCRIPTION:
        Disables timer fields so the user cannot enter anything.  This is done immediately after
        the timer begins and reads the values from those fields.  Needn't worry about clearing
        focus because setInputType() combined with dismissKeyboard() effectively does that.
     */
    public void disableTimerFields() {
        // Disable hr/min fields.
        EditText tempET = findViewById(R.id.field_hours);
        tempET.setInputType(0x00000000); //type: none
        tempET = findViewById(R.id.field_minutes);
        tempET.setInputType(0x00000000); //type: none
    }
    
    
    /*
    NAME:           enableTimerFields
    PARAMETERS:     None.
    RETURN:         None.
    DESCRIPTION:
        Enables timer fields so the user can enter hr/min values.  This is done when the timer
        reaches 0 naturally or is reset.  Simply pausing the timer does not allow the user to
        enter new hr/min values.
     */
    public void enableTimerFields() {
        // Enable hr/min fields.
        EditText tempET = findViewById(R.id.field_hours);
        tempET.setInputType(0x00000002); //type: number
        tempET = findViewById(R.id.field_minutes);
        tempET.setInputType(0x00000002); //type: number
    }
    
}
