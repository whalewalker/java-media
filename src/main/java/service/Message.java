package service;

public interface Message<T> {
    public String getSenderId();
    public String getRecipientId();
}
