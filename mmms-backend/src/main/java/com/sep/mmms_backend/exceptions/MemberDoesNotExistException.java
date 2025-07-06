package com.sep.mmms_backend.exceptions;

public class MemberDoesNotExistException extends RuntimeException {
    final int memberId;
    public MemberDoesNotExistException(ExceptionMessages message, int memberId){
        super(message.toString());
        this.memberId = memberId;
    }

    public int getMemberId(){
        return memberId;
    }
}
