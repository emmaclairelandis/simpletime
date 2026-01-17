javac -cp "lib/*" -d bin src/zundatracker/*.java

rm -rf tmp
mkdir tmp
cd tmp

# unpack all dependency jars
for j in /mnt/d/Repos/zundatracker/lib/*.jar; do
    jar xf "$j"
done

# copy compiled classes
cp -r /mnt/d/Repos/zundatracker/bin/* .

# create the fat (🤤) jar
jar cfm /mnt/d/Repos/zundatracker/zundatracker.jar /mnt/d/Repos/zundatracker/manifest.txt *