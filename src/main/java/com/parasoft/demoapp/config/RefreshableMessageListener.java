package com.parasoft.demoapp.config;

import java.util.HashMap;
import java.util.Map;

public abstract class RefreshableMessageListener<T> {

    protected final Map<String, T> listenedListenerContainers = new HashMap<>();

    protected abstract void registerListener(String destinationName);

    public abstract void refreshDestination(String destinationName);

    public abstract void stopAllListenedDestinations();
}
