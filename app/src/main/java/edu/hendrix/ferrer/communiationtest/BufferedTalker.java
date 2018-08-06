package edu.hendrix.ferrer.communiationtest;

import android.util.Log;

/**
 * Created by gabriel on 8/5/18.
 */

public class BufferedTalker {
    private byte[] received;
    private int totalReceived = 0;

    private static final String TAG = BufferedTalker.class.getSimpleName();

    public BufferedTalker(int expectedBytes) {
        received = new byte[expectedBytes];
    }

    public boolean finished() {
        return totalReceived == received.length;
    }

    public void updateWith(int bytesReceived, byte[] buffer) {
        if (bytesReceived > 0) {
            for (int i = 0; i < bytesReceived; i++) {
                received[totalReceived] = buffer[i];
                totalReceived += 1;
            }
            Log.i(TAG, "Received " + totalReceived + "/" + buffer.length);
        } else {
            Log.i(TAG, "Trouble with receive: code " + bytesReceived);
        }
    }

    public byte[] getReceived() {
        byte[] bytes = new byte[received.length];
        for (int i = 0; i < received.length; i++) {
            bytes[i] = received[i];
        }
        return bytes;
    }
}
