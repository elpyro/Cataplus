package com.cataplus.facturacionycatalogospdf;

import static android.text.InputType.TYPE_CLASS_NUMBER;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;

import com.cataplus.facturacionycatalogospdf.R;

import metodos.DecimalDigitsInputFilter;

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);




        //IMPUT TYPE PARA EDITTEXTPREFERENCES
        androidx.preference.EditTextPreference editTextPreference = getPreferenceManager().findPreference("descuento");
        editTextPreference.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
                editText.setInputType(TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


            }
        });

        androidx.preference.EditTextPreference editTextPreference_impuesto = getPreferenceManager().findPreference("impuesto");
        editTextPreference_impuesto.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
                editText.setInputType(TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            }
        });

        androidx.preference.EditTextPreference editTextPreference_envio = getPreferenceManager().findPreference("envio");
        editTextPreference_envio.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
                editText.setInputType(TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            }
        });


    }

    @Override
        public void onDestroy() {
            super.onDestroy();
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getContext());
        Preferencias_app.obtener_preferencias(preferencias,getContext());
    }

}