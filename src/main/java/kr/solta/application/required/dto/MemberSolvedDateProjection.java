package kr.solta.application.required.dto;

import java.time.LocalDate;

public interface MemberSolvedDateProjection {
    Long getMemberId();
    String getMemberName();
    LocalDate getSolvedDate();
}
