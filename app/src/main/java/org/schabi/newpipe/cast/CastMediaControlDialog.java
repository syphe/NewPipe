package org.schabi.newpipe.cast;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.StreamInfo;

/**
 * Created by simon on 1/06/16.
 */
public class CastMediaControlDialog extends DialogFragment {
    private View mView;
    private StreamInfo mInfo;
    private MediaControl mMediaControl;
    private Caster mCaster;

    public void init(StreamInfo info, MediaControl mediaControl) {
        mInfo = info;
        mMediaControl = mediaControl;
        mCaster = Caster.getInstance();
    }

    /*@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Dialog dialog = builder.setTitle("Casting: " + mInfo.title)
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_cast_control, null))
                .create();

        return dialog;
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.dialog_cast_control, container, false);

            ImageButton playPauseButton = (ImageButton)mView.findViewById(R.id.play_pause);
            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCaster.getIsPlaying()) {
                        mMediaControl.pause(new CastMediaControlResponseListener("Pausing"));
                        mCaster.setIsPlaying(false);
                    }
                    else {
                        mMediaControl.play(new CastMediaControlResponseListener("Playing"));
                        mCaster.setIsPlaying(true);
                    }
                }
            });

            ImageButton closeButton = (ImageButton)mView.findViewById(R.id.stop);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCaster.stopVideo();
                    dismiss();
                }
            });
        }

        return mView;
    }
}

class CastMediaControlResponseListener implements ResponseListener<Object> {
    String mAction;

    public CastMediaControlResponseListener(String action) {
        mAction = action;
    }

    @Override
    public void onSuccess(Object object) {
        Log.i("CastMediaControlDialog", mAction + ": onSuccess");
    }

    @Override
    public void onError(ServiceCommandError error) {
        Log.i("CastMediaControlDialog", mAction + ": onError: " + error.getMessage());
    }
}
