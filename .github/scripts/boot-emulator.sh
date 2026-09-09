#!/usr/bin/env bash

# 1. Read device pool from properties file (ignoring commented lines)
CONFIG_FILE="src/test/resources/config/config.properties"
DEVICE_POOL=$(grep -E "^mobile\.local\.device\.pool=" "$CONFIG_FILE" | cut -d'=' -f2 | tr -d ' \r')
export MOBILE_LOCAL_DEVICE_POOL="$DEVICE_POOL"

echo "Parsed Device Pool from Config: $MOBILE_LOCAL_DEVICE_POOL"

# Split comma-separated emulator names into array
IFS=',' read -r -a EMULATOR_LIST <<< "$MOBILE_LOCAL_DEVICE_POOL"

# 2. Boot each emulator defined in the pool dynamically
for DEVICE in "${EMULATOR_LIST[@]}"; do
   # Extract port number from device ID (e.g., emulator-5554 -> 5554)
  PORT=$(echo "$DEVICE" | sed -E 's/.*-([0-9]+)/\1/')
  AVD_NAME="Pixel_6_${PORT}"

  # Check if device is already online (reactivecircus action boots the primary device automatically on 5554)
  if $ANDROID_HOME/platform-tools/adb devices | grep -q "$DEVICE"; then
    echo "✅ Primary Emulator $DEVICE is already running."
  else
    echo "🚀 Booting background emulator $DEVICE on port $PORT..."
    echo "no" | $ANDROID_HOME/cmdline-tools/latest/bin/avdmanager create avd -n "$AVD_NAME" -k "system-images;android-34;default;x86_64" --force
    $ANDROID_HOME/emulator/emulator -avd "$AVD_NAME" -port "$PORT" -no-window -no-audio -no-snapshot > /dev/null 2>&1 &

    $ANDROID_HOME/platform-tools/adb -s "$DEVICE" wait-for-device
    while [ "$($ANDROID_HOME/platform-tools/adb -s "$DEVICE" shell getprop sys.boot_completed 2>&1 | tr -d '\r')" != "1" ]; do
      sleep 3
    done
    echo "✅ Emulator $DEVICE booted successfully!"
  fi
done

echo "List of connected ADB devices:"
$ANDROID_HOME/platform-tools/adb devices

./gradlew test --tests "com.framework.runners.MobileTestNGRunner" "-Dcucumber.filter.tags=$TAGS" "-Denv=$ENV_NAME" "-Dextent.reporter.spark.out=build/reports/extent/mobile-extent-report.html" --no-daemon