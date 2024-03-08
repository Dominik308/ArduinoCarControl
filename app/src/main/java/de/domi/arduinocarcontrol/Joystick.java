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
import android.widget.TextView;
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
        ImageButton connect = findViewById(R.id.connect);
        EditText speed = findViewById(R.id.speed);
        TextView angleAndPower = findViewById(R.id.angleAndPower);

        JoystickView joystick = findViewById(R.id.joystick);

        joystick.setOnMoveListener((angle, strength) -> {
            try {
                angleAndPower.setText("Angle: " + angle + " Power: " + strength);
                if (socket != null) {
                    if (strength == 0)
                        sendCommand(BluetoothInfo.COMMAND_STOP);
                    else if ((angle > 337 || angle <= 22) && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_RIGHT);
                    else if (angle <= 67 && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_FORWARD_RIGHT);
                    else if (angle <= 112 && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_FORWARD);
                    else if (angle <= 157 && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_FORWARD_LEFT);
                    else if (angle <= 202 && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_LEFT);
                    else if (angle <= 247 && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_BACKWARD_LEFT);
                    else if (angle <= 292 && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_BACKWARD);
                    else if (angle <= 337 && strength > 0)
                        sendCommand(BluetoothInfo.COMMAND_BACKWARD_RIGHT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        ImageButton sendSpeed = findViewById(R.id.sendSpeed);
        connect.setOnClickListener(v -> connectToDevice(device, uuid));
        sendSpeed.setOnClickListener(v -> setSpeed(String.valueOf(speed.getText()) + '\n'));

        // start new Intent
        ImageButton keyboard = findViewById(R.id.keyboard);
        keyboard.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }


    void setSpeed(String speed) {
        try {
            if (outputStream != null) {
                outputStream.write(speed.getBytes());
                outputStream.flush();
                Toast.makeText(this, "New Speed: " + speed, Toast.LENGTH_SHORT).show();
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
                Thread.sleep(50);
                Toast.makeText(this, "Command sent: " + command, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("MSG", "Output stream is null");
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show());
            }
        } catch (IOException e) {
            Log.e("MSG", "Failed to send command", e);
            e.fillInStackTrace();
            runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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