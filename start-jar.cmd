@echo off
start "" javaw --module-path target/lib --add-modules javafx.controls,javafx.graphics,javafx.media -cp "target/arkanoid-1.0.jar;target/lib/*" -jar ./target/arkanoid-1.0.jar
exit