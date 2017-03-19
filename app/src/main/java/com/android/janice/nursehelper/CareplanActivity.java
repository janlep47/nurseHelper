package com.android.janice.nursehelper;


import android.os.Bundle;

/**
 * Created by janicerichards on 3/19/17.
 */

public class CareplanActivity extends net.sf.andpdf.pdfviewer.PdfViewerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public int getPreviousPageImageResource() { return R.drawable.left_arrow; }
    public int getNextPageImageResource() { return R.drawable.right_arrow; }
    public int getZoomInImageResource() { return R.drawable.zoom_in; }
    public int getZoomOutImageResource() { return R.drawable.zoom_out; }
    public int getPdfPasswordLayoutResource() { return R.layout.pdf_file_password; }
    public int getPdfPageNumberResource() { return R.layout.dialog_pagenumber; }
    public int getPdfPasswordEditField() { return R.id.etPassword; }
    public int getPdfPasswordOkButton() { return R.id.btOK; }
    public int getPdfPasswordExitButton() { return R.id.btExit; }
    public int getPdfPageNumberEditField() { return R.id.pagenum_edit; }
}
