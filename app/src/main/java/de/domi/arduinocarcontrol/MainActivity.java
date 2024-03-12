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

public class MainActivity extends AppCompatActivity {
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private TextView status;
    private PopupWindow popupWindow;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
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
        disconnect.setOnClickListener(v -> {
            try {
                if (socket != null) {
                    socket.close();
                    runOnUiThread(() -> {
                        status.setTextColor(Color.RED);
                        status.setText("Status: Not Connected");
                        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        });

        //Go to Joystick
        ImageButton joystick = findViewById(R.id.joystick);
        joystick.setOnClickListener(v -> {
            try {
                if (socket != null) {
                    socket.close();
                    runOnUiThread(() -> {
                        status.setTextColor(Color.RED);
                        status.setText("Status: Not Connected");
                        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (IOException e) {
                e.fillInStackTrace();
            }

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
        connect.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View popupView = inflater.inflate(R.layout.popup_layout, null);

            // Initialize a new instance of PopupWindow
            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Set an elevation value for the popup window
            popupWindow.setElevation(5.0f);

            ListView listView = popupView.findViewById(R.id.listView);

            // Create an ArrayAdapter with the options
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
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

        sendSpeed.setOnClickListener(v -> {
            try {
                if (outputStream != null) {
                    String newSpeed = String.valueOf(speed.getText()) + '\n';
                    outputStream.write(newSpeed.getBytes());
                    outputStream.flush();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "New Speed: " + speed.getText(), Toast.LENGTH_SHORT).show());
                } else {
                    Log.e("MSG", "Output stream is null");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
                }
                runOnUiThread(() -> {
                    status.setTextColor(Color.RED);
                    status.setText("Status: Not Connected");
                    Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show();
                });

            } catch (IOException e) {
                Log.e("MSG", "Failed to send command", e);
                e.fillInStackTrace();
            }
        });

        status = findViewById(R.id.status);
        ImageButton selfDrive = findViewById(R.id.selfDrive);
        selfDrive.setOnClickListener(v -> sendCommand(BluetoothInfo.COMMAND_SELF_DRIVE));
    }

    @SuppressLint("SetTextI18n")
    private void sendCommand(char command) {
        try {
            if (outputStream != null) {
                outputStream.write(command);
                outputStream.flush(); // Flush the output stream
            } else {
                Log.e("MSG", "Output stream is null");
                status.setText("Status: Not Connected");
                status.setTextColor(Color.RED);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
            }
        } catch (IOException e) {
            Log.e("MSG", "Failed to send command", e);
            e.fillInStackTrace();
            status.setText("Status: Not Connected");
            status.setTextColor(Color.RED);
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
        }
    }

    void connectToDevice(String address) {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                BluetoothDevice device = BluetoothInfo.bluetoothAdapter.getRemoteDevice(address);
                socket = device.createRfcommSocketToServiceRecord(BluetoothInfo.uuid);
                socket.connect();
                outputStream = socket.getOutputStream();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show();
                    status.setText("Status: Connected");
                    status.setTextColor(Color.GREEN);
                });

            } catch (IOException e) {
                e.fillInStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Failed to connect to device", Toast.LENGTH_LONG).show();
                    status.setText("Status: Not Connected");
                    status.setTextColor(Color.RED);
                });
            }
        }).start();
    }

    @SuppressLint("SetTextI18n")
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