/*
 * Author: Daniel Shapiro
 * Instructor: Professor Lewis
 * Course: CSC 252, Spring 2026
 * Program Description: This program implements methods to simulate the CPU and how the
 *      computer reads and performs instructions. The methods are:
 *      'getInstruction' gets the instruction from an address
 *      'extract_instructionFields' decodes the instruction and fills out a struct call InstructionFields.
 *      'fill_CPUControl' fills out another struct called CPUControl based on the instruction, and it defines
 *      the Control bits for the CPU.
 *      'getALUinput1' gets the first input to the ALU, 'getALUinput2' gets the second input to the ALU; behaves as the
 *      MUX infront of the second input for the ALU.
 *      'execute_ALU' does the operations of the ALU: AND, OR, ADD, LESS THAN, XOR.
 *      'execute_MEM' is the Data Memory chunk. Determines if memory needs to be used, written too or read from. It then
 *      performs the operation it needs to based on the control bits.
 *      'getNextPC' get the next instruction. This is also where it hands the jumps and branches.
 *      'execute_updateRegs' this updates the correct register the instruction wants to be written too. If it's a
 *      instruction that does not need a register written too then no writing to a register happens.
 *
 *      Extra Instructions: andi, bne, lb
 */

#include "sim4.h"

/*
 * getInstruction(curPC, *instructionMemory) -- Takes in a WORD 'curPC' and a
 *      WORD pointer 'instructionMemory'. Method index's instructionMemory
 *      using 'curPc'/4 since the instructionMemory is an array of WORDS
 *      and 'curPc' increments in bytes. This is a workaround for the
 *      simulation. Method returns WORD the instruction itself.
 */
WORD getInstruction(WORD curPC, WORD *instructionMemory) {
    return instructionMemory[curPC/4];
}

/*
 * extract_instructionFields(instruction, *fieldsOut) -- Takes in a WORD 'instruction' and a
 *      InstructionFields pointer 'fieldsOut'. Method masks the 'instruction' WORD and
 *      shifts the bits to the left to get the specific bits for the fields of the
 *      'fieldsOut' parameter. Method does not return anything.
 */
void extract_instructionFields(WORD instruction, InstructionFields *fieldsOut) {
    fieldsOut->opcode = (instruction & 0xFC000000) >> 26;
    fieldsOut->rs = (instruction & 0x03E00000) >> 21;
    fieldsOut->rt = (instruction & 0x001F0000) >> 16;
    fieldsOut->rd = (instruction & 0x0000F800) >> 11;
    fieldsOut->shamt = (instruction & 0x000007C0) >> 6;
    fieldsOut->funct = (instruction & 0x0000003F);
    fieldsOut->imm16 = (instruction & 0x0000FFFF);

    // This is for ADDI -- EXTRA -- no sign extend
    if (fieldsOut->opcode == 0x0c) {
        fieldsOut->imm32 = fieldsOut->imm16;
    }
    // Else we want sign extend
    else {
        fieldsOut->imm32 = signExtend16to32(fieldsOut->imm16);
    }
    fieldsOut->address = (instruction & 0x03FFFFFF);
}
/*
 * fill_CPUControl(*fields, *controlOut) -- Method takes in two pointers 'fields' which points to the
 *      InstructionFields struct that was filled out in extract_instructionFields method. The other
 *      pointer 'controlOut' points to the CPUControl struct and this method fills out all of the
 *      fields for that struct. This method looks at the opcode and determines which instruction
 *      it is and then sets the correct control bits for the CPU. Method returns a 1 is the instruction
 *      is recognized, 0 if the opcode or funct is invalid. The ALU.op can be: AND - 0, OR - 1, ADD - 2,
 *      LESS THAN - 3, XOR - 4. The instructions that this method implements is: add, addu, sub, addi, addiu,
 *      and, or, xor, slt, slti, lw, sw, beq, j. The extra instructions that are implemented are andi, bne and lb.
 *      With these extra instructions comes with extra control wires. 'extra1' if it is 1 it says that the branch is
 *      a bne if 0 its a beq. 'extra2' if it is a 1 it says that it is a lb, 0 it is not.
 */
int  fill_CPUControl(InstructionFields *fields, CPUControl *controlOut) {

    // If R format
    if (fields->opcode == 0x0) {

        // add OR addu
        if ((fields->funct == 0x20) || (fields->funct == 0x21)) {
            controlOut->ALUsrc = 0; // mux control second input
            controlOut->ALU.op = 2;
            controlOut->ALU.bNegate = 0;

            controlOut->memRead = 0;
            controlOut->memWrite = 0;
            controlOut->memToReg = 0;

            controlOut->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            controlOut->regWrite = 1; // writing to a destination

            controlOut->branch = 0;
            controlOut->jump = 0;

            controlOut->extra1 = 0;
            controlOut->extra2 = 0;
            controlOut->extra3 = 0;

            return 1; // opcode and funct was recognized
        }

        // sub OR subu
        if ((fields->funct == 0x22) || (fields->funct == 0x23)) {
            controlOut->ALUsrc = 0; // mux control second input
            controlOut->ALU.op = 2;
            controlOut->ALU.bNegate = 1;

            controlOut->memRead = 0;
            controlOut->memWrite = 0;
            controlOut->memToReg = 0;

            controlOut->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            controlOut->regWrite = 1; // writing to a destination

            controlOut->branch = 0;
            controlOut->jump = 0;

            controlOut->extra1 = 0;
            controlOut->extra2 = 0;
            controlOut->extra3 = 0;

            return 1; // opcode and funct was recognized
        }

        // and
        if (fields->funct == 0x24) {
            controlOut->ALUsrc = 0; // mux control second input
            controlOut->ALU.op = 0;
            controlOut->ALU.bNegate = 0;

            controlOut->memRead = 0;
            controlOut->memWrite = 0;
            controlOut->memToReg = 0;

            controlOut->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            controlOut->regWrite = 1; // writing to a destination

            controlOut->branch = 0;
            controlOut->jump = 0;

            controlOut->extra1 = 0;
            controlOut->extra2 = 0;
            controlOut->extra3 = 0;

            return 1; // opcode and funct was recognized
        }

        // or
        if (fields->funct == 0x25) {
            controlOut->ALUsrc = 0; // mux control second input
            controlOut->ALU.op = 1;
            controlOut->ALU.bNegate = 0;

            controlOut->memRead = 0;
            controlOut->memWrite = 0;
            controlOut->memToReg = 0;

            controlOut->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            controlOut->regWrite = 1; // writing to a destination

            controlOut->branch = 0;
            controlOut->jump = 0;

            controlOut->extra1 = 0;
            controlOut->extra2 = 0;
            controlOut->extra3 = 0;

            return 1; // opcode and funct was recognized
        }

        // xor
        if (fields->funct == 0x26) {
            controlOut->ALUsrc = 0; // mux control second input
            controlOut->ALU.op = 4;
            controlOut->ALU.bNegate = 0;

            controlOut->memRead = 0;
            controlOut->memWrite = 0;
            controlOut->memToReg = 0;

            controlOut->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            controlOut->regWrite = 1; // writing to a destination

            controlOut->branch = 0;
            controlOut->jump = 0;

            controlOut->extra1 = 0;
            controlOut->extra2 = 0;
            controlOut->extra3 = 0;

            return 1; // opcode and funct was recognized
        }

        // slt
        if (fields->funct == 0x2A) {
            controlOut->ALUsrc = 0; // mux control second input
            controlOut->ALU.op = 3;
            controlOut->ALU.bNegate = 1;

            controlOut->memRead = 0;
            controlOut->memWrite = 0;
            controlOut->memToReg = 0;

            controlOut->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            controlOut->regWrite = 1; // writing to a destination

            controlOut->branch = 0;
            controlOut->jump = 0;

            controlOut->extra1 = 0;
            controlOut->extra2 = 0;
            controlOut->extra3 = 0;

            return 1; // opcode and funct was recognized
        }

        return 0; // if funct is incorrect
    }

    // I-Format

    // j
    if (fields->opcode == 0x02) {
        controlOut->ALUsrc = 0; // mux control second input
        controlOut->ALU.op = 0;
        controlOut->ALU.bNegate = 0;

        controlOut->memRead = 0;
        controlOut->memWrite = 0;
        controlOut->memToReg = 0;

        controlOut->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 0; // writing to a destination

        controlOut->branch = 0;
        controlOut->jump = 1;

        controlOut->extra1 = 0;
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // beq
    if (fields->opcode == 0x04) {
        controlOut->ALUsrc = 0; // mux control second input
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 1;

        controlOut->memRead = 0;
        controlOut->memWrite = 0;
        controlOut->memToReg = 0;

        controlOut->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 0; // writing to a destination

        controlOut->branch = 1;
        controlOut->jump = 0;

        controlOut->extra1 = 0; // says hey we DO NOT want to do bne
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // addi OR addiu
    if ((fields->opcode == 0x8) || (fields->opcode == 0x9)) {
        controlOut->ALUsrc = 1; // mux control second input
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;

        controlOut->memRead = 0;
        controlOut->memWrite = 0;
        controlOut->memToReg = 0;

        controlOut->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 1; // writing to a destination

        controlOut->branch = 0;
        controlOut->jump = 0;

        controlOut->extra1 = 0;
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // slti
    if (fields->opcode == 0x0a) {
        controlOut->ALUsrc = 1; // mux control second input
        controlOut->ALU.op = 3;
        controlOut->ALU.bNegate = 1;

        controlOut->memRead = 0;
        controlOut->memWrite = 0;
        controlOut->memToReg = 0;

        controlOut->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 1; // writing to a destination

        controlOut->branch = 0;
        controlOut->jump = 0;

        controlOut->extra1 = 0;
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // sw
    if (fields->opcode == 0x2b) {
        controlOut->ALUsrc = 1; // mux control second input
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;

        controlOut-> memRead = 0;
        controlOut-> memWrite = 1;
        controlOut-> memToReg = 0; // ignored

        controlOut-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 0; // writing to a destination

        controlOut->branch = 0;
        controlOut->jump = 0;

        controlOut->extra1 = 0;
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // lw
    if (fields->opcode == 0x23) {
        controlOut->ALUsrc = 1; // mux control second input
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;

        controlOut-> memRead = 1;
        controlOut-> memWrite = 0;
        controlOut-> memToReg = 1; // write to register

        controlOut-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 1; // writing to a destination

        controlOut->branch = 0;
        controlOut->jump = 0;

        controlOut->extra1 = 0;
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // andi -- EXTRA INSTRUCTION
    if (fields->opcode == 0x0c) {
        controlOut->ALUsrc = 1; // mux control second input
        controlOut->ALU.op = 0; // AND
        controlOut->ALU.bNegate = 0;

        controlOut-> memRead = 0;
        controlOut-> memWrite = 0;
        controlOut-> memToReg = 0; // write to register

        controlOut-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 1; // writing to a destination

        controlOut->branch = 0;
        controlOut->jump = 0;

        controlOut->extra1 = 0;
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // bne -- EXTRA INSTRUCTION
    if (fields->opcode == 0x05) {
        controlOut->ALUsrc = 0; // mux control second input
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 1;

        controlOut->memRead = 0;
        controlOut->memWrite = 0;
        controlOut->memToReg = 0;

        controlOut->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 0; // writing to a destination

        controlOut->branch = 1;
        controlOut->jump = 0;

        controlOut->extra1 = 1; // says hey we want to do bne
        controlOut->extra2 = 0;
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    // lb -- EXTRA INSTRUCTION
    if (fields->opcode == 0x20) {
        controlOut->ALUsrc = 1; // mux control second input
        controlOut->ALU.op = 2;
        controlOut->ALU.bNegate = 0;

        controlOut-> memRead = 1;
        controlOut-> memWrite = 0;
        controlOut-> memToReg = 1; // write to register

        controlOut-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        controlOut->regWrite = 1; // writing to a destination

        controlOut->branch = 0;
        controlOut->jump = 0;

        controlOut->extra1 = 0;
        controlOut->extra2 = 1; // says that we are loading a byte
        controlOut->extra3 = 0;

        return 1; // opcode was recognized
    }

    return 0;
}

/*
 * getALUinput1(*controlIn, fieldsIn, rsVal, rtVal, reg32, reg33, oldPC) -- Takes in a CPUControl pointer 'controlIn',
 *      InstructionFields pointer 'fieldsIn', 5 WORDS: 'rsVal', 'rtVal', 'reg32', 'reg33' and 'oldPC'. This determines
 *      what the first input is for the ALU. Since the first input does not have a mux in front of it the first
 *      input is just the rsVal. This method returns a WORD the 'rsVal'.
 */
WORD getALUinput1(CPUControl *controlIn,
                  InstructionFields *fieldsIn,
                  WORD rsVal, WORD rtVal, WORD reg32, WORD reg33,
                  WORD oldPC) {
    return rsVal;
}

/*
 * getALUinput2(*controlIn, fieldsIn, rsVal, rtVal, reg32, reg33, oldPC) -- Takes in a CPUControl pointer 'controlIn',
 *      InstructionFields pointer 'fieldsIn', 5 WORDS: 'rsVal', 'rtVal', 'reg32', 'reg33' and 'oldPC'. This determines
 *      what the second input is for the ALU. This method behaves as the MUX in front of the ALU. If the ALUsrc is 0
 *      then it is a R-Format so method returns the WORD 'rsVal' else it returns the WORD the 16 bit constant sign
 *      extended.
 */
WORD getALUinput2(CPUControl *controlIn,
                  InstructionFields *fieldsIn,
                  WORD rsVal, WORD rtVal, WORD reg32, WORD reg33,
                  WORD oldPC) {

    // Based on the ALUSrc value, if 0 then its R-format
    if (controlIn->ALUsrc == 0) {
        return rtVal;
    }
    return fieldsIn->imm32; // return the constant
}

/*
 * execute_ALU(*controlIn, input1, input2, *aluResultOut) -- Method takes in a CPUControl pointer 'controlIn', two
 *      WORDS: 'input1' & 'input2' and a ALUResult pointer 'aluResultOut'. Method performs the ALU and sets the correct
 *      output based on 'controlIn'. Method does not return, only modifies 'aluResultOut'.
 *
 */
void execute_ALU(CPUControl *controlIn,
                 WORD input1, WORD input2,
                 ALUResult  *aluResultOut) {

    // AND
    int andResult = input1 & input2;

    // OR
    int orResult = input1 | input2;

    // ADD
    int addORsubResult;

    if (controlIn->ALU.bNegate == 0) {
        addORsubResult = input1 + input2;
    }
    // SUB
    else {
        addORsubResult = input1 - input2;
    }

    // Set Less than
    int sltResult = input1 < input2;

    // XOR
    int xorResult = input1 ^ input2;

    // OUTPUT -- AND
    if (controlIn->ALU.op == 0) {
        aluResultOut->result = andResult;
        aluResultOut->extra = 0;
        aluResultOut->zero = (andResult == 0);
        return;
    }

    // OUTPUT -- OR
    if (controlIn->ALU.op == 1) {
        aluResultOut->result = orResult;
        aluResultOut->extra = 0;
        aluResultOut->zero = (orResult == 0);
        return;
    }

    // OUTPUT -- ADD/ SUB
    if (controlIn->ALU.op == 2) {
        aluResultOut->result = addORsubResult;
        aluResultOut->extra = 0;
        aluResultOut->zero = (addORsubResult == 0);
        return;
    }

    // OUTPUT -- LESS THAN
    if (controlIn->ALU.op == 3) {
        aluResultOut->result = sltResult;
        aluResultOut->extra = 0;
        aluResultOut->zero = (sltResult == 0);
        return;
    }

    // OUTPUT -- XOR
    if (controlIn->ALU.op == 4) {
        aluResultOut->result = xorResult;
        aluResultOut->extra = 0;
        aluResultOut->zero = (xorResult == 0);
    }
}

/*
 * execute_MEM(*controlIn, *aluResultIn, rsVal, rtVal, *memory, *resultOut) -- Takes in a CPUControl pointer
 *      'controlIn', ALUResult pointer 'aluResultIn;, two WORDs 'rsVal' & 'rtVal', WORD pointer 'memory' and MEMResult
 *      pointer 'resultOut'. This method acts as the Data Memory Unit. The 'controlIn' determines if we need to read,
 *      write or neither. If we write or neither happens the 'resultOut' is set to 0. 'resultOut' is only set if it
 *      reads a value from memory. Method does not return anything.
 */
void execute_MEM(CPUControl *controlIn,
                 ALUResult  *aluResultIn,
                 WORD        rsVal, WORD rtVal,
                 WORD       *memory,
                 MemResult  *resultOut) {

    // controlIn determines if we are using memory or not

    // If we need to write
    if (controlIn->memWrite == 1) {

        // *memory is an array of words, since its array of words & address array of bytes need to divide by 4
        memory[(aluResultIn->result) / 4] = rtVal;
        resultOut->readVal = 0;
        return;
    }

    // If we need to read
    if (controlIn->memRead == 1) {

        if (controlIn->extra2 == 1) {
            char* memBytes = (char*)memory; // cast it to be an array of bytes to then index the byte itself
            resultOut->readVal = (int)memBytes[aluResultIn->result]; // cast back to int
            return;
        }
        else {
            resultOut->readVal = memory[(aluResultIn->result) / 4];
            return;
        }
    }
    resultOut->readVal = 0; // nothing happens.
}

/*
 * getNextPC(*fields, *controlIn, aluZero, rsVal, rtVal,oldPC) -- Takes in an InstructionFields pointer 'fields', a
 *      CPUControl pointer 'controlIn', integer 'aluZero', three WORDs 'rsVal', 'rtVal' and 'oldPC'. Method determines
 *      what the next instruction will be. First it determines what PC+4 will be then it checks if it needs to branch or
 *      jump. If it needs to do one of those it does the operations to find where to go. If not then it just goes to
 *      PC+4. Method returns a WORD, address to the next instruction.
 */
WORD getNextPC(InstructionFields *fields, CPUControl *controlIn, int aluZero,
               WORD rsVal, WORD rtVal,
               WORD oldPC) {

    // Get the next instruction
    int nextInstruction = oldPC + 4;

    // Do we branch?
    if (controlIn->branch == 1) {
        int constantShifted = fields->imm32 << 2; // shift the constant
        int ALUaddBranch = constantShifted + nextInstruction;

        // If the ALU result is zero BRANCH and it is beq
        if ((aluZero == 1) && (controlIn->extra1 == 0)) {
            return ALUaddBranch;
        }

        // If the ALU result is NOT zero BRANCH and it is bne
        if ((aluZero == 0) && (controlIn->extra1 == 1)) {
            return ALUaddBranch;
        }
    }

    if (controlIn->jump == 1) {
        int shiftedJaddress = fields->address << 2;
        int upperfourPCbits = nextInstruction & 0xf0000000;
        return upperfourPCbits + shiftedJaddress;
    }

    return nextInstruction; // No jump or branch so return next instruction
}

/*
 * execute_updateRegs(*fields, *controlIn, *aluResultIn, *memResultIn,*regs) -- Takes in InstructionFields pointer
 *      'fields', CPUControl pointer 'controlIn', ALUResult pointer 'aluResultIn', MEMResult pointer 'memResultIn' and
 *      array of WORDs pointer 'regs'. This method performs the writeback phase of the CPU. First it determines the
 *      MemtoReg MUX and what is the output of that MUX. Next it then determines if writing to a register needs to
 *      happen. If so then it determines which register to write. If not nothing happens. Method does not return
 *      anything. It just manipulates the structs.
 */
void execute_updateRegs(InstructionFields *fields, CPUControl *controlIn,
                        ALUResult  *aluResultIn, MemResult *memResultIn,
                        WORD       *regs) {

    int whatBeingStored; // is either from memory or the ALU

    // from memory
    if (controlIn->memToReg == 1) {
        whatBeingStored = memResultIn->readVal;

    }
    // not using memory only the ALU
    else {
        whatBeingStored = aluResultIn->result;
    }

    // Determine if we need to write to a register
    if (controlIn->regWrite == 1) {
        // use rt
        if (controlIn->regDst == 0) {
            regs[fields->rt] = whatBeingStored;
        }

        // use rd
        else {
            regs[fields->rd] = whatBeingStored;
        }
    }
}