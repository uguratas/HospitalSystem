package com.example.hospitalsystem.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAppointmentActivity extends AppCompatActivity {


    private Spinner patientSpinner;
    private Spinner doctorSpinner;
    private int selectedPatientId = -1;
    private int selectedDoctorId = -1;
    private DatePicker datePicker;

    private List<String> patientNamesList;
    private List<String> doctorNamesList;

    private HashMap<String, Integer> patientNameIdMap;
    private HashMap<String, Integer> doctorNameIdMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        patientSpinner = findViewById(R.id.patient_spinner);
        doctorSpinner = findViewById(R.id.doctor_spinner);
        datePicker = findViewById(R.id.appointment_date_picker);

        // Veritabanından hasta ve doktor isimlerini alın
       // patientNamesList = getPatientNamesFromDatabase();
        //doctorNamesList = getDoctorNamesFromDatabase();

        patientNamesList = new ArrayList<>();
        doctorNamesList = new ArrayList<>();
        patientNameIdMap = new HashMap<>();
        doctorNameIdMap = new HashMap<>();

        // Spinner'lara veri eklemek için uygun adapterleri oluşturun
        ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patientNamesList);
        ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctorNamesList);

        // Spinner'lara adapterleri atayın
        patientSpinner.setAdapter(patientAdapter);
        doctorSpinner.setAdapter(doctorAdapter);

        getPatient(patientSpinner);
        getDoctor(doctorSpinner);

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Call the onBackPressed method when the back button is clicked
            }
        });

        patientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPatientName = patientNamesList.get(position);
                selectedPatientId = patientNameIdMap.get(selectedPatientName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPatientId = -1;
            }
        });

        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDoctorName = doctorNamesList.get(position);
                selectedDoctorId = doctorNameIdMap.get(selectedDoctorName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDoctorId = -1;
            }
        });

        Button createAppointmentButton = findViewById(R.id.create_appointment_button);
        createAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Doğrulama adımlarını kontrol et
                if (selectedPatientId == -1 || selectedDoctorId == -1) {
                    Toast.makeText(getApplicationContext(), "Lütfen bir hasta ve bir doktor seçin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tarih seçimi doğrula
                if (!isDateValid()) {
                    Toast.makeText(getApplicationContext(), "Geçersiz bir tarih seçtiniz.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Randevu oluştur
                createAppointment();
            }
        });
    }
    private void getPatient(final Spinner patientSpinner) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.d("JSON Response", response);
                            patientNamesList.clear();
                            patientNameIdMap.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int patientId = jsonObject.getInt("patient_id");
                                String patientName = jsonObject.getString("patient_name");
                                String patientSurname = jsonObject.getString("patient_surname");

                                // Hasta ismini listeye ekleyin
                                String patientFullName = patientName + " " + patientSurname;
                                patientNamesList.add(patientFullName);

                                // Hasta ismi ve ID'sini eşleştirin
                                patientNameIdMap.put(patientFullName, patientId);
                            }

                            // ArrayAdapter kullanarak hasta isimlerini spinner'a yükleyin
                            ArrayAdapter<String> patientAdapter = new ArrayAdapter<>(CreateAppointmentActivity.this, android.R.layout.simple_spinner_item, patientNamesList);
                            patientSpinner.setAdapter(patientAdapter);
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

    private void getDoctor(final Spinner doctorSpinner) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.d("JSON Response", response);
                            doctorNamesList.clear();
                            doctorNameIdMap.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int doctorId = jsonObject.getInt("doctor_id");
                                String doctorName = jsonObject.getString("doctor_name");
                                String doctorSurname = jsonObject.getString("doctor_surname");
                                String doctorExpression = jsonObject.getString("doctor_expression");

                                // Doktor ismini listeye ekleyin
                                String doctorFullName = doctorName + " " + doctorSurname + " - " + doctorExpression;
                                doctorNamesList.add(doctorFullName);

                                // Doktor ismi ve ID'sini eşleştirin
                                doctorNameIdMap.put(doctorFullName, doctorId);
                            }
                            // ArrayAdapter kullanarak doktor isimlerini spinner'a yükleyin
                            ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(CreateAppointmentActivity.this, android.R.layout.simple_spinner_item, doctorNamesList);
                            doctorSpinner.setAdapter(doctorAdapter);
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
                params.put("get_all_doctors", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private boolean isDateValid() {
        // Seçilen tarihi alın
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        // Şu anki tarihi alın
        Calendar today = Calendar.getInstance();

        // Tarih nesnesini oluştur
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        // Seçilen tarihin bugünkü tarihten sonra olduğunu doğrula
        return !selectedDate.before(today);
    }

    private void createAppointment() {

        if (selectedPatientId == -1 || selectedDoctorId == -1) {
            Toast.makeText(getApplicationContext(), "Lütfen bir hasta ve bir doktor seçin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Seçilen tarihi alın
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        // Tarihi istenen formata çevirin (YYYY-MM-DD)
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String appointmentDate = dateFormat.format(calendar.getTime());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PhpUrlManager.APPOINTMENT_PHP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Hata: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("patient_id", String.valueOf(selectedPatientId));
                params.put("doctor_id", String.valueOf(selectedDoctorId));
                params.put("appointment_date", appointmentDate);

                params.put("patient_name", getSelectedPatientName());
                params.put("doctor_name", getSelectedDoctorName());
                params.put("doctor_expression",getSelectedDoctorExpression());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String getSelectedPatientName() {
        int selectedPatientPosition = patientSpinner.getSelectedItemPosition();
        return patientNamesList.get(selectedPatientPosition);
    }

    private String getSelectedDoctorName() {
        int selectedDoctorPosition = doctorSpinner.getSelectedItemPosition();
        String selectedDoctorString = doctorNamesList.get(selectedDoctorPosition);
        String[] parts = selectedDoctorString.split(" - ");
        if (parts.length > 1) {
            return parts[0].trim(); // The expertise should be at index 1 after the split
        } else {
            return ""; // Return an empty string or handle the case where expertise is not available
        }
    }

    private String getSelectedDoctorExpression() {
        int selectedDoctorExpression = doctorSpinner.getSelectedItemPosition();
        String selectedDoctorString = doctorNamesList.get(selectedDoctorExpression);
        String[] parts = selectedDoctorString.split(" - ");
        if (parts.length > 1) {
            return parts[1].trim(); // The expertise should be at index 1 after the split
        } else {
            return ""; // Return an empty string or handle the case where expertise is not available
        }
    }
}
