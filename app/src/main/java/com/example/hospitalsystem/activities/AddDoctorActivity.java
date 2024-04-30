package com.example.hospitalsystem.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AddDoctorActivity extends AppCompatActivity {
    private EditText doctorIdEditText;
    private EditText doctorNameEditText;
    private EditText doctorSurnameEditText;
    private Spinner doctorExpressionSpinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        doctorIdEditText = findViewById(R.id.doctor_id_edittext);
        doctorNameEditText = findViewById(R.id.doctor_name_edittext);
        doctorSurnameEditText = findViewById(R.id.doctor_surname_edittext);
        doctorExpressionSpinner = findViewById(R.id.doctor_expression_spinner);
        String[] expressions = {"KBB", "Göz", "Genel Cerrahi", "Dahiliye"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expressions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorExpressionSpinner.setAdapter(spinnerAdapter);

        Button addDoctorButton = findViewById(R.id.add_doctor_button);
        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDoctor();
            }
        });
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Call the onBackPressed method when the back button is clicked
            }
        });
    }
    private void addDoctor() {
        final int doctorId = Integer.parseInt(doctorIdEditText.getText().toString());
        final String doctorName = doctorNameEditText.getText().toString();
        final String doctorSurname = doctorSurnameEditText.getText().toString();
        final String doctorExpression = doctorExpressionSpinner.getSelectedItem().toString();

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
                params.put("add_doctor", "true");
                params.put("doctor_id", String.valueOf(doctorId));
                params.put("doctor_name", doctorName);
                params.put("doctor_surname", doctorSurname);
                params.put("doctor_expression", doctorExpression);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
