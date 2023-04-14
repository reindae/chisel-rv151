// See README.md for license details.

package mux_alu_a

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose among rs1, pc, alu result, loaded data from mem for RISC-V Core
 */

class mux_alu_a extends Module {
  val io = IO(new Bundle {
    val rs1 = Input(UInt(32.W))
    val pc = Input(UInt(32.W))
    val alu_out = Input(UInt(32.W))
    val mem_out = Input(UInt(32.W))
    val a_sel = Input(UInt(2.W))
    val alu_a = Output(UInt(32.W))
  })

  io.alu_a := MuxLookup(io.a_sel, io.rs1, Array(
    0.U -> io.rs1,
    1.U -> io.pc,
    2.U -> io.alu_out,
    3.U -> io.mem_out
  ))

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_alu_a)
}