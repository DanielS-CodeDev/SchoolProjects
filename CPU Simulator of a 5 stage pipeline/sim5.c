/*
 * Author: Daniel Shapiro
 * Instructor: Professor Lewis
 * Program Title: sim5.c
 * Program Description: This program simulates a 5 stage pipeline CPU. The IF stage is given to use from the test
 *      cases. The first method extracts the opcode and sets instruction fields. The second instruction determines if
 *      we need to stall. We only stall if an instruction 2 cycles ahead is modifying a SW rt value or the instruction
 *      ahead of us is a LW and the current instruction in the ID phase needs the lw value so we need to stall. Other
 *      functions include branch logic, execution logic of the ID, EX, MEM and WB phases and also the MUXs in front
 *      of each ALU input in the EX stage.
 */

#include "sim5.h"
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

    fieldsOut->imm32 = signExtend16to32(fieldsOut->imm16);

    fieldsOut->address = (instruction & 0x03FFFFFF);
}

/*
 * IDtoIF_get_stall(fields, old_idex, old_exmem) -- Takes in a InstructionFields pointer
 *      'fields', a ID_EX pointer 'old_idex' and a EX_MEM pointer 'old_exmem'. This method
 *      determines if we need to stall or not. The first check is to check if our current
 *      instruction is a SW then is the rt value being updated and is being updated in two
 *      clock cycles ahead in the EX/MEM pipeline then we must stall. The next check is to
 *      determine if the ID/EX pipeline register holds a memRead, if so that means that a
 *      lw is one instruction ahead so we must stall. Next we check if the LW ahead rt is
 *      the same as the input rs, if so we stall. The next thing we check is if the lw rt
 *      is the same as the input as the current instructions rt and if so we need to check
 *      if we need to stall. Most I-format instructions use rt as a destination reg. so
 *      we do not need to stall if the instruction is the few specific I-format instructions.
 *      If it is not one of those then we need to stall. To signal to stall this function
 *      returns a int (bool) 1 that we need to stall and a 0 not stalling.
 */
int IDtoIF_get_stall(InstructionFields *fields, ID_EX  *old_idex, EX_MEM *old_exmem) {

    // Check the sw case if an instruction in exmem is going to write on the input for sw
    if (fields->opcode == 0x2b) {

        int targetReg = -1;

        // Checks 1 instruction ahead

        // Means that the writeReg is rt
        if (old_idex->regDst == 0) {
            targetReg = old_idex->rt;
        }

        // Means that the writeReg is rd
        if (old_idex->regDst == 1) {
            targetReg = old_idex->rd;
        }

        if (targetReg == fields->rt) {
            return 0;
        }

        // checks 2 instructions ahead


        if (fields->rt == old_exmem->writeReg) {

            // When the 2 steps ahead is a sw
            if (old_exmem->memWrite == 1) {
                return 0;
            }
            return 1;
        }

    }

    // If lw is not ahead do not stall
    if (old_idex->memRead == 0) {       // this is the issue it should go in here
        return 0;
    }

    // If the lw rt (dest) is the same as the input rs stall
    if (old_idex->rt == fields->rs) {
        return 1;
    }

    // If the lw rt (dest) is the same as the input rt
    if (old_idex->rt == fields->rt) {

        // Check the opcode if I-format that uses rt as a dest no stall
        if ((fields->opcode == 0x08) || (fields->opcode == 0x09)) { // checks addi and addiu
            return 0;
        }

        if (fields->opcode == 0x0a) { // checks slti
            return 0;
        }

        if (fields->opcode == 0x0c) { // checks andi
            return 0;
        }

        if (fields->opcode == 0x0d) { // checks ori
            return 0;
        }

        if (fields->opcode == 0x0f) { // checks lui
            return 0;
        }

        if (fields->opcode == 0x23) { // checks lw -- rt for lw is a destination
            return 0;
        }

        if ((fields->opcode == 0x2b) && (old_idex->memRead == 1)) {
            return 0;
        }

        return 1; // else return to stall

    }
    return 0; // if unknown
}

/*
 * IDtoIF_get_branchControl(fields, rsVal, rtVal) -- Takes in a InstructionFields
 *      pointer 'fields' and 2 words 'rsVal' and 'rtVal'. This method determines
 *      based on opcode if we need to jump or if the opcode is a branch if we
 *      should actually branch. Method returns an integer. 1 we branch/jump, 0
 *      we do not.
 */
int IDtoIF_get_branchControl(InstructionFields *fields, WORD rsVal, WORD rtVal) {

    // If this is a jump instruction j
    if (fields->opcode == 0x02) {
        return 2; // absolute jump calc jumpAddr()
    }

    // if this is a beq check if it can branch
    if (fields->opcode == 0x04) {
        if ((rsVal - rtVal) == 0) {
            return 1; // branch
        }
        return 0; // don't branch
    }

    // if this is a bne check if it can branch
    if (fields->opcode == 0x05) {
        if ((rsVal - rtVal) != 0) {
            return 1; // branch
        }
        return 0; // don't branch
    }
    return 0; // return 0 for non-branching opcodes
}

/*
* calc_branchAddr(pcPlus4, fields) -- Takes in a WORD 'pcPlus4' and a
 *      InstructionFields pointer 'fields' method calculates the
*       branch address. Returns a WORD of the address that needs to
 *      be branched too.
 */
WORD calc_branchAddr(WORD pcPlus4, InstructionFields *fields) {
    int constantShifted = fields->imm32 << 2; // shift the constant
    return constantShifted + pcPlus4;
}

/*
 * calc_jumpAddr (pcPlus4, fields) -- Takes in a WORD 'pcPlus4' and a
 *      InstructionFields pointer 'fields' method calculates the
 *      jump address. Returns a WORD of the address that needs to
 *      be jumped too.
 */
WORD calc_jumpAddr  (WORD pcPlus4, InstructionFields *fields) {
    int shiftedJaddress = fields->address << 2;
    int upperfourPCbits = pcPlus4 & 0xf0000000;
    return upperfourPCbits + shiftedJaddress;
}

/*
 * stall(IDstall, new_idex) -- Takes in an integer (bool) 'IDstall' and an
 *      ID_EX 'new_idex' and implements a stall by setting all the
 *      fields for the ID/EX pipeline regiester to all 0's. Function does
 *      not return anything. Used in execute_ID().
 */
void stall(int IDstall, ID_EX *new_idex) {
    // If a stall it pushes a stall
    new_idex->rs = 0; new_idex->rt = 0; new_idex->rd = 0;
    new_idex->rsVal = 0; new_idex->rtVal = 0;
    new_idex->imm16 = 0; new_idex->imm32 = 0;
    new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

    new_idex->ALUsrc = 0; // mux control second input
    new_idex->ALU.op = 0;
    new_idex->ALU.bNegate = 0;

    new_idex->memRead = 0;
    new_idex->memWrite = 0;
    new_idex->memToReg = 0;

    new_idex->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
    new_idex->regWrite = 0; // writing to a destination
}

/*
 * execute_ID(IDstall, fieldsIn, pcPlus4, rsVal, rtVal, new_idex) -- Takes in an integer
 *      'Idstall', a InstructionFields pointer 'fieldsIn', 3 WORDs 'pcPlus4', 'rsVal',
 *       'rtVal' and a ID_EX 'new_idex'. This function decodes the opcode's and if need
 *       be the funct. Once the opcode and/or the funct is decoded it fills in the
 *       'new_idex'. Function return 1 if the opcode/ funct was detected, 0 if not.
 *       ALU.op: 0 - AND, 1 - OR, 2 - ADD/SUB, 3 - SLT, 4 - XOR, 5 - NOP and ALUsrc:
 *       0 - for register value, 1 - for imm16 sign extended, 2 - for imm16 zero extended.
 *       ALUsrc is the control for the second input to the ALU in EX phase.
 *       nor - 6 , lui - 7. Each bit represents one of the extra
 *       fields. Method is to simulate the execution of the ID phase of the pipline. The
 *       decoding of the instruction only happens if we do not stall. Branches everything
 *       is set to 0 since branching is done in the ID phase and not the EX anymore.
 */
int execute_ID(int IDstall, InstructionFields *fieldsIn, WORD pcPlus4, WORD rsVal, WORD rtVal,
                                                                        ID_EX *new_idex) {
    // If R format
    if (fieldsIn->opcode == 0x0) {

        // add OR addu
        if ((fieldsIn->funct == 0x20) || (fieldsIn->funct == 0x21)) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }

            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 2;
            new_idex->ALU.bNegate = 0;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized
        }

        // sub OR subu
        if ((fieldsIn->funct == 0x22) || (fieldsIn->funct == 0x23)) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }

            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

            new_idex->extra1 = 0;
            new_idex->extra2 = 0;
            new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 2;
            new_idex->ALU.bNegate = 1;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized
        }

        // and
        if (fieldsIn->funct == 0x24) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }

            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 0;
            new_idex->ALU.bNegate = 0;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized
        }

        // or
        if (fieldsIn->funct == 0x25) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }

            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 1;
            new_idex->ALU.bNegate = 0;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized
        }

        // xor
        if (fieldsIn->funct == 0x26) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }

            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 4;
            new_idex->ALU.bNegate = 0;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized
        }

        // slt
        if (fieldsIn->funct == 0x2A) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }
            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 1; // says that we will do nor
            new_idex->extra2 = 0;
            new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 3;
            new_idex->ALU.bNegate = 1;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized
        }

        // nor
        if (fieldsIn->funct == 0x27) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }
            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 6;
            new_idex->ALU.bNegate = 0;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized

        }

        // nop
        if (fieldsIn->funct == 0x00) {

            if (IDstall == 1) {
                stall(IDstall, new_idex); // if we need to stall, then stall
                return 1;
            }
            new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
            new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
            new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
            new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

            new_idex->ALUsrc = 0; // mux control second input
            new_idex->ALU.op = 5;
            new_idex->ALU.bNegate = 0;

            new_idex->memRead = 0;
            new_idex->memWrite = 0;
            new_idex->memToReg = 0;

            new_idex->regDst = 1; // which destination are we writing to, 1 - rd, 0 - rt
            new_idex->regWrite = 1; // writing to a destination

            return 1; // opcode and funct was recognized

        }

        return 0; // if funct is incorrect

    }

    // I-Format

    // j
    if (fieldsIn->opcode == 0x02) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = 0; new_idex->rt = 0; new_idex->rd = 0;
        new_idex->rsVal = 0; new_idex->rtVal = 0;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 0; // mux control second input
        new_idex->ALU.op = 0;
        new_idex->ALU.bNegate = 0;

        new_idex->memRead = 0;
        new_idex->memWrite = 0;
        new_idex->memToReg = 0;

        new_idex->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 0; // writing to a destination

        return 1; // opcode was recognized
    }
    // beq
    if (fieldsIn->opcode == 0x04) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = 0; new_idex->rt = 0; new_idex->rd = 0;
        new_idex->rsVal = 0; new_idex->rtVal = 0;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 0; // mux control second input
        new_idex->ALU.op = 0;
        new_idex->ALU.bNegate = 0;

        new_idex->memRead = 0;
        new_idex->memWrite = 0;
        new_idex->memToReg = 0;

        new_idex->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 0; // writing to a destination

        return 1; // opcode was recognized
    }

    // addi OR addiu
    if ((fieldsIn->opcode == 0x8) || (fieldsIn->opcode == 0x9)) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }

        new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
        new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 1; // mux control second input
        new_idex->ALU.op = 2;
        new_idex->ALU.bNegate = 0;

        new_idex->memRead = 0;
        new_idex->memWrite = 0;
        new_idex->memToReg = 0;

        new_idex->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 1; // writing to a destination

        return 1; // opcode was recognized
    }

    // slti
    if (fieldsIn->opcode == 0x0a) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
        new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 1; // mux control second input
        new_idex->ALU.op = 3;
        new_idex->ALU.bNegate = 1;

        new_idex->memRead = 0;
        new_idex->memWrite = 0;
        new_idex->memToReg = 0;

        new_idex->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 1; // writing to a destination

        return 1; // opcode was recognized
    }

    // sw
    if (fieldsIn->opcode == 0x2b) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
        new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 1; // mux control second input
        new_idex->ALU.op = 2;
        new_idex->ALU.bNegate = 0;

        new_idex-> memRead = 0;
        new_idex-> memWrite = 1;
        new_idex-> memToReg = 0; // ignored

        new_idex-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 0; // writing to a destination

        return 1; // opcode was recognized
    }

    // lw
    if (fieldsIn->opcode == 0x23) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }

        new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
        new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 1; // mux control second input
        new_idex->ALU.op = 2;
        new_idex->ALU.bNegate = 0;

        new_idex-> memRead = 1;
        new_idex-> memWrite = 0;
        new_idex-> memToReg = 1; // write to register

        new_idex-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 1; // writing to a destination

        return 1; // opcode was recognized
    }

    // andi
    if (fieldsIn->opcode == 0x0c) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
        new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 2; // mux control second input -- 2 for 0 extended
        new_idex->ALU.op = 0; // AND
        new_idex->ALU.bNegate = 0;

        new_idex-> memRead = 0;
        new_idex-> memWrite = 0;
        new_idex-> memToReg = 0; // write to register

        new_idex-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 1; // writing to a destination

        return 1; // opcode was recognized
    }

    // bne
    if (fieldsIn->opcode == 0x05) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = 0; new_idex->rt = 0; new_idex->rd = 0;
        new_idex->rsVal = 0; new_idex->rtVal = 0;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 0; // mux control second input
        new_idex->ALU.op = 0;
        new_idex->ALU.bNegate = 0;

        new_idex->memRead = 0;
        new_idex->memWrite = 0;
        new_idex->memToReg = 0;

        new_idex->regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 0; // writing to a destination

        return 1; // opcode was recognized
    }

    // ori
    if (fieldsIn->opcode == 0x0d) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
        new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 2; // mux control second input -- 2 for 0 extended
        new_idex->ALU.op = 1;
        new_idex->ALU.bNegate = 0;

        new_idex-> memRead = 0;
        new_idex-> memWrite = 0;
        new_idex-> memToReg = 0; // write to register

        new_idex-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 1; // writing to a destination

        return 1; // opcode was recognized
    }

    // lui
    if (fieldsIn->opcode == 0x0f) {

        if (IDstall == 1) {
            stall(IDstall, new_idex); // if we need to stall, then stall
            return 1;
        }
        new_idex->rs = fieldsIn->rs; new_idex->rt = fieldsIn->rt; new_idex->rd = fieldsIn->rd;
        new_idex->rsVal = rsVal; new_idex->rtVal = rtVal;
        new_idex->imm16 = fieldsIn->imm16; new_idex->imm32 = fieldsIn->imm32;
        new_idex->extra1 = 0; new_idex->extra2 = 0; new_idex->extra3 = 0;

        new_idex->ALUsrc = 2; // mux control second input -- 2 0 extend
        new_idex->ALU.op = 7;
        new_idex->ALU.bNegate = 0;

        new_idex-> memRead = 0;
        new_idex-> memWrite = 0;
        new_idex-> memToReg = 0; // write to register

        new_idex-> regDst = 0; // which destination are we writing to, 1 - rd, 0 - rt
        new_idex->regWrite = 1; // writing to a destination

        return 1; // opcode was recognized
    }

    return 0; // opcode not regonized
}

/*
 * EX_getALUinput1(in, old_exMem, old_memWb) -- Takes in a ID_EX pointer 'in' and an
 *      EX_MEM pointer 'old_exMem' and a MEM_WB pointer 'old_memWb' and this method acts
 *      as the MUX infront of the first input of the ALU in the EX phase and it determines
 *      what will go into the input. It determines if we need an input from an instruction
 *      1 ahead, 2 ahead or not at all. Method returns a WORD.
 */
WORD EX_getALUinput1(ID_EX *in, EX_MEM *old_exMem, MEM_WB *old_memWb) {

    // Looks one instruction ahead. Checks if the instruction ahead is writing to a reg.
    // Then it checks if the register it is writing to is our rs input if so then we get
    // The ALU value it has in the exMem
    if (old_exMem->regWrite && (old_exMem->writeReg == in->rs)) {
        return old_exMem->aluResult;
    }

    // Looks two instructions ahead. Checks if the 2 ahead instruction is writing to a reg.
    // Then it checks if the register it is writing to is our rs input. If so then we need
    // to check if we need to get the ALU result or a result from memory.
    if (old_memWb->regWrite && (old_memWb->writeReg == in->rs)) {

        // Checks the control wire of the MUX. If 1 then we get memory
        if (old_memWb->memToReg) {
            return old_memWb->memResult; // get the result from memory
        }
        return old_memWb->aluResult; // get the alu result
    }

    // If the value we have already is ok and not affected then we just return it
    return in->rsVal;

}

/*
 * EX_getALUinput2(in, old_exMem, old_memWb) -- Takes in a ID_EX pointer 'in' and an
 *      EX_MEM pointer 'old_exMem' and a MEM_WB pointer 'old_memWb' and this method acts
 *      as the MUX infront of the first input of the ALU in the EX phase and it determines
 *      what will go into the input. It determines if we need an input from an instruction
 *      1 ahead, 2 ahead or not at all. On top of that it determines if we need the sign
 *      extended 32 bit constant or the zero extended one. Method returns a WORD.
 */
WORD EX_getALUinput2(ID_EX *in, EX_MEM *old_exMem, MEM_WB *old_memWb) {
    // Since this is the second input MUX for the ALU need to check for I formats

    // If we need to return a zero extended constant
    if (in->ALUsrc == 2) {
        return in->imm16;
    }

    // If we need to return a sign extended constant
    if (in->ALUsrc == 1) {
        return in->imm32;
    }

    // Looks one instruction ahead. Checks if the instruction ahead is writing to a reg.
    // Then it checks if the register it is writing to is our rt input if so then we get
    // The ALU value it has in the exMem
    if (old_exMem->regWrite && (old_exMem->writeReg == in->rt)) {
        return old_exMem->aluResult;
    }

    // Looks two instructions ahead. Checks if the 2 ahead instruction is writing to a reg.
    // Then it checks if the register it is writing to is our rt input. If so then we need
    // to check if we need to get the ALU result or a result from memory.
    if (old_memWb->regWrite && (old_memWb->writeReg == in->rt)) {

        // Checks the control wire of the MUX. If 1 then we get memory
        if (old_memWb->memToReg) {
            return old_memWb->memResult; // get the result from memory
        }
        return old_memWb->aluResult; // get the alu result
    }

    // Just input from the Read Data 2
    // If the value we have already is ok and not affected then we just return it
    return in->rtVal;
}

/*
 * execute_EX(in, input1, input2, new_exMem) -- Takes in a ID_EX pointer 'in' 2 WORDs
 *      'input1' & 'input2' and a EX_MEM pointer 'new_exMem'. This method acts as the
 *      ALU and performs the operations of the ALU and then sets the fields of the
 *      EX/MEM pipline register 'new_exMem'. Nothing is returned.
 */
void execute_EX(ID_EX *in, WORD input1, WORD input2, EX_MEM *new_exMem) {

    // AND
    int andResult = input1 & input2;

    // OR/ ORI
    int orResult = input1 | input2;

    // ADD
    int addORsubResult;

    if (in->ALU.bNegate == 0) {
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

    // NOR -- !(A || B)
    int norResult = ~(input1 | input2);

    // LUI
    int luiResult = (input2 << 16); // shifting by 16 bits buts 0 in the lower half

    // NOP
    int nopResult = 0;

    // Update the EX/MEM pipline reg.

    new_exMem->rt = in->rt; // necessary for forwarding *into* SW instructions -- used to detect a hazard
    new_exMem->rtVal = in->rtVal; // carries the data-to-write for SW -- used if not a hazard
    new_exMem->memRead = in->memRead;
    new_exMem->memWrite = in->memWrite;
    new_exMem->memToReg = in->memToReg;
    new_exMem->regWrite = in->regWrite;

    // Means that the writeReg is rt
    if (in->regDst == 0) {
        new_exMem->writeReg = in->rt;
    }

    // Means that the writeReg is rd
    if (in->regDst == 1) {
        new_exMem->writeReg = in->rd;
    }

    // OUTPUT -- AND
    if (in->ALU.op == 0) {
        new_exMem->aluResult = andResult;
        return;
    }

    // OUTPUT -- OR/ ORI
    if (in->ALU.op == 1) {
        new_exMem->aluResult = orResult;
        return;
    }

    // OUTPUT -- ADD/ SUB
    if (in->ALU.op == 2) {
        new_exMem->aluResult = addORsubResult;
        return;
    }

    // OUTPUT -- LESS THAN
    if (in->ALU.op == 3) {
        new_exMem->aluResult = sltResult;
        return;
    }

    // OUTPUT -- XOR
    if (in->ALU.op == 4) {
        new_exMem->aluResult = xorResult;
        return;
    }

    // OUTPUT -- NOP
    if (in->ALU.op == 5) {
        new_exMem->aluResult = nopResult;
        return;
    }

    // OUTPUT -- NOR
    if (in->ALU.op == 6) {
        new_exMem->aluResult = norResult;
        return;
    }

    // OUTPUT -- LUI
    if (in->ALU.op == 7) {
        new_exMem->aluResult = luiResult;
    }
}

/*
 * execute_MEM(in, old_memWb, mem, new_memwb) -- Takes in a EX_MEM pointer 'in' and a
 *      MEM_WB pointer 'old_memWb' a WORD pointer 'mem' and a MEM_WB pointer 'new_memwb'.
 *      This method executes the MEM phase and it first determines if forwarding for sw
 *      should happen. Then it determines if writing happens, next it determines if reading
 *      happens or nothing uses the Data memory. Function does not return anything it just
 *      updates a 'new_memwb' pipline register.
 */
void execute_MEM(EX_MEM *in, MEM_WB *old_memWb, WORD *mem, MEM_WB *new_memwb) {

    // This represents a new MUX infront of the Write dataport
    // Need to check -- forwarding sw. If instruction 1 ahead of sw modifies the thing that
    // sw wants to store so update that value. Only forward when it is a sw and the thing out front in MEM/WB
    // Is going to write to a register
    int writingValue;
    if ((in->memWrite) && (old_memWb->writeReg == in->rt) && (old_memWb->regWrite == 1)) {
        // Need to check if that value is a ALU or Mem result

        // If it is a 1 then it is a Read mem result
        if (old_memWb->memToReg) {
            writingValue = old_memWb->memResult;
        }
        else {
            writingValue = old_memWb->aluResult; // means it is an ALU result
        }
    }
    else {
        writingValue = in->rtVal; // if no forwarding then it is just the rtVal
    }

    // *in determines if we are using memory or not

    // If we need to write
    if (in->memWrite == 1) {

        // *memory is an array of words, since its array of words & address array of bytes need to divide by 4
        mem[(in->aluResult) / 4] = writingValue;
        new_memwb->memResult = 0; // 0 since you stored it
        new_memwb->aluResult = in->aluResult;
        new_memwb->memToReg = in->memToReg;
        new_memwb->writeReg = in->writeReg;
        new_memwb->regWrite = in->regWrite;
        return;
    }

    // If we need to read
    if (in->memRead == 1) {

        new_memwb->memResult = mem[(in->aluResult) / 4];
        new_memwb->aluResult = in->aluResult;
        new_memwb->memToReg = in->memToReg;
        new_memwb->writeReg = in->writeReg;
        new_memwb->regWrite = in->regWrite;
        return;

    }

    // This is when not using the memory unit
    new_memwb->writeReg = in->writeReg;
    new_memwb->memResult = 0; // nothing happens.
    new_memwb->aluResult = in->aluResult;
    new_memwb->memToReg = in->memToReg;
    new_memwb->regWrite = in->regWrite;
}

/*
 * execute_WB(in, regs) -- Takes in a MEM_WB pointer 'in' and a WORD 'reg'
 *      and determines what is being written to memory and then writes
 *      it to memory. Nothing is returned. This function simulates the
 *      WB phase of the pipline.
 */
void execute_WB (MEM_WB *in, WORD *regs) {
    int whatBeingStored; // is either from memory or the ALU

    // from memory
    if (in->memToReg == 1) {
        whatBeingStored = in->memResult;

    }
    // not using memory only the ALU
    else {
        whatBeingStored = in->aluResult;
    }

    // Determine if we need to write to a register
    if (in->regWrite == 1) {
        regs[in->writeReg] = whatBeingStored;

    }

}