# MineSweeper
MineSweeper   
 I. EVERY THING IN MineSweeper (package MineSweeper)  
 
 II. notation in map/knownWorld/testWorld：  
 Integer.MAX_VALUE : undiscovered cell  
 Integer.MAX_VALUE - 1 : safe cell during inference process(still in undiscovered state)  
 0 - #(any number) : clue (indicate how many mines around current cell)  
 negative number(except MIN_VALUE) : clue during inference process(don't want to interference any clue which get from game env)  
 Integer.MIN_VALUE/MINE_PLACEHOLDER : mine   
 
 
