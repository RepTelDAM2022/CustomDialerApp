package pk.mohammadadnan.customdialerapp;

import static android.content.ContentValues.TAG;

import android.media.MediaPlayer;
import android.os.Environment;
import android.telecom.Call;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class CallManager {

    private static final String TAG = "CallManager";

    private static BehaviorSubject subject;
    private static Call currentCall = null;
    public static CallManager INSTANCE;
    private static String mFileName = null;



    public static Observable updates() {
        BehaviorSubject behaviorSubject = subject;
        return (Observable)behaviorSubject;
    }

    public static void updateCall(@Nullable Call call) {
        currentCall = call;
        if (call != null) {
            subject.onNext(MappersJava.toGsmCall(call));
        }

    }

    public static void cancelCall() {
        Call call = currentCall;
        if (call != null) {
            if (call.getState() == Call.STATE_RINGING) {
                INSTANCE.rejectCall();
            } else {
                INSTANCE.disconnectCall();
            }
        }

    }

    public static void acceptCall() {
        Call call = currentCall;
        if (call != null) {
            call.answer(call.getDetails().getVideoState());
            Log.i(TAG, "acceptCall: je suis dans le accept call");
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/RepTel/Annonce.3gp";
            MediaPlayer mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();;
                mPlayer.start();
                Log.i(TAG, "acceptCall: Media player is playing");
            } catch (IOException e) {
                Log.i(TAG, "playAudio: failed");
            }



        }

    }

    private static void rejectCall() {
        Call call = currentCall;
        if (call != null) {
            call.reject(false, "");
        }

    }

    private static void disconnectCall() {
        Call call = currentCall;
        if (call != null) {
            call.disconnect();
        }

    }

    static {
        CallManager var0 = new CallManager();
        INSTANCE = var0;
        subject = BehaviorSubject.create();
    }
}
