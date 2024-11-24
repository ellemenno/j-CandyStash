# j-CandyStash

> a simple textmode version of battleship, but with candy

CandyStash runs in the terminal with a text-based interface, using 50 columns and 25 rows.

You compete against a computer opponent in a race to discover hidden candy.

```
 .------------------------------------------------.
|                              candy stash         |
|p1 123456789   p2 123456789                       |
| a .........    a /..//////   X bite              |
| b .L.CC....    b /X///////   / unknown           |
| c .L.CC....    c /////////   . empty             |
| d .L.CCMM..    d /X.XXX///   L licorice rope 1x4 |
| e .L.......    e /////////   C chocolate bar 2x3 |
| f .........    f /////////   M marshmallow   2x1 |
|                                                  |
|p1 guess: _                                       |
|               p2 guess: _                        |
 '------------------------------------------------'
```

There are three candy snacks of various dimensions, hidden on a 9 x 6 grid:
- `L` licorice rope 1x4
- `C` chocolate bar 2x3
- `M` marshmallow   2x1

Enter the row-col grid coordinates (e.g. a1) to peek into a cell. 'q' or 'quit' to bail.
