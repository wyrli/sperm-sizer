# :straight_ruler: Sperm Sizer
Sperm Sizer is a tool that helps you **measure the lengths of sperm cells** in digital photos by simply **clicking on the sperm cell** you want to measure. It can be customized to measure different components such as the head, body and tail.

> [![Windows](https://img.shields.io/badge/Windows-0078D6?logo=data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTUiIGhlaWdodD0iMTUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PG1hc2sgaWQ9IndpbmRvd01hc2siPjxyZWN0IHdpZHRoPSIxNSIgaGVpZ2h0PSIxNSIgZmlsbD0id2hpdGUiLz48cmVjdCB4PSI3IiB5PSIwIiB3aWR0aD0iMSIgaGVpZ2h0PSIxNSIgZmlsbD0iYmxhY2siLz48cmVjdCB4PSIwIiB5PSI3IiB3aWR0aD0iMTUiIGhlaWdodD0iMSIgZmlsbD0iYmxhY2siLz48L21hc2s+PHJlY3Qgd2lkdGg9IjE1IiBoZWlnaHQ9IjE1IiBmaWxsPSJ3aGl0ZSIgbWFzaz0idXJsKCN3aW5kb3dNYXNrKSIvPjwvc3ZnPg==)](https://github.com/wyrli/sperm-sizer/releases/download/1.7/sperm-sizer-1.7-windows-x64.zip) [![macOS](https://img.shields.io/badge/macOS-000000?logo=apple&logoColor=white)](https://github.com/wyrli/sperm-sizer/releases/download/1.7/sperm-sizer-1.7-macos-arm64.zip)  
> [**Download Sperm Sizer 1.7**](https://github.com/wyrli/sperm-sizer/releases/tag/1.7)

![Sperm Sizer Demo](https://i.imgur.com/aPbazPy.gif)

# :page_facing_up: Scientific Article
For more information and ground-truth data, view our methods paper on Sperm Sizer.

> [**Sperm Sizer: a program to semi-automate the measurement of sperm length**](https://link.springer.com/article/10.1007/s00265-021-03013-4)

Please cite this paper if you decide to use Sperm Sizer in your publication.

# :star: Features
* **Simple Measurements:** Select points along a sperm cell to get a result.
* **Multicomponent Support:** Measure the entire length of a sperm cell or different parts of it.
* **CSV Exports:** Export measurements to a CSV file.
* **Image Exports:** Export measured sperm cells as annotated images.

# :keyboard: Controls
| Select | Pan | Zoom | Undo | Redo | Next Image | Previous Image |
| :--: | :--: | :--: | :--: | :--: | :--: | :--: |
| <kbd>Left Click</kbd> | Drag with <kbd>Right Click</kdb> | <kbd>Scroll Wheel</kdb> | <kbd>A</kdb> | <kbd>D</kdb> | <kbd>S</kdb> | <kbd>W</kdb> |

# :joystick: Usage
![Sperm Cell Measurement](https://i.imgur.com/QFV2mVL.gif)

### Basic Instructions
1. Use the `Add Images` button to load photos of the sperm cells you want to measure.
2. For each sperm cell, select:
    1. The start of the sperm cell.
    2. The connection between the head and the body.
    3. The connection between the body and the tail.
    4. The end of the sperm cell.
3. Export the measurements using the `Export Measurements` button.

### Requirements
Older versions of Sperm Sizer (1.6.6 and below) require Java 8. Newer versions (1.7 and above) bundle Java with the application, so no separate installation is needed.

# :microscope: Measurements
### Unit Conversions
Measurements are provided in `pixels`. For conversion into a different unit (e.g. `micrometers`), export the measurements to a CSV file and perform the conversions using Microsoft Excel.

| Measurements in Microsoft Excel |
| :--: |
| ![Measurements in Microsoft Excel](https://i.imgur.com/4TMBZbg.png) |

### Definition of Length
The length of a line is calculated by treating each pixel as a coordinate and summing up the straight-line distance between each pixel. For example, a 3-pixel straight line would have a length of 2 pixels, and a 2-pixel diagonal line would have a length of √2 (≈1.41) pixels.

| 3-pixel Straight Line (_l_ = 2)  | 2-pixel Diagonal Line (_l_ = √2) |
| :--: | :--: |
| ![3-pixel Straight Line](https://i.imgur.com/EmTdcVn.png) | ![2-pixel Diagonal Line](https://i.imgur.com/vqlfpL5.png) |

# :blue_book: How It Works
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

# :gear: Settings
Sperm Sizer can be configured via [config.ini](/src/com/wyrli/spermsizer/config/config.ini), a file located in the same directory as Sperm Sizer.

# :bulb: Tips
### Optimal Conditions
For best results, ensure that:
* The sperm cell you are measuring is not overlapping with itself or another. `(Scenario A)`
* The sperm cells on your image are darker than the background. `(Scenario B)`
* There is good contrast between the sperm cells and the background. `(Scenario C)`

**Note:** If the sperm cells on your image are _lighter_ than the background, consider inverting the colors (e.g. using Microsoft Paint).

| Scenario | Ideal | Not Ideal |
| :--: | :--: | :--: |
| A | ![Non-overlapping Sperm Cell](https://i.imgur.com/Sklcsqh.png) | ![Overlapping Sperm Cell](https://i.imgur.com/1ktBRy4.png) |
| B | ![Dark Cells on Light Background](https://i.imgur.com/0hjBMbi.png) | ![Light Cells on Dark Background](https://i.imgur.com/2FySoDG.png) |
| C | ![Good Contrast](https://i.imgur.com/gVPeu8G.png) | ![Bad Contrast](https://i.imgur.com/KzIQS52.png) |

### Straight-line Extensions
![Drawing Lines](https://i.imgur.com/IoFL4q9.gif)

When selecting the first or last point on a sperm cell, you have the option of drawing a line. To do this, simply <kbd>Left Click</kbd> and drag. This is useful when you are having trouble measuring sperm cells with very faint heads and/or tails.

**Note:** When drawing a line, always drag from **head to tail**. Dragging in the opposite direction (tail to head) will produce incorrect results.

| Dragging from Head to Tail | Dragging from Tail to Head |
| :--: | :--: |
| ![Drawing Line from Head to Tail](https://i.imgur.com/tLotMDT.gif) | ![Drawing Line from Tail to Head](https://i.imgur.com/iLUbaiw.gif) |

### Custom Components
![Two-point Measurement](https://i.imgur.com/JH69uz5.gif)

To measure different components, edit the `Labels` property in [config.ini](/src/com/wyrli/spermsizer/config/config.ini). For example, if you only want to measure the entire length of a sperm cell, use `Labels=Sperm Cell`. This would measure **1 component** named "Sperm Cell", requiring **2 inputs**.

**Note:** By default, `Labels=Head,Body,Tail`. This means **3 components** are measured ("Head", "Body" and "Tail"), requiring **4 inputs**.

# :information_source: Additional Information
Sperm Sizer uses a version of ImageJ where the skeletonization function in the `BinaryProcessor` class is subject to a timeout. This allows for interruptible skeletonization. The JAR is available the [lib](/lib/) directory.
