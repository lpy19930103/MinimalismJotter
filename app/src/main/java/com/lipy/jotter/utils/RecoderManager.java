package com.lipy.jotter.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.SeekBar;

import com.lipy.jotter.constants.Constant;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音频管理
 * Created by lipy on 2017/4/7.
 */
public class RecoderManager {
    static SeekBar seekBar;
    static long playTime;
    private static boolean isPlaying = false;

    public static void startRecord(MediaRecorder recorder, String path) {
        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(path);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                recorder.prepare();
            } catch (IllegalStateException e) {
                Logger.INSTANCE.e("IllegalStateException called");
            } catch (IOException e) {
                Logger.INSTANCE.e("record prepare() failed");
            }
            recorder.start();
        }
    }

    public static void stopRecord(MediaRecorder recorder) {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public static void voicePlay(final MediaPlayer player, String path, final SeekBar seekBar, final Chronometer chronometer) {
        if (player != null) {
            setChronometer(chronometer, Constant.CHRONOMETER_START);
            if (!isPlaying) {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(path);
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = true;
            }


            player.start();

            //设置Bar的最大值
            int max = player.getDuration();
            seekBar.setMax(max);
            //定时器更新进度条
            final Timer timer = new Timer();
            TimerTask timeTask = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    int a = player.getCurrentPosition();
                    seekBar.setProgress(player.getCurrentPosition());
                }
            };
            timer.schedule(timeTask, 0, 500);

        /*player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                player.start();
                seekBar.setMax(player.getDuration());
                seekBar.setEnabled(true);
            }
        });*/

            //音频播放完之后重新设置显示
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    seekBar.setProgress(0);
                    setChronometer(chronometer, Constant.CHRONOMETER_RESET);
                    timer.cancel();
                }
            });
        }
    }

    public static void voicePause(MediaPlayer player, Chronometer chronometer) {
        // 判断音乐是否在播放
        if (player != null && player.isPlaying()) {
            setChronometer(chronometer, Constant.CHRONOMETER_STOP);
            // 暂停音乐播放器
            player.pause();
        }
    }

    private static void setChronometer(Chronometer chronometer, int mode) {
        switch (mode) {
            case Constant.CHRONOMETER_START:
                if (playTime != 0) {
                    chronometer.setBase(chronometer.getBase() +
                            (SystemClock.elapsedRealtime() - playTime));
                } else {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                }
                chronometer.start();
                break;
            case Constant.CHRONOMETER_STOP:
                chronometer.stop();
                playTime = SystemClock.elapsedRealtime();
                break;
            case Constant.CHRONOMETER_RESET:
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                playTime = 0;
                break;
            default:
                break;
        }
    }
}
