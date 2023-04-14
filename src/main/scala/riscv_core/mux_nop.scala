// See README.md for license details.

package mux_nop

import chisel3._
import chisel3.util._
import chisel3.stage.ChiselStage

/**
 * A mux to choose between 0x13(empty inst) or pc_4; pc stalling case for RISC-V Core
 */

class mux_nop extends Module {
  val io = IO(new Bundle {
    val pc_4 = Input(UInt(32.W))
    val pc_sel = Input(Bool())
    val inst = Output(UInt(32.W))
  })
  
  io.inst := Mux(io.pc_sel, 0x13.U, io.pc_4)

}

object getVerilog extends App {
  (new ChiselStage).emitVerilog(new mux_nop)
}