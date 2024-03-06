package de.domi.arduinocarcontrol;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private BluetoothSocket socket;
    private OutputStream outputStream;

    // Define constants for commands
    private static final char COMMAND_FORWARD = 'F';
    private static final char COMMAND_BACKWARD = 'B';
    private static final char COMMAND_LEFT = 'L';
    private static final char COMMAND_RIGHT = 'R';
    private static final char COMMAND_FORWARD_LEFT = 'G';
    private static final char COMMAND_FORWARD_RIGHT = 'H';
    private static final char COMMAND_BACKWARD_LEFT = 'I';
    private static final char COMMAND_BACKWARD_RIGHT = 'J';
    private static final char COMMAND_ROTATE_LEFT = 'C';
    private static final char COMMAND_ROTATE_RIGHT = 'D';
    private static final char COMMAND_STOP = 'S';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:23:09:01:3A:1B");

        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                outputStream = socket.getOutputStream();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show());
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to connect to device", Toast.LENGTH_LONG).show());
            }
        }).start();

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

        forwardButton.setOnClickListener(v -> sendCommand(COMMAND_FORWARD));
        backwardButton.setOnClickListener(v -> sendCommand(COMMAND_BACKWARD));
        leftButton.setOnClickListener(v -> sendCommand(COMMAND_LEFT));
        rightButton.setOnClickListener(v -> sendCommand(COMMAND_RIGHT));
        forwardLeftButton.setOnClickListener(v -> sendCommand(COMMAND_FORWARD_LEFT));
        forwardRightButton.setOnClickListener(v -> sendCommand(COMMAND_FORWARD_RIGHT));
        backwardLeftButton.setOnClickListener(v -> sendCommand(COMMAND_BACKWARD_LEFT));
        backwardRightButton.setOnClickListener(v -> sendCommand(COMMAND_BACKWARD_RIGHT));
        rotateLeftButton.setOnClickListener(v -> sendCommand(COMMAND_ROTATE_LEFT));
        rotateRightButton.setOnClickListener(v -> sendCommand(COMMAND_ROTATE_RIGHT));
        stopButton.setOnClickListener(v -> sendCommand(COMMAND_STOP));
    }

    private void sendCommand(char command) {
        try {
            if (outputStream != null) {
                outputStream.write(command);
                outputStream.flush(); // Flush the output stream
                Toast.makeText(this, "Command sent: " + command, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("MSG", "Output stream is null");
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
            }
        } catch (IOException e) {
            Log.e("MSG", "Failed to send command", e);
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to send command", Toast.LENGTH_LONG).show());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
