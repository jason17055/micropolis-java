========================================================================

Divercity

========================================================================

Divercity is an Extension of MicropolisJ which is Micropolis for the Java platform.
It was developed during a three-week programming course at Freie Universitaet Berlin.

Copyright (C) for Divercity: 2014 Arne Roland, Benjamin Kretz, Estela Gretenkord i Berenguer, Fabian Mett, Marvin Becker, Tom Brewe, Tony Schwedek, Ullika Scholz, Vanessa Schreck.

Copyright (C) for MicropolisJ: 2013 Jason Long (jason@long.name).
Portions Copyright (C) 1989-2007 Electronic Arts Inc.

MicropolisJ is based on Micropolis, Unix version, which was developed by
Don Hopkins (dhopkins@DonHopkins.com, http://www.DonHopkins.com) for
DUX Software under license from Maxis, in or around 1990.  This version
was later modified for inclusion in the One Laptop Per Child (OLPC)
program, and released as free and open source software under the GPL in
2008.
Copyright (C) 1989-2007 Electronic Arts Inc.

The original Micropolis game was designed and implemented by Will Wright.
Copyright (C) 2002 by Electronic Arts.

========================================================================

How to Run This Program
-----------------------

First of all, you must have Java (version 7 or better) installed on your
computer. You can get Java at http://java.com/download.

Next, simply double-click the enclosed micropolisj.jar file to run the
program.

QUICK DOWNLOAD, COMPILE and RUN
---------------------------------

to get the dev branch:
run this command in commandline in your favorite directory:
git clone git@github.com:Team--Rocket/micropolis-java.git divercity ; cd divercity; ant

then simply run (in divercity directory):
java -jar micropolisj.jar

Attention: 
Enabling sound might result in a slower UI experience.


new Features
-------------
* **more realistic traffic**: we replaced old traffic generation with A* path finding. We generate paths from residential areas along roads and rails, is a road heavily used it's getting less likely for new cars to use that road, so the traffic balances.
  * visits to buildings influence game variables like growth of zones, police efficience, culture effect etc. Visits are generated with our new traffic simulation
  * police and fire efficience is dependent on the traffic and transportation situation as well now. instead of a fixed radius, a breadth-first-search is used now to determine the effect area, making it much more realistic 
* **new buildings**:
  * *school*: generates education points, is necessary to build a university
  * two types of *university*: generates education points, enables research by right clicking or using the query tool
  * *museum*: generates a culture effect
  * *open air area*: generates a culture effect
  * *city hall*: adds a new city center which increases the landvalue around it. Also statistics can be views by right clicking or using the query tool on it
  * *big park*
  * *Energy*: wind power plant and solar power plant can be researched in a university
  * *transportation*: new big road type, rails now need a station to be used
* **research**: in each of the two new universities continuos improvements can be researched as well as unlocking special buildings, such as the new power plants is possible there
* **improved graphics**: we tweaked some colors to be more eye pleasing
  * new splash screen
* **more realistic pollution spreading**: we now use a gaussian distribution to spread pollution around the map
* scolling with keyboard arrows works now
* bug fixing


![screenshot](https://raw.githubusercontent.com/Team--Rocket/divercity/master/betterScreenshot.png)

========================================================================

LICENCE
---------------------------------

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.  You should have received a
copy of the GNU General Public License along with this program.  If
not, see <http://www.gnu.org/licenses/>.

ADDITIONAL TERMS per GNU GPL Section 7

No trademark or publicity rights are granted.  This license does NOT
give you any right, title or interest in the trademark SimCity or any
other Electronic Arts trademark.  You may not distribute any
modification of this program using the trademark SimCity or claim any
affliation or association with Electronic Arts Inc. or its employees.
Any propagation or conveyance of this program must include this
copyright notice and these terms.

If you convey this program (or any modifications of it) and assume
contractual liability for the program to recipients of it, you agree
to indemnify Electronic Arts for any liability that those contractual
assumptions impose on Electronic Arts.

You may not misrepresent the origins of this program; modified
versions of the program must be marked as such and not identified as
the original program.

This disclaimer supplements the one included in the General Public
License.  TO THE FULLEST EXTENT PERMISSIBLE UNDER APPLICABLE LAW, THIS
PROGRAM IS PROVIDED TO YOU "AS IS," WITH ALL FAULTS, WITHOUT WARRANTY
OF ANY KIND, AND YOUR USE IS AT YOUR SOLE RISK.  THE ENTIRE RISK OF
SATISFACTORY QUALITY AND PERFORMANCE RESIDES WITH YOU.  ELECTRONIC ARTS
DISCLAIMS ANY AND ALL EXPRESS, IMPLIED OR STATUTORY WARRANTIES,
INCLUDING IMPLIED WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY,
FITNESS FOR A PARTICULAR PURPOSE, NONINFRINGEMENT OF THIRD PARTY
RIGHTS, AND WARRANTIES (IF ANY) ARISING FROM A COURSE OF DEALING,
USAGE, OR TRADE PRACTICE.  ELECTRONIC ARTS DOES NOT WARRANT AGAINST
INTERFERENCE WITH YOUR ENJOYMENT OF THE PROGRAM; THAT THE PROGRAM WILL
MEET YOUR REQUIREMENTS; THAT OPERATION OF THE PROGRAM WILL BE
UNINTERRUPTED OR ERROR-FREE, OR THAT THE PROGRAM WILL BE COMPATIBLE
WITH THIRD PARTY SOFTWARE OR THAT ANY ERRORS IN THE PROGRAM WILL BE
CORRECTED.  NO ORAL OR WRITTEN ADVICE PROVIDED BY ELECTRONIC ARTS OR
ANY AUTHORIZED REPRESENTATIVE SHALL CREATE A WARRANTY.  SOME
JURISDICTIONS DO NOT ALLOW THE EXCLUSION OF OR LIMITATIONS ON IMPLIED
WARRANTIES OR THE LIMITATIONS ON THE APPLICABLE STATUTORY RIGHTS OF A
CONSUMER, SO SOME OR ALL OF THE ABOVE EXCLUSIONS AND LIMITATIONS MAY
NOT APPLY TO YOU.
