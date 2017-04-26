package it.scoppelletti.spaceship.bluetooth.sample.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import io.reactivex.Completable;
import io.reactivex.functions.Action;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.bluetooth.BluetoothAction;
import it.scoppelletti.spaceship.bluetooth.BluetoothExt;
import it.scoppelletti.spaceship.bluetooth.sample.R;
import it.scoppelletti.spaceship.io.IOExt;

@Slf4j
public final class Printer implements Action, BluetoothAction {
    private final String myDeviceAddress;
    private final String myBody;
    private final UUID myServiceID;
    private String myDeviceName;

    private Printer(Context ctx, String deviceAddress, String body) {
        myDeviceAddress = deviceAddress;
        myBody = body;
        myServiceID = UUID.fromString(ctx.getString(R.string.service_key));
    }

    public static Completable newInstance(@NonNull Context ctx,
            @NonNull String deviceAddress, @NonNull String body) {
        Printer printer;

        if (ctx == null) {
            throw new NullPointerException("Argument ctx is null.");
        }
        if (TextUtils.isEmpty(deviceAddress)) {
            throw new NullPointerException("Argument deviceAddress is null.");
        }
        if (TextUtils.isEmpty(body)) {
            throw new NullPointerException("Argument body is null.");
        }

        printer = new Printer(ctx.getApplicationContext(), deviceAddress, body);
        return Completable.fromAction(printer);
    }

    @Override
    public void run() throws Exception {
        BluetoothExt.run(this);
    }

    @Override
    public void run(@NonNull BluetoothAdapter adapter) {
        BluetoothDevice device;
        BluetoothSocket clientSocket;

        try {
            device = adapter.getRemoteDevice(myDeviceAddress);
        } catch (RuntimeException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_btDeviceNotFound)
                    .messageArguments(myDeviceAddress)
                    .cause(ex).build();
        }
        if (device == null) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_btDeviceNotFound)
                    .messageArguments(myDeviceAddress).build();
        }

        myDeviceName = BluetoothExt.getName(device);

        try {
            clientSocket = device.createRfcommSocketToServiceRecord(
                    myServiceID);
        } catch (IOException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_btCreateSocket)
                    .messageArguments(myDeviceName, myServiceID)
                    .cause(ex).build();
        }

        try {
            print(clientSocket);
        } finally {
            clientSocket = IOExt.close(clientSocket);
        }
    }

    private void print(BluetoothSocket clientSocket) {
        OutputStream out;
        BufferedWriter writer;

        try {
            clientSocket.connect();
        } catch (IOException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_btConnectToSocket)
                    .messageArguments(myDeviceName, myServiceID)
                    .cause(ex).build();
        }

        try {
            out = clientSocket.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(myBody);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_btWriteToSocket)
                    .messageArguments(myDeviceName, myServiceID)
                    .cause(ex).build();
        }
    }

    @Override
    public void onDisabled() {
        throw new ApplicationException.Builder(
                R.string.it_scoppelletti_err_btNotEnabled).build();
    }

    @Override
    public void onNotSupported() {
        throw new ApplicationException.Builder(
                R.string.it_scoppelletti_err_btNotSupported).build();
    }
}
