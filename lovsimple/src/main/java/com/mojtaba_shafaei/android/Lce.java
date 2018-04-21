package com.mojtaba_shafaei.android;

abstract class Lce<T> {
    static <T> Lce<T> data(final T data) {
        return new Lce<T>() {
            @Override
            boolean isLoading() {
                return false;
            }

            @Override
            boolean hasError() {
                return false;
            }

            @Override
            Throwable getError() {
                return null;
            }

            @Override
            T getData() {
                return data;
            }
        };
    }

    static <T> Lce<T> error(final Throwable error) {
        return new Lce<T>() {
            @Override
            boolean isLoading() {
                return false;
            }

            @Override
            boolean hasError() {
                return true;
            }

            @Override
            Throwable getError() {
                return error;
            }

            @Override
            T getData() {
                return null;
            }
        };
    }

    static <T> Lce<T> loading() {
        return new Lce<T>() {
            @Override
            boolean isLoading() {
                return true;
            }

            @Override
            boolean hasError() {
                return false;
            }

            @Override
            Throwable getError() {
                return null;
            }

            @Override
            T getData() {
                return null;
            }
        };
    }

    abstract boolean isLoading();

    abstract boolean hasError();

    abstract Throwable getError();

    abstract T getData();

    @Override
    public String toString() {
        return "Lce{isLoading = " + isLoading() +
                ", hasError = " + (hasError() ? hasError() + "[" + getError() + "]" : hasError()) +
                ", data = " + getData() + "}";
    }
}
