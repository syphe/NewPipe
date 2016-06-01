package org.schabi.newpipe.cast;

import android.content.Context;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.command.ServiceCommandError;

import java.util.List;

/**
 * Created by SimonFi on 1/06/2016.
 */
public class CastListener implements ConnectableDeviceListener {
    private static CastListener sInstance;
    private DiscoveryManager mDiscoveryManager;
    private ConnectableDevice mCurrentDevice;

    private CastListener() {
    }

    public static CastListener getInstance() {
        if (sInstance == null) {
            sInstance = new CastListener();
        }
        return sInstance;
    }

    public void init(Context context) {
        DiscoveryManager.init(context);

        mDiscoveryManager = DiscoveryManager.getInstance();
        mDiscoveryManager.start();
    }

    @Override
    public void onDeviceReady(ConnectableDevice device) {

    }

    @Override
    public void onDeviceDisconnected(ConnectableDevice device) {

    }

    @Override
    public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {

    }

    @Override
    public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {

    }

    @Override
    public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {

    }
}
