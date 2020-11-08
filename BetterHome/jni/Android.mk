LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := BetterHome
LOCAL_SRC_FILES := BetterHome.cpp

include $(BUILD_SHARED_LIBRARY)
