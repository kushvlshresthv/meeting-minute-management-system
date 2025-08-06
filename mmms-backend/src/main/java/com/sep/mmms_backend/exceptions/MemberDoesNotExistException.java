package com.sep.mmms_backend.exceptions;

import java.util.HashSet;
import java.util.Set;

public class MemberDoesNotExistException extends RuntimeException {
    final Set<Integer> memberIds = new HashSet<>();
    public MemberDoesNotExistException(ExceptionMessages message, int memberId){
        super(message.toString());
        this.memberIds.add(memberId);
    }


    public MemberDoesNotExistException(ExceptionMessages message, Set<Integer> memberIds){
        super(message.toString());
        if(memberIds != null)
            this.memberIds.addAll(memberIds);
    }

    public Set<Integer> getMemberIds(){
        return memberIds;
    }
}
