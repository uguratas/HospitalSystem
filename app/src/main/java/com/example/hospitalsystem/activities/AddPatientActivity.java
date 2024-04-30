package com.example.hospitalsystem.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.helper.PhpUrlManager;

import java.util.HashMap;
import java.util.Map;

public class AddPatientActivity extends AppCompatActivity {


    private EditText patientIdEditText;
    private EditText patientNameEditText;
    private EditText patientSurnameEditText;
    private RadioGroup patientGenderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        patientIdEditText = findViewById(R.id.patient_id_edittext);
        patientNameEditText = findViewById(R.id.patient_name_edittext);
        patientSurnameEditText = findViewById(R.id.patient_surname_edittext);
        patientGenderRadioGroup = findViewById(R.id.patient_gender_radiogroup);

        Button addPatientButton = findViewById(R.id.add_patient_button);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPatient();
            }
        });
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Call the onBackPressed method when the back button is clicked
            }
        });
    }

    private void addPatient() {
        final int patient_id = Integer.parseInt(patientIdEditText.getText().toString());
        final String patient_name = patientNameEditText.getText().toString();
        final String patient_surname = patientSurnameEditText.getText().toString();
        final String patient_gender;

        int selectedId = patientGenderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_male) {
            patient_gender = "Male";
        } else if (selectedId == R.id.radio_female) {
            patient_gender = "Female";
        } else {
            // User hasn't selected a gender, notify the user
            Toast.makeText(getApplicationContext(), "Please select a gender.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("add_patient", "true");
                params.put("patient_id", String.valueOf(patient_id));
                params.put("patient_name", patient_name);
                params.put("patient_surname", patient_surname);
                params.put("patient_gender", patient_gender);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
