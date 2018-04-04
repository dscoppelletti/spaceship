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

package it.scoppelletti.spaceship.bluetooth.preference;

import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import it.scoppelletti.spaceship.bluetooth.BluetoothAction;
import it.scoppelletti.spaceship.bluetooth.BluetoothExt;
import it.scoppelletti.spaceship.bluetooth.R;

/**
 * Selects a bonded Bluetooth device.
 *
 * @since 1.0.0
 */
public final class BluetoothDeviceListPreference extends ListPreference {

    /**
     * Constructor.
     *
     * @param ctx   The context.
     * @param attrs The attributes. May be {@code null}.
     */
    public BluetoothDeviceListPreference(@NonNull Context ctx,
            @Nullable AttributeSet attrs) {
        super(ctx, attrs);

        BluetoothExt.run(new BluetoothDeviceListPreference.ListBondedDevices());
    }

    @Override
    public void onAttached() {
        super.onAttached();
        setSummary();
    }

    @Override
    protected void onClick() {
        // The user may enable or disable Bluetooth after opening the settings
        // activity:
        // Reload the paired devices.
        BluetoothExt.run(new BluetoothDeviceListPreference.ListBondedDevices());

        super.onClick();
    }

    @Override
    protected void notifyChanged() {
        super.notifyChanged();
        setSummary();
    }

    /**
     * Sets the summary.
     */
    private void setSummary() {
        CharSequence entry;

        entry = getEntry();
        if (TextUtils.isEmpty(entry)) {
            setSummary(getContext().getString(
                    R.string.it_scoppelletti_bluetooth_lbl_noDeviceSelected));
        } else {
            setSummary(entry);
        }
    }

    /**
     * Loads the paired devices.
     */
    private final class ListBondedDevices implements BluetoothAction {

        /**
         * Sole constructor.
         */
        ListBondedDevices() {
        }

        @Override
        public void run(@NonNull BluetoothAdapter adapter) {
            int i, n;
            CharSequence[] entries, entryValues;
            Set<BluetoothDevice> devices;

            devices = adapter.getBondedDevices();
            n = devices.size();
            if (n > 0) {
                entries = new CharSequence[n];
                entryValues = new CharSequence[n];
                i = 0;
                for (BluetoothDevice device : devices) {
                    entries[i] = BluetoothExt.getName(device);
                    entryValues[i] = device.getAddress();
                    i++;
                }
            } else {
                entries = new CharSequence[1];
                entryValues = new CharSequence[1];

                entries[0] = getContext().getText(
                        R.string.it_scoppelletti_bluetooth_lbl_noDevicePaired);
                entryValues[0] = "";
            }

            setEntries(entries);
            setEntryValues(entryValues);
        }

        @Override
        public void onDisabled() {
            CharSequence[] entries, entryValues;

            entries = new CharSequence[1];
            entryValues = new CharSequence[1];

            entries[0] = getContext().getText(
                    R.string.it_scoppelletti_bluetooth_lbl_notEnabled);
            entryValues[0] = "";

            setEntries(entries);
            setEntryValues(entryValues);
        }

        @Override
        public void onNotSupported() {
            CharSequence[] entries, entryValues;

            entries = new CharSequence[1];
            entryValues = new CharSequence[1];

            entries[0] = getContext().getText(
                    R.string.it_scoppelletti_bluetooth_lbl_notSupported);
            entryValues[0] = "";

            setEntries(entries);
            setEntryValues(entryValues);
        }
    }
}
