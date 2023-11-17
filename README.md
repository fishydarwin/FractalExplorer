## Fractal Explorer

![FEX Logo](https://raw.githubusercontent.com/fishydarwin/FractalExplorer-JS/main/assets/icon.png)

Fractal Explorer is an app written in Java Swing which provides a Fractal Renderer and a custom scripting language (FEXL) to let you view some basic fractals.

The iteration code is written will iterate over each pixel on your CPU, using as many threads as possible, as well as using a pixel cache, to determine how to color each pixel based on how far the iteration went.

You may change the colors of the rendering, as well as enable quality sacrifices (half-resolution, skipping pixels in checkerboard etc...) to obtain faster rendering speeds, which is especially helpful on lower-end CPUs.

If you are interested in a GPU version of this application with reduced features, please check out the sister project [FEX-JS](https://www.github.com/fishydarwin/FractalExplorer-JS).

### Why CPU?

As of this moment, it is not always possible to use double-precision floating-point format to store the numbers used. Likewise, without programming knowledge, viewing fractals is often difficult.

By using the CPU, we can obtain higher zoom levels, and we can use our own scripting language. If zooming or ease-of-use are not a concern to you, please consider the much faster GPU variant: [FEX-JS](https://www.github.com/fishydarwin/FractalExplorer-JS).

#### This README.md file is currently W.I.P and will be revised soon.