package handler.observable;

import java.util.ArrayList;
import java.util.List;

public class ObservableValue<T> {

    protected T value;

    public ObservableValue() {
        this(null);
    }

    public ObservableValue(T value) {
        this.value = value;
        listeners = new ArrayList<>();
    }

    protected List<IObserverListener<T>> listeners;

    public void AddListener(IObserverListener<T> listener) {
        listeners.add(listener);
    }

    public void RemoveListener(IObserverListener<T> listener) {
        listeners.remove(listener);
    }

    public T GetValue() {
        return value;
    }

    public void SetValue(T value) {
        T old = this.value;
        this.value = value;
        NotifyListeners(old);
    }

    protected void NotifyListeners() {
        NotifyListeners(null);
    }

    protected void NotifyListeners(final T oldValue) {
        listeners.forEach(listener -> listener.OnChange(this, oldValue, this.value));
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if(value.equals(o)) return true;

        ObservableValue<?> that = (ObservableValue<?>) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
