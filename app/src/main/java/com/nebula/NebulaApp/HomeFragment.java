package com.nebula.NebulaApp;

import android.app.Dialog;
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


public class HomeFragment extends Fragment {
    private ImageButton scanQr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home,container,false);
        scanQr  =  v.findViewById(R.id.scanQrCode);

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
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(requireActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                Toast.makeText(requireActivity().getApplicationContext(), "\t\tSuccess 🥳\n"+intentResult.getContents(), Toast.LENGTH_SHORT).show();
//                showSuccessDialog();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showSuccessDialog() {
        // Create a dialog object
        Dialog dialog = new Dialog(requireContext().getApplicationContext());

// Set the dialog layout
        dialog.setContentView(R.layout.scan_success_dialog);
// Show the dialog
        dialog.show();

    }

}