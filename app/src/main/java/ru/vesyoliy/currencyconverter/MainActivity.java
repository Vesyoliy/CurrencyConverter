package ru.vesyoliy.currencyconverter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.vesyoliy.currencyconverter.ui.converter.CurrencyConverterFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment currencyConverterFragment = fragmentManager.findFragmentByTag(
                CurrencyConverterFragment.class.getSimpleName());

        if (currencyConverterFragment == null) {
            currencyConverterFragment = CurrencyConverterFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container, currencyConverterFragment,
                            CurrencyConverterFragment.class.getSimpleName())
                    .commit();
        }
    }
}
