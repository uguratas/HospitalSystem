package com.example.hospitalsystem.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.helper.PhpUrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayPatientsActivity extends AppCompatActivity {
    private ListView listViewPatients;
    private ArrayList<String> patientsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_patients);
        listViewPatients = findViewById(R.id.listViewPatients);
        patientsList = new ArrayList<>();
        getAllPatients(listViewPatients);
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Call the onBackPressed method when the back button is clicked
            }
        });
    }

    private void deletePatient(final int position, final ListView listView) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        patientsList.remove(position);
                        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
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
                params.put("delete_patient", "true");
                params.put("patient_id", patientsList.get(position).split(",")[0].replaceAll("[^0-9]", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getAllPatients(final ListView listView) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("JSON Response", response);
                            JSONArray jsonArray = new JSONArray(response);

                            Log.d("JSON Array", jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int patientId = jsonObject.getInt("patient_id");
                                String patientName = jsonObject.getString("patient_name");
                                String patientSurname = jsonObject.getString("patient_surname");
                                String patientGender = jsonObject.getString("patient_gender");

                                patientsList.add("ID:                   " + patientId +
                                        ", Name:             " + patientName +
                                        ", Surname:       " + patientSurname +
                                        ", Gender:           " + patientGender);
                                Log.d("Patient Data", "ID: " + patientId +
                                        ", İsim: " + patientName +
                                        ", Soyisim: " + patientSurname +
                                        ", Cinsiyet: " + patientGender);

                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DisplayPatientsActivity.this, R.layout.list_item_patient, patientsList) {
                                @NonNull
                                @Override
                                public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    View view = convertView;
                                    if (view == null) {
                                        view = getLayoutInflater().inflate(R.layout.list_item_patient, parent, false);
                                    }

                                    TextView textViewPatientId = view.findViewById(R.id.textViewPatientId);
                                    TextView textViewPatientName = view.findViewById(R.id.textViewPatientName);
                                    TextView textViewPatientSurname = view.findViewById(R.id.textViewPatientSurname);
                                    TextView textViewPatientGender = view.findViewById(R.id.textViewPatientGender);
                                    LinearLayout layoutDeletePatient = view.findViewById(R.id.layoutDeletePatient);

                                    String patientInfo = patientsList.get(position);
                                    String[] patientInfoArray = patientInfo.split(",");

                                    textViewPatientId.setText(patientInfoArray[0].trim());
                                    textViewPatientName.setText(patientInfoArray[1].trim());
                                    textViewPatientSurname.setText(patientInfoArray[2].trim());
                                    textViewPatientGender.setText(patientInfoArray[3].trim());
                                    layoutDeletePatient.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deletePatient(position, listViewPatients); // Hasta silme metodu çağırılıyor
                                        }
                                    });

                                    return view;
                                }
                            };

                            listView.setAdapter(adapter);
                            Log.d("Patients List Size", String.valueOf(patientsList.size()));

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
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
                params.put("get_all_patients", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
