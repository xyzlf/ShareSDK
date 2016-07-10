package com.xyzlf.share.library.request;

import android.os.AsyncTask;

/**
 * Created by zhanglifeng on 16/6/17
 */
public abstract class AbstractAsyncTask<T> extends AsyncTask<Void, Integer, T> {
    private Exception exception;
    private T data;

    @Override
    protected T doInBackground(Void... voids) {
        try {
            data = doLoadData();
            exception = null;
        } catch (Exception e) {
            data = null;
            exception = e;
        }
        return getData();
    }

    protected abstract T doLoadData() throws Exception;

    public Exception getException() {
        return exception;
    }

    public T getData() {
        return data;
    }

    @Override
    protected void onPostExecute(T t) {
        try {
            if (exception == null) {
                onSuccess(t);
            } else {
                onException(exception);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            onFinally();
        }

    }

    public void onSuccess(T t) {
    }

    public void onFinally() {
    }

    public void onException(Exception exception) {

    }
}
