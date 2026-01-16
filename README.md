# SimpleTime

SimpleTime is a simple and open-source time tracking software made in Java for CLI. 

## Installation

The following libraries are needed for this project:
- [jackson-annotations-2.15.2](https://download.dcache.org/nexus/service/rest/repository/browse/public-snapshots/com/fasterxml/jackson/core/jackson-annotations/2.15.2/)
- [jackson-core-2.15.2](https://download.dcache.org/nexus/service/rest/repository/browse/public-snapshots/com/fasterxml/jackson/core/jackson-core/2.15.2/)
- [jackson-databind-2.15.2](https://download.dcache.org/nexus/service/rest/repository/browse/public-snapshots/com/fasterxml/jackson/core/jackson-databind/2.15.2/)

How to download:
- `cd your/directory/goes/here`
- `git clone git@github.com:emmaclairelandis/simpletime.git`

How to compile:
- `javac -cp "lib/*" -d bin src/simpletime/*.java`
- `./compile.sh`

Your directory tree should generally look like the following:
```
├── LICENSE
├── README.md
├── bin
│   └── simpletime
│       ├── ConsoleUtils.class
│       ├── Main.class
│       └── TimerManager.class
├── compile.sh
├── data
│   └── data.json
├── lib
│   ├── jackson-annotations-2.15.2.jar
│   ├── jackson-core-2.15.2.jar
│   └── jackson-databind-2.15.2.jar
├── manifest.txt
└── src
    └── simpletime
        ├── ConsoleUtils.java
        ├── Main.java
        └── TimerManager.java
```