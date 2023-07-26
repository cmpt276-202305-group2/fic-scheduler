package com.group2.server.model;

public enum SchedulingRequest {
    /// This instructor prefers to teach a full day (i.e. 2 classes in the same day)
    PREFER_TWO_CLASSES_ON_TEACHING_DAY,

    /// This instructor prefers to teach a half day (at most 1 class per day)
    PREFER_ONE_CLASS_ON_TEACHING_DAY,

    /// This instructor must only teach a half day at most (1 class per day)
    REQUIRE_ONE_CLASS_ON_TEACHING_DAY,

    /// This instructor prefers to teach different subjects if teaching a full day
    PREFER_DIFFERENT_COURSES_ON_TEACHING_DAY,

    /// This instructor prefers multiple teaching days to be sequential and not
    /// broken up through the week
    PREFER_SEQUENTIAL_TEACHING_DAYS,
}
