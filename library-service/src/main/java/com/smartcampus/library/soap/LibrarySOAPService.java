package com.smartcampus.library.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://soap.library.smartcampus.com/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface LibrarySOAPService {

    @WebMethod
    String bookLoan(
            @WebParam(name = "bookId") String bookId,
            @WebParam(name = "studentId") String studentId
    ) throws BookAlreadyLoanedFault;

    @WebMethod
    String reserveRoom(
            @WebParam(name = "roomId") String roomId,
            @WebParam(name = "studentId") String studentId,
            @WebParam(name = "bookingDate") String bookingDate,
            @WebParam(name = "timeSlot") String timeSlot
    ) throws RoomAlreadyBookedFault;
}
