package pk.mohammadadnan.customdialerapp;

import static android.content.ContentValues.TAG;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.telecom.Call;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class CallManager extends AppCompatActivity {

    private static final String TAG = "CallManager";

    private static BehaviorSubject subject;
    private static Call currentCall = null;
    public static CallManager INSTANCE;
    private static MediaRecorder recorder;
    private static String savedNumber;
    private static boolean recordStarted = false;
    static long annonceDuration;
    static MediaPlayer mPlayer = new MediaPlayer();



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
            String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/RepTel/Annonce.3gp";
//            try {
//                mPlayer.setDataSource(mFileName);
//                mPlayer.prepare();
//                mPlayer.start();
//                annonceDuration = mPlayer.getDuration(); //resultat en millis
//                Log.i(TAG, "annonceDuration: " + annonceDuration);
//                Log.i(TAG, "acceptCall: Media player is playing file = " + mFileName + " annonce duration = " + annonceDuration);
//            } catch (IOException e) {
//                Log.i(TAG, "playAudio: failed");
//             }
        }

        Log.i(TAG, "acceptCall: juste avant le demarrage du countdown");
        new CountDownTimer(4000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {startCallRecording();}
            @Override
            public void onFinish() {} }.start();

    }

    private static void startCallRecording() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        String time =  dateFormat.format(new Date()) ;

        Log.i(TAG, "startCallRecording: je commence l'enregistrement");

        final Handler handler = new Handler();
        recorder = new MediaRecorder();
        //recorder.setAudioSamplingRate(8000);
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.OGG);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RepTel/M" + time);

        Log.i(TAG, "enregistrement : " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/RepTel/M" + time);
        Log.i(TAG, "enregistrement de l'appel dans : " + recorder.toString() );

        try {
            recorder.prepare();
            Log.i(TAG, "recorder.prepare");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.i(TAG, "catch illegalStateException: " + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "catch IOException: " + e);
        }
        Log.i(TAG, "recorder.start");
        recorder.start();
        recordStarted = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recorder.stop();
                recorder.release();
                recordStarted=false;
                Log.i(TAG, "run: recorder stop");
            }
        }, 30000);

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
