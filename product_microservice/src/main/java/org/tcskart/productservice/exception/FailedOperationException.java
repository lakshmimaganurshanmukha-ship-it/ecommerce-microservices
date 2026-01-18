package org.tcskart.productservice.exception;

public class FailedOperationException extends RuntimeException {
       public FailedOperationException(String message) {
    	   super(message);
       }
}
