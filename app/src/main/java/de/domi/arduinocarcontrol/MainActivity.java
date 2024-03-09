package de.domi.arduinocarcontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private BluetoothSocket socket;
    private OutputStream outputStream;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton forwardButton = findViewById(R.id.forward);
        ImageButton backwardButton = findViewById(R.id.backward);
        ImageButton leftButton = findViewById(R.id.left);
        ImageButton rightButton = findViewById(R.id.right);
        ImageButton forwardLeftButton = findViewById(R.id.forward_left);
        ImageButton forwardRightButton = findViewById(R.id.forward_right);
        ImageButton backwardLeftButton = findViewById(R.id.backward_left);
        ImageButton backwardRightButton = findViewById(R.id.backward_right);
        ImageButton rotateLeftButton = findViewById(R.id.rotate_left);
        ImageButton rotateRightButton = findViewById(R.id.rotate_right);
        ImageButton stopButton = findViewById(R.id.stop);
        ImageButton connect = findViewById(R.id.connect);
        EditText speed = findViewById(R.id.speed);
        ImageButton sendSpeed = findViewById(R.id.sendSpeed);
        ImageButton disconnect = findViewById(R.id.disconnect);
        disconnect.setOnClickListener(v -> onDestroy());

        //start new Intent
        ImageButton joystick = findViewById(R.id.joystick);
        joystick.setOnClickListener(v -> {
            onDestroy();
            Intent intent = new Intent(getApplicationContext(), Joystick.class);
            startActivity(intent);
        });

        forwardButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_FORWARD);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        backwardButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_BACKWARD);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        leftButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_LEFT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        rightButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_RIGHT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        forwardLeftButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_FORWARD_LEFT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        forwardRightButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_FORWARD_RIGHT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        backwardLeftButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_BACKWARD_LEFT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        backwardRightButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_BACKWARD_RIGHT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });


        rotateLeftButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_ROTATE_LEFT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        rotateRightButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(BluetoothInfo.COMMAND_ROTATE_RIGHT);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(BluetoothInfo.COMMAND_STOP);
            }
            return true;
        });

        stopButton.setOnClickListener(v -> sendCommand(BluetoothInfo.COMMAND_STOP));
        connect.setOnClickListener(v -> connectToDevice());

        sendSpeed.setOnClickListener(v -> {
            try {
                if (outputStream != null) {
                    String newSpeed = String.valueOf(speed.getText()) + '\n';
                    outputStream.write(newSpeed.getBytes());
                    outputStream.flush();
                    Toast.makeText(MainActivity.this, "New Speed: " + speed.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MSG", "Output stream is null");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
                }
            } catch (IOException e) {
                Log.e("MSG", "Failed to send command", e);
                e.fillInStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void sendCommand(char command) {
        try {
            if (outputStream != null) {
                outputStream.write(command);
                outputStream.flush(); // Flush the output stream
            } else {
                Log.e("MSG", "Output stream is null");
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
            }
        } catch (IOException e) {
            Log.e("MSG", "Failed to send command", e);
            e.fillInStackTrace();
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
        }
    }

    void connectToDevice() {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                socket = BluetoothInfo.device.createRfcommSocketToServiceRecord(BluetoothInfo.uuid);
                socket.connect();
                outputStream = socket.getOutputStream();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show());
            } catch (IOException e) {
                e.fillInStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to connect to device", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show());
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}