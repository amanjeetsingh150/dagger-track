//
// Created by Amanjeet Singh on 27/05/21.
//

#ifndef DAGGERTRACK_JNI_INTERFACE_H
#define DAGGERTRACK_JNI_INTERFACE_H

extern "C" jlong
Java_com_droidsingh_daggertrack_DaggerTrackClocks_getCpuTime(JNIEnv *env, jobject clazz);

#endif //DAGGERTRACK_JNI_INTERFACE_H
