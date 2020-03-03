# RU440_2
MineSweeper   
 hehe..................   
 lol...................    
 wwwwwwwwwwwwwwwwwwwwww    
 HI  <= DT  
 I. EVERY THING IN MineSweeper (package MineSweeper)  
 
 II. notation in map/knownWorld/testWorld：  
 Integer.MAX_VALUE : undiscovered cell  
 Integer.MAX_VALUE - 1 : safe cell during inference process(still in undiscovered state)  
 0 - #(any number) : clue (indicate how many mines around current cell)  
 negative number(except MIN_VALUE) : clue during inference process(don't want to interference any clue which get from game env)  
 Integer.MIN_VALUE/MINE_PLACEHOLDER : mine   
 
 III. next....     
 add probability     
 keep inference when it attempts put a safe mark to a undiscovered cell    
 double check(when we can get nothing from inference, before we randomly pick up new point, we may need to double check if there are no more info.)  
