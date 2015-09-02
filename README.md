# Peg Thing®

> based on the secret mind sharpening tool passed down from Ye Olden Days and now distributed by Cracker Barrel, Inc.

This is a re-frame/Reagent implementation of Peg Thing®. I took the game logic from [this chapter](http://www.braveclojure.com/functional-programming/) Daniel Higginbotham's book, [Clojure for the Brave and True](http://www.braveclojure.com/).

Try it [here](https://prbroadfoot.github.io/examples/peg-thing/index.html).

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

```
lein clean
lein cljsbuild once min
```
