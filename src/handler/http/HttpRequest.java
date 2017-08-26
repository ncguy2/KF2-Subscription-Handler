package handler.http;

//
// Author: Nick Guy
// Created at: 25/08/2017
//

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class HttpRequest<T> {

    public HttpRequest() {
        requestParams = new HashMap<>();
        headers = new HashMap<>();
        DefaultHeaders();
    }

    public HttpRequest<T> SetParam(String key, Object value) {
        requestParams.put(key, value);
        return this;
    }
    
    public HttpRequest<T> SetHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpRequest<T> setHref(String href) {
        this.href = href;
        SetHeader("Content-Length", Integer.toString(href.getBytes().length));
        return this;
    }

    public HttpRequest<T> SetMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequest<T> SetAsync(boolean async) {
        this.async = async;
        return this;
    }

    public HttpRequest<T> SetResponseMapper(Function<String, T> mapper) {
        this.mapper = mapper;
        return this;
    }

    public HttpRequest<T> SetOnSuccess(Consumer<T> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public HttpRequest<T> SetOnFail(Runnable onFail) {
        this.onFail = onFail;
        return this;
    }

    /** Accessor method to allow for invokation of the fail callback */
    public void OnFail() {
        if(onFail != null) onFail.run();
    }

    public Optional<T> Request() {
        if(async) {
            RequestAsync();
            return Optional.empty();
        }
        return Optional.ofNullable(mapper.apply(RequestSync()));
    }

    public String GetParameterString() {
        return EncodeParameters();
    }

    protected void RequestAsync() {
        Thread t = new Thread(() -> {
            String response = RequestSync();
            if(response == null) {
                if(onFail != null) onFail.run();
                return;
            }
            T mappedResponse = mapper.apply(response);
            if(mappedResponse != null) {
                if(onSuccess != null) {
                    onSuccess.accept(mappedResponse);
                }else{
                    // This should never be the case, if the request is async, a success callback should be set
                    System.out.println("Why am I handling a request that is never used...");
                }
            }else System.err.println("Http request responded, but the data could not be mapped correctly.");
        });
        t.setDaemon(true);
        t.setName("Http async request");
        t.start();
    }

    protected String RequestSync() {
        try {
            URL url = new URL(this.href);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(this.method.name());
            headers.forEach(connection::setRequestProperty);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(EncodeParameters());
            dos.close();

            InputStream is;
            boolean success = true;
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            }else{
                is = connection.getErrorStream();
                success = false;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                response.append(line).append("\n");
            reader.close();
            if(success)
                return response.toString();
            System.err.println(response.toString());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void DefaultHeaders() {
        SetHeader("Content-Language", "en-GB");
    }

    protected String EncodeParameters() {
        return requestParams.entrySet()
                .stream()
                .map(p -> UrlEncoder.UrlEncodeUTF8(p.getKey()) + "=" + UrlEncoder.UrlEncodeUTF8(p.getValue().toString()))
                .reduce((k, v) -> k + "&" + v)
                .orElse("");
    }

    /** [REQUIRED] The Http request URL */
    protected String href;
    /** [REQUIRED] The function to map the Http response to a usable data type */
    protected Function<String, T> mapper;

    /** Should the request be made asynchronously */
    protected boolean async = true;
    /** The Http method */
    protected HttpMethod method = HttpMethod.GET;
    /**
     * The request parameters for the Http request
     * TODO support nested parameters?
     */
    protected Map<String, Object> requestParams;
    /** The request headers */
    protected Map<String, String> headers;

    /**
     * Function to execute on a 200 response code
     * @implNote Only relevant if async == true
     */
    protected Consumer<T> onSuccess;
    /**
     * Function to execute on any failed event
     * @implNote Only relevant if async == true
     */
    protected Runnable onFail;

}
