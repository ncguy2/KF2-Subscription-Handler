package handler.task;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

public class BaseBackgroundTask<T> extends Thread {

    protected final T context;

    public BaseBackgroundTask(T context) {
        this.context = context;
    }

}
