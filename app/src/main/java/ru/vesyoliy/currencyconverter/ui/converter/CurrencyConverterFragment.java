package ru.vesyoliy.currencyconverter.ui.converter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.vesyoliy.currencyconverter.R;
import ru.vesyoliy.currencyconverter.di.DependencyHolder;
import ru.vesyoliy.currencyconverter.ui.base.BasePresenterFragment;
import ru.vesyoliy.currencyconverter.model.Currency;

public final class CurrencyConverterFragment extends BasePresenterFragment<CurrencyConverterContract.View, CurrencyConverterContract.Presenter>
        implements CurrencyConverterContract.View {

    private Spinner mInitialCurrencyNameSpinner;
    private Spinner mResultCurrencyNameSpinner;
    private EditText mInitialCurrencyValueView;
    private TextView mResultCurrencyValueView;
    private View mProgressContainer;
    private LinearLayout mControlsContainer;

    ArrayAdapter<String> mCurrencyNamesAdapter;

    public static Fragment newInstance() {
        return new CurrencyConverterFragment();
    }

    //region Lifecycle
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_currency_converter, container, false);

        mInitialCurrencyNameSpinner = (Spinner) mainView.findViewById(R.id.converter_initial_currency_name_spinner);
        mResultCurrencyNameSpinner = (Spinner) mainView.findViewById(R.id.converter_result_currency_name_spinner);
        mInitialCurrencyValueView = (EditText) mainView.findViewById(R.id.converter_initial_currency_value_text);
        mResultCurrencyValueView = (TextView) mainView.findViewById(R.id.converter_result_currency_value_text);
        mProgressContainer = mainView.findViewById(R.id.converter_progress_container);
        mControlsContainer = (LinearLayout) mainView.findViewById(R.id.converter_controls_container);

        mInitialCurrencyValueView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPresenter.onInitialCurrencyValueChanged(editable.toString());
            }
        });

        mCurrencyNamesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_selectable_list_item);
        mInitialCurrencyNameSpinner.setAdapter(mCurrencyNamesAdapter);
        mResultCurrencyNameSpinner.setAdapter(mCurrencyNamesAdapter);

        mInitialCurrencyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onInitialCurrencyChanged(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mResultCurrencyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.onResultCurrencyChanged(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return mainView;
    }
    //endregion

    //region BasePresenterFragment
    @Override
    protected int getPresenterLoaderId() {
        return R.id.currency_converter_presenter_loader_id;
    }

    @NonNull
    @Override
    protected CurrencyConverterContract.Presenter createPresenter() {
        return ((DependencyHolder) getActivity().getApplication()).getCurrencyConverterComponent()
                .getCurrencyConverterPresenter();
    }

    @NonNull
    @Override
    protected CurrencyConverterContract.View getPresenterView() {
        return this;
    }
    //endregion

    //region CurrencyConverterContract
    @Override
    public void setCurrencyNames(@Nullable List<Currency> currencyList) {
        mCurrencyNamesAdapter.clear();
        if (currencyList != null) {
            for (Currency currency : currencyList) {
                mCurrencyNamesAdapter.addAll(currency.getName());
            }
        }
    }

    @Override
    public void setInitialCurrencyValue(@Nullable String value) {
        mInitialCurrencyValueView.setText(value);
    }

    @Override
    public void setInitialCurrencyValueCursorPosition(int position) {
        mInitialCurrencyValueView.setSelection(position);
    }

    @Override
    public void setResultCurrencyValue(@Nullable String value) {
        mResultCurrencyValueView.setText(value);
    }

    @Override
    public void setInitialCurrencyIndex(int index) {
        mInitialCurrencyNameSpinner.setSelection(index);
    }

    @Override
    public void setResultCurrencyIndex(int index) {
        mResultCurrencyNameSpinner.setSelection(index);
    }

    @Override
    public void showLoading() {
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressContainer.setVisibility(View.GONE);
    }

    @Override
    public void showControls() {
        mControlsContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideControls() {
        mControlsContainer.setVisibility(View.GONE);
    }

    @Override
    public void showError(@StringRes int errorTextId) {
        Toast.makeText(getContext(), errorTextId, Toast.LENGTH_LONG).show();
    }
    //endregion
}
