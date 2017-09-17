package handler.observable;

public interface IObserverListener<T> {

    void OnChange(ObservableValue<T> observable, T oldValue, T newValue);

}
