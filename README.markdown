Segmentation
============

This project was an assignment of the Computer Graphics course at [ITBA] [1] in 2008. See copyright notes at the bottom.

It features an [**image segmentation**] [2] framework written in Java, using standard libraries (no external dependencies required).

See instructions below on how to run.

  [1]: http://www.itba.edu.ar
  [2]: http://en.wikipedia.org/wiki/Segmentation_(image_processing)

Features
---------

  - 3 segmentation algorithms: K-means, Split & merge and Antipole tree
  - 3 segmentation features: by color, by color histogram (RGB or HSB) and by spacial color histogram
  - Filters: Blur, sharpen, equalization, resolution reduction, max, min, max-min, midpoint, average
  - Zooming
  - Panels for comparing original and segmented image, in vertical or horizontal layout

File contents
-------------

  - `bin`: Reserved for .jar and .class files
  - `build.xml`: Apache Ant build file
  - `COPYING`: GNU General Public License
  - `doc`: Documentation
  - `img`: Sample images to test
  - `README.markdown`: This README file
  - `resources`: Contains icons used by the GUI
  - `src`: Source code, fully written in Java

Program usage
-------------

Build the application with Apache Ant (just run `ant`) and open the compiled .jar file. A GUI of the segmentation framework will be displayed.

    java -jar bin/cgtpe1.jar

Copyright
---------

    Copyright (c) 2008
     - Rafael Martín Bigio <rbigio@itba.edu.ar>
     - Santiago Andrés Coffey <scoffey@itba.edu.ar>
     - Andrés Santiago Gregoire <agregoir@itba.edu.ar>

    Segmentation is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Segmentation is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Segmentation.  If not, see <http://www.gnu.org/licenses/>.

