# Spotify CLI

This CLI can be used to control your Spotify account. It is meant as an example project to show how to build a command line interface in a language that runs on the JVM.

Watch [the talk on YouTube](https://www.youtube.com/watch?v=MWrAt1L6xpM) to learn more about this CLI!

## Installation

Download a binary from the releases page, and put the binary in your `$PATH`.

## Autocomplete

This CLI supports autocomplete for various shells (`bash`, `zsh`, `fish`). To enable autocomplete, source the completion script upon starting a shell (through your profile, or `rc` file):

```bash
source <(spot --generate-completion bash)
```

Replace `bash` with the shell of your choice.

## Compiling

In order to compile the source to a valid binary (that can be run standalone), make sure that you have the following installed:

- GraalVM 20.3.0.r11 (or greater; should also work)
- Native Image GraalVM component (`gu install native-image`)

Make sure when running maven, you've set GraalVM as the JDK to use in that shell (hence, `JAVA_HOME` is set to GraalVM).
Tip: use [`direnv`](https://github.com/direnv/direnv), and create an `.envrc` file in the root of this repository, with the following contents:
```bash
export JAVA_HOME=path/to/graalvm/java_home
export PATH=$JAVA_HOME/bin:$PATH
```

This automatically activates and sets GraalVM as the active JDK.

### Client credentials

To successfully use the CLI, make sure to create `application.conf` in `src/main/resources`, with the following contents:
```hocon
cli {
  clientId = "<the client id for your Spotify app>"
  clientSecret = "<the client secret for your Spotify app>"
}
```

To create a `clientId` and `clientSecret`, go to the [Spotify Developer dashboard](https://developer.spotify.com/dashboard/applications).

## License

Feel free to use, copy, and modify this code to fit your needs. A reference towards this repository is appreciated :)

## Disclaimer

The requested scopes for this app are far too much, don't do this in real life ;)

## Credits

Multi platform builds inspired by [this blog post](https://blogs.oracle.com/developers/building-cross-platform-native-images-with-graalvm).
