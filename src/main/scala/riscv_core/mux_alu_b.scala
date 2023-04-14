// See README.md for license details.

package mux_alu_b

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose among rs2, imm, alu result, loaded data from mem for RISC-V Core
 */

class mux_alu_b extends Module {
  val io = IO(new Bundle {
    val rs2 = Input(UInt(32.W))
    val imm = Input(UInt(32.W))
    val alu_out = Input(UInt(32.W))
    val mem_out = Input(UInt(32.W))
    val b_sel = Input(UInt(2.W))
    val alu_b = Output(UInt(32.W))
  })

  io.alu_b := MuxLookup(io.b_sel, io.rs2, Array(
    0.U -> io.rs2,
    1.U -> io.imm,
    2.U -> io.alu_out,
    3.U -> io.mem_out
  ))

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_alu_b)
}