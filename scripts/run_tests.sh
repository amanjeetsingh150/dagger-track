echo "Installing maven local snapshot for dagger track......\n"
./gradlew dagger-track:publishToMavenLocal -PVERSION_NAME=LOCAL_SNAPSHOT

echo "Installing maven local snapshot for dagger track clocks......\n"
./gradlew dagger-track-clocks:publishToMavenLocal -PVERSION_NAME=LOCAL_SNAPSHOT

./gradlew dagger-track:test