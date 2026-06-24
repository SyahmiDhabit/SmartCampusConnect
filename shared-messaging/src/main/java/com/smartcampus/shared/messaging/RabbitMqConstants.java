package com.smartcampus.shared.messaging;

public final class RabbitMqConstants {

    public static final String EXCHANGE = "smartcampus.events";
    public static final String DLX_EXCHANGE = "smartcampus.dlx";
    public static final String DLQ = "smartcampus.dlq";

    public static final String NOTIFICATION_QUEUE = "notification.events.queue";
    public static final String REPORTING_QUEUE = "reporting.events.queue";

    public static final String RK_ENROLMENT_CREATED = "enrolment.created";
    public static final String RK_ENROLMENT_DROPPED = "enrolment.dropped";
    public static final String RK_LIBRARY_LOAN_CREATED = "library.loan.created";
    public static final String RK_LIBRARY_BOOKING_CREATED = "library.booking.created";
    public static final String RK_STUDENT_CREATED = "student.created";
    public static final String RK_STUDENT_UPDATED = "student.updated";
    public static final String RK_LOAD_TEST = "loadtest.event";

    public static final String ET_ENROLMENT_CREATED = "ENROLMENT_CREATED";
    public static final String ET_ENROLMENT_DROPPED = "ENROLMENT_DROPPED";
    public static final String ET_LIBRARY_LOAN_CREATED = "LIBRARY_LOAN_CREATED";
    public static final String ET_LIBRARY_BOOKING_CREATED = "LIBRARY_BOOKING_CREATED";
    public static final String ET_STUDENT_CREATED = "STUDENT_CREATED";
    public static final String ET_STUDENT_UPDATED = "STUDENT_UPDATED";
    public static final String ET_LOAD_TEST = "LOAD_TEST_EVENT";

    private RabbitMqConstants() {
    }
}
