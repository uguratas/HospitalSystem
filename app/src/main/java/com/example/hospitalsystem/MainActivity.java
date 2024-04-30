package com.example.hospitalsystem;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hospitalsystem.activities.AddDoctorActivity;
import com.example.hospitalsystem.activities.AddPatientActivity;
import com.example.hospitalsystem.activities.CreateAppointmentActivity;
import com.example.hospitalsystem.activities.DisplayDoctorsActivity;
import com.example.hospitalsystem.activities.DisplayPatientsActivity;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addPatientButton = findViewById(R.id.add_patient_button1);
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPatientActivity();
            }
        });

        Button addDoctorButton = findViewById(R.id.add_doctor_button1);
        addDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDoctorActivity();
            }
        });


        Button getAllPatientsButton = findViewById(R.id.get_all_patients_button);
        getAllPatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDisplayPatientsActivity();
            }
        });

        Button getAllDoctorsButton = findViewById(R.id.get_all_doctors_button);
        getAllDoctorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDisplayDoctorsActivity();
            }
        });
        Button createAppointmentButton = findViewById(R.id.create_appointment_button);
        createAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateAppointmentActivity();
            }
        });
    }
    private void openAddPatientActivity() {
        Intent intent = new Intent(this, AddPatientActivity.class);
        startActivity(intent);
    }
    private void openAddDoctorActivity() {
        Intent intent = new Intent(this, AddDoctorActivity.class);
        startActivity(intent);
    }

    private void openDisplayPatientsActivity() {
        Intent intent = new Intent(this, DisplayPatientsActivity.class);
        startActivity(intent);
    }

    private void openDisplayDoctorsActivity() {
        Intent intent = new Intent(this, DisplayDoctorsActivity.class);
        startActivity(intent);
    }
    private void openCreateAppointmentActivity(){
        Intent intent = new Intent(this, CreateAppointmentActivity.class);
        startActivity(intent);
    }


}
