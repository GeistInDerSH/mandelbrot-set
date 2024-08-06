# Mandelbrot Set

Exploring generating image of the
[Mandelbrot Set](https://en.wikipedia.org/wiki/Mandelbrot_set)
in Clojure.

## Example Images

!["Pink and Purple"](example/png/ppr.png)
![Yellow Magenta](example/png/ym.png)
![Red Blue Green Grey](example/png/rbg_gr_old.png)

## TODO

* [Smooth Coloring](https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set#Continuous_(smooth)_coloring)
    * Current images have a "stepped" look due to the escape-time algorithm
* Non-Square Image Generation
    * Non-square images generate a "noisy" image that doesn't look like anything
* GIF Generation
    * Multiple images that loop, zooming into the original image
    * Likely will require additional optimization in image generation