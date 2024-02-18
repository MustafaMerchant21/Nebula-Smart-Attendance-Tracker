package com.nebula.NebulaApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HomeFragment extends Fragment {
    String studentInstituteSecreteCode = "GGSP001a9b9ca8da91"; // Fetch institute secret code of student inside this var;
    public static class SHAEncoding
    {
        public byte[] obtainSHA(String s) throws NoSuchAlgorithmException
        {
            MessageDigest msgDgst = MessageDigest.getInstance("SHA-512");
            return msgDgst.digest(s.getBytes(StandardCharsets.UTF_8));
        }

        public String toHexStr(byte[] hash)
        {
            BigInteger no = new BigInteger(1, hash);
            StringBuilder hexStr = new StringBuilder(no.toString(16));
            while (hexStr.length() < 32) hexStr.insert(0, '0');
            return hexStr.toString();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home,container,false);
        ImageButton scanQr = v.findViewById(R.id.scanQrCode);

        scanQr.setOnClickListener(v1 -> {
            IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
            intentIntegrator.setPrompt("Scan the QR Code");
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.initiateScan();
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then toast a message as "cancelled" --->
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(requireActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else
            { // if the intentResult is not null we'll set the content and format of scan message --->
                String urlData = intentResult.getContents();
                String studentInstituteSecreteCodeEncoded;
                SHAEncoding sha = new SHAEncoding();
                try {
                    studentInstituteSecreteCodeEncoded = sha.toHexStr(sha.obtainSHA(studentInstituteSecreteCode));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("\n" + studentInstituteSecreteCode + " : " + studentInstituteSecreteCodeEncoded);
                if (urlData.equals(studentInstituteSecreteCodeEncoded)){
                    // perform attendance marking process here --->
                    Toast.makeText(requireActivity().getApplicationContext(),"Attendance Marked! 🥳",Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}