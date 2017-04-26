/*
 * Copyright (C) 2014-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.spaceship.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.ApplicationException;

/**
 * Operations on Bluetooth.
 *
 * @since 1.0.0
 */
@Slf4j
public final class BluetoothExt {

    /**
     * Private constructor for static class.
     */
    private BluetoothExt() {
    }

    /**
     * Returns the name of a device.
     *
     * @param  device The device.
     * @return        The name of the device. If not defined, returns the
     *                address of the device.
     */
    @NonNull
    public static String getName(@NonNull BluetoothDevice device) {
        String s;

        if (device == null) {
            throw new NullPointerException("Argument device is null.");
        }

        s = device.getName();
        if (TextUtils.isEmpty(s)) {
            s = device.getAddress();
        }

        return s;
    }

    /**
     * Returns whether Bluetooth is enabled or disabled. If Bluetooth is not
     * enabled, launches an activity for prompting user if he would like enable
     * it.
     *
     * @param  activity The activity.
     * @param  reqCode  The request code.
     * @return          Returns {@code true} if Bluetooth is enabled,
     *                  {@code false} otherwise.
     */
    public static boolean enable(Activity activity, int reqCode) {
        Intent intent;
        BluetoothAdapter adapter;

        if (activity == null) {
            throw new NullPointerException("Argument activity is null.");
        }

        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_btNotSupported)
                    .build();
        }

        if (adapter.isEnabled()) {
            return true;
        }

        intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, reqCode);
        return false;
    }

    /**
     * Executes a Bluetooth action.
     *
     * @param action The action.
     */
    public static void run(@NonNull BluetoothAction action) {
        BluetoothAdapter adapter;

        if (action == null) {
            throw new NullPointerException("Argument action is null.");
        }

        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            myLogger.warn("Bluetooth not supported.");
            action.onNotSupported();
            return;
        }

        if (!adapter.isEnabled()) {
            myLogger.warn("Bluetooth disabled.");
            action.onDisabled();
            return;
        }

        try {
            BluetoothExt.cancelDiscovery(adapter);
            action.run(adapter);
        } finally {
            BluetoothExt.cancelDiscovery(adapter);
        }
    }

    /**
     * Cancels the pending discovery of Bluetooth devices.
     *
     * @param  adapter The adapter. May be {@code null}.
     */
    private static void cancelDiscovery(BluetoothAdapter adapter) {
        if (adapter == null) {
            return;
        }

        if (adapter.isDiscovering()) {
            if (adapter.cancelDiscovery()) {
                myLogger.debug(
                        "Discovery of Bluetooth devices has been cancelled.");
            } else {
                myLogger.warn(
                        "Failed to cancel discovery of Bluetooth devices.");
            }
        }
    }
}
