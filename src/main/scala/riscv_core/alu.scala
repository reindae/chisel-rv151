// See README.md for license details.

package alu

import opcode._
import chisel3._
import chisel3.util._


/**
 * ALU for RISC-V Core
 */

class alu extends Module {
  val io = IO(new Bundle {
    val in_A = Input(UInt(32.W))
    val in_B = Input(UInt(32.W))
    val alu_sel = Input(UInt(4.W))
    val result = Output(UInt(32.W))
  })

  io.result := MuxLookup(io.alu_sel, 0.U, Array(
    opcode.ALU_ADD -> (io.in_A + io.in_B),
    opcode.ALU_SUB -> (io.in_A - io.in_B),
    opcode.ALU_SRA -> (io.in_A.asSInt >> io.in_B(4,0)).asUInt,
    opcode.ALU_SLL -> (io.in_A << io.in_B(4,0)),
    opcode.ALU_SRL -> (io.in_A >> io.in_B(4,0)),
    opcode.ALU_SLT -> (Mux(io.in_A.asSInt < io.in_B.asSInt, 1.U, 0.U)),
    opcode.ALU_SLTU -> (Mux(io.in_A < io.in_B, 1.U, 0.U)),
    opcode.ALU_XOR -> (io.in_A ^ io.in_B),
    opcode.ALU_AND -> (io.in_A & io.in_B),
    opcode.ALU_OR -> (io.in_A | io.in_B),
    opcode.ALU_BSEL -> io.in_B
  ))

}
