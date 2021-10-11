package org.app;

@SuppressWarnings("serial")
public class NameNotFoundException extends Exception {
    @Override
    public String toString() {
        return "Given name not found";
    }
}
