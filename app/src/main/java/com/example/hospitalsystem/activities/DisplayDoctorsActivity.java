package com.example.hospitalsystem.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

public class DisplayDoctorsActivity extends AppCompatActivity {
    private ListView listViewDoctors;
    private ArrayList<String> doctorList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_doctors);
        listViewDoctors = findViewById(R.id.listViewDoctors);
        doctorList = new ArrayList<>();
        getAllDoctors(listViewDoctors);
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Call the onBackPressed method when the back button is clicked
            }
        });
    }
    private void deleteDoctor(final int position, final ListView listView) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doctorList.remove(position);
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
                params.put("delete_doctor", "true");
                params.put("doctor_id", doctorList.get(position).split(",")[0].replaceAll("[^0-9]", ""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getAllDoctors(final ListView listView) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.d("JSON Response", response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int doctorId = jsonObject.getInt("doctor_id");
                                String doctorName = jsonObject.getString("doctor_name");
                                String doctorSurname = jsonObject.getString("doctor_surname");
                                String doctorExpression = jsonObject.getString("doctor_expression");
                                doctorList.add("ID:                   " + doctorId +
                                        ", Name:             " + doctorName +
                                        ", Surname:       " + doctorSurname +
                                        ", Expression:    " + doctorExpression);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DisplayDoctorsActivity.this, R.layout.list_item_patient, doctorList) {
                                @NonNull
                                @Override
                                public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    View view = convertView;
                                    if (view == null) {
                                        view = getLayoutInflater().inflate(R.layout.list_item_doctor, parent, false);
                                    }

                                    TextView textViewDoctorID = view.findViewById(R.id.textViewDoctorId);
                                    TextView textViewDoctorName = view.findViewById(R.id.textViewDoctorName);
                                    TextView textViewDoctorSurname = view.findViewById(R.id.textViewDoctorSurname);
                                    TextView textViewDoctorExpression = view.findViewById(R.id.textViewDoctorExpression);
                                    LinearLayout layoutDeleteDoctor = view.findViewById(R.id.layoutDeleteDoctor);

                                    String doctorInfo = doctorList.get(position);
                                    String[] doctorInfoArray = doctorInfo.split(",");

                                    textViewDoctorID.setText(doctorInfoArray[0].trim());
                                    textViewDoctorName.setText(doctorInfoArray[1].trim());
                                    textViewDoctorSurname.setText(doctorInfoArray[2].trim());
                                    textViewDoctorExpression.setText(doctorInfoArray[3].trim());
                                    layoutDeleteDoctor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteDoctor(position,listViewDoctors);

                                        }
                                    });
                                    return view;
                                }
                            };

                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("get_all_doctors", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
