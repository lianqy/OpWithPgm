package com.example.lianqy.test;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DataInputStream in;
    private int i;
    //private ImageView pit = (ImageView)findViewById(R.id.pgm);
    private final String[] url = {"test_map.pgm", "map.pgm", "tb_condo_2.pgm", "willow-full.pgm"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button)findViewById(R.id.btn);
        final ImageView pit = (ImageView)findViewById(R.id.pgm);
        i = 1;

        try {
            FileInputStream file = new FileInputStream(Environment.getExternalStorageDirectory() + "/data/test_map.pgm");
            in = new DataInputStream(file);
            Bitmap temp = decodePgmBitmap(in);
            pit.setImageBitmap(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileInputStream file = new FileInputStream(Environment.getExternalStorageDirectory() + "/data/" + url[i]);
                    in = new DataInputStream(file);
                    Bitmap temp = decodePgmBitmap(in);
                    pit.setImageBitmap(temp);
                    if (i < 3) {
                        i ++;
                    } else {
                        i = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public static Bitmap decodePgmBitmap(InputStream is) throws IOException {
        DataInputStream in = new DataInputStream(is);
        Log.d("TAG", in.toString());
        Bitmap img;
        char ch0 = (char) in.readByte();
        char ch1 = (char) in.readByte();
        if (ch0 == 'P') {
            Log.i("-------------", "p" + " [0]=" + ch0 + ", [1]=" + ch1);
        }
        if (ch0 != 'P') {
            Log.i("-------------", "Not a pgm image!" + " [0]=" + ch0 + ", [1]=" + ch1);
            //System.exit(0);
        }
        if (ch1 != '2' && ch1 != '5') {
            Log.i("-------------", "Not a pgm image!" + " [0]=" + ch0 + ", [1]=" + ch1);
            //System.exit(0);
        }
        in.readByte();                  //读空格
        char c = (char) in.readByte();

        if (c == '#')                    //读注释行
        {
            do {
                c = (char) in.readByte();
            } while ((c != '\n') && (c != '\r'));
            c = (char) in.readByte();
        }

        //读出宽度
        if (c < '0' || c > '9') {
            Log.d("TAG", "Error!");
        }

        int k = 0;
        do {
            k = k * 10 + c - '0';
            c = (char) in.readByte();
        } while (c >= '0' && c <= '9');
        int width = k;

        //读出高度
        c = (char) in.readByte();
        if (c < '0' || c > '9') {
            System.out.print("Errow!");
        }

        k = 0;
        do {
            k = k * 10 + c - '0';
            c = (char) in.readByte();
        } while (c >= '0' && c <= '9');
        int height = k;

        //读出灰度最大值
        c = (char) in.readByte();
        if (c < '0' || c > '9') {
            Log.i("Errow!", "灰度读取错误");
        }

        k = 0;
        do {
            k = k * 10 + c - '0';
            c = (char) in.readByte();
        } while (c >= '0' && c <= '9');
        int maxpix = k;

        int[] pixels = new int[width * height];
        Log.d("TAG", "width = " + width);
        Log.d("TAG", "height = " + height);
        for (int i = 0; i < width * width; i++) {
            int b = 0;
            try {
                b = in.readByte();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (b < 0) b = b + 256;
            pixels[i] = (255 << 24) | (b << 16) | (b << 8) | b;
        }
        double scaleRate = 1;
        Log.d("pattern", width + " " + height);

        img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        img.setPixels(pixels, 0, (int) (width), 0, 0, (int) (width * scaleRate), (int) (height * scaleRate));
        Log.d("TAG", String.valueOf(img.getByteCount()));
        return img;
    }

}
