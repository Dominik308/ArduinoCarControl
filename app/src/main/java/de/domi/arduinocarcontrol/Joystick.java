package de.domi.arduinocarcontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
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
    private PopupWindow popupWindow;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
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
                //speed.setText(String.valueOf(Math.round(2.55 * strength)));
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

        turnLeft.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_ROTATE_LEFT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        turnRight.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_ROTATE_RIGHT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });


        ImageButton sendSpeed = findViewById(R.id.sendSpeed);
        connect.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View popupView = inflater.inflate(R.layout.popup_layout, null);

            // Initialize a new instance of PopupWindow
            popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // Set an elevation value for the popup window
            popupWindow.setElevation(5.0f);

            ListView listView = popupView.findViewById(R.id.listView);

            // Create an ArrayAdapter with the options
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Joystick.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1,
                    new String[]{"00:23:09:01:3A:1B", "00:23:09:01:37:BE"});

            // Set the adapter to the ListView
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                String address = (String) parent.getItemAtPosition(position);
                connectToDevice(address);
                runOnUiThread(() -> popupWindow.dismiss());
            });

            // Dismiss the popup when the user taps outside of it
            popupView.setOnTouchListener((v1, event) -> {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    runOnUiThread(() -> popupWindow.dismiss());
                    return true;
                }
                return false;
            });

            popupWindow.showAsDropDown(connect, 0, -180);
        });
        sendSpeed.setOnClickListener(v -> setSpeed(String.valueOf(speed.getText()) + '\n'));

        // Go to MainActivity
        ImageButton keyboard = findViewById(R.id.keyboard);
        keyboard.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            onDestroy();
        });

        ImageButton disconnect = findViewById(R.id.disconnect);
        disconnect.setOnClickListener(v -> onDestroy());

        ImageButton selfDrive = findViewById(R.id.selfDrive);
        selfDrive.setOnClickListener(v -> sendCommand(BluetoothInfo.COMMAND_SELF_DRIVE));
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

    @SuppressLint("SetTextI18n")
    void connectToDevice(String address) {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(Joystick.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                BluetoothDevice device = BluetoothInfo.bluetoothAdapter.getRemoteDevice(address);
                socket = device.createRfcommSocketToServiceRecord(BluetoothInfo.uuid);
                socket.connect();
                outputStream = socket.getOutputStream();
                runOnUiThread(() -> {
                    Toast.makeText(Joystick.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show();
                    status.setText("Status: Connected");
                    status.setTextColor(Color.GREEN);
                });

            } catch (IOException e) {
                e.fillInStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(Joystick.this, "Failed to connect to device", Toast.LENGTH_LONG).show();
                    status.setText("Status: Not Connected");
                    status.setTextColor(Color.RED);
                });
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
                runOnUiThread(() -> {
                    Toast.makeText(Joystick.this, "Failed to send command", Toast.LENGTH_LONG).show();
                    status.setText("Status: Not Connected");
                    status.setTextColor(Color.RED);
                });
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

                runOnUiThread(() -> {
                    Toast.makeText(Joystick.this, "Disconnected", Toast.LENGTH_LONG).show();
                    status.setText("Status: Not Connected");
                    status.setTextColor(Color.RED);
                });
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}