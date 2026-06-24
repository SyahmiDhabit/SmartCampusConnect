package com.smartcampus.library.soap;

import jakarta.xml.ws.WebFault;

@WebFault(name = "BookAlreadyLoanedFault", targetNamespace = "http://soap.library.smartcampus.com/")
public class BookAlreadyLoanedFault extends Exception {
    
    public BookAlreadyLoanedFault(String message) {
        super(message);
    }
}
