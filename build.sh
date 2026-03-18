#!/bin/bash
# ChadSuno Build Script
# Usage: ./build.sh 2>&1 | tee build.log

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  ChadSuno Build Script${NC}"
echo -e "${GREEN}  'I use arch, btw'${NC}"
echo -e "${GREEN}========================================${NC}"

# Detect Android SDK
if [ -z "$ANDROID_SDK_ROOT" ]; then
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
        export ANDROID_HOME="$HOME/Android/Sdk"
        echo -e "${YELLOW}Using Android SDK at: $ANDROID_SDK_ROOT${NC}"
    elif [ -d "/opt/android-sdk" ]; then
        export ANDROID_SDK_ROOT="/opt/android-sdk"
        export ANDROID_HOME="/opt/android-sdk"
        echo -e "${YELLOW}Using Android SDK at: $ANDROID_SDK_ROOT${NC}"
    else
        echo -e "${RED}ERROR: Android SDK not found!${NC}"
        echo "Please set ANDROID_SDK_ROOT or install to ~/Android/Sdk"
        exit 1
    fi
fi

# Check for required components
check_sdk_components() {
    echo -e "\n${YELLOW}Checking SDK components...${NC}"
    
    local missing=0
    
    if [ ! -d "$ANDROID_SDK_ROOT/build-tools/35.0.0" ]; then
        echo -e "${RED}Missing: build-tools;35.0.0${NC}"
        missing=1
    else
        echo -e "${GREEN}✓ build-tools;35.0.0${NC}"
    fi
    
    if [ ! -d "$ANDROID_SDK_ROOT/platforms/android-35" ]; then
        echo -e "${RED}Missing: platforms;android-35${NC}"
        missing=1
    else
        echo -e "${GREEN}✓ platforms;android-35${NC}"
    fi
    
    if [ $missing -eq 1 ]; then
        echo -e "\n${YELLOW}Installing missing components...${NC}"
        if [ -f "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]; then
            yes | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --sdk_root="$ANDROID_SDK_ROOT" \
                "build-tools;35.0.0" "platforms;android-35"
        else
            echo -e "${RED}ERROR: sdkmanager not found!${NC}"
            exit 1
        fi
    fi
}

# Build the project
build_project() {
    echo -e "\n${YELLOW}Building ChadSuno...${NC}"
    
    # Clean previous build
    echo "Cleaning previous build..."
    rm -rf app/build build .gradle 2>/dev/null || true
    
    # Build debug APK
    echo "Compiling..."
    gradle assembleDebug --no-daemon --stacktrace
    
    # Check if APK was created
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        echo -e "\n${GREEN}========================================${NC}"
        echo -e "${GREEN}  BUILD SUCCESSFUL!${NC}"
        echo -e "${GREEN}========================================${NC}"
        echo -e "${GREEN}APK Location: app/build/outputs/apk/debug/app-debug.apk${NC}"
        ls -lh app/build/outputs/apk/debug/app-debug.apk
        return 0
    else
        echo -e "\n${RED}========================================${NC}"
        echo -e "${RED}  BUILD FAILED${NC}"
        echo -e "${RED}========================================${NC}"
        echo "Check the log for errors."
        return 1
    fi
}

# Main
cd "$(dirname "$0")"
check_sdk_components
build_project

echo -e "\n${YELLOW}To install on device:${NC}"
echo "  adb install -r app/build/outputs/apk/debug/app-debug.apk"
echo -e "\n${YELLOW}To run:${NC}"
echo "  adb shell am start -n dev.nsomnia.chadsuno/.MainActivity"
