package org.schabi.newpipe.cast;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.connectsdk.core.MediaInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.discovery.CapabilityFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;

import org.schabi.newpipe.extractor.StreamInfo;
import org.schabi.newpipe.extractor.VideoStream;

import java.util.List;

/**
 * Created by SimonFi on 1/06/2016.
 */
public class Caster implements ConnectableDeviceListener {
    private static final String TAG = "Caster";
    private static Caster sInstance;
    private DiscoveryManager mDiscoveryManager;
    private ConnectableDevice mCurrentDevice;
    private LaunchSession mLaunchSession;
    private MediaControl mMediaControl;
    private boolean mIsPlaying;

    private Caster() {
    }

    public static Caster getInstance() {
        if (sInstance == null) {
            sInstance = new Caster();
        }
        return sInstance;
    }

    public ConnectableDevice getCurrentDevice() {
        return mCurrentDevice;
    }

    public void setCurrentDevice(ConnectableDevice device) {
        mCurrentDevice = device;
        mCurrentDevice.addListener(this);
        mCurrentDevice.connect();
    }

    public boolean getIsPlaying() {
        return mIsPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        mIsPlaying = isPlaying;
    }

    public void init(Context context) {
        DiscoveryManager.init(context);

        CapabilityFilter videoFilter = new CapabilityFilter(
                MediaPlayer.Display_Video,
                MediaControl.Any,
                VolumeControl.Volume_Up_Down
        );

        mDiscoveryManager = DiscoveryManager.getInstance();
        //mDiscoveryManager.setCapabilityFilters(videoFilter);
        mDiscoveryManager.start();
    }

    public void playVideo(final Fragment activity, final StreamInfo info, VideoStream videoStream) {
        if (mCurrentDevice == null) {
            return;
        }

        String iconURL = "http://www.connectsdk.com/files/2013/9656/8845/test_image_icon.jpg"; // credit: sintel-durian.deviantart.com
        String mimeType = "video/mp4"; // audio/* for audio files

        /*SubtitleInfo subtitles = null;
        if (getTv().hasCapability(MediaPlayer.Subtitle_WebVTT)) {
            subtitles = new SubtitleInfo.Builder("http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/sintel_en.vtt")
                    .setMimeType("text/vtt")
                    .setLanguage("en")
                    .setLabel("English subtitles")
                    .build();
        }*/
        MediaInfo mediaInfo = new MediaInfo.Builder(videoStream.url, mimeType)
                .setTitle(info.title)
                .setDescription(info.description)
                .setIcon(info.uploader_thumbnail_url)
                //.setSubtitleInfo(subtitles)
                .build();

        // These variables should be class fields
        // LaunchSession mLaunchSession;
        // MediaControl mMediaControl;
        // ConnectableDevice mDevice;

        MediaPlayer.LaunchListener listener = new MediaPlayer.LaunchListener() {
            @Override
            public void onSuccess(MediaPlayer.MediaLaunchObject object) {
                // save these object references to control media playback
                mLaunchSession = object.launchSession;
                mMediaControl = object.mediaControl;

                setIsPlaying(true);

                // you will want to enable your media control UI elements here
                CastMediaControlDialog dialog = new CastMediaControlDialog();
                dialog.init(info, mMediaControl);
                dialog.show(activity.getActivity().getSupportFragmentManager(), "CastMediaControlDialog");
            }

            @Override
            public void onError(ServiceCommandError error) {
                Log.d("App Tag", "Play media failure: " + error);
            }
        };

        mCurrentDevice.getMediaPlayer().playMedia(mediaInfo, false, listener);
    }

    public void stopVideo() {
        if (mMediaControl != null) {
            mMediaControl.stop(null);
        }
        setIsPlaying(false);
    }

    @Override
    public void onDeviceReady(ConnectableDevice device) {
        Log.i(TAG, "onDeviceReady: " + device.getFriendlyName());
    }

    @Override
    public void onDeviceDisconnected(ConnectableDevice device) {
        Log.i(TAG, "onDeviceDisconnected: " + device.getFriendlyName());
    }

    @Override
    public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
        Log.i(TAG, "onPairingRequired: " + device.getFriendlyName());
    }

    @Override
    public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {
        Log.i(TAG, "onCapabilityUpdated: " + device.getFriendlyName());
    }

    @Override
    public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
        Log.i(TAG, "onConnectionFailed: " + device.getFriendlyName() + ", error: " + error.getMessage());
    }
}
