// See README.md for license details.
package opcode

import chisel3._
import chisel3.util._

object opcode {

  // CSR instructions
  val OPC_CSR = 0x73.U(7.W)

  // Special immediate instructions
  val OPC_LUI = 0x37.U(7.W)
  val OPC_AUIPC = 0x17.U(7.W)

  // Jump instructions
  val OPC_JAL = 0x6F.U(7.W)
  val OPC_JALR = 0x67.U(7.W)

  // Branch instructions
  val OPC_BRANCH = 0x63.U(7.W)

  // Load and store instructions
  val OPC_STORE = 0x23.U(7.W)
  val OPC_LOAD = 0x03.U(7.W)

  // Arithmetic instructions
  val OPC_ARI_RTYPE = 0x33.U(7.W)
  val OPC_ARI_ITYPE = 0x13.U(7.W)

  // 5-bit Opcodes
  val OPC_LUI_5 = 0x0D.U(5.W)
  val OPC_AUIPC_5 = 0x05.U(5.W)
  val OPC_JAL_5 = 0x0B.U(5.W)
  val OPC_JALR_5 = 0x09.U(5.W)
  val OPC_BRANCH_5 = 0x08.U(5.W)
  val OPC_STORE_5 = 0x04.U(5.W)
  val OPC_LOAD_5 = 0x00.U(5.W)
  val OPC_ARI_RTYPE_5 = 0x0C.U(5.W)
  val OPC_ARI_ITYPE_5 = 0x04.U(5.W)

  // Function codes for Branch instructions
  val FNC_BEQ = 0x0.U(3.W)
  val FNC_BNE = 0x1.U(3.W)
  val FNC_BLT = 0x4.U(3.W)
  val FNC_BGE = 0x5.U(3.W)
  val FNC_BLTU = 0x6.U(3.W)
  val FNC_BGEU = 0x7.U(3.W)

  // Function codes for Load and Store instructions
  val FNC_LB = 0x0.U(3.W)
  val FNC_LH = 0x1.U(3.W)
  val FNC_LW = 0x2.U(3.W)
  val FNC_LBU = 0x4.U(3.W)
  val FNC_LHU = 0x5.U(3.W)
  val FNC_SB = 0x0.U(3.W)
  val FNC_SH = 0x1.U(3.W)
  val FNC_SW = 0x2.U(3.W)

  // Function codes for Arithmetic R-type and I-type instructions
  val FNC_ADD_SUB = 0x0.U(3.W)
  val FNC_SLL = 0x1.U(3.W)
  val FNC_SLT = 0x2.U(3.W)
  val FNC_SLTU = 0x3.U(3.W)
  val FNC_XOR = 0x4.U(3.W)
  val FNC_OR = 0x6.U(3.W)
  val FNC_AND = 0x7.U(3.W)
  val FNC_SRL_SRA = 0x5.U(3.W)

  // ADD and SUB use the same opcode + function code
  // SRA and SRL also use the same opcode + function code
  // For these operations, we also need to look at bit 30 of the instruction
  val FNC2_ADD = 0x0.U(1.W)
  val FNC2_SUB = 0x1.U(1.W)
  val FNC2_SRL = 0x0.U(1.W)
  val FNC2_SRA = 0x1.U(1.W)

  val FNC_RW = 0x1.U(3.W)
  val FNC_RWI = 0x5.U(3.W)

  val FNC7_0 = 0x00.U(7.W) // ADD, SRL
  val FNC7_1 = 0x20.U(7.W) // SUB, SRA

  // ALU selector
  val ALU_ADD = 0x1.U(4.W) 
  val ALU_AND = 0x2.U(4.W) 
  val ALU_OR = 0x3.U(4.W) 
  val ALU_XOR = 0x4.U(4.W) 
  val ALU_SUB = 0x5.U(4.W) 
  val ALU_SRA = 0x6.U(4.W) 
  val ALU_SLL = 0x7.U(4.W) 
  val ALU_SLT = 0x8.U(4.W) 
  val ALU_SLTU = 0x9.U(4.W) 
  val ALU_SRL = 0xA.U(4.W) 
  val ALU_BSEL = 0xB.U(4.W) 


}