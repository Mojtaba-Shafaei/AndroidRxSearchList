package com.mojtaba_shafaei.android;

public abstract class Lce<T> {
    public static <T> Lce<T> data(final T data) {
        return new Lce<T>() {
            @Override
            public boolean isLoading() {
                return false;
            }

            @Override
            public boolean hasError() {
                return false;
            }

            @Override
            public Throwable getError() {
                return null;
            }

            @Override
            public T getData() {
                return data;
            }
        };
    }

    public static <T> Lce<T> error(final Throwable error) {
        return new Lce<T>() {
            @Override
            public boolean isLoading() {
                return false;
            }

            @Override
            public boolean hasError() {
                return true;
            }

            @Override
            public Throwable getError() {
                return error;
            }

            @Override
            public T getData() {
                return null;
            }
        };
    }

    public static <T> Lce<T> loading() {
        return new Lce<T>() {
            @Override
            public boolean isLoading() {
                return true;
            }

            @Override
            public boolean hasError() {
                return false;
            }

            @Override
            public Throwable getError() {
                return null;
            }

            @Override
            public T getData() {
                return null;
            }
        };
    }

    public abstract boolean isLoading();

    public abstract boolean hasError();

    public abstract Throwable getError();

    public abstract T getData();

    @Override
    public String toString() {
        return "Lce{isLoading = " + isLoading() +
                ", hasError = " + (hasError() ? hasError() + "[" + getError() + "]" : hasError()) +
                ", data = " + getData() + "}";
    }
}
