rm -rf tmp
mkdir tmp
cd tmp

# unpack all dependency jars
for j in ~/Repos/simpletime/lib/*.jar; do
    jar xf "$j"
done

# copy your compiled classes
cp -r ~/Repos/simpletime/bin/* .

# create the fat (🤤) jar
jar cfm ~/Repos/simpletime/simpletime.jar ~/Repos/simpletime/manifest.txt *