package exceptions;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

	public class HandleException extends ExceptionHandlerWrapper {

	private ExceptionHandler wrapped;

    public HandleException(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void handle() throws FacesException {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        for (Iterator<ExceptionQueuedEvent> iter = getUnhandledExceptionQueuedEvents().iterator(); iter.hasNext();) {
            Throwable exception = iter.next().getContext().getException(); // There it is!

            // Now do your thing with it. This example implementation merely prints the stack trace.
            exception.printStackTrace();

            // You could redirect to an error page (bad practice).
            // Or you could render a full error page (as OmniFaces does).
            // Or you could show a FATAL faces message.
            // Or you could trigger an oncomplete script.
            // etc..
        }

        getWrapped().handle();
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

}
