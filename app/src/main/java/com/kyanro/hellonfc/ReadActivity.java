package com.kyanro.hellonfc;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

public class ReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Log.d("mylog", "not supported");
            finish();
            return;
        }

        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("mylog", "ndef");
        for (String tech : tag.getTechList()) {
            Log.d("mylog", "tech:" + tech);
        }

        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            Log.d("mylog", "ndef message:" + ndef.getNdefMessage().toString());
            String type = ndef.getType();
            Log.d("mylog", "type:" + type);
            NdefMessage ndefMessage = ndef.getNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord record : records) {
                Log.d("mylog", "mime type:" + new String(record.getType()));
                Log.d("mylog", "payload:" + new String(record.getPayload()));
                Log.d("mylog", "toString:" + record.toString());
            }
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
    }
}