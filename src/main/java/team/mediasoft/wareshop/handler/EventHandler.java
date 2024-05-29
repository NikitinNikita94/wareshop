package team.mediasoft.wareshop.handler;


import team.mediasoft.wareshop.web.request.EventSource;

public interface EventHandler <T extends EventSource> {

    boolean canHandle(EventSource eventSource);

    String handleEvent(T eventSource);
}
