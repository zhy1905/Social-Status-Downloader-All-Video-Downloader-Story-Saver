package ban.rossyn.util;

import android.content.Context;

import androidx.annotation.Keep;

@Keep
public abstract class CPU {
    public static native int getCheckRes(Context context, int i);

    public static native String getClock(Context context, byte[] bArr, int i);

    public static native String getMagic(Context context, int i);

    static {
        System.loadLibrary("core");
    }

    public static synchronized String getMagicData(Context context, int i) {
        Class cls = CPU.class;
        synchronized (cls) {
            synchronized (cls) {
                synchronized (cls) {
                    return getMagic(context, i);
                }
            }
        }

    }

    public static synchronized String getClockData(Context context, byte[] bArr, int i) {
        Class cls = CPU.class;
        synchronized (cls) {
            synchronized (cls) {
                synchronized (cls) {
                    return getClock(context, bArr, i);
                }
            }
        }
    }

    public static synchronized int getCheckResData(Context context, int i) {
        Class cls = CPU.class;
        synchronized (cls) {
            synchronized (cls) {
                synchronized (cls) {
                    return getCheckRes(context, i);
                }
            }
        }

    }
}