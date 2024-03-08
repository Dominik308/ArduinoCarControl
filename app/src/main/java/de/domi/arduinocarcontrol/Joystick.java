package de.domi.arduinocarcontrol;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class Joystick extends AppCompatActivity {
    private BluetoothSocket socket;
    private OutputStream outputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:23:09:01:3A:1B");

        JoystickView joystick = findViewById(R.id.joystick);
        joystick.setOnMoveListener((angle, strength) -> {
            int a = angle + strength;

        });





        ImageButton connect = findViewById(R.id.connect);
        EditText speed = findViewById(R.id.speed);
        ImageButton sendSpeed = findViewById(R.id.sendSpeed);
        connect.setOnClickListener(v -> connectToDevice(device, uuid));
        sendSpeed.setOnClickListener(v -> {
            try {
                if (outputStream != null) {
                    String newSpeed = String.valueOf(speed.getText()) + '\n';
                    outputStream.write(newSpeed.getBytes());
                    outputStream.flush();
                    Toast.makeText(this, "New Speed: " + speed.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MSG", "Output stream is null");
                    runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show());
                }
            } catch (IOException e) {
                Log.e("MSG", "Failed to send command", e);
                e.fillInStackTrace();
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show());
            }
        });

        // start new Intent
        ImageButton keyboard = findViewById(R.id.keyboard);
        keyboard.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }

    void connectToDevice(BluetoothDevice device, UUID uuid) {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(Joystick.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                outputStream = socket.getOutputStream();
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show());
            } catch (IOException e) {
                e.fillInStackTrace();
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to connect to device", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void sendCommand(char command) {
        try {
            if (outputStream != null) {
                outputStream.write(command);
                outputStream.flush(); // Flush the output stream
                Toast.makeText(this, "Command sent: " + command, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("MSG", "Output stream is null");
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show());
            }
        } catch (IOException e) {
            Log.e("MSG", "Failed to send command", e);
            e.fillInStackTrace();
            runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}