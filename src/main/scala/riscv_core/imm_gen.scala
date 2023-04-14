// See README.md for license details.

package imm_gen

import opcode._
import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage


/**
 * Immediate generator for RISC-V Core
 */

class imm_gen extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val imm = Output(UInt(32.W))
  })


  io.imm := MuxLookup(io.inst(6,0), 0.U, Array(
    opcode.OPC_LUI -> Cat(io.inst(31,12), 0.U(12.W)),
    opcode.OPC_AUIPC -> Cat(io.inst(31,12), 0.U(12.W)),
    opcode.OPC_JAL -> Cat(Fill(12,io.inst(31)), io.inst(19,12), io.inst(20), io.inst(30,21), 0.U(1.W)),
    opcode.OPC_JALR -> Cat(Fill(20,io.inst(31)), io.inst(31,20)),
    opcode.OPC_BRANCH -> Cat(Fill(20,io.inst(31)), io.inst(7), io.inst(30,25), io.inst(11,8), 0.U(1.W)),
    opcode.OPC_LOAD -> Cat(Fill(20,io.inst(31)), io.inst(31,20)),
    opcode.OPC_STORE -> Cat(Fill(20,io.inst(31)), io.inst(31,25), io.inst(11,7)),
    opcode.OPC_ARI_ITYPE -> Cat(Fill(20,io.inst(31)), io.inst(31,20)),
    opcode.OPC_CSR -> Cat(0.U(27.W), io.inst(19,15))
  ))


  // Another way to write it using switch statement
  // io.imm := 0.U

  // switch(io.inst(6,0)) {
  //   is(opcode.OPC_LUI, opcode.OPC_AUIPC) {
  //     io.imm := Cat(io.inst(31,12), 0.U(12.W))
  //   }
  //   is(opcode.OPC_JAL) {
  //     io.imm := Cat(Fill(12,io.inst(31)), io.inst(19,12), io.inst(20), io.inst(30,21), 0.U(1.W))
  //   }
  //   is(opcode.OPC_JALR, opcode.OPC_LOAD, opcode.OPC_ARI_ITYPE) {
  //     io.imm := Cat(Fill(20,io.inst(31)), io.inst(31,20))
  //   }
  //   is(opcode.OPC_BRANCH) {
  //     io.imm := Cat(Fill(20,io.inst(31)), io.inst(7), io.inst(30,25), io.inst(11,8), 0.U(1.W))
  //   }
  //   is(opcode.OPC_STORE) {
  //     io.imm := Cat(Fill(20,io.inst(31)), io.inst(31,25), io.inst(11,7))
  //   }
  //   is(opcode.OPC_CSR) {
  //     io.imm := Cat(0.U(27.W), io.inst(19,15))
  //   }
  // }

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new imm_gen)
}


