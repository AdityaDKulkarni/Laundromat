package com.laundry.laundry.NFC;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;
import android.widget.Toast;

import com.laundry.laundry.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Aditya Kulkarni
 */

public class NFCHandler {
    private NfcAdapter nfcAdapter;
    private Context context;
    private String TAG = getClass().getSimpleName();

    public NFCHandler(NfcAdapter nfcAdapter, Context context) {
        this.nfcAdapter = nfcAdapter;
        this.context = context;
    }

    public boolean isNfcEnabled() {
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Log.d(TAG, context.getString(R.string.nfc_is_enabled));
            return true;
        }
        return false;
    }

    public NfcAdapter getNfcAdapter() {
        return nfcAdapter;
    }

    public void formatTheTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Log.d(TAG, context.getString(R.string.tag_not_ndef_formattable));
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(context, context.getString(R.string.tag_written), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToTag(Tag tag, NdefMessage ndefMessage) {
        try {

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                formatTheTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Log.d(TAG, context.getString(R.string.tag_not_writeable));
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(context, context.getString(R.string.tag_written), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | FormatException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.write_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public NdefRecord createRecord(String data) {
        try {

            byte[] language;

            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = data.getBytes("UTF-8");
            final int languageLength = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payloadStream =
                    new ByteArrayOutputStream(1 + languageLength + textLength);

            payloadStream.write((byte) (languageLength & 0x1F));
            payloadStream.write(language, 0, languageLength);
            payloadStream.write(text, 0, textLength);

            NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT,
                    new byte[0],
                    payloadStream.toByteArray());

            return ndefRecord;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public NdefMessage createMessage(String data) {

        NdefRecord ndefRecord = createRecord(data);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }

    public String getNdefRecord(NdefRecord ndefRecord) {
        String data = null;

        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            data = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public String getTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] records = ndefMessage.getRecords();

        if (records != null && records.length > 0) {
            Log.d(TAG, getNdefRecord(records[0]));
            return getNdefRecord(records[0]);
        }

        return null;
    }

    public String getNfcId(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString().toUpperCase();
    }
}
