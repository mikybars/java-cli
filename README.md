> To make the most of this demo you will need a [free Twilio trial account](https://www.twilio.com/docs/usage/tutorials/how-to-use-your-free-trial-account). To set it up just export the variables `$TWILIO_ACCOUNT_SID` and `$TWILIO_AUTH_TOKEN` with the credentials you will find in the [settings pane](https://www.twilio.com/console/project/settings) of  your project.

## Usage

As with any [jbang](https://jbang.dev/)-enabled application you won't need to explicitly install ANYTHING to run this demo: 

```bash
$ curl -sL https://sh.jbang.dev | bash -s - https://github.com/mperezi/java-cli/blob/master/SendSms.java
```

Anyway, if you prefer to have jbang installed on your system the simplest way is via [SDKMAN!](https://sdkman.io/):

```bash
$ sdk install jbang
$ jbang https://github.com/mperezi/java-cli/blob/master/SendSms.java
```

Finally you can get a copy of the repo and run the file locally like a regular shell script:

```bash
$ git clone https://github.com/mperezi/java-cli
$ cd java-cli
$ ./SendSms.java  # you will need jbang installed for this
```

## The Making Of

Start by creating a template file:

```bash
$ jbang init --template=cli SendSms.java
```

At this point you've already got a full-blown cli app (powered by [picocli](https://picocli.info/)) that you can run:

```bash
$ ./SendSms.java -h
Usage: SendSms [-hV] <greeting>
SendSms made with jbang
      <greeting>   The greeting to print
  -h, --help       Show this help message and exit.
  -V, --version    Print version information and exit.
```

Now you can edit it with your favorite editor and fill in your business logic:

```bash
$  jbang edit --live --open=idea SendSms.java
```

At some point you will need to satisfy some dependency. With jbang this is accomplished by declaring all the dependencies as comments at the top of the file:

```
//SendSms.java:
//DEPS com.twilio.sdk:twilio:8.4.0
```

A handy trick for both searching and getting your dependencies formatted as above is by leveraging the gavsearch utiity:

```bash
$ jbang gavsearch@jbangdev twilio
```

(Btw this nice @feature of jbang is called [Catalogs](https://github.com/jbangdev/jbang#catalogs))

In your code you can use whatever feature of Java you like. In the example I've used [`String.isBlank()`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#isBlank()) from Java 11 and the `var` keyword from Java 10 for demonstration purposes. You can tell jbang your desired version of Java in the command line:

```bash
$ jbang --java 11 SendSms.java
```

jbang will grab the appropriate version from [AdoptOpenJDK](https://adoptopenjdk.net/) if necessary and store it in its cache. You can then check the list of JDK managed by jbang:

```bash
$ jbang jdk list
[jbang] Available installed JDKs:
 * 11
$ jbang jdk home 11
echo /Users/miguel.perez/.jbang/cache/jdks/11
$ /Users/miguel.perez/.jbang/cache/jdks/11/bin/java -version
openjdk version "11.0.8" 2020-07-14
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.8+10)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.8+10, mixed mode)
```

Alternatively, if you prefer the standalone way of executing shell scripts (`./SendSms.java`) you will need to provide the Java version in the same fashion as you did with your dependencies:

```
//SendSms.java:
//JAVA 11+
```

## Building a Native Image

Finally jbang integrates with [GraalVM native image](https://www.graalvm.org/reference-manual/native-image/) technology to provide a binary file of your app that will run much faster than the JDK one (a mandatory requisite for cli apps in any language btw).

For this you will need GraalVM installed on your system:

```bash
$ sdk install java 20.3.0.r11-grl
$ sdk use java 20.3.0.r11-grl
```

jbang provides a specific subcommand for building artifacts:

```bash
$ jbang build --native SendSms.java
```

The resulting binary will be stored in the jbang's cache:

```bash
$ file ~/.jbang/cache/jars/SendSms.java.*.bin
/Users/miguel.perez/.jbang/cache/jars/SendSms.java.e4928160fc29b089fc162411fde0fc5e0fd1394eff6671566639ad3789181ede.jar.bin: Mach-O 64-bit executable x86_64
```

Please remember to add `info.picocli:picocli-codegen` if you use `--native` with picocli as that will ensure it will actually work with `native-image`.

Finally you can benchmark the different execution times of the two approaches by running them through `time`:

```bash
$ time ./SendSms.java
./SendSms.java  1,37s user 0,15s system 184% cpu 0,823 total

$ alias sendsms="$HOME/.jbang/cache/jars/SendSms.java.XXXX.bin"
$ time sendsms
  0,01s user 0,01s system 78% cpu 0,017 total
# 🤯
```

