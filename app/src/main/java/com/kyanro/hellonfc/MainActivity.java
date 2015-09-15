package com.kyanro.hellonfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    IntentFilter[] mIntentFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mIntentFilters = new IntentFilter[]{filter};

        Log.d("mylog", "onCreate end");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            return;
        }

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        List<String> techList = Arrays.asList(tag.getTechList());
        if (techList.contains(Ndef.class.getName())) {
            Log.d("mylog", "ndef");
            StringBuilder idm = new StringBuilder();

            for (byte b : tag.getId()) {
                idm.append(Integer.toHexString(b & 0xff));
            }
            Log.d("mylog", "idm:" + idm.toString());
            for (String tech : techList) {
                Log.d("mylog", "tech:" + tech);
            }

            Ndef ndef = Ndef.get(tag);
            try {
                final String mimeType = "text/x.com.kyanro.game.carry-the-package";
                final String payload = "carry the package!";
                NdefRecord record =
                        new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeType.getBytes(), new byte[0], payload.getBytes());
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                ndef.connect();
                Log.d("mylog", "max size:" + ndef.getMaxSize());
                Log.d("mylog", "msg size:" + msg.toByteArray().length);
                if (msg.toByteArray().length > ndef.getMaxSize()) {
                    Log.d("mylog", "too large message");
                }
                ndef.writeNdefMessage(msg);

            } catch (IOException | FormatException e) {
                e.printStackTrace();
                Log.d("mylog", "cant read.");
            } finally {
                try {
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (techList.contains(NdefFormatable.class.getName())) {
            Log.d("mylog", "ndef formattable");
        } else {
            Log.d("mylog", "this tag is not supported");
        }
    }
}
