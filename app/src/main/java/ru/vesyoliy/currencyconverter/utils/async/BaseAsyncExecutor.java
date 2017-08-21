package ru.vesyoliy.currencyconverter.utils.async;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

/**
 * Class for executing code in a background thread. It creates a new <code>Thread</code>
 * that executes <code>execute()</code> method. And it publishes an error on a <code>@MainThread</code>
 * if <code>execute()</code> throws an <code>Exception</code>.
 * Also it creates <code>Handler</code> for routing <code>onSuccess()</code> and <code>onError()</code>
 * to a <code>@MainThread</code>.
 * To execute code on a background thread override <code>execute()</code> and publish result from it
 * using {@link BaseAsyncExecutor#publishResultOnMainThread(Object)}
 * @param <T> - type of result for onSuccess(T result) method. Also you can process an error by yourself
 * and publish error with {@link BaseAsyncExecutor#publishErrorOnMainThread(Throwable)}
 */
public abstract class BaseAsyncExecutor<T> implements Subscription {
    @Nullable
    private volatile Action<T> mOnSuccess;

    @Nullable
    private volatile Action<Throwable> mOnError;

    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private final Thread mWorkerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                execute();
            } catch(Exception e) {
                publishErrorOnMainThread(e);
            }
            dispose();
        }
    });

    protected BaseAsyncExecutor(@Nullable Action<T> onSuccess, @Nullable Action<Throwable> onError) {
        mOnSuccess = onSuccess;
        mOnError = onError;
        mWorkerThread.setName(BaseAsyncExecutor.class.getSimpleName());
        mWorkerThread.start();
    }

    /**
     * frees onSuccess and onError links and interrupts the background thread
     */
    @Override
    public synchronized void dispose() {
        mOnSuccess = null;
        mOnError = null;
        mWorkerThread.interrupt();
    }

    /**
     * posts executing of an <code>onError</code> callback on a <code>@MainThread</code> if it is not null
     */
    protected synchronized void publishErrorOnMainThread(final Throwable error) {
        final Action<Throwable> onError = mOnError;
        if (onError != null) {
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onError.execute(error);
                }
            });
        }
    }

    /**
     * posts executing of an <code>onSuccess</code> callback on a <code>@MainThread</code> if it is not null
     * @param result - the result for a consumer
     */
    protected synchronized void publishResultOnMainThread(final T result) {
        final Action<T> onSuccess = mOnSuccess;
        if (onSuccess != null) {
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess.execute(result);
                }
            });
        }
    }

    /**
     * It is the place for your code. It is executed on a background thread.
     * To return result for the subscriber you need to use
     * {@link BaseAsyncExecutor#publishResultOnMainThread(Object)}.
     * @throws Exception - any exception not caught inside <code>execute()</code>
     */
    protected abstract void execute() throws Exception;
}
