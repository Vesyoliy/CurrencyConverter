package ru.vesyoliy.currencyconverter.ui.converter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ru.vesyoliy.currencyconverter.interactor.CurrencyLoadingInteractor;
import ru.vesyoliy.currencyconverter.model.Currency;
import ru.vesyoliy.currencyconverter.utils.async.Action;
import ru.vesyoliy.currencyconverter.utils.async.Subscription;
import ru.vesyoliy.currencyconverter.utils.network.NetworkStateObserver;

import static junit.framework.Assert.assertTrue;
import static ru.vesyoliy.currencyconverter.ui.converter.CurrencyConverterContract.Presenter;
import static ru.vesyoliy.currencyconverter.ui.converter.CurrencyConverterContract.View;

public class CurrencyConverterPresenterTest {

    private final Currency rub = new Currency("Русский рубль", "1.0000");
    private final Currency usd = new Currency("Доллар", "59.9576");

    @Test
    public void testStartLoadingAtCreation() {
        NetworkStateObserver networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        assertTrue("Loading must be started", interactor.loadingCurrencyCount == 1);
    }

    @Test
    public void testSubscribeOnNetworkStateIfLoadingFailed() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new RuntimeException());
        assertTrue("Presenter must subscribe on network state", networkStateObserver.subscribeCount == 1);
    }

    @Test
    public void testSubscribeOnNetworkStateIfLoadingSuccessful() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));
        assertTrue("Presenter must not subscribe on network state", networkStateObserver.subscribeCount == 0);
    }

    @Test
    public void testUpdateAfterNewStateIsConnected() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new RuntimeException());

        networkStateObserver.publishNetworkState(true);

        assertTrue("Loading must start after network is connected", interactor.loadingCurrencyCount == 2);
    }

    @Test
    public void testUpdateAfterNewStateIsFailed() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new RuntimeException());

        networkStateObserver.publishNetworkState(false);

        assertTrue("Loading must start after network is connected", interactor.loadingCurrencyCount == 1);
    }

    @Test
    public void testUpdateAfterNewStateIsConnectedIfLoadingAlreadyStarted() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new RuntimeException());

        networkStateObserver.publishNetworkState(true);
        networkStateObserver.publishNetworkState(true);

        assertTrue("Loading must start after network is connected", interactor.loadingCurrencyCount == 2);
    }

    @Test
    public void testUnsubscribeNetworkListenerAfterLoading() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new RuntimeException());

        networkStateObserver.publishNetworkState(true);
        interactor.publishSuccess(new ArrayList<Currency>(0));

        assertTrue("Presenter must unsubscribe after loading is finished",
                networkStateObserver.unsubscribeCount == 1);
    }

    @Test
    public void testUnsubscribeNetworkListenerAfterFailedLoading() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new RuntimeException());

        networkStateObserver.publishNetworkState(true);
        interactor.publishError(new Throwable());

        assertTrue("Presenter must not unsubscribe after loading is finished",
                networkStateObserver.unsubscribeCount == 0);
    }

    @Test
    public void testUnsubscribeNetworkListenerInOnDestroy() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new RuntimeException());

        presenter.onDestroy();

        assertTrue("Presenter must unsubscribe network listener",
                networkStateObserver.unsubscribeCount == 1);
    }

    @Test
    public void testUnsubscribeNetworkListenerInOnDestroyWithoutSubscription() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));

        presenter.onDestroy();

        assertTrue("Presenter must not unsubscribe network listener",
                networkStateObserver.unsubscribeCount == 0);
    }

    @Test
    public void testUnsubscribeNetworkListenerInOnDestroyAfterSuccessReloading() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new Throwable());
        networkStateObserver.publishNetworkState(true);
        interactor.publishSuccess(new ArrayList<Currency>());
        networkStateObserver.unsubscribeCount = 0;

        presenter.onDestroy();

        assertTrue("Presenter must not unsubscribe network listener",
                networkStateObserver.unsubscribeCount == 0);
    }

    @Test
    public void testDisposeNotFinishedLoadingSubscription() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);

        presenter.onDestroy();

        assertTrue("Presenter must dispose not finished loading subscription", interactor.disposeCount == 1);
    }

    @Test
    public void testDisposeSuccessfulFinishedLoadingSubscription() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));
        presenter.onDestroy();

        assertTrue("Presenter must not dispose finished subscription", interactor.disposeCount == 0);
    }

    @Test
    public void testDisposeFailedFinishedLoadingSubscription() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new Throwable());
        presenter.onDestroy();

        assertTrue("Presenter must not dispose finished subscription", interactor.disposeCount == 0);
    }

    @Test
    public void testDisposeSuccessfulFinishedSecondLoadingSubscription() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new Throwable());
        networkStateObserver.publishNetworkState(true);
        interactor.publishSuccess(new ArrayList<Currency>());
        presenter.onDestroy();

        assertTrue("Presenter must not dispose finished subscription", interactor.disposeCount == 0);
    }

    @Test
    public void testDisposeFailedFinishedSecondLoadingSubscription() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new Throwable());
        networkStateObserver.publishNetworkState(true);
        interactor.publishError(new Throwable());
        presenter.onDestroy();

        assertTrue("Presenter must not dispose finished subscription", interactor.disposeCount == 0);
    }

    @Test
    public void testDisposeNotFinishedSecondLoadingSubscription() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new Throwable());
        networkStateObserver.publishNetworkState(true);
        presenter.onDestroy();

        assertTrue("Presenter must dispose not finished subscription", interactor.disposeCount == 1);
    }

    //region ViewTest

    @Test
    public void testShowLoadingAtAttachingViewIfLoadingNotFinished() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        presenter.attachView(view);

        assertTrue("Loading must be shown", view.showLoadingCount == 1);
        assertTrue("Loading must be shown", view.hideLoadingCount == 0);
        assertTrue("Controls must be hidden", view.showControlsCount == 0);
        assertTrue("Controls must be hidden", view.hideControlsCount == 1);
    }

    @Test
    public void testShowControlsAtAttachingViewIfLoadingSuccessfullyFinished() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));
        presenter.attachView(view);

        assertTrue("Loading must be hidden", view.showLoadingCount == 0);
        assertTrue("Loading must be hidden", view.hideLoadingCount == 1);
        assertTrue("Controls must be hidden", view.showControlsCount == 1);
        assertTrue("Controls must be hidden", view.hideControlsCount == 0);
    }

    @Test
    public void testShowControlsAtAttachingViewIfLoadingFinishedWithError() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new Throwable());
        presenter.attachView(view);

        assertTrue("Loading must be hidden", view.showLoadingCount == 0);
        assertTrue("Loading must be hidden", view.hideLoadingCount == 1);
        assertTrue("Controls must be hidden", view.showControlsCount == 1);
        assertTrue("Controls must be hidden", view.hideControlsCount == 0);
    }

    @Test
    public void testSetCurrencyAtAttachingIfItLoaded() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(1));
        presenter.attachView(view);

        assertTrue("Presenter must set currency names into view", view.setCurrencyNamesCount == 1);
    }

    @Test
    public void testSetCurrencyAtAttachingIfItNotLoaded() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishError(new Throwable());
        presenter.attachView(view);

        assertTrue("Presenter must set currency names into view", view.setCurrencyNamesCount == 1);
    }

    @Test
    public void testSetInitialCurrencyValueAtAttaching() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));
        presenter.attachView(view);

        assertTrue("Presenter must set initial currency value into view", view.setInitialCurrencyValueCount == 1);
    }

    @Test
    public void testSetResultCurrencyValueAtAttaching() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));
        presenter.attachView(view);

        assertTrue("Presenter must set result currency value into view", view.setResultCurrencyValueCount == 1);
    }

    @Test
    public void testSetInitialCurrencySelectionAtAttaching() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));
        presenter.attachView(view);

        assertTrue("Presenter must set initial currency value into view", view.setInitialCurrencySelectionCount == 1);
    }

    @Test
    public void testSetResultCurrencySelectionAtAttaching() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);
        interactor.publishSuccess(new ArrayList<Currency>(0));
        presenter.attachView(view);

        assertTrue("Presenter must set result currency value into view", view.setResultCurrencySelectionCount == 1);
    }

    @Test
    public void testMaxCharactersCount() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);

        List<Currency> currencyList = new ArrayList<>(1);
        currencyList.add(rub);

        interactor.publishSuccess(currencyList);
        presenter.attachView(view);

        String value = "123456789012345678901234";

        presenter.onInitialCurrencyValueChanged(value);

        assertTrue("Initial currency value must be reduced to length: "
                + CurrencyConverterPresenterImpl.MAX_CHARACTERS_NUMBER,
                value.substring(0, CurrencyConverterPresenterImpl.MAX_CHARACTERS_NUMBER).equals(view.initialCurrencyValue));

        assertTrue("Cursor must be set into the " + CurrencyConverterPresenterImpl.MAX_CHARACTERS_NUMBER
                    + " position", view.cursorPosition == CurrencyConverterPresenterImpl.MAX_CHARACTERS_NUMBER);
    }

    @Test
    public void testUpdateResultIfInitialValueChanging() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);

        List<Currency> currencyList = new ArrayList<>(1);
        currencyList.add(rub);
        currencyList.add(usd);

        interactor.publishSuccess(currencyList);
        presenter.attachView(view);

        presenter.onInitialCurrencyChanged(1);
        presenter.onResultCurrencyChanged(0);

        view.setResultCurrencyValueCount = 0;

        String value = "1";
        presenter.onInitialCurrencyValueChanged(value);

        assertTrue("Result value must be changed", view.setResultCurrencyValueCount == 1);
        assertTrue("Value must be " + usd.getValue() + ", but it is " + view.resultCurrencyValue,
                view.resultCurrencyValue.equals(usd.getValue()));

    }

    @Test
    public void testChangeResultCurrencyIfInitialValueNotSet() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);

        List<Currency> currencyList = new ArrayList<>(1);
        currencyList.add(rub);
        currencyList.add(usd);

        interactor.publishSuccess(currencyList);
        presenter.attachView(view);

        presenter.onInitialCurrencyValueChanged("");
        presenter.onInitialCurrencyChanged(1);

        view.setResultCurrencyValueCount = 0;

        presenter.onResultCurrencyChanged(0);

        assertTrue("Result value must be changed", view.setResultCurrencyValueCount == 1);
        assertTrue("Value must be null", view.resultCurrencyValue == null);
    }

    @Test
    public void testChangeInitialCurrencyIfInitialValueNotSet() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);

        List<Currency> currencyList = new ArrayList<>(1);
        currencyList.add(rub);
        currencyList.add(usd);

        interactor.publishSuccess(currencyList);
        presenter.attachView(view);

        presenter.onInitialCurrencyValueChanged("");
        presenter.onResultCurrencyChanged(0);

        view.setResultCurrencyValueCount = 0;

        presenter.onInitialCurrencyChanged(1);

        assertTrue("Result value must be changed", view.setResultCurrencyValueCount == 1);
        assertTrue("Value must be null", view.resultCurrencyValue == null);
    }

    @Test
    public void testUpdateResultAtInitialCurrencyChanging() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);

        List<Currency> currencyList = new ArrayList<>(1);
        currencyList.add(rub);
        currencyList.add(usd);

        interactor.publishSuccess(currencyList);
        presenter.attachView(view);

        presenter.onInitialCurrencyChanged(0);
        presenter.onResultCurrencyChanged(0);

        String value = "1";
        presenter.onInitialCurrencyValueChanged(value);

        view.setResultCurrencyValueCount = 0;

        presenter.onInitialCurrencyChanged(1);

        assertTrue("Result value must be changed", view.setResultCurrencyValueCount == 1);
        assertTrue("Value must be " + usd.getValue() + ", but it is " + view.resultCurrencyValue,
                view.resultCurrencyValue.equals(usd.getValue()));

    }

    @Test
    public void testUpdateResultAtResultCurrencyChanging() {
        NetworkStateObserverMock networkStateObserver = new NetworkStateObserverMock();
        CurrencyLoadingInteractorMock interactor = new CurrencyLoadingInteractorMock();
        ViewMock view = new ViewMock();

        Presenter presenter = new CurrencyConverterPresenterImpl(interactor, networkStateObserver);

        List<Currency> currencyList = new ArrayList<>(1);
        currencyList.add(rub);
        currencyList.add(usd);

        interactor.publishSuccess(currencyList);
        presenter.attachView(view);

        presenter.onInitialCurrencyChanged(1);
        presenter.onResultCurrencyChanged(1);

        String value = "1";
        presenter.onInitialCurrencyValueChanged(value);

        view.setResultCurrencyValueCount = 0;

        presenter.onResultCurrencyChanged(0);

        assertTrue("Result value must be changed", view.setResultCurrencyValueCount == 1);
        assertTrue("Value must be " + usd.getValue() + ", but it is " + view.resultCurrencyValue,
                view.resultCurrencyValue.equals(usd.getValue()));

    }

    //endregion

    public static class NetworkStateObserverMock implements NetworkStateObserver {

        protected int subscribeCount;
        protected int unsubscribeCount;

        protected NetworkStateChangeListener networkStateChangeListener;

        @Override
        public void subscribe(@NonNull NetworkStateChangeListener listener) {
            subscribeCount++;
            networkStateChangeListener = listener;
        }

        @Override
        public void unsubscribe(@NonNull NetworkStateChangeListener listener) {
            unsubscribeCount++;
            networkStateChangeListener = null;
        }

        protected  void publishNetworkState(boolean connected) {
            networkStateChangeListener.onNetworkStateChanged(connected);
        }
    }

    public static class CurrencyLoadingInteractorMock implements CurrencyLoadingInteractor {

        protected int loadingCurrencyCount;
        protected int disposeCount;

        protected Action<List<Currency>> onSuccess;
        protected Action<Throwable> onError;

        @NonNull
        @Override
        public Subscription loadCurrency(@NonNull Action<List<Currency>> onSuccess, @NonNull Action<Throwable> onError) {
            loadingCurrencyCount++;

            this.onSuccess = onSuccess;
            this.onError = onError;

            return new Subscription() {
                @Override
                public void dispose() {
                    disposeCount++;
                }
            };
        }

        protected void publishSuccess(List<Currency> currencies) {
            onSuccess.execute(currencies);
        }

        protected void publishError(Throwable error) {
            onError.execute(error);
        }
    }

    class ViewMock implements View {

        int showLoadingCount;
        int hideLoadingCount;
        int showControlsCount;
        int hideControlsCount;
        int setCurrencyNamesCount;
        int setInitialCurrencyValueCount;
        int setResultCurrencyValueCount;
        int setInitialCurrencySelectionCount;
        int setResultCurrencySelectionCount;
        int cursorPosition = -1;
        int showErrorCount;
        String initialCurrencyValue;
        String resultCurrencyValue;

        @Override
        public void setCurrencyNames(List<Currency> currencyList) {
            setCurrencyNamesCount++;
        }

        @Override
        public void setInitialCurrencyValue(@Nullable String value) {
            setInitialCurrencyValueCount++;
            initialCurrencyValue = value;
        }

        @Override
        public void setInitialCurrencyValueCursorPosition(int position) {
            cursorPosition = position;

        }

        @Override
        public void setResultCurrencyValue(@Nullable String value) {
            setResultCurrencyValueCount++;
            resultCurrencyValue = value;
        }

        @Override
        public void setInitialCurrencyIndex(int index) {
            setInitialCurrencySelectionCount++;
        }

        @Override
        public void setResultCurrencyIndex(int index) {
            setResultCurrencySelectionCount++;
        }

        @Override
        public void showLoading() {
            showLoadingCount++;
        }

        @Override
        public void hideLoading() {
            hideLoadingCount++;
        }

        @Override
        public void showControls() {
            showControlsCount++;
        }

        @Override
        public void hideControls() {
            hideControlsCount++;
        }

        @Override
        public void showError(@StringRes int errorTextId) {
            showErrorCount++;
        }
    }
}
