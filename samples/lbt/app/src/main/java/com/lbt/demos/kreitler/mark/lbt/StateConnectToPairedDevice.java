package com.lbt.demos.kreitler.mark.lbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.lbt.demos.kreitler.mark.demoUI.Widget;
import com.lbt.demos.kreitler.mark.demoUI.WidgetButton;
import com.lbt.demos.kreitler.mark.demoUI.WidgetLabel;
import com.lbt.demos.kreitler.mark.states.GameState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Mark on 11/7/2016.
 */

public class StateConnectToPairedDevice extends GameState {
    public static String ID = "ConnectToPairedDevice";

    private WidgetLabel labelTitle              = null;
    private BluetoothAdapter BA                 = null;
    private Set<BluetoothDevice> pairedDevices  = null;

    public StateConnectToPairedDevice(BluetoothAdapter BA) {
        super(ID);

        this.BA = BA;
    }

    @Override
    public void enter() {
        ArrayList<CanvasView.Placeable> buttons = new ArrayList<CanvasView.Placeable>();

        Widget.RemoveAll();

        labelTitle = new WidgetLabel(null, 0, 0, "WidgetLabel", WidgetLabel.DEFAULT_TEXT_SIZE, "yellow", "arial");

        if (BA != null) {
            pairedDevices = BA.getBondedDevices();

            Iterator<BluetoothDevice> iDevice = pairedDevices.iterator();
            while(iDevice.hasNext()) {
                BluetoothDevice btDevice = iDevice.next();

                WidgetButton wb = new WidgetButton(null, 0, 0, 150, 30, 0.5f, 0.5f, btDevice.getName(), "arial");
                assert(wb != null);

                buttons.add(wb);
                wb.setUserData(btDevice.getAddress());
            }

            CanvasView.PlacePlaceables(buttons, 0.25f, 0.1f, 0.5f, true);

            labelTitle.setColor("yellow", "black");
            labelTitle.setText("Choose a device");
            CanvasView.PlacePlaceable(labelTitle, 0.5f, 0.1f);
        }
        else {
            labelTitle.setColor("red", "black");
            labelTitle.setText("NO DEVICES FOUND!");
            CanvasView.PlacePlaceable(labelTitle, 0.5f, 0.5f);
        }

    }

    public void onPressed(WidgetButton button) {
        // TODO: when button is pressed, transition to 'connecting' state.
    }

    // Implementation //////////////////////////////////////////////////////////////////////////////
}
