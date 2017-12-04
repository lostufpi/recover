package exceptions;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class MyExceptionHandlerFactory extends ExceptionHandlerFactory{

	private ExceptionHandlerFactory parent;

    public MyExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        //return new HandleException(parent.getExceptionHandler());
    	ExceptionHandler result = parent.getExceptionHandler();
        result = new HandleException(result);
        return result;
    }
}
