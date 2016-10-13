package com.quantumgroup.quantum;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Mr. Vanson on 8/27/2016.
 */
public  class ObjectToFileUtil {
    private ObjectToFileUtil() {}

    public static void writeObject(Context context, String fileName, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.flush();
        oos.close();

        fos.close();
    }

    public static Object readObject(Context context, String fileName) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);

        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        fis.close();
        return object;
    }
}