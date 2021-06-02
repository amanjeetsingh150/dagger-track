//
// Created by Amanjeet Singh on 27/05/21.
//
#include <jni.h>
#include "jni_interface.h"
#include <sys/resource.h>
#include <sys/sysinfo.h>
#include <jni.h>

static constexpr auto kMillisInSec = 1000;
static constexpr auto kMicrosInMillis = 1000;

jlong get_cpu_time_from_rusage();

inline uint64_t timeval_sum_to_millis(timeval &tv1, timeval &tv2) {
    return (tv1.tv_sec + tv2.tv_sec) * kMillisInSec +
           (tv1.tv_usec + tv2.tv_usec) / kMicrosInMillis;
}

extern "C" jlong
Java_com_droidsingh_daggertrack_DaggerTrackClocks_getCpuTime(JNIEnv *env, jobject clazz) {
    return get_cpu_time_from_rusage();
}

jlong get_cpu_time_from_rusage() {
    rusage rusageStats{};

    getrusage(RUSAGE_THREAD, &rusageStats);
    uint64_t cpu_time_millis = timeval_sum_to_millis(
            rusageStats.ru_utime,
            rusageStats.ru_stime
    );

    return cpu_time_millis;
}
