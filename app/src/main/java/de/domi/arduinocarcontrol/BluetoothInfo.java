package de.domi.arduinocarcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class BluetoothInfo {
    protected static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    protected static final BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:23:09:01:3A:1B");
    protected static final char COMMAND_FORWARD = 'F';
    protected static final char COMMAND_BACKWARD = 'B';
    protected static final char COMMAND_LEFT = 'L';
    protected static final char COMMAND_RIGHT = 'R';
    protected static final char COMMAND_FORWARD_LEFT = 'G';
    protected static final char COMMAND_FORWARD_RIGHT = 'H';
    protected static final char COMMAND_BACKWARD_LEFT = 'I';
    protected static final char COMMAND_BACKWARD_RIGHT = 'J';
    protected static final char COMMAND_ROTATE_LEFT = 'C';
    protected static final char COMMAND_ROTATE_RIGHT = 'D';
    protected static final char COMMAND_STOP = 'S';

}