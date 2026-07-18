# Author: Daniel Shapiro
# Instructor: Professor Lewis
# Program Name: asm6.s
# Program Description: This program is a mini toy example of the Chrome offline dinosaur jump game.
#     This program uses the MARS BitMap Display and Keyboard and Display MMIO Simulator. Instructions
#     on how to set that up is below. In the Keyboard and Display when you press the space bar the white
#     dot (the player) jumps up a pixel then after sometime falls back down. The goal is to avoid the
#     green moving dot. The player has to jump over the green dot that is moving from right to left at the
#     bottom of the screen. Everytime the player successfully jumps over the green moving dot the green moving
#     dot gets faster. The green dot moves faster by making the speed of the frames faster as the game goes on.
#     If the green dot and the player collide then a end screen is played which is the screen turning to red line
#     by line from the bottom up. Then player stats is printed to the terminal, the total points, jumps and a
#     encouraging message. This program implements the game logic and the drawing to the screen for the player and
#     the green dot.


# -- How to set up Bitmap Display -- #
# assumes a 64x64 display.  To set this up, do the following steps:
#    - Tools->Bitmap Display
#    - Set "Unit width/height in pixels" to 8
#    - Set "Display width/height in pixels" to 512
#    - Set "Base Address for display" to static data
#    - Click "Connect to MIPS" *after* the rest are set

# -- How to set up Keyboard and Display MMIO Simulator -- #
#    - Toos->Keyboard and Display MMIO Simulator
#    - Click "Connect to MIPS"
#    - Put key strokes in the "KEYBOARD: Characters typed here are stored to Receiver Data 0xffff0004" section

# -- Program Notes -- #
# 512 pixels each drawn 8 pixels wide so each row have 512/8 = 64 columns
# since each row is 64 columns and then 64 x 4 = 256 bytes for one row
# every pixel is one word
.data

DISPLAY:                .space  16384              # Chunk of memory for the Bitmap Display
DUFFER:                 .word   1234               # this is because I add a black square behind the green when moving so it overspills by 1

# -- Strings used in the program -- #

POINTS_MESSAGE:         .asciiz "POINTS: "
NEWLINE:                .asciiz "\n"
TOTAL_POINTS_MESSAGE:   .asciiz "TOTAL POINTS: "
TOTAL_JUMPS_MESSAGE:    .asciiz "TOTAL JUMPS: "
KIND_MESSAGE:           .asciiz "Better luck next time.\n"


.text
# -- MAIN FUNCTION -- #
# Has the game loop and sets the frames speed.

main:
    # CODE
    # s0 = 0xffff;
    # s2 = 0;
    # s3 = 252;
    # draw_dot(16128);
    # moving_dot(s3);
    # s6 = 0;
    # s7 = 0;
    #
    # REGISTERS
    # s0 - keyboard control register
    # s2 - total number of jumps
    # s3 - columns we are at start at the last column for green dot
    # s6 - green frame delay decreaser
    # s7 - totalPoints
    # a0 - parameter input
    lui  $s0, 0xffff                               # s0 = 0xFFFF0000 (keyboard control register)
    
    addi $s2, $zero, 0                             # s2 = total number of jumps
    addi $s3, $zero, 252                           # s3 = 252 (set green to start at far left column)
    addi $s6, $zero, 0                             # set the green frame delay decreaser
    addi $s7, $zero, 0                             # current points
    
    addi $a0, $zero, 16128                         # set input to row 63 to have dot not jump
    jal  draw_dot                                  # call function to draw
    
    add  $a0, $zero, $s3                           # set input to row 63 to have dot not jump
    jal  moving_dot                                # call function to draw

    # CODE
    # while (true) {
    #     ....
    # }
    #
    # This is an infinite loop. Will keep on going until the white and
    # green dots collide and if they do then the program ends.
    GAME_LOOP:
    # -- CHARACTER SECTION -- 
    
    # CODE
    # s4 = 0;
    # s1 = s0[1];
    # if (s1 == t0) {
    #     s0[1] = 0;
    #     s4 = 1;
    #     a0 = 15872;
    #     draw_dot(a0);
    #
    # REGISTERS
    # s0 - keyboard control register
    # s1 - the character from the keybaord
    # s4 - boolean check 1: character jumping 0: character not jumping
    # a0 - input to draw_dot
    # t0 - space decimal number
    addi $s4, $zero, 0                             # bool to check if jumped -- set to 0 initally
    
    lw   $s1, 4($s0)                               # read the actual typed character
    addi $t0, $zero, 32                            # holds the decimal number for ' '
    bne  $s1, $t0, GREEN_LOOP_START                # if (s1 != ' ') branch to GREEN_LOOP
    
    addi $s2, $s2, 1                               # increment the number of jumps
    sw   $zero, 4($s0)                             # clear the ' '
    addi $s4, $zero, 1                             # is jumping -- sets the bool to 1
    
    addi $a0, $zero, 15872                         # set input to row 62 to have dot jump
    jal  draw_dot                                  # call funciton to draw
    
    # -- GREEN CHARACTER SECTION -- #
    
    # CODE
    # s5 = 3;
    # while (s5 != 0) {
    #     s3 -= 4;
    #     a0 = s3;
    #     moving_dot(S3);
    #
    #     if (s3 == 0) {
    #         s3 = 252;
    #         s6 += 20;
    #         t2[0] = 0; // set far botton left corner to black
    #     } 
    # 
    # REGISTERS
    # s3 - offset starts at the far right bottom corner of screen (address)
    # s4 - boolean check 1: character jumping 0: character not jumping
    # s5 - green character frame counter, decreases as it loops
    # s6 - used to increase the speed of the frame
    # a0 - input to collision_check & moving_dot
    # t0 - DISPLAY base address
    # t1 - color black
    # t2 - base address of the far left corner
    
    GREEN_LOOP_START:
    addi $s5, $zero, 3                             # green frame counter (will decrease as it goes on)
    
    GREEN_LOOP:
    beq  $s5, $zero, DONE_GREEN                    # if (s0 == 0) ecit the loop and go to DONE_GREEN
    
    addi $s3, $s3, -4                              # s3 -= 4
    add  $a0, $zero, $s3                           # set input to row 63 to have dot not jump
    jal  moving_dot                                # call function to draw
    
    bne  $s3, $zero CHECK_GREEN_COLLISION          # if (s3 != 0) skip and go to CHECK_GREEN_COLLISION
    addi $s3, $zero, 252                           # set the start of the green block back at the front
    add  $s6, $s6, 20
    
    la   $t0, DISPLAY                              # Get the base address to draw
    addi $t1, $zero, 0                             # set color to black
    addi $t2, $t0, 16128                           # set dot to row 63
    sw   $t1, 0($t2)                               # draw to row 63, column a0
    
    # CODE
    # if (s4 != 0) {
    #     a0 = s3;
    #     collision_check(a0, a1, a2);
    # }
    #
    # v0 = update_points(a0, a1);
    #
    # if (v0 !- 0) {
    #     s7 += 1;
    # }
    #     
    # v0 = 32;
    # a0 = 250 - s6;
    # syscall;
    # s5 -= 1;
    # }
    #
    # if (s4 != 0) {
    #    draw_dot(16128); // have the dot not jump
    # }
    #
    # REGISTERS
    # s2 - total jumps
    # s3 - offset starts at the far right bottom corner of screen (address)
    # s4 - boolean check 1: character jumping 0: character not jumping
    # s5 - green character frame counter, decreases as it loops
    # s6 - used to increase the speed of the frame
    # s7 - total points
    # a0 - input to collision_check & moving_dot
    # t0 - DISPLAY base address
    # t1 - color black
    # t2 - base address of the far left corner
    CHECK_GREEN_COLLISION:
    bne $s4, $zero, NO_COLLISON_CHECK              # if (s4 == 0) skip the collision check
    
    add $a0, $zero, $s3                            # a0 = s3 which column
    add $a1, $zero, $s7                            # set the first parameter of collision check to totalPoints
    add $a2, $zero, $s2                            # set the second parameter of collision check to totalJumps
    jal collision_check
    
    NO_COLLISON_CHECK:
    
    add $a0, $zero, $s3                            # set the first parameter to what column green is in
    add $a1, $zero, $s7                            # set the second parameter to the number of current points
    
    jal update_points
    
    beq  $v0, $zero, UPDATE_FRAME                  # if v0 == 0 skip updating points
    addi $s7, $s7, 1                               # s7 += 1
 
    UPDATE_FRAME:
    # -- FRAME -- #
    addi $t0, $zero, 250                           # t0 - default speed of frame
    
    li   $v0, 32                                   # set code to sleep
    sub  $a0, $t0, $s6                             # set it to sleep for 0.5 sec
    syscall
    
    addi $s5, $s5, -1                              # decrement green frame counter
    j    GREEN_LOOP

    DONE_GREEN:
    beq  $s4, $zero, GAME_LOOP                     # if player not jumping then go back to the top of game loop
    
    addi $a0, $zero, 16128                         # set input to row 63 to have dot not jump
    jal  draw_dot                                  # call function to draw

    j    GAME_LOOP                                 # jump back to top of GAME_LOOP

    
    
# -- draw_dot -- #
# Draws the white dot either up or down depending on the rowAddress input.

.globl draw_dot
draw_dot:
    # CODE
    # void draw_dotint(int rowAddress) {
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    addiu $sp, $sp, -24                            # allocate space for the stack, 24 bytes -- 6 slots
    sw    $fp, 0($sp)                              # save the frame pointer of the caller
    sw    $ra, 4($sp)                              # save the return address
    addiu $fp, $sp, 24                             # set up the frame pointer to point to bottom of frame stack
    
    # CODE
    # t0 = DISPLAY[];
    # t3 = 16218
    # t1 = 0
    # if (rowAddress != 16128) {
    #     t2 = t0 + 100;
    #     t2 = t2 + 16128;
    #     t2[0] = t1;
    # }
    # else {
    #     t2 = t0 + 100;
    #     t2 = t2 + 15872;
    #     t2[0] = t1;
    # }
    # 
    # REGISTERS
    # t0 - base address for the display
    # t1 - color of pixel (black)
    # t2 - base address plus an offset to row 25 + row address constant
    # t3 - 16128, row address for the last row
    # a0 - rowAddress
    la   $t0, DISPLAY                              # Get the base address to draw
    addi $t3, $zero, 16128                         # t3 = 16128   (get to row 63)
    addi $t1, $zero, 0                             # set color to black
    
    beq  $a0, $t3, ERASE_TOP_DOT                   # if (rowAddress == 16128) go to ERASE_TOP_DOT
    addi $t2, $t0, 100                             # t2 address for the 25 column (25*4)
    addi $t2, $t2, 16128                           # t2 is address of where the dot will be
    sw   $t1, 0($t2)                               # draw to row 63, col 25
    j    DRAW
    
    ERASE_TOP_DOT:
    addi $t2, $t0, 100                             # t2 address for the 25 column (25*4)
    addi $t2, $t2, 15872                           # t2 is address of where the dot will be
    sw   $t1, 0($t2)                               # draw to row 62, col 25
    
    
    DRAW:
    # CODE
    # t1 = -1;
    # t2 = t0 + 100;
    # t2 = t2 + a0
    # t2[0] = t1;
    # 
    # REGISTERS
    # t0 - base address for the display
    # t1 - color of pixel (white)
    # t2 - base address plus an offset to row 25 + rowAddress
    # a0 - rowAddress
    addi $t1, $zero,-1                             # set color to white
    addi $t2, $t0, 100                             # t2 address for the 25 column (25*4)
    add  $t2, $t2, $a0                             # t2 is address of where the dot will be
    sw   $t1, 0($t2)                               # draw to row 62 or row 63, col 25
  
    DRAW_DONE:
    # CODE
    # }
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    lw    $fp, 0($sp)                              # restore fp to its original value
    lw    $ra, 4($sp)                              # restore ra to its original value
    addiu $sp, $sp, 24                             # free up the stack space
    jr    $ra                                      # return from the function 


# -- moving_dot -- #
# Logic for the green dot that moves from right to left. It makes the pixel to the right
# of the input 'column' black and the pixel at the 'column' green.

.globl moving_dot
moving_dot:
    # CODE
    # void moving_dot(int column) {
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    addiu $sp, $sp, -24                            # allocate space for the stack, 24 bytes -- 6 slots
    sw    $fp, 0($sp)                              # save the frame pointer of the caller
    sw    $ra, 4($sp)                              # save the return address
    addiu $fp, $sp, 24                             # set up the frame pointer to point to bottom of frame stack
    
    # CODE 
    # t0 = DISPLAY;
    # t1 = 0;
    # t2 = t0 + 16128; // (256 bytes * 63) + 16128
    # t2 = t2 + column;
    # DISPLAY[t2 + 4] = t1;
    # 
    # t1 = 65280;
    # DISPLAY[t2] = t1;
    #
    # REGISTERS
    # t0 - base address for the display
    # t1 - colors
    # t2 - address calculation
    la   $t0, DISPLAY                              # Get the base address to draw
    
    addi $t1, $zero, 0                             # set color to black
    addi $t2, $t0, 16128                           # set dot to row 63
    add  $t2, $t2, $a0                             # set column
    sw   $t1, 4($t2)                               # draw to row 63, column a0
    
    addi $t1, $zero, 65280                         # set color to green
    sw   $t1, 0($t2)                               # draw to row 63, column a0

    # CODE
    # }
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    lw    $fp, 0($sp)                              # restore fp to its original value
    lw    $ra, 4($sp)                              # restore ra to its original value
    addiu $sp, $sp, 24                             # free up the stack space
    jr    $ra   


# -- collision_check -- #
# Checks weather the white and green done collide. If so then the game ends. Calls the end_screen
# function and the print_stats function.

.globl collision_check
collision_check:
    # CODE
    # void collision_check(int greenCoord, int totalPoints, int totalJumps) {
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    addiu $sp, $sp, -24                            # allocate space for the stack, 24 bytes -- 6 slots
    sw    $fp, 0($sp)                              # save the frame pointer of the caller
    sw    $ra, 4($sp)                              # save the return address
    addiu $fp, $sp, 24                             # set up the frame pointer to point to bottom of frame stack
    
    # CODE
    # t0 = 100;
    # if (greenCoord == 100) {
    #     sys exit;
    # }
    # 
    # REGISTERS
    # t0 - 100        25th column
    # a0 - first input (greenCoord)
    # v0 - set the syscall type to end program
    addi $t0, $zero, 100               # 25 * 4 (checks the column)
    bne  $a0, $t0, COLLISION_CHECK_DONE            # if (greenCoord != 100) skip ending the program
    
    jal  end_screen                                # jump to end screen function
    
    add  $a0, $zero, $a1                           # set print_stats first param to totalPoints
    add  $a1, $zero, $a2                           # set print stats second param to totalJumps
    
    jal  print_stats                               # print stats
    
    li   $v0, 10                                   # end the program
    syscall
        
    COLLISION_CHECK_DONE:
    # CODE
    # }
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    lw    $fp, 0($sp)                              # restore fp to its original value
    lw    $ra, 4($sp)                              # restore ra to its original value
    addiu $sp, $sp, 24                             # free up the stack space
    jr    $ra   
    
    
# -- end_screen -- #
# Paints the screen red row by row starting at the bottom.   
    
.globl end_screen
end_screen:
    # CODE
    # void end_screen() {
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    addiu $sp, $sp, -24                            # allocate space for the stack, 24 bytes -- 6 slots
    sw    $fp, 0($sp)                              # save the frame pointer of the caller
    sw    $ra, 4($sp)                              # save the return address
    addiu $fp, $sp, 24                             # set up the frame pointer to point to bottom of frame stack
    
    # CODE
    # t0 = DISPLAY    // base address
    # t1 = 16380      // current offset last open slot in the bottom right corner
    # t2 = 0x00FF0000 // red
    # t6 = 63         // max outer loop interations dicates the number of times we loop over rows
    # t7 = 0          // current outer loop index
    #
    # REGISTERS
    # t0 - base address
    # t1 - urrent offset last open slot in the bottom right corner
    # t2 - red
    # t6 - max outer loop interations dicates the number of times we loop over rows
    # t7 - current outer loop index
    la    $t0, DISPLAY                             # Get the base address to draw
    addi  $t1, $zero, 16380                        # current color address
    addi  $t2, $zero, 0x00FF0000                   # set color to red
    
    addi  $t6, $zero, 63                           # max iterations for outer loop
    addi  $t7, $zero, 0                            # current iteration for outer loop
    
    # CODE
    # while (t6 < t7) {
    #     t3 = 63;
    #     t4 = 0;
    #      ... inner loop...
    # }
    # 
    # REGISTERS
    # t0 - base address
    # t1 - urrent offset last open slot in the bottom right corner
    # t2 - red
    # t3 - inner loop max iterations (since 64 columns)
    # t4 - current interation in the inner loop
    # t5 - various temporaries
    # t6 - max outer loop interations dicates the number of times we loop over rows
    # t7 - current outer loop index
    END_SCREEN_OUTERLOOP:
    
    slt  $t5, $t6 $t7                              # t5 = t6 < t7
    bne  $t5, $zero, END_SCREEN_DONE               # if (t6 > t7) exit loop
    
    addi $t3, $zero, 63                            # max iterations for inner loop
    addi $t4, $zero, 0                             # current iteration for inner loop
    
    # CODE
    # while (t3 < t7) {
    #     t8 = t0 + t1;
    #     t1 -= 4;
    #     t8[0] = t2;
    #     t4 += 1;
    # }
    # 
    # REGISTERS
    # t0 - base address
    # t1 - urrent offset last open slot in the bottom right corner
    # t2 - red
    # t3 - inner loop max iterations (since 64 columns)
    # t4 - current interation in the inner loop
    # t5 - various temporaries
    # t6 - max outer loop interations dicates the number of times we loop over rows
    # t7 - current outer loop index
    # t8 - t0 (base address) + t1 (current offset)
    END_SCREEN_ROW_LOOP:
    
    slt  $t5, $t3, $t4                             # t5 = t3 < t4
    bne  $t5, $zero, DONE_ROW_LOOP                 # if (t3 > t4) exit loop
    
    add  $t8, $t0, $t1                             # t8 = t0 (base address) + t1 (current offset)
    addi $t1, $t1, -4                              # go backwards in the screen (t1) is the current offset
    sw   $t2, 0($t8)                               # draw to row, column
   
    addi $t4, $t4, 1                               # current iteration for inner loop + 1
    
    j END_SCREEN_ROW_LOOP
    
    # CODE
    # v0 = 32
    # a0 = 50
    # syscall
    #
    # REGISTERS
    # t0 - base address
    # t1 - urrent offset last open slot in the bottom right corner
    # t2 - red
    # t3 - inner loop max iterations (since 64 columns)
    # t4 - current interation in the inner loop
    # t5 - various temporaries
    # t6 - max outer loop interations dicates the number of times we loop over rows
    # t7 - current outer loop index
    # t8 - t0 (base address) + t1 (current offset)
    DONE_ROW_LOOP:
    li   $v0, 32                                   # set code to sleep
    li   $a0, 50                                   # set it to sleep for 50 ms
    syscall
    
    addi $t7, $t7, 1                               # current iteration for outer loop + 1
    
    j    END_SCREEN_OUTERLOOP
    
    END_SCREEN_DONE:
    # CODE
    # }
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    lw    $fp, 0($sp)                              # restore fp to its original value
    lw    $ra, 4($sp)                              # restore ra to its original value
    addiu $sp, $sp, 24                             # free up the stack space
    jr    $ra   
    

# -- update_points -- #
# Prints the total points after a successful jump over the green dot. Returns 1 if it was the successful
# jump. 0 if it was not a jump over the green dot.    
      
.globl update_points
update_points:
    # CODE
    # int update_points(int greenCoord, int currentPoints) {
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    addiu $sp, $sp, -24                            # allocate space for the stack, 24 bytes -- 6 slots
    sw    $fp, 0($sp)                              # save the frame pointer of the caller
    sw    $ra, 4($sp)                              # save the return address
    addiu $fp, $sp, 24                             # set up the frame pointer to point to bottom of frame stack
    
    # CODE
    # v0 = 0;
    # t0 = 100
    # if (greenCoord == t0) {
    #     print("POINTS: " + (currentPoints + 1) + "\n");
    #     v0 = 1;
    # }
    # return v0;
    #
    # REGISTERS
    # t0 - holds the column we want to check
    # v0 - return value, 1 if we need to update points 0 if we do not
    # a0 - first paremeter into function (greenCoord, column the green dot is in) and for syscalls
    # a1 - second paramter into function (currentPoints)
    add  $v0, $zero, 0                             # by default it says to not update points
    addi $t0, $zero, 100                           # 25 * 4 (checks the column)
    bne  $a0, $t0, END_UPDATE_POINTS               # if (greenCoord != 100) skip ending the program 
    
    addi $v0, $zero, 4                             # print string
    la   $a0, POINTS_MESSAGE                       # set string to POINTS_MESSAGE
    syscall
    
    addi $v0, $zero, 1                             # print int
    addi $a0, $a1, 1                               # update points + 1, will update outside of function as well
    syscall
    
    addi $v0, $zero, 4                             # print string
    la   $a0, NEWLINE                              # set string to NEWLINE
    syscall
    
    add  $v0, $zero, 1                             # return 1, says to update points
    
    END_UPDATE_POINTS:
    # CODE
    # }
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    lw    $fp, 0($sp)                              # restore fp to its original value
    lw    $ra, 4($sp)                              # restore ra to its original value
    addiu $sp, $sp, 24                             # free up the stack space
    jr    $ra   

    
# -- print_stats -- #
# Prints the total number of points, jumps and a 'Better luck next time.' message.    
    
.globl print_stats
print_stats:
    # CODE
    # int print_stats(totalPoints, totalJumps) {
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    addiu $sp, $sp, -24                            # allocate space for the stack, 24 bytes -- 6 slots
    sw    $fp, 0($sp)                              # save the frame pointer of the caller
    sw    $ra, 4($sp)                              # save the return address
    addiu $fp, $sp, 24                             # set up the frame pointer to point to bottom of frame stack
    
    # CODE
    # t0 = a0;
    # print("TOTAL POINTS: " + t0 + "\n");
    # print("TOTAL JUMPS: " + a1 " "\n");
    # print("Better luck next time.");
    #
    # REGISTERS
    # t0 - holds a0 the totalPoints parameter
    # a0 - first parameter (totalPoints) and input for syscalls
    # a1 - second parameter (totalJumps)
    # v0 - sets what type the syscalls must print
    
    add  $t0, $zero, $a0                           # save a0 to a temp t0
    
    addi $v0, $zero, 4                             # print string
    la   $a0, TOTAL_POINTS_MESSAGE                 # set string to TOTAL_POINTS_MESSAGE
    syscall
    
    addi $v0, $zero, 1                             # print int
    add  $a0, $t0, $zero                           # update totalPoints
    syscall
    
    addi $v0, $zero, 4                             # print string
    la   $a0, NEWLINE                              # set string to NEWLINE
    syscall
    
    addi $v0, $zero, 4                             # print string
    la   $a0, TOTAL_JUMPS_MESSAGE                  # set string to TOTAL_JUMPS_MESSAGE    
    syscall
    
    addi $v0, $zero, 1                             # print int
    add  $a0, $a1, $zero                           # update totalJumps
    syscall
    
    addi $v0, $zero, 4                             # print string
    la   $a0, NEWLINE                              # set string to NEWLINE
    syscall
    
    addi $v0, $zero, 4                             # print string
    la   $a0, KIND_MESSAGE                         # set string to TOTAL_JUMPS_MESSAGE    
    syscall
    
    # CODE
    # }
    #
    # REGISTERS
    # sp - stack pointer
    # fp - frame pointer
    # ra - return address
    lw    $fp, 0($sp)                              # restore fp to its original value
    lw    $ra, 4($sp)                              # restore ra to its original value
    addiu $sp, $sp, 24                             # free up the stack space
    jr    $ra   
