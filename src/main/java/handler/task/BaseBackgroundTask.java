package handler.task;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import handler.threading.Task;

public abstract class BaseBackgroundTask<T> extends Task {

    protected final T context;

    public BaseBackgroundTask(T context) {
        this.context = context;
    }

}
