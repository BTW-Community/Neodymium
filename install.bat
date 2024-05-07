set MAPPINGS_VERSION=1.0.3
cd "%~dp0"
rd /s /q "./BTW_dev"
call gradlew.bat --no-daemon downloadAssets
mkdir BTW_dev
mkdir custom_mappings
tar.exe -xf mavenRepo/btw/community/mappings/%MAPPINGS_VERSION%/mappings-%MAPPINGS_VERSION%.jar -C custom_mappings
java -jar libs/tiny-remapper-0.8.6+local-fat.jar %~f1 "BTW_dev/%~nx1" custom_mappings/mappings/mappings.tiny intermediary named %userprofile%/.gradle/caches/fabric-loom/1.6.4/minecraft-merged-intermediary.jar"
tar.exe -xf %userprofile%/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged/1.6.4-btw.community.mappings.1_6_4.%MAPPINGS_VERSION%-v2/minecraft-merged-1.6.4-btw.community.mappings.1_6_4.%MAPPINGS_VERSION%-v2.jar -C BTW_dev
tar.exe -xf "BTW_dev/%~nx1" -C BTW_dev
del BTW_dev\%~nx1
cd BTW_dev
tar.exe -a -cf ../BTW_dev.zip *
cd ..
move BTW_dev.zip BTW_dev\BTW_dev.zip
echo Done!
PAUSE