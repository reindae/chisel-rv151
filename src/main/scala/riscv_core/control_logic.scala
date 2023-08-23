// See README.md for license details.

package control_logic

import opcode._
import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * Control logic for RISC-V Core (single cycle)
 */

class control_logic extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val BrEq = Input(Bool())
    val BrLt = Input(Bool())

    val PCSel = Output(Bool())
    val ALUSel = Output(UInt(4.W))
    val BrUn = Output(Bool())
    val ASel = Output(Bool())
    val BSel = Output(Bool())
    val CSRSel = Output(Bool())
    val CSRWEn = Output(Bool())
    val DMEM_in = Output(UInt(32.W))
    val DMEM_WEn = Output(UInt(4.W))
    val DMEM_out = Output(UInt(4.W))
    val sign_ext = Output(Bool())
    val WBSel = Output(UInt(2.W))
    val RegWEn = Output(Bool())
    val has_rd = Output(Bool()) 
  })

  // Current opcode and func3
  val opc = io.curr_inst(6,0)
  val f3 = io.curr_inst(14,12)

  // Default value
  io.PCSel := 0.U
  io.ALUSel := 0.U
  io.BrUn := 0.U
  io.ASel := 0.U
  io.BSel := 0.U
  io.CSRWEn := 0.U
  io.CSRSel := 0.U
  io.DMEM_WEn := 0.U
  io.DMEM_in := 0.U
  io.DMEM_out := 0.U
  io.sign_ext := 0.U
  io.WBSel := 0.U
  io.RegWEn := 0.U
  io.has_rd := 0.U


  switch (opc) {
    // R-Type
    is (opcode.OPC_ARI_RTYPE) {
      io.ALUSel := MuxLookup(f3, 0.U, Array(
        opcode.FNC_ADD_SUB -> (Mux((io.inst(30) === opcode.FNC2_ADD), opcode.ALU_ADD, opcode.ALU_SUB)),
        opcode.FNC_OR -> opcode.ALU_OR,
        opcode.FNC_AND -> opcode.ALU_AND,
        opcode.FNC_XOR -> opcode.ALU_XOR,
        opcode.FNC_SLL -> opcode.ALU_SLL,
        opcode.FNC_SLT -> opcode.ALU_SLT,
        opcode.FNC_SLTU -> opcode.ALU_SLTU,
        opcode.FNC_SRL_SRA -> Mux((io.inst(30) === opcode.FNC2_SRL), opcode.ALU_SRL, opcode.ALU_SRA)
      ))
    }
  
    // I-Type
    is (opcode.OPC_ARI_ITYPE) {
      io.BSel := 1.U
      io.ALUSel := MuxLookup(f3, 0.U, Array(
        opcode.FNC_ADD_SUB -> opcode.ALU_ADD,
        opcode.FNC_OR -> opcode.ALU_OR,
        opcode.FNC_AND -> opcode.ALU_AND,
        opcode.FNC_XOR -> opcode.ALU_XOR,
        opcode.FNC_SLL -> opcode.ALU_SLL,
        opcode.FNC_SLT -> opcode.ALU_SLT,
        opcode.FNC_SLTU -> opcode.ALU_SLTU,
        opcode.FNC_SRL_SRA -> Mux((io.inst(30) === opcode.FNC2_SRL), opcode.ALU_SRL, opcode.ALU_SRA)
      ))
    }
    is (opcode.OPC_LOAD) {
      io.BSel := 1.U
      io.ALUSel := opcode.ALU_ADD
    }

    // S-Type
    is (opcode.OPC_STORE) {
      io.BSel := 1.U
      io.ALUSel := opcode.ALU_ADD
      io.DMEM_WEn := MuxLookup(f3, 0.U, Array(
        opcode.FNC_SB -> ("b0001".U(4.W) << io.curr_ALU_low2),
        opcode.FNC_SH -> ("b0011".U(4.W) << (Cat(0.U(1.W), io.curr_ALU_low2(1)) << 1)),
        opcode.FNC_SW -> "b1111".U(4.W)
      ))
      io.DMEM_in := MuxLookup(f3, 0.U, Array(
        opcode.FNC_SB -> Cat(0.U(24.W), 1.U(8.W)),
        opcode.FNC_SH -> Cat(0.U(16.W), 1.U(16.W)),
        opcode.FNC_SW -> 1.U(32.W)
      ))
    }

    // B-Type
    is (opcode.OPC_BRANCH) {
      io.ASel := 1.U
      io.BSel := 1.U
      io.ALUSel := opcode.ALU_ADD
      io.PCSel := MuxLookup(f3, 0.U, Array(
        opcode.FNC_BEQ -> io.BrEq,
        opcode.FNC_BNE -> !io.BrEq,
        opcode.FNC_BLT -> io.BrLt,
        opcode.FNC_BGE -> !io.BrLt,
        opcode.FNC_BLTU -> io.BrLt,
        opcode.FNC_BGEU -> !io.BrLt
      ))
      io.BrUn := Mux((f3 === opcode.FNC_BLTU || f3 === opcode.FNC_BGEU), 1.U, 0.U)
    }

    // J-Type
    is (opcode.OPC_JAL) {
      io.PCSel := 1.U
      io.ASel := 1.U
      io.BSel := 1.U
      io.ALUSel := opcode.ALU_ADD
    }
    is (opcode.OPC_JALR) {
      io.PCSel := 1.U
      io.ASel := 0.U
      io.BSel := 1.U
      io.ALUSel := opcode.ALU_ADD
    }

    // U-Type
    is (opcode.OPC_AUIPC) {
      io.ASel := 1.U
      io.BSel := 1.U
      io.ALUSel := opcode.ALU_ADD
    }
    is (opcode.OPC_LUI) {
      io.ASel := 1.U
      io.BSel := 1.U
      io.ALUSel := opcode.ALU_BSEL
    }

    // CSR
    is (opcode.OPC_CSR) {
      io.CSRSel := f3 === opcode.FNC_RWI
      io.CSRWEn := 1.U
    }


  }

  switch (prev_opc) {
    is (opcode.OPC_ARI_RTYPE, opcode.OPC_ARI_ITYPE, opcode.OPC_AUIPC, opcode.OPC_LUI) {
      io.RegWEn := io.prev_inst(11,7) =/= 0.U
      io.WBSel := "b01".U(2.W)
      io.has_rd := io.prev_inst(11,7) =/= 0.U
    }

    is (opcode.OPC_JAL, opcode.OPC_JALR) {
      io.RegWEn := io.prev_inst(11,7) =/= 0.U
      io.WBSel := "b10".U(2.W)
      io.has_rd := io.prev_inst(11,7) =/= 0.U
    }

    is (opcode.OPC_LOAD) {
      io.RegWEn := io.prev_inst(11,7) =/= 0.U
      io.sign_ext := Mux((prev_fn3 === opcode.FNC_LBU || prev_fn3 === opcode.FNC_LHU), 0.U, 1.U)
      io.DMEM_out := MuxLookup(prev_fn3, 0.U, Array(
        opcode.FNC_LW -> ("b1111".U(4.W)),
        opcode.FNC_LW -> ("b0011".U(4.W) << (Cat(0.U(1.W), io.prev_ALU_low2(1)) << 1)),
        opcode.FNC_LW -> ("b0011".U(4.W) << (Cat(0.U(1.W), io.prev_ALU_low2(1)) << 1)),
        opcode.FNC_LW -> ("b0001".U(4.W) << io.prev_ALU_low2),
        opcode.FNC_LW -> ("b0001".U(4.W) << io.prev_ALU_low2)
      ))
    }

    is (opcode.OPC_CSR) {
      io.has_rd := io.prev_inst(11,7) =/= 0.U
    }
  }

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new control)
}