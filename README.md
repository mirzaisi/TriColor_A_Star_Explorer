# 3x3 Tile Puzzle (A* Search)

Java/Swing app for a 3x3 board with tiles R, G, and B. It runs A* with Hamming distance, enforces turn order R → G → B, and stops after 10 moves if no solution.

## Features

- Interactive boards to set **Initial** and **Goal** states (exactly one R/G/B each).
- Colored tiles and enforced turn order.
- A popup **state-tree diagram** showing explored states as nodes with mini 3x3 boards, curved edges, initial/goal highlighting, and zoom controls.

## How to Use

1) Set the Initial and Goal boards: click a cell to cycle `empty → R → G → B → empty` (colors indicate tile).
2) Press **Solve with A Star to** to run A*; a popup shows the search tree and solution summary.
3) Press **Reset boards** to clear both boards.

## Run (Windows PowerShell)

```powershell
javac -d bin src\model\*.java src\search\*.java src\UI\*.java src\Main.java
java -cp bin Main
```

## Notes

- Moves allowed in 8 directions into empty cells only.
- Goal check compares tile positions (turn value is not part of the goal match).
