package de.domi.arduinocarcontrol;

import android.Manifest;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class Joystick extends AppCompatActivity {
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        ImageButton connect = findViewById(R.id.connect);
        ImageButton turnLeft = findViewById(R.id.turnLeft);
        ImageButton turnRight = findViewById(R.id.turnRight);
        EditText speed = findViewById(R.id.speed);
        TextView angleAndPower = findViewById(R.id.angleAndPower);
        status = findViewById(R.id.status);
        JoystickView joystick = findViewById(R.id.joystick);

        joystick.setOnMoveListener((angle, strength) -> {
            try {
                angleAndPower.setText("Angle: " + angle + " Power: " + strength);
                speed.setText(String.valueOf(Math.round(2.55 * strength)));
                //setSpeed(String.valueOf(speed.getText()));

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
                } else
                    runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command!", Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        turnLeft.setOnClickListener(v -> sendCommand(BluetoothInfo.COMMAND_ROTATE_LEFT));
        turnRight.setOnClickListener(v -> sendCommand(BluetoothInfo.COMMAND_ROTATE_RIGHT));


        ImageButton sendSpeed = findViewById(R.id.sendSpeed);
        connect.setOnClickListener(v -> connectToDevice());
        sendSpeed.setOnClickListener(v -> setSpeed(String.valueOf(speed.getText()) + '\n'));

        // start new Intent
        ImageButton keyboard = findViewById(R.id.keyboard);
        keyboard.setOnClickListener(v -> {
            onDestroy();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        ImageButton disconnect = findViewById(R.id.disconnect);
        disconnect.setOnClickListener(v -> onDestroy());
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

    void connectToDevice() {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(Joystick.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                socket = BluetoothInfo.device.createRfcommSocketToServiceRecord(BluetoothInfo.uuid);
                socket.connect();
                outputStream = socket.getOutputStream();
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show());
                status.setText("Status: Connected");
                status.setTextColor(Color.GREEN);

            } catch (IOException e) {
                e.fillInStackTrace();
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to connect to device", Toast.LENGTH_LONG).show());
                status.setText("Status: Not Connected");
                status.setTextColor(Color.RED);
            }
        }).start();
    }

    private void sendCommand(char command) {
        try {
            if (outputStream != null) {
                outputStream.write(command);
                outputStream.flush(); // Flush the output stream
            } else {
                Log.e("MSG", "Output stream is null");
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show());
                status.setText("Status: Not Connected");
                status.setTextColor(Color.RED);
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
            if (socket != null) {
                socket.close();
                status.setText("Status: Not Connected");
                status.setTextColor(Color.RED);
                runOnUiThread(() -> Toast.makeText(Joystick.this, "Disconnected", Toast.LENGTH_LONG).show());
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}