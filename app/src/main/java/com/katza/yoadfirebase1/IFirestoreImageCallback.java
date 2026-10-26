package com.katza.yoadfirebase1;

public interface IFirestoreImageCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception error);


}
