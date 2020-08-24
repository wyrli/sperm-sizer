# :straight_ruler: Sperm Sizer
Sperm Sizer is a tool that helps you **measure the lengths of sperm cells** in digital photos by simply **clicking on the sperm cell** you want to measure. It can be customized to measure different components such as the head, body and tail.

# Features
* **Simple Measurements:** Select points along a sperm cell to get a result.
* **Multicomponent Support:** Measure the entire length of a sperm cell or different parts of it.
* **CSV Exports:** Export measurements to a CSV file.
* **Image Exports:** Export measured sperm cells as annotated images.

# Controls
| Select | Pan | Zoom | Undo | Redo | Next Image | Previous Image |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| <kbd>Left Click</kbd> | Drag with <kbd>Right Click</kdb> | <kbd>Scroll Wheel</kdb> | <kbd>A</kdb> | <kbd>D</kdb> | <kbd>S</kdb> | <kbd>W</kdb> |

# Usage
![Sperm Cell Measurement](https://i.imgur.com/QFV2mVL.gif)

### Basic Instructions
1. Load photos of the sperm cells you want to measure.
2. For each sperm cell, select:
    1. The start of the sperm cell.
    2. The connection between the head and the body.
    3. The connection between the body and the tail.
    4. The end of the sperm cell.
3. Export the measurements.

# Measurements
### Unit Conversions
Measurements are provided in `pixels`. For conversion into a different unit (e.g. `micrometers`), export the measurements to a CSV file and perform the conversions using Microsoft Excel.

| Measurements in Microsoft Excel |
| :---: |
|![Measurements in Microsoft Excel](https://i.imgur.com/4TMBZbg.png) |

### Definition of Length
The length of a line is calculated by treating each pixel as a coordinate and summing up the straight-line distance between each pixel. For example, a 3-pixel straight line would have a length of 2 pixels, and a 2-pixel diagonal line would have a length of √2 (≈1.41) pixels.

| 3-pixel Straight Line (_l_ = 2)  | 2-pixel Diagonal Line (_l_ = √2) |
| :---: | :---: |
| ![3-pixel Straight Line](https://i.imgur.com/EmTdcVn.png) | ![2-pixel Diagonal Line](https://i.imgur.com/vqlfpL5.png) |

# How Does It Work?
![How It Works](https://i.imgur.com/5GR4pPk.gif)

### Tracing Algorithm
```
1. Designate four points.
2. Binarize the image via thresholding.
    * Use the smallest threshold value that results in all points being connected.
3. Select the group of pixels connecting the points together and:
    1. Remove all other pixels.
    2. Fill holes.
4. Perform line smoothing:
    1. Apply Gaussian blur.
    2. Repeat Step 2.
5. Skeletonize.
6. Remove all pixels within TrimRadius pixels of the first and last point.
7. Snap both ends of the line to the first and last point.
```

**Note:** Snapping, trim radius and Gaussian blur intensity can be configured in the settings.

# Settings
Sperm Sizer can be configured via `config.ini`, a file located in the same directory as Sperm Sizer.
