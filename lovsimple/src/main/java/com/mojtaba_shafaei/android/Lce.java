package com.mojtaba_shafaei.android;

abstract class Lce<T>{

abstract boolean isLoading();

abstract boolean hasError();

abstract Throwable getError();

abstract T getData();

static <T> Lce<T> data(final T data){
  return new Lce<T>(){
    @Override
    public boolean isLoading(){
      return false;
    }

    @Override
    public boolean hasError(){
      return false;
    }

    @Override
    public Throwable getError(){
      return null;
    }

    @Override
    public T getData(){
      return data;
    }
  };
}

static <T> Lce<T> error(final Throwable error){
  return new Lce<T>(){
    @Override
    public boolean isLoading(){
      return false;
    }

    @Override
    public boolean hasError(){
      return true;
    }

    @Override
    public Throwable getError(){
      return error;
    }

    @Override
    public T getData(){
      return null;
    }
  };
}

static <T> Lce<T> loading(){
  return new Lce<T>(){
    @Override
    public boolean isLoading(){
      return true;
    }

    @Override
    public boolean hasError(){
      return false;
    }

    @Override
    public Throwable getError(){
      return null;
    }

    @Override
    public T getData(){
      return null;
    }
  };
}
}
